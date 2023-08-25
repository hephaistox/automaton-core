(ns automaton-core.adapters.bb-edn
  "Adapter for `bb.edn`"
  (:require
   [automaton-core.adapters.edn-utils :as edn-utils]
   [automaton-core.adapters.files :as files]))

(def bb-edn-filename
  "Should not be used externally except in test namespaces"
  "bb.edn")

(defn update-bb-edn
  "Update the `bb-edn` with the mono file with the file parameter, keep :tasks and :init keys and refresh aliases with tasks content
  Params:
  * `bb-edn-dir` name of the dir where the bb.edn is
  * `tasks` list of tasks to push in the `bb.edn` file"
  [bb-edn-dir update-bb-edn-fn]
  (let [bb-edn-filename (files/create-file-path bb-edn-dir bb-edn-filename)
        bb-edn (edn-utils/read-edn bb-edn-filename)
        updated-bb-edn (update-bb-edn-fn bb-edn)]
    (edn-utils/spit-edn bb-edn-filename
                        updated-bb-edn)))
