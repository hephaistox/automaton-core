(ns automaton-core.core
  "Gather all components to app
  This core is for test only, if you need a repl running only automaton-core code"
  (:require [mount.core :as mount]
            [mount.tools.graph :as graph]
            [automaton-core.i18n.language.lang-core]
            [automaton-core.i18n.translate.translate-core]
            [automaton-core.log :as log]
            [automaton-core.configuration.core])
  (:gen-class))

(defn -main
  "Main entry point for repl"
  [& _args]
  (log/info "Start `automaton-core`")
  (log/trace "Component dependencies: " (graph/states-with-deps))
  (mount/start))
