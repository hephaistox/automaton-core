(ns automaton-core.adapters.deps-edn
  "Proxy to deps.edn file"
  (:require
    [automaton-core.adapters.edn-utils :as edn-utils]))

(def deps-edn
  "deps.edn")

(defn load-deps
  "Load the current project `deps.edn` files"
  []
  (edn-utils/read-edn deps-edn))

(defn extract-paths
  "Extracts the paths from a given `deps.edn`
   e.g. {:run {...}}"
  ([{:keys [paths aliases]} exclude-aliases]
   (apply concat
          paths
          (map (fn [[_alias-name paths]]
                 (apply concat
                        (vals (select-keys paths [:extra-paths :paths]))))
               (apply dissoc aliases
                      exclude-aliases))))
  ([deps-edn]
   (extract-paths deps-edn #{})))

(defn extract-deps
  "Extract dependencies in a `deps.edn` file"
  [{:keys [deps aliases] :as _deps-edn}]
  (map (fn [[deps-name deps-map]]
         [deps-name deps-map])
       (concat deps
               (into {}
                     (apply concat
                            (map (fn [[_ alias-defs]]
                                   (vals
                                    (select-keys alias-defs [:extra-deps :deps])))
                                 aliases))))))

(defn remove-deps
  "Remove a dependency"
  [deps dep-lib-to-remove]
  (filter (fn [[dep-lib version]]
            (when-not (= dep-lib-to-remove
                         dep-lib)
              [dep-lib version]))
          deps))
