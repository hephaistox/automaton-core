(ns automaton-core.utils.map
  "Utility for map data structure"
  (:require
   [clojure.set  :as set]
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
  (let [rename
        (fn [el]
          (if (map? el)
            (seq (set/rename-keys
                  el
                  (->> (keys el)
                       (map
                        (fn [k] [k (keyword (str (name prefix) "." (name k)))]))
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
                (cond
                  (map? v) (let [prefixed (prefixify-map k v)]
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
  "Add the key to all map values (which are expected to be maps)."
  [m]
  (into {}
        (mapv (fn [[lang-id language]]
                (let [language (cond-> language
                                 (map? language) (assoc :id lang-id))]
                  [lang-id language]))
              m)))

(defn maps-to-key
  "`maps` is a list of map which value matching key `k` is used as a key to store the map as a value."
  [maps k]
  (->> (for [m maps] [(get m k) m])
       (filter (comp some? first))
       (into {})))

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
   on walk/stringify-keys.
   When key or value is nil, the pair is removed, as the hashmap doesn't allow null keys/values."
  [m modify-type-fn]
  (let [f (fn [[k v]]
            (let [k (if (keyword? k) (str (symbol k)) k)
                  v (if (keyword? v) (str (symbol v)) v)]
              (cond
                (map? v) [k (modify-type-fn v)]
                (and (some? v) (some? k)) [k v])))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn replace-keys
  "Replace keys in `m2` with keys from `m1`. Similiar to merge but non-existen keys in first map won't be added. e.g. (replace-keys {:a 3 :b 2} {:a 1}) -> {:a 3}"
  [m1 m2]
  (->> (select-keys m1 (keys m2))
       (merge m2)))

(defn remove-nil-vals
  [m]
  (->> m
       (keep (fn [[k v]] (when-not (nil? v) [k v])))
       (into {})))

(defn remove-nil-submap-vals
  [m]
  (->> (for [[k v] m] (if (map? v) [k (remove-nil-vals v)] [k v]))
       remove-nil-vals
       (into {})))

(defn map-difference
  [m1 m2]
  (loop [m (transient {})
         ks (concat (keys m1) (keys m2))]
    (if-let [k (first ks)]
      (let [e1 (find m1 k)
            e2 (find m2 k)]
        (cond
          (and e1 e2 (not= (e1 1) (e2 1))) (recur (assoc! m k (e1 1)) (next ks))
          (not e1) (recur (assoc! m k (e2 1)) (next ks))
          (not e2) (recur (assoc! m k (e1 1)) (next ks))
          :else (recur m (next ks))))
      (persistent! m))))

(defn keys->sequence-number
  "Return a map associating a key of the map `m` with a number, numbered from 1 to n."
  [m]
  (zipmap (keys (into {} m)) (iterate inc 1)))

(defn translate-keys
  "Translate keys of map `m` thanks to the `translation`."
  [m translation]
  (->> m
       (map (fn [[k v]] [(get translation k k) v]))
       (into {})))

(defn translate-vals
  "Translate vals of map `m` thanks to the `translation`."
  [m translation]
  (->> m
       (map (fn [[k v]] [k (get translation v k)]))
       (into {})))

(defn submap?
  "Is `sub` existing in `m`?"
  [sub m]
  (if (and (map? sub) (map? m))
    (every? (fn [[k v]] (and (contains? m k) (submap? v (get m k)))) sub)
    (= sub m)))

(defn get-key-or-before
  "Returns the key if it exists in the sorted-map

  Note this is not an efficient way

  Params:
  * `m` the sorted map
  * `n`"
  [^clojure.lang.PersistentTreeMap m n]
  (->> m
       keys
       (take-while (partial >= n))
       last))

(defn get-key-or-after
  "Returns the key if it exists in the sorted-map

  Note this is not an efficient way

  Params:
  * `m` the sorted map
  * `n`"
  [^clojure.lang.PersistentTreeMap m n]
  (->> m
       keys
       (take-while (partial <= n))
       last))
