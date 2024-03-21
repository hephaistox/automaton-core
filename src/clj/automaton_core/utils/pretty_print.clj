(ns automaton-core.utils.pretty-print
  "Gathers functions related to pretty printing or pretty formatting."
  (:require
   [automaton-core.adapters.string :as bas]
   [clojure.pprint                 :as pp]))

(defn one-liner-print
  "Prepare the element `elt` to display in the print
           Params:
            * `elt` data to show, which type will be checked"
  [elt]
  (if (or (map? elt) (set? elt) (vector? elt))
    (-> elt
        pp/pprint
        with-out-str
        bas/remove-last-character)
    elt))

(defn seq->string
  "Returns string from sequence, that is readable in write outputs."
  [message]
  (if (seqable? message) (apply str "" (map one-liner-print message)) message))
