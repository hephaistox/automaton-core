(ns automaton-core.repl-core
  "repl for `automaton-core` stand alone
  This namespace is called to start a repl on `automaton-core` only"
  (:require
   [automaton-core.log :as log]
   [automaton-core.repl :as core-repl]
   [clojure.core.async :refer [<! chan go]]
   [mount.core :as mount]
   [mount.tools.graph :as graph])
  (:gen-class))

(defn -main
  "Main entry point for repl"
  [& _args]
  (log/info "Start `automaton-core`")
  (log/trace "Component dependencies: " (graph/states-with-deps))
  (mount/start)
  (core-repl/start-repl)
  (let [c (chan 1)]
    (go
      (<! c))))
