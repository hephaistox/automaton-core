(ns automaton-core.repl
  "REPL component"
  (:require
   [cider.nrepl :as nrepl-mw]
   [nrepl.server :refer [default-handler start-server stop-server]]

   [automaton-core.adapters.files :as files]
   [automaton-core.log :as log]
   [automaton-core.configuration.core :as conf]))

(def nrepl-port-filename
  "Name of the `.nrepl-port` file"
  ".nrepl-port")

(defn custom-nrepl-handler
  "We build our own custom nrepl handler"
  [nrepl-mws]
  (apply default-handler
         nrepl-mws))

(def repl
  "Store the repl instance in the atom"
  (atom {}))

(defn get-nrepl-port-parameter
  []
  (conf/read-param [:dev :clj-nrepl-port]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn get-active-nrepl-port
  "Retrieve the nrepl port, available for REPL"
  []
  (:nrepl-port @repl))

(defn stop-repl
  "Stop the repl"
  [repl-port]
  (log/info "Stop nrepl server on port" repl-port)
  (stop-server (:repl @repl))
  (reset! repl {}))

(defn start-repl*
  "Launch a new repl"
  [{:keys [middleware]}]
  (let [repl-port (get-nrepl-port-parameter)
        dirs (files/list-subdir ".")]
    (doseq [dir dirs]
      (when-not (files/hidden? dir)
        (files/write-file (str repl-port)
                          (files/create-file-path dir nrepl-port-filename))))
    (reset! repl {:nrepl-port repl-port
                  :repl (do
                          (log/info "nrepl available on port " repl-port)
                          (println "repl port is available on:" repl-port)
                          (start-server :port repl-port
                                        :handler (custom-nrepl-handler middleware)))})

    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. #(do
                                  (log/info "SHUTDOWN in progress" repl-port)
                                  (stop-repl repl-port))))))

(defn start-repl
  "Start repl, setup and catch errors"
  []
  (try
    (start-repl* {:middleware (conj nrepl-mw/cider-middleware 'refactor-nrepl.middleware/wrap-refactor)})
    :started
    (catch Exception e
      (log/error "Uncaught exception" e))))
