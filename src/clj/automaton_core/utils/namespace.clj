(ns automaton-core.utils.namespace
  (:require [clojure.string :as str]))

(defn namespaced-keyword
  "Create a namepsaced keyword"
  [ns kw]
  (-> (str/join "/"
                (->> [(cond (nil? ns) nil
                            :else (name ns))
                      (cond (nil? kw) nil
                            :else (name kw))]
                     (filterv some?)))
      symbol))
