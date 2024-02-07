(ns automaton-core.utils.namespace
  (:require
   [automaton-core.log :as core-log]
   [clojure.string :as str]))

(defn namespaced-keyword
  "Create a namespaced keyword"
  [ns kw]
  (->> [(when-not (nil? ns) (name ns)) (when-not (nil? kw) (name kw))]
       (filterv some?)
       (str/join "/")
       symbol))

(defn require-ns
  "Require the namespace of the body-fn
  Params:
  * `f` is a function full qualified symbol. It could be a string"
  [f]
  (some-> f
          symbol
          namespace
          symbol
          require))

(defn symbol-to-fn-call
  "Resolve the symbol and execute the associated function"
  [f & args]
  (if-let [res (try (require-ns f)
                    (some-> f
                            resolve)
                    (catch Exception e (core-log/warn-exception e)))]
    (apply res args)
    (core-log/warn-format "No valid function passed, (i.e %s)" f)))
