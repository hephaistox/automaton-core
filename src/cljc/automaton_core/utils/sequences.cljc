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
