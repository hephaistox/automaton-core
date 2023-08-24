(ns automaton-core.log
  "Entry point for log. Is now a simple redirection to clojure tools logging
  Set to log4j2 [check setup in](clojure/deps.edn)
 Check https://logging.apache.org/log4j/2.x/manual/configuration.html for configuration details"
  (:require [clojure.tools.logging :as l]))

(defmacro trace [& message]
  `(l/trace ~@message))

(defmacro debug [& message]
  `(l/debug ~@message))

(defmacro info [& message]
  `(l/info ~@message))

(defmacro warn [& message]
  `(l/warn ~@message))

(defmacro error [& message]
  `(l/error ~@message))

(defmacro fatal [& message]
  `(l/error ~@message))
