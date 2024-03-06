(ns automaton-core.repl.entry-point
  "REPL entry point"
  (:require
   [automaton-core.repl :as core-repl]
   [cider.nrepl :as cider-nrepl]
   [refactor-nrepl.middleware])
  (:gen-class))

(defn- tst [& _args] nil)

(defn -main
  "Main entry point for repl"
  [& args]
  (core-repl/start-repl args
                        (conj cider-nrepl/cider-middleware
                              'refactor-nrepl.middleware/wrap-refactor)
                        tst))
