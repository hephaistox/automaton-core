(ns automaton-core.utils.string-to-id
  "Transform a string into an id
  That id are currently used and tested for React"
  (:require
    [clojure.string :as str]
    [automaton-core.utils.uuid-gen :as uuid]))

(defn string-to-id
  "Transform what is not alphanumerical to an id
  If `txt` is an empty string, a uuid turned into a string is returned
  Params:
  * `txt` text to transform"
  [txt]
  (if (str/blank? txt)
    (-> (uuid/unguessable)
        str)
    (-> txt
        str
        (str/replace #"[^\w]" "-")
        str/lower-case)))
