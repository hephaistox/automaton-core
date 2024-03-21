(ns automaton-core.os.code-formatter
  "Format code
  Proxy to [zprint](https://github.com/kkinnear/zprint)"
  (:require
   [automaton-core.adapters.time :as time]
   [clojure.string               :as str]
   [zprint.core                  :as zprint]))

(defn add-header
  [header]
  (when-not (str/blank? header) (print-str ";;" header (time/now-str) "\n")))

(defn format-content
  "Format the `clj` or `edn` file
  Returns a string, ready to be spitted, with the header
  Params:
  * `header` (optional) is written at the top of the file
  * `content` is a data structure to format"
  ([header content]
   (let [format-content (-> content
                            zprint/zprint-str)]
     (apply str [(add-header header) format-content])))
  ([content] (format-content nil content)))
