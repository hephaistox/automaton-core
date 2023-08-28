(ns automaton-core.adapters.version
  "Version of an application"
  (:require
   [automaton-core.adapters.edn-utils :as edn-utils]
   [automaton-core.adapters.files :as files]))

(def version-filename
  "The name of the version file to be found in ready to be deployed application"
  "version.edn")

(defn slurp-version
  "Get the version of the current application, if not found a local dev value is given"
  []
  (if (files/is-existing-file? version-filename)
    (edn-utils/read-edn-or-nil version-filename)
    {:release "local-dev"}))
