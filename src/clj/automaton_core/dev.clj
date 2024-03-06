(ns automaton-core.dev
  "Will be `refer :all` in the subproject `user` namespace, default namepsace for subproject REPL

  Linter doesn't detect that all that functions are callable from the user namespace of the repl."
  (:require
   [clj-memory-meter.core :as mm]))

;; See https://github.com/clojure-goes-fast/clj-memory-meter, for details
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn measure-time-sample [] (mm/measure "hello world"))
