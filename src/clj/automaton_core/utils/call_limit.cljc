(ns automaton-core.utils.call-limit "Limit the number of calls of a function")

(def ^:private n "Number of call per key" (atom {}))

(defn allow-one-only-call
  "For key `k`, allow one call only of function `f`.
  Following calls will throw an exception."
  [f k]
  (if (pos? (get @n k 0))
    (throw (ex-info (str "More than one call to " k " detected") {}))
    (do (swap! n update k (fnil inc 0)) (f))))

(defn remove-fn-call
  "Removes a call so next `allow-one-only-call` will be successful.
  Use it when the previous call scope of `allow-one-only-call` is lost. Especially useful for tests."
  [k]
  (when (pos? (get @n k 0)) (swap! n update k dec)))
