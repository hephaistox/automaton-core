(ns automaton-core.app.apps
  "Manage set of applications in the monorepo"
  (:require [automaton-core.app.build-config :as build-config]
            [automaton-core.adapters.files :as files]
            [automaton-core.adapters.deps-edn :as deps-edn]))

(defn load-apps
  "For the list of build_config.edn file names,
  create a succession of app of it
  Returns a map with the following keys valued:
  * `app-dir` the directory of the app
  * `deps-edn` the deps-edn file content
  * `build-config` the build-config file content

  Params:
  * `build_configs` collection of build_config file path"
  [app-dir]
  (->> (build-config/search-for-build-config app-dir)
       (mapv (fn [build-config-file]
               (let [path (files/extract-path build-config-file)
                     build-config (build-config/load-build-config path)]
                 {:app-dir path
                  :deps-edn (deps-edn/load-deps-edn path)
                  :as-lib (get-in build-config [:publication :as-lib])
                  :app-name (get build-config :app-name)
                  :build-config build-config})))))

