(ns automaton-core.files.json
  "Everything about json manipulation"
  (:require
   [automaton-core.adapters.files :as files]
   [automaton-core.log :as core-log]
   [clojure.data.json :as json]
   [clojure.java.io :as io]))

(defn read-file
  [filepath]
  (try (json/read-str (files/read-file filepath))
       (catch Exception e
         (core-log/error-exception e "Loading json file has failed " filepath)
         nil)))

(defn write-file
  [target-path content]
  (try (with-open [w (io/writer target-path)]
         (json/write content w :indent true :escape-slash false))
       (catch Exception e
         (core-log/error-exception e "Writing json file has failed")
         (core-log/error-data {:target-path target-path
                               :content content
                               :e e})
         nil)))
