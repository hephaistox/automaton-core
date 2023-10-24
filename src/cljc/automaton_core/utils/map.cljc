(ns automaton-core.utils.map
  "Utility for map data structure"
  (:require [clojure.set :as set]
            [clojure.walk :as walk]))

(defn idx-of
  "Return the index of the first found value in the sequence"
  [v value]
  (ffirst (filter #(= value (second %)) (map-indexed vector v))))

(defn idx-of-pred
  "Same as idx-of but with a predicate"
  [v pred]
  (when (and pred (fn? pred))
    (ffirst (filter #(pred (second %)) (map-indexed vector v)))))

(defn deep-merge
  "Deep merge nested maps.
  Last map has higher priority

  This code comes from this [gist](https://gist.github.com/danielpcox/c70a8aa2c36766200a95)"
  [& maps]
  (apply merge-with
    (fn [& args]
      (if (every? #(or (map? %) (nil? %)) args)
        (apply deep-merge args)
        (last args)))
    maps))

(defn prefixify-map
  [prefix thing]
  (if (map? thing)
    (set/rename-keys
      thing
      (->> (keys thing)
           (map (fn [k] [k (keyword (str (name prefix) "." (name k)))]))
           (into {})))
    thing))

(defn prefixify-vec
  [prefix thing]
  (let [rename (fn [el]
                 (if (map? el)
                   (seq (set/rename-keys el
                                         (->> (keys el)
                                              (map (fn [k] [k
                                                            (keyword
                                                              (str (name prefix)
                                                                   "."
                                                                   (name k)))]))
                                              (into {}))))
                   el))]
    (if (vector? thing)
      (let [maps (filter map? thing)
            non-maps (remove map? thing)]
        (merge (->> (map rename maps)
                    (apply concat)
                    (group-by key)
                    (map (fn [[k vs]] {k (map second vs)}))
                    (into {}))
               (when (seq non-maps) {prefix non-maps})))
      thing)))

(defn prefixify-children
  [thing]
  (if (map? thing)
    (->> thing
         (map (fn [[k v]]
                (cond (map? v) (let [prefixed (prefixify-map k v)]
                                 (if (map? prefixed) prefixed {k v}))
                      (vector? v) (let [prefixed (prefixify-vec k v)]
                                    (if (map? prefixed) prefixed {k v}))
                      :else {k v})))
         (apply merge))
    thing))

(defn crush
  "Crush the map"
  [m]
  (when (map? m)
    (->> (walk/postwalk prefixify-children m)
         (map (fn [[k v]] {k (if (sequential? v) (flatten v) v)}))
         (into {}))))

(defn add-ids
  "Add the key to all map values (which are expected to be maps)
  Params:
  * `m` map"
  [m]
  (into {}
        (mapv (fn [[lang-id language]]
                (let [language (cond-> language
                                 (map? language) (assoc :id lang-id))]
                  [lang-id language]))
          m)))

(defn update-kw
  "Update the keywords `kws` in map `m` with function `f`"
  [m kws f]
  (reduce (fn [m k]
            (if (contains? m k) (let [v (get m k)] (assoc m k (f v))) m))
    m
    kws))

(defn apply-to-keys
  "Apply function `f` to each key in `ks` in the maps in `maps`
  Params:
  * `f` fn with three arguments. First is `k` the key currently update (one of `ks`), second is `m` the current value of the m while it is updated, third is `v` the current value of key `k` in map `m`
  * `maps` is a sequence of map
  * `ks` keys in the map to apply `f` to"
  [maps f & ks]
  (mapv (fn [m] (reduce (fn [m k] (assoc m k (f m k (get m k)))) m ks)) maps))

(defn map-util-hashmappify-vals
  "Converts an ordinary Clojure map into a Clojure map with nested map
   values recursively translated into what modify-type-fn is returning. Based
   on walk/stringify-keys."
  [m modify-type-fn]
  (let [f (fn [[k v]]
            (let [k (if (keyword? k) (name k) k)
                  v (if (keyword? v) (name v) v)]
              (if (map? v) [k (modify-type-fn v)] [k v])))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))
