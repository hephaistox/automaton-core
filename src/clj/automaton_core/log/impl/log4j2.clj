(ns automaton-core.log.impl.log4j2
  "It's a simple redirection to clojure tools logging
   Set to log4j2 [check setup in](clojure/deps.edn)

   For more information read docs/tutorial/logging.md"
  (:require
   [automaton-core.utils.pretty-print :as pretty-print]
   [clojure.tools.logging             :as l]))

(defn log-fn
  [ns level & message]
  (l/log ns level nil (pretty-print/seq->string message)))
