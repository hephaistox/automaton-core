(ns automaton-core.i18n.utils
  (:require
   [automaton-core.utils.map :as crusher]
   [clojure.set :refer [union]]))

(def french
  "fr")

(def english
  "en")

(defn language-report
  "For all keys of a dictionnary, return the list of languages set
  `expected-languages` is the languages sequence the report is limited to"
  [dictionary expected-languages]
  (let [filtered-dictionary (select-keys dictionary expected-languages)]
    (apply merge-with union
           (map (fn [[language dict-map]]
                  (into {}
                        (map (fn [v]
                               [v #{language}])
                             (keys
                              (crusher/crush dict-map)))))
                filtered-dictionary))))

(defn key-with-missing-languages
  "Return a map with the path to a translation, with the list of existing languages
  key-exceptions is a sequence or set of all keys that should be excluded from the error list
  `expected-languages` is the languages the report is limited to"
  [dictionary expected-languages key-exceptions]
  (let [key-set-exceptions (into #{} key-exceptions)]
    (filter (fn [[k v]]
              (and (not (contains? key-set-exceptions k))
                   (not= v expected-languages)))
            (language-report dictionary
                             expected-languages))))
