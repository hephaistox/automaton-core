(ns automaton-core.repl.entry-point
  "REPL entry point"
  (:require
   [automaton-core.repl :as core-repl])
  (:gen-class))

(defn- tst [& _args] (constantly nil))

(defn -main
  "Main entry point for repl"
  [& args]
  (core-repl/start-repl args (core-repl/default-middleware) tst))
