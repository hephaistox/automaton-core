(ns automaton-core.utils.map
  "Utility for map data structure"
  (:require [clojure.set :as set]
            [clojure.walk :as walk]))

(defn idx-of
  "Return the index of the first found value in the sequence"
  [v value]
  (ffirst
   (filter #(= value (second %))
           (map-indexed vector v))))

(defn idx-of-pred
  "Same as idx-of but with a predicate"
  [v pred]
  (when (and pred
             (fn? pred))
    (ffirst
     (filter #(pred (second %))
             (map-indexed vector v)))))

;; See https://gist.github.com/danielpcox/c70a8aa2c36766200a95
(defn deep-merge
  "Deep merge nested maps"
  [& maps]
  (apply merge-with (fn [& args]
                      (if (every? #(or (map? %) (nil? %)) args)
                        (apply deep-merge args)
                        (last args)))
         maps))

(defn prefixify-map [prefix thing]
  (if (map? thing)
    (set/rename-keys
     thing
     (->> (keys thing)
          (map (fn [k]
                 [k
                  (keyword (str (name prefix) "." (name k)))]))
          (into {})))
    thing))

(defn prefixify-vec [prefix thing]
  (let [rename (fn [el]
                 (if (map? el)
                   (seq
                    (set/rename-keys
                     el
                     (->> (keys el)
                          (map (fn [k]
                                 [k
                                  (keyword (str (name prefix) "." (name k)))]))
                          (into {}))))
                   el))]
    (if (vector? thing)
      (let [maps (filter map? thing)
            non-maps (remove map? thing)]
        (merge
         (->> (map rename maps)
              (apply concat)
              (group-by key)
              (map (fn [[k vs]]
                     {k (map second vs)}))
              (into {}))
         (when (seq non-maps)
           {prefix non-maps})))
      thing)))

(defn prefixify-children [thing]
  (if (map? thing)
    (->> thing
         (map (fn [[k v]]
                (cond (map? v)
                      (let [prefixed (prefixify-map k v)]
                        (if (map? prefixed)
                          prefixed
                          {k v}))

                      (vector? v)
                      (let [prefixed (prefixify-vec k v)]
                        (if (map? prefixed)
                          prefixed
                          {k v}))

                      :else
                      {k v})))
         (apply merge))
    thing))

(defn crush
  "Crush the map"
  [m]
  (when (map? m)
    (->> (walk/postwalk prefixify-children m)
         (map (fn [[k v]]

                {k (if (sequential? v)
                     (flatten v)
                     v)}))
         (into {}))))
