#_{:heph-ignore {:forbidden-words ["automaton-web"]}}
(ns automaton-core.log.strategy.static-ns-level
  "Base strategy to statically define which logger to use

  This strategy is a static one, it means it is applied at the compile time, so:
  * Any evolution of that strategy requires a new deployment.
  * All drop logs are not time consuming (except if the caller line makes some computation - which is the responsability of the caller)
  * All selected lines could then decide on runtime if there are needed or not. For instance log4j2 logger could use its own parameters to update dynamically which one is used or not

  See `automaton-core.log.strategy.static-ns-level/ns-rules` for rule definition details "
  (:require
   [automaton-core.log.log-levels :as log-levels]
   [automaton-core.log.registry   :as log-registry]
   [automaton-core.log.strategy   :as log-strategy]))

(def ns-rules
  "Decides the level of log depending on namespace
  The order is important, the rules at the beginning of the sequence are superseeding the last ones
  So the first matching rule is applying
  This order (a little bit counter intuitive) has been chosen for performance/simplicity reasons.


  * `rule-id` - id of the rule, if set to :default is going to be treated as default logger, when multiple with same logger defined, the first one will be chosen, default logger is ignored when the same logger is defined in ns specific rule.
  * `re` - is the regular expression to apply on the namespace to decide if the rule apply or not
  * `env` - is the environment in which the log should be printed, if not specified it will be considered in all envs.
  * `min-level` - is the minimum expected log level to be printed (so all greater or equal log levels are printed). The levels below the min-level are silenced. Not providing that value means only max-level is accepted or if it's not set all levels.
  * `max-level` - is the maximum expected log level to be printed (so all below or equal log levels are printed). The levels above the max-level are silenced. Not providing that value means only min-level is accepted or if it's also not set all levels.
  * `:logger` - seq of loggers ids to apply, loggers themselves are defined later on, in clj and cljs sides as their implementation depends on the technology most of the time.
"
  [{:rule-id :default
    :env :production
    :min-level :trace
    :max-level :warn
    :logger ::log-registry/error-tracking-context}
   {:rule-id :default
    :env :production
    :min-level :error
    :logger ::log-registry/error-tracking-alert}
   {:rule-id :default
    :logger ::log-registry/text-based
    :min-level :info}
   {:rule-id :monorepo-app
    :re #"monorepo-app.*"
    :min-level :info
    :logger ::log-registry/print}
   {:rule-id :ac-edn-utils
    :re #"automaton-core.adapters.edn-utils"
    :min-level :info
    :logger ::log-registry/text-based}
   {:rule-id :aw-duplex-messages
    :re #"automaton-web.duplex.message-handler"
    :min-level :info
    :logger ::log-registry/text-based}
   {:rule-id :log-http
    :re #"automaton-web.middleware.log-http"
    :min-level :info
    :logger ::log-registry/text-based}])

(defn apply-ns-rule
  "If `ns` match the regular expression `re` then return a vector with:
  * the minimum level required to display that message
  * a description of the rule as a second parameter (useful for tracing the decision if log are needed)"
  [ns
   {:keys [re]
    :as rule}]
  (when re (when (re-find re (str ns)) rule)))

(defn- filter-rules-default-loggers
  "Returns sequence of rules that has id set to :default"
  [rules]
  (filter (fn [rule] (= :default (:rule-id rule))) rules))

(defn- filter-rules-by-ns
  "Returns all rules that fit for the provided ns."
  [rules ns]
  (keep (partial apply-ns-rule ns) rules))

(defn- filter-rules-by-unique-loggers
  "Returns sequence of rules where each logger is unique.
   If there is more than one of the same loggers, the first one is chosen."
  [rules]
  (map first (vals (group-by :logger rules))))

(defn- filter-rules-loggers-not-in
  "Returns coll1 rules that doesn't have a logger in rules from coll2"
  [coll1 coll2]
  (filter (fn [coll1-rule]
            (every? (fn [coll2-rule]
                      (not= (:logger coll1-rule) (:logger coll2-rule)))
                    coll2))
          coll1))

(defn- rules-seq-loggers
  "Returns vector of loggers from sequences with rules.
   If there is no rules with loggers, returns empty vector."
  [& rules-seq]
  (reduce (fn [loggers rule] (conj loggers (:logger rule)))
          []
          (into [] (apply concat rules-seq))))

(defn filter-rules-by-env
  [rules env]
  (filter (fn [rule] (if (:env rule) (= env (:env rule)) true)) rules))

(defn filter-rules-by-level
  [rules level]
  (filter #(log-levels/execute-level? {:min-level (:min-level %)
                                       :max-level (:max-level %)
                                       :level level})
          rules))

(defrecord StaticNsLevelStrategy [config ns-rules]
  log-strategy/Strategy
    (apply-strategy [_ ns level]
      (let [env (:env config)
            default-loggers-rules (filter-rules-default-loggers ns-rules)
            default-loggers-rules-accepted (-> default-loggers-rules
                                               (filter-rules-by-env env)
                                               (filter-rules-by-level level))
            matching-ns-rules (-> ns-rules
                                  (filter-rules-by-ns ns)
                                  filter-rules-by-unique-loggers)
            default-loggers-rules-kept (filter-rules-loggers-not-in
                                        default-loggers-rules-accepted
                                        matching-ns-rules)
            matching-ns-rules-kept (filter-rules-by-level matching-ns-rules
                                                          level)
            loggers (rules-seq-loggers default-loggers-rules-kept
                                       matching-ns-rules-kept)
            loggers* (if (empty? loggers) [::log-registry/no-op] loggers)]
        loggers*))
    (rule-ids [_]
      (->> ns-rules
           rules-seq-loggers
           (cons ::log-registry/no-op)
           set)))

(defn make-static-ns-level-strategy
  "Build the `log-strategy/Strategy` instance applying the static ns level rule
  Params:
  * `config` config data that is used with ns-rules for helping to choose the right rules (e.g. currently we pass there config with enviroment data)
  * `ns-rules` set of rules to feed the chooser
  "
  ([config ns-rules*] (->StaticNsLevelStrategy config ns-rules*))
  ([config] (make-static-ns-level-strategy config ns-rules)))
