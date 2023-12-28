(ns automaton-core.app.build-config
  "Adapter to `build_config.edn`"
  (:require [automaton-core.adapters.files :as files]
            [automaton-core.adapters.edn-utils :as edn-utils]))

(def build-config-filename "build_config.edn")

(defn load-build-config
  "Load the `build_config.edn` file
  Params:
  * `app-dir` the root of the app where the build_config.edn can be found."
  [app-dir]
  (->> build-config-filename
       (files/create-file-path app-dir)
       edn-utils/read-edn))

(defn search-for-build-configs
  "Scan the directory to find build-config files, starting in the current directory
  Useful to discover applications
  Search in the local directory, useful for application repo
  and in subdir, useful for monorepo

  It is important not to search everywehere in the paths as `tmp` directories may contains unwanted `build_config.edn` files

  Params:
  * `root-dir`
  Returns the list of directories with `build_config.edn` in it"
  [root-dir]
  (->> (files/search-files root-dir (str "{" build-config-filename ",*/" build-config-filename ",*/*/" build-config-filename "}"))
       flatten
       (filterv (comp not nil?))))
