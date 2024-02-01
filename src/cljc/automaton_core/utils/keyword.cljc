(ns automaton-core.utils.keyword
  "Utility functions for keywords."
  (:require
   [clojure.string :as str]))

(defn- trim-colon
  "If string `s` starts with `:` char it is removed."
  [s]
  (if (= ":" (first s)) (rest s) s))

(defn keywordize
  "Change string to appropriate clojure keyword"
  [s]
  (-> (name s)
      trim-colon
      str/lower-case
      (str/replace "_" "-")
      (str/replace "." "-")
      (keyword)))

(defn sanitize-map-keys
  "Changes all keywords in a map to appropriate clojure keys."
  [map]
  (reduce-kv (fn [acc key value]
               (let [new-key (if (keyword? key) (keywordize key) key)
                     new-val (if (map? value) (sanitize-map-keys value) value)]
                 (assoc acc new-key new-val)))
             {}
             map))
