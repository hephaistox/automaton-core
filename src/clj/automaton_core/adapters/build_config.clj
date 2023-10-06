(ns automaton-core.adapters.build-config
  "Manage `build-config.edn` file"
  (:require
   [automaton-core.adapters.edn-utils :as edn-utils]
   [automaton-core.adapters.files :as files]))

(def build-config-filename
  "build_config.edn")

(def build-config-repo-link
  [:publication :link])

(def repo-name
  [:publication :repo-name])

(def prod-web-link
  [:run-env :prod-env :web-link])

(def test-web-link
  [:run-env :test-env :web-link])

(defn search-for-build-config
  "Scan the directory to find build-config files, starting in the current directory
  Useful to discover applications
  Search in the local directory, useful for application repo
  and in subdir, useful for monorepo

  It is important not to search everywehere in the paths as `tmp` directories may contains unwanted `build_config.edn` files

  Params:
  * none
  Returns the list of directories with `build_config.edn` in it"
  ([config-path]
   (->> (files/search-files config-path
                            (str "{"build-config-filename",*/" build-config-filename ",*/*/" build-config-filename "}"))
        flatten
        (filter (comp not nil?))))
  ([] (search-for-build-config "")))

(defn build-configs-libs
  [accepted-libs]
  (let [build-configs (search-for-build-config)
        libs (keep
              (fn [build-filename]
                (let [edn-file (edn-utils/read-edn build-filename)]
                  (when-let [lib (get-in
                                  edn-file
                                  [:publication :as-lib])]
                    (when (some #{lib} accepted-libs)
                      {:lib lib
                       :dir (get-in edn-file [:monorepo
                                              :app-dir])}))))
              build-configs)]
    libs))

(defn spit-build-config
  "Spit a build config file
  Params:
  * `app-dir` where to store the build_config file
  * `content` to spit
  * `msg` (optional) to add on the top of the file"
  ([app-dir content msg]
   (let [filename (files/create-file-path app-dir
                                          build-config-filename)]
     (edn-utils/spit-edn filename
                         content
                         msg)
     filename))
  ([app-dir content]
   (spit-build-config app-dir content nil)))
