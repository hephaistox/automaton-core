(ns automaton-core.adapters.log
  "Is a copy of automaton-core.log namespace, this ns decorrelate from clojure logging tool

  Currently using log4j2 [check in](clojure/deps.edn)
Check https://logging.apache.org/log4j/2.x/manual/configuration.html for configuration details"
  (:require
   [clojure.pprint :as pp]
   [clojure.tools.logging :as l]))

(defn prettify
  "Transform all "
  [& args]
  (map (fn [elt]
         (if (or (map? elt)
                 (set? elt)
                 (vector? elt))
           (with-out-str
             (pp/pprint elt))
           elt))
       args))

(defmacro trace [& message]
  `(l/trace (prettify ~@message)))

(defmacro debug [& message]
  `(l/debug (prettify ~@message)))

(defmacro info [& message]
  `(l/info (prettify ~@message)))

(defmacro warn [& message]
  `(l/warn (prettify ~@message)))

(defmacro error [& message]
  `(l/error (prettify ~@message)))

(defmacro fatal [& message]
  `(l/fatal (prettify ~@message)))
