(ns automaton-core.adapters.deps-edn
  "Proxy to read of deps.edn file"
  (:require
   [automaton-core.adapters.edn-utils :as edn-utils]
   [automaton-core.adapters.files :as files]
   [automaton-core.log :as log]))

(def deps-edn
  "deps.edn")

(defn get-deps-filename
  "Get the deps-file of the application
  Params:
  * `app-dir` is where the application is stored"
  [app-dir]
  (files/create-file-path app-dir
                          deps-edn))

(defn load-deps
  "Load the current project `deps.edn` files"
  []
  (edn-utils/read-edn deps-edn))

(defn load-deps-edn
  "Load the deps.edn file of the app, passed as a parameter,
  Params:
  * `app-dir` the directory of the app, where `deps.edn` is stored
  Returns nil if the file does not exists or is malformed"
  [app-dir]
  (edn-utils/read-edn-or-nil (get-deps-filename app-dir)))

(defn update-commit-id
  "Update the `deps-edn` with the `commit-id` for the dependency `as-lib`
  Params:
  * `as-lib` is the symbol for the library to update
  * `commit-id` is the sha of the commit
  * `deps-edn` is the content of the file"
  [as-lib commit-id deps-edn]
  (if-let [old-commit-id (get-in deps-edn [:deps as-lib :git/sha])]
    (if (= commit-id
           old-commit-id)
      (do
        (log/trace "Skip update as it already uptodate")
        deps-edn)
      (do
        (log/trace "Change commit from `" old-commit-id "` to `" commit-id "`")
        (assoc-in deps-edn
                  [:deps as-lib :git/sha]
                  commit-id)))
    (do
      (log/trace "Skip as it does not use lib `%s:%s`")
      deps-edn)))

(defn extract-paths
  "Extracts the `:paths` and `:extra-paths` from a given `deps.edn`
   e.g. {:run {...}}
  Params:
  * `deps-edn` content the deps edn file to search extract path in
  * `excluded-aliases` is a collection of aliases to exclude"
  ([{:keys [paths aliases]
     :as _deps-edn} excluded-aliases]
   (let [selected-aliases (apply dissoc aliases
                                 excluded-aliases)
         alias-paths (mapcat (fn [[_alias-name paths]]
                               (apply concat
                                      (vals (select-keys paths [:extra-paths :paths]))))
                             selected-aliases)]
     (->> alias-paths
          (concat paths)
          sort
          dedupe
          (into []))))
  ([deps-edn]
   (extract-paths deps-edn #{})))

(defn extract-deps
  "Extract dependencies in a `deps.edn` file
  Params:
  * `deps-edn` is the content of the file to search dependencies in
  * `excluded-aliases` is a collection of aliases to exclude"
  [{:keys [deps aliases] :as _deps-edn} excluded-aliases]
  (let [selected-aliases (apply dissoc aliases excluded-aliases)]
    (->> selected-aliases
         (map (fn [[_ alias-defs]]
                (vals
                 (select-keys alias-defs [:extra-deps :deps]))))
         (apply concat)
         (into {})
         (concat deps)
         (map (fn [[deps-name deps-map]]
                [deps-name deps-map])))))

(defn remove-deps
  "Remove the dependency called `dep-lib-to-remove` in the `deps`
  * `deps-edn` the deps edn content to update
  * `dep-libs-to-remove` is the list of dependencies to remove"
  [deps-edn dep-libs-to-remove]
  (update deps-edn
          :deps
          #(apply dissoc %
                  dep-libs-to-remove)))
