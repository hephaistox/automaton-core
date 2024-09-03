(ns automaton-core.utils.sequences "Manipulation of sequences")

(defn trim-leading-nil
  "Remove nil values at the end of a sequence
  Params:
  * `aseq` a sequence which will be tested starting from the end
  * `nil-fn` is the function to test nullity, (defaulted to `nil?`)"
  ([aseq nil-fn]
   (loop [aseq aseq
          i 40] ;; For security reason
     (when-not (pos? i) (throw (ex-info "Infinite loop detected" {})))
     (if (and (seq aseq) (nil-fn (last aseq))) (recur (butlast aseq) (dec i)) aseq)))
  ([aseq] (trim-leading-nil aseq nil?)))

(defn index-of
  "Find the position of x, the first occurence matching(x) in coll.

  Returns its position
   Params:
  * `coll` collection to search in
  * `pred` pred is a function returning true when called on the element to return the sequence of"
  [coll pred]
  (let [idx? (fn [i a] (when (pred a) i))] (first (keep-indexed idx? coll))))

(defn indexed
  "Returns a lazy sequence of [index, item] pairs, where items come
  from 's' and indexes count up from zero.

  (indexed '(a b c d))  =>  ([0 a] [1 b] [2 c] [3 d])"
  [s]
  (map vector (iterate inc 0) s))

(defn positions
  "Returns a lazy sequence containing the positions at which pred
   is true for items in coll."
  [pred coll]
  (for [[idx elt] (indexed coll) :when (pred elt)] idx))

(defn concat-at
  "Concatenate sequences `x` and `y`, `y` is inserted at position `kw`.
  If `kw` not found, `y` is concatenated at the end of `x`."
  [x kw y]
  (if-let [i (first (positions #{kw} x))]
    (concat (take i x) y (drop (inc i) x))
    (concat x y)))

(defn position-by-values
  "Returns a map which associates each value of the vector to the positions where it happens.

  Params:
  * `v`"
  [v]
  (reduce (fn [res i]
            (update res (nth v i) (fn [previous] (if (empty? previous) [i] (conj previous i)))))
          {}
          (range (count v))))
