(ns automaton-core.repl.entry-point
  "REPL entry point"
  (:require
   [automaton-build.repl.launcher :as build-repl-launcher])
  (:gen-class))

(defn -main
  "Main entry point for repl"
  [& _args]
  (build-repl-launcher/start-repl))
