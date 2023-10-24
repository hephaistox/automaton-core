(ns automaton-core.os.code-formatter
  "Format code
  Proxy to [zprint](https://github.com/kkinnear/zprint)"
  (:require [automaton-core.adapters.files :as files]
            [automaton-core.adapters.commands :as cmds]
            [automaton-core.adapters.time :as time]
            [clojure.string :as str]))

(defn format-file
  "Format the `clj` or `edn` file
  Params:
  * `filename` to format
  * `header` (optional) is written at the top of the file"
  ([filename header]
   (let [format-content (slurp filename)]
     (files/spit-file filename
                      (apply str
                        [(when-not (str/blank? header)
                           (print-str ";;" header (time/now-str) "\n"))
                         format-content]))
     (cmds/exec-cmds [[["zprint" "-w" filename] {:dir ".", :out :string}]])))
  ([filename] (format-file filename nil)))

