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
     (if (and (seq aseq) (nil-fn (last aseq)))
       (recur (butlast aseq) (dec i))
       aseq)))
  ([aseq] (trim-leading-nil aseq nil?)))

(defn index-of
  "Find the position of x, the first occurence matching(x) in coll.

  Returns its position
   Params:
  * `coll` collection to search in
  * `pred` pred is a function returning true when called on the element to return the sequence of"
  [coll pred]
  (let [idx? (fn [i a] (when (pred a) i))] (first (keep-indexed idx? coll))))

(defn position-by-values
  "Returns a map which associates each value of the vector to the positions where it happens.

  Params:
  * `v`"
  [v]
  (reduce (fn [res i]
            (update res
                    (nth v i)
                    (fn [previous]
                      (if (empty? previous) [i] (conj previous i)))))
          {}
          (range (count v))))
