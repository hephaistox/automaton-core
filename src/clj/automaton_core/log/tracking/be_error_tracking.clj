(ns automaton-core.log.tracking.be-error-tracking
  "Backend monitoring live exceptions for remote enviroments (e.g. production, local acceptance, global acceptance)."
  (:require
   [automaton-core.log.tracking.be-sentry :as core-log-tracking-be-sentry]))

(defn init-error-tracking!
  [{:keys [dsn env]}]
  (when-not dsn (prn "dsn is missing in init-error-tracking!"))
  (when-not env (prn "env is missing in init-error-tracking!"))
  (core-log-tracking-be-sentry/init-sentry! {:dsn dsn
                                             :env env}))

(defn- sentry-data
  [ns level & message]
  (let [context (if (map? (first message))
                  (merge (first message) (when ns {:ns ns}))
                  (when ns {:ns ns}))
        message (if (map? (first message)) (rest message) message)]
    {:message message
     :level level
     :context context}))

(defn add-context
  [ns level & message]
  (core-log-tracking-be-sentry/send-breadcrumb!
   (apply sentry-data ns level message)))

(defn error-alert
  [ns level & message]
  (core-log-tracking-be-sentry/send-event!
   (apply sentry-data ns level message)))
