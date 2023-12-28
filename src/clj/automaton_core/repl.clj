(ns automaton-core.repl
  "REPL component

  Design decision:
  * This REPL is available in `automaton-core.repl` for enabling the repl for local acceptance and production
  * This namespace rely on `automaton-core.configuration`, which means no log could be done before configuration is loaded"
  (:require [automaton-core.adapters.files :as files]
            [automaton-core.configuration :as conf]
            [automaton-core.log :as core-log]
            [automaton-core.repl.portal :as core-portal]
            [cider.nrepl :as cider-nrepl]
            [clojure.core.async :refer [<!! chan]]
            [nrepl.server :refer [default-handler start-server stop-server]]))

(def nrepl-port-filename "Name of the `.nrepl-port` file" ".nrepl-port")

(defn custom-nrepl-handler "We build our own custom nrepl handler" [nrepl-mws] (apply default-handler nrepl-mws))

(def repl "Store the repl instance in the atom" (atom {}))

(defn get-nrepl-port-parameter [] (conf/read-param [:dev :clj-nrepl-port] 8000))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn get-active-nrepl-port "Retrieve the nrepl port, available for REPL" [] (:nrepl-port @repl))

(defn stop-repl
  "Stop the repl"
  [repl-port]
  (core-log/info "Stop nrepl server on port" repl-port)
  (stop-server (:repl @repl))
  (reset! repl {}))

(defn create-nrepl-files
  "Consider all deps.edn files as the root of a clojure project and creates a .nrepl-port file next to it"
  [repl-port]
  (let [build-configs (files/search-files "" "**build_config.edn")
        nrepl-ports (map #(files/file-in-same-dir % nrepl-port-filename) build-configs)]
    (doseq [nrepl-port nrepl-ports] (files/write-file nrepl-port (str repl-port)))))

(defn start-repl*
  "Launch a new repl"
  [{:keys [middleware]}]
  (let [repl-port (get-nrepl-port-parameter)]
    (core-portal/start)
    (core-portal/portal-connect)
    (create-nrepl-files repl-port)
    (reset! repl {:nrepl-port repl-port
                  :repl (do (core-log/info "nrepl available on port " repl-port)
                            (println "repl port is available on:" repl-port)
                            (start-server :port repl-port :handler (custom-nrepl-handler middleware)))})
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. #(do (core-log/info "SHUTDOWN in progress, stop repl on port `" repl-port "`")
                                    (core-portal/stop)
                                    (-> (files/search-files "" (str "**" nrepl-port-filename))
                                        (files/delete-files))
                                    (stop-repl repl-port))))))

(defn start-repl
  "Start repl, setup and catch errors"
  []
  (try (start-repl* {:middleware (conj cider-nrepl/cider-middleware 'refactor-nrepl.middleware/wrap-refactor)})
       :started
       (catch Exception e (core-log/error (ex-info "Uncaught exception" {:error e})))))

(defn -main
  "Entry point for simple / emergency repl"
  [& _args]
  (let [c (chan)]
    (start-repl)
    (<!! c)))
