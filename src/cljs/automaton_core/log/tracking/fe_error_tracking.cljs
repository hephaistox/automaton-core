(ns automaton-core.log.tracking.fe-error-tracking
  "Automaton web monitoring live exceptions for remote enviroments (e.g. production, local acceptance, global acceptance)."
  (:require
   [automaton-core.log.tracking.fe-sentry :as core-log-tracking-fe-sentry]))

(defn- sentry-data
  [ns level & message]
  (let [context (if (map? (first message)) (merge (first message) {:ns ns}) {:ns ns})
        message (apply str (if (map? (first message)) (rest message) message))
        level (if (= :trace level) :debug level)]
    {:message message
     :level level
     :context context}))

(defn add-context
  [ns level & message]
  (core-log-tracking-fe-sentry/send-breadcrumb! (apply sentry-data ns level message)))

(defn error-alert
  [ns level & message]
  (core-log-tracking-fe-sentry/send-event! (apply sentry-data ns level message)))
