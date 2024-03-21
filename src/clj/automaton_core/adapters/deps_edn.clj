(ns automaton-core.adapters.deps-edn
  "Proxy to deps.edn file"
  (:require
   [automaton-core.adapters.edn-utils :as edn-utils]
   [automaton-core.adapters.files     :as files]))

(def deps-edn "deps.edn")

(defn load-deps
  "Load the current project `deps.edn` files"
  []
  (edn-utils/read-edn deps-edn))

(defn extract-paths
  "Extracts the `:paths` and `:extra-paths` from a given `deps.edn`
   e.g. {:run {...}}
  Params:
  * `deps-edn` content the deps edn file to search extract path in
  * `excluded-aliases` is a collection of aliases to exclude"
  ([{:keys [paths aliases]
     :as _deps-edn}
    excluded-aliases]
   (let [selected-aliases (apply dissoc aliases excluded-aliases)
         alias-paths
         (mapcat (fn [[_alias-name paths]]
                   (apply concat
                          (vals (select-keys paths [:extra-paths :paths]))))
          selected-aliases)]
     (->> alias-paths
          (concat paths)
          sort
          dedupe
          (into []))))
  ([deps-edn] (extract-paths deps-edn #{})))

(defn extract-deps
  "Extract dependencies in a `deps.edn` file
  Params:
  * `deps-edn` is the content of the file to search dependencies in
  * `excluded-aliases` is a collection of aliases to exclude"
  [{:keys [deps aliases]
    :as _deps-edn}
   excluded-aliases]
  (let [selected-aliases (apply dissoc aliases excluded-aliases)]
    (->> selected-aliases
         (map (fn [[_ alias-defs]]
                (vals (select-keys alias-defs [:extra-deps :deps]))))
         (apply concat)
         (into {})
         (concat deps)
         (map (fn [[deps-name deps-map]] [deps-name deps-map])))))

(defn remove-deps
  "Remove the dependency called `dep-lib-to-remove` in the `deps`
  * `deps-edn` the deps edn content to update
  * `dep-libs-to-remove` is the list of dependencies to remove"
  [deps-edn dep-libs-to-remove]
  (update deps-edn :deps #(apply dissoc % dep-libs-to-remove)))

(defn is-hephaistox-deps
  "For a deps entry, return true if the dependency is from hephaistox monorepo

  Params:
  * `dep` is a pair of value, as seen in the `:deps` map"
  [dep]
  (->> dep
       first
       namespace
       (= "hephaistox")))

(defn hephaistox-deps
  "Filter the
  Params:
  * `deps-edn` the deps-edn file content"
  [deps-edn]
  (->> deps-edn
       :deps
       (filter is-hephaistox-deps)
       keys
       vec))

(defn update-dep-local-root
  "Update the local root directories in a dependency map (of one lib)
  After the update, the local root path will be relative and starting from `base-dir`

  Params:
  * `base-dir` is the directory where you look at that app from
  * `dep-map` dep is a dependency map (of one lib)"
  [base-dir dep-map]
  (if (contains? dep-map :local/root)
    (update dep-map :local/root (partial files/create-dir-path base-dir))
    dep-map))

(defn update-alias-local-root
  "Update the local root directories in an alias
  After the update, the local root path will be relative and starting from `base-dir`

  Params:
  * `base-dir` is the directory where you look at that app from
  * `alias-map` is the content of the alias as found in the `deps.edn` file"
  [base-dir alias-map]
  (cond-> alias-map
    (contains? alias-map :extra-deps)
    (update :extra-deps
            #(update-vals % (partial update-dep-local-root base-dir)))
    (contains? alias-map :deps)
    (update :deps #(update-vals % (partial update-dep-local-root base-dir)))))

(defn update-aliases-local-root
  "Update all aliases local root to be relative starting from base-dir
  Params:
  * `base-dir`
  * `aliases-map` the map describing the alias as seen in deps.edn"
  [base-dir aliases-map]
  (update-vals aliases-map (partial update-alias-local-root base-dir)))

(defn update-deps-edn-local-root
  "Update all the deps-edn file to be relative starting from base-dir,
  Are considered `:deps` and `:extra-deps` in aliases and at the root of the `deps.edn` file
  Params:
  * `base-dir`
  * `deps-edn` the content of the file"
  [base-dir deps-edn]
  (->> (update deps-edn :aliases (partial update-aliases-local-root base-dir))
       (update-alias-local-root base-dir)))
