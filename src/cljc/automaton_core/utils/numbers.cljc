(ns automaton-core.utils.numbers
  "Helpers for manipulating numbers in the same way between clj and cljs")

(defn check-val-in-range
  "Returns `nil` if `val` is in the range `[min;max[`.
  Otherwise, returns `val` itself."
  [min max val]
  (cond
    (> min val) val
    (<= max val) val
    :else nil))


(defn check-vals-in-range
  "Check if `vals` are between the range of integers (i.e. `[min;max[`.
  Returns the value if it exceeds the range."
  [min max vals]
  (filter (partial check-val-in-range min max) vals))
