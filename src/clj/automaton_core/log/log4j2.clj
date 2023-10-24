(ns automaton-core.log.log4j2
  "It's a simple redirection to clojure tools logging
   Set to log4j2 [check setup in](clojure/deps.edn)

   For more information read docs/tutorial/logging.md"
  (:require [automaton-core.adapters.string :as bas]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.tools.logging :as l]))

(defn prettify-elt
  "Prepare the element `elt` to display log as text in the console
  Params:
  * `elt` data to show, which type will be checked"
  [elt]
  (if (or (map? elt) (set? elt) (vector? elt))
    (-> elt
        pp/pprint
        with-out-str
        bas/remove-last-character)
    elt))

(defn prettify
  [message]
  (if (seqable? message) (str/join "" (map prettify-elt message)) message))

(defn log-fn [ns level & message] (l/log ns level nil (prettify message)))
