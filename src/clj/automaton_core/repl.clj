(ns automaton-core.repl
  "REPL component. Could be used remotely to connect on production or locally to connect a development environment

  Design decision:
  * This REPL is available in `automaton-core.repl` for enabling the repl for local acceptance and production
  * This namespace rely on `automaton-core.configuration`, which means no log could be done before configuration is loaded"
  (:require
   [automaton-core.adapters.files  :as files]
   [automaton-core.configuration   :as core-conf]
   [automaton-core.log             :as core-log]
   [automaton-core.log.terminal    :as core-log-terminal]
   [automaton-core.portal.server   :as core-portal-server]
   [automaton-core.utils.namespace :as core-namespace]
   [nrepl.server                   :refer
                                   [default-handler start-server stop-server]]))

(defn- force-option?
  [args]
  (filter some? (map #(contains? #{"-f" "--force"} %) args)))

(def nrepl-port-filename "Name of the `.nrepl-port` file" ".nrepl-port")

(def repl "Store the repl instance in the atom" (atom {}))

(defn get-nrepl-port-parameter
  []
  (core-conf/read-param [:dev :clj-nrepl-port] 8000))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn get-active-nrepl-port
  "Retrieve the nrepl port, available for REPL"
  []
  (:nrepl-port @repl))

(defn- stop-repl
  "Stop the repl"
  []
  (stop-server (:repl @repl))
  (reset! repl {}))

(defn create-nrepl-files
  "Consider all deps.edn files as the root of a clojure project and creates a .nrepl-port file next to it"
  [repl-port]
  (let [build-configs (files/search-files "" "**build_config.edn")
        nrepl-ports (map #(files/file-in-same-dir % nrepl-port-filename)
                         build-configs)]
    (doseq [nrepl-port nrepl-ports]
      (files/write-file nrepl-port (str repl-port)))))

(defn- start-repl*
  [middlewares]
  (let [repl-port (get-nrepl-port-parameter)]
    (create-nrepl-files repl-port)
    (reset! repl {:nrepl-port repl-port
                  :repl (do (core-log/info "nrepl available on port " repl-port)
                            (core-log-terminal/log
                             "-> Repl port is available on: "
                             repl-port)
                            (start-server :port repl-port
                                          :handler (apply default-handler
                                                          middlewares)))})
    (core-portal-server/start)
    (.addShutdownHook
     (Runtime/getRuntime)
     (Thread. #(do (core-log-terminal/log
                    "SHUTDOWN in progress, stop repl on port `"
                    repl-port
                    "`")
                   (shutdown-agents)
                   (stop-repl)
                   (-> (files/search-files "" (str "**" nrepl-port-filename))
                       (files/delete-files))
                   (core-portal-server/stop)
                   (println "SHUTDOWN ends successfully"))))))

(defn default-middleware
  []
  (let [cider-middlewares (core-namespace/try-require
                           'cider.nrepl/cider-middleware)
        nrepl-middleware (core-namespace/try-require
                          'refactor-nrepl.middleware/wrap-refactor)]
    (cond-> []
      cider-middlewares (concat @(resolve cider-middlewares))
      nrepl-middleware (conj nrepl-middleware)
      true vec)))

(defn start-repl
  "Start repl, setup and catch errors
  Params:
  * `mdws` List of middlewares"
  [args mdws main-fn]
  (try (when-not (force-option? args) (main-fn))
       (start-repl* mdws)
       (ns user
         (:require
          [automaton-core.dev :refer :all]))
       :started
       (catch Exception e
         (core-log/error (ex-info "Failed to start, relaunch with -force option"
                                  {:error e}))
         nil)))
