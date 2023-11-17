(ns automaton-core.app.compose-deps-edn.compose-deps-edn-impl
  (:require [automaton-core.adapters.files :as files]
            [automaton-core.adapters.deps-edn :as deps-edn]
            [automaton-core.app.deps-graph :as deps-graph]
            [automaton-build-app.os.files :as build-files]))

(defn update-app-src
  "The paths in the collection pathv are updated so the directory of the app is a prefix"
  [app-dir pathv]
  (->> pathv
       (map (fn [src-item] (files/create-dir-path app-dir src-item)))
       sort
       dedupe
       vec))

#_{:clj-kondo/ignore [:unused-private-var]}
(defn- local-root
  [compose-deps-edn-dir apps]
  (let [lib-to-app-dir (->> apps
                            (map (fn [app] [(:as-lib app) (:app-dir app)]))
                            (filter (comp some? first))
                            (into {}))]
    (map
     (fn [[lib _lib-details :as dep]]
       (if (deps-edn/is-hephaistox-deps dep) [lib {:local/root (files/relativize (get lib-to-app-dir lib) compose-deps-edn-dir)}] dep)))))

(defn compare-deps [deps1 deps2] (if (pos? (compare (:mvn/version deps1) (:mvn/version deps2))) deps1 deps2))

(defn update-deps
  "Return the map of dependencies, as in the deps.edn :deps keyword.

  Add dependencies from `:deps-edn` of each project and `:deps-edn` and `:extra-deps` of all aliases`
    Params:
  * `excluded-aliases` aliases that will not be considered
  * `apps` collection of apps"
  [excluded-aliases apps]
  (->> apps
       (map (fn [{:keys [deps-edn]
                  :as _app}]
              (->> (deps-edn/extract-deps deps-edn excluded-aliases)
                   (remove deps-edn/is-hephaistox-deps)
                   (into {}))))
       (apply merge-with compare-deps)
       (deps-edn/update-alias-local-root "..")
       (into (sorted-map))))

(defn path
  "Add all `path` directories of all projects (cust-app or not) in the everything app.
  Concerns `src` and `tests` directories.
  The function adds a prefix with the name of the app, so the source files will be found from `clojure/name-of-the-app/src` dir
  :run aliases are excluded.
  Params:
  * `compose-deps-edn-dir` directory where the compose deps.edn file will be stored
  * `excluded-aliases` aliases that will not be added in the path
  * `apps` collection of apps"
  [compose-deps-edn-dir excluded-aliases apps]
  (->> apps
       (mapcat (fn [{:keys [app-dir deps-edn]}]
                 (->> (deps-edn/extract-paths deps-edn excluded-aliases)
                      (update-app-src (files/relativize app-dir compose-deps-edn-dir)))))
       sort
       dedupe
       vec))

(defn create-test-alias-src-path
  "The test alias tells what are classpath for tests, the composite deps.edn needs to add this to the "
  [apps]
  (->> apps
       (mapcat (fn [{:keys [app-dir]
                     :as app}]
                 (->> (get-in app [:deps-edn :aliases :common-test :extra-paths])
                      (mapv (partial build-files/create-dir-path app-dir)))))
       (filterv some?)))

(defn aliases
  "Return the map of aliases, as in the deps.edn :aliases keyword.
  Return the aliases coming from the monorepo-app, present it properlly, adjust directories

  Params:
  * `compose-deps-edn-dir` directory where the compose deps.edn file will be stored
  * `excluded-aliases` aliases that will not be considered
  * `apps` collection of apps"
  [compose-deps-edn-dir excluded-aliases apps]
  (let [{:keys [app-dir]
         :as app}
        (deps-graph/app-by-name "monorepo-app" apps)]
    (-> (->> app
             :deps-edn
             :aliases
             (sorted-set-by (fn [a b] (compare (:app-name a) (:app-name b))))
             (remove excluded-aliases)
             (into {}))
        (update-in [:env-development-test :main-opts] #(concat % (interleave (repeat "-d") (create-test-alias-src-path apps))))
        ((partial deps-edn/update-aliases-local-root (files/relativize app-dir compose-deps-edn-dir))))))
