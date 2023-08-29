(ns automaton-core.dev
  "Will be `refer :all` in the subproject `user` namespace, default namepsace for subproject REPL"
  (:require
   [clj-memory-meter.core :as mm]

   [automaton-core.repl :as repl]))

(defn start
  "Start repl"
  [& _args]
  (repl/start-repl))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)

;; See https://github.com/clojure-goes-fast/clj-memory-meter, for details
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn measure-time-sample
  []
  (mm/measure "hello world"))
