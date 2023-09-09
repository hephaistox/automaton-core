(ns automaton-core.repl-core
  "Gather all components to app
  This core is for test only, if you need a repl running only automaton-core code"
  (:require
   [automaton-core.configuration.core]
   [automaton-core.i18n.language.lang-core]
   [automaton-core.repl :as core-repl]
   [automaton-core.i18n.translate.translate-core]
   [automaton-core.log :as log]
   [mount.core :as mount]
   [clojure.core.async :refer [go chan <!]]
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
