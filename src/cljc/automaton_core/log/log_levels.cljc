(ns automaton-core.log.log-levels "Defines the possible log levels and their sequence")

(def levels-sequence
  "List error levels in our app, first is more detailed, last is least detailed"
  {:trace 10
   :debug 20
   :info 30
   :warn 40
   :error 50
   :fatal 60})

(defn execute-min-level?
  "Returns true if we decide to apply log to that levl regarding minimimum required
  Params:
  * `min-level-kw` - keyword of the minimum log level expected
  * `level-kw` - keyword of the log to test"
  [min-level-kw level-kw]
  (when (and level-kw min-level-kw)
    (apply <= (map (partial get levels-sequence) [min-level-kw level-kw]))))

(defn execute-max-level?
  [max-level-kw level-kw]
  (when (and level-kw max-level-kw)
    (apply >= (map (partial get levels-sequence) [max-level-kw level-kw]))))

(defn execute-level?
  "Returns true if the `level` is in range of `min-level` and `max-level`.
   If no `min-level` or `max-level` is supplied, only the other is taken into account.
   In all the other cases returns nil."
  [{:keys [min-level max-level level]}]
  (cond
    (and min-level max-level) (and (execute-min-level? min-level level)
                                   (execute-max-level? max-level level))
    (some? min-level) (execute-min-level? min-level level)
    (some? max-level) (execute-max-level? max-level level)
    :else nil))
