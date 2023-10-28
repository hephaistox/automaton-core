(ns automaton-core.log.strategy.static-ns-level
  "Base strategy to statically define which logger to use

  This strategy is a static one, it means it is applied at the compile time, so:
  * Any evolution of that strategy requires a new deployment.
  * All drop logs are not time consuming (except if the caller line makes some computation - which is the responsability of the caller)
  * All selected lines could then decide on runtime if there are needed or not. For instance log4j2 logger could use its own parameters to update dynamically which one is used or not

  See `automaton-core.log.strategy.static-ns-level/ns-rules` for rule definition details "
  (:require [automaton-core.log.log-levels :as log-levels]
            [automaton-core.log.strategy :as log-strategy]
            [automaton-core.log.registry :as log-registry]))

(def ns-rules
  "Decides the level of log depending on namespace
  The order is important, the rules at the beginning of the sequence are superseeding the last ones
  So the first matching rule is applying
  This order (a little bit counter intuitive) has been chosen for performance/simplicity reasons

  * `rule-id` - id of the rule, used for tests and readibility
  * `re` - is the regular expression to apply on the namespace to decide if the rule apply or not
  * `min-level` - is the minimum expected log level to be printed (so all greater or equal log levels are printed). Not providing that value means all levels accepted
  * `:logger` - seq of loggers ids to apply, loggers themselves are defined later on, in clj and cljs sides as their implementation depends on the technology most of the time"
  [{:rule-id :rule2,
    :re #"automaton-core.log",
    :min-level :debug,
    :logger [::log-registry/print]}
   {:rule-id :rule1,
    :re #"automaton-core.*",
    :min-level :info,
    :logger [::log-registry/print]}])

(defn apply-ns-rule
  "If `ns` match the regular expression `re` then return a vector with:
  * the minimum level required to display that message
  * a description of the rule as a second parameter (useful for tracing the decision if log are needed)"
  [ns {:keys [re], :as rule}]
  (when re (when (re-find re (str ns)) rule)))

(defrecord StaticNsLevelStrategy [ns-rules]
  log-strategy/Strategy
    (apply-strategy [_ ns level]
      (let [active-rule (some-> (keep (partial apply-ns-rule ns) ns-rules)
                                first)]
        (if (some-> active-rule
                    :min-level
                    (log-levels/execute-level? level))
          (:logger active-rule)
          [::log-registry/text-based ::log-registry/error-tracking])))
    (rule-ids [_]
      (set (cons ::log-registry/no-op
                 (reduce (fn [acc rule] (concat acc (:logger rule)))
                   []
                   ns-rules)))))

(defn make-static-ns-level-strategy
  "Build the `log-strategy/Strategy` instance applying the static ns level rule
  Params:
  * `ns-rules` set of rules to feed the chooser"
  ([ns-rules*] (->StaticNsLevelStrategy ns-rules*))
  ([] (make-static-ns-level-strategy ns-rules)))
