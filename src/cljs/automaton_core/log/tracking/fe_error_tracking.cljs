(ns automaton-core.log.tracking.fe-error-tracking
  "Automaton web monitoring live exceptions for remote enviroments (e.g. production, local acceptance, global acceptance)."
  (:require [automaton-core.log.tracking.fe-sentry :as sentry]))

(defn init-error-tracking!
  [{:keys [dsn env traced-website]}]
  (sentry/init-sentry! {:dsn dsn, :traced-website traced-website, :env env}))

(defn log-fn
  [ns level & message]
  (let [context
          (if (map? (first message)) (merge (first message) {:ns ns}) {:ns ns})
        message (if (map? (first message)) (rest message) message)]
    (if (or (= level :error) (= level :fatal))
      (sentry/send-event! {:message message, :level level, :context context})
      (sentry/send-breadcrumb!
        {:message message, :level level, :context context}))))
