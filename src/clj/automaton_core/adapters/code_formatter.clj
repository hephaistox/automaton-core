(ns automaton-core.adapters.code-formatter
  "Format code
  Proxy to [zprint](https://github.com/kkinnear/zprint)"
  (:require
   [automaton-core.adapters.files :as files]
   [automaton-core.adapters.time :as time]
   [clojure.string :as str]
   [zprint.core :as zp]))

(defn format-content
  "Format the data structure"
  [content]
  (zp/zprint-str content))


(defn format-file
  "Format the `clj` or `edn` file
  Params:
  * `filename` to format
  * `header` (optional) is written at the top of the file"
  ([filename header]
   (let [format-content (zp/zprint-file-str (slurp filename)
                                            nil)]
     (files/spit-file filename
                      (apply str [(when-not (str/blank? header)
                                    (println-str ";;" header (time/now-str)))
                                  format-content]))))
  ([filename]
   (format-file filename nil)))
