(ns automaton-core.log.tracking.be-error-tracking
  "Backend monitoring live exceptions for remote enviroments (e.g. production, local acceptance, global acceptance)."
  (:require [automaton-core.log.tracking.be-sentry :as sentry]))

(defn init-error-tracking!
  [{:keys [dsn env]}]
  (sentry/init-sentry!
   {:dsn dsn
    :env env}))

(defn log-fn [ns level & message]
  (let [context (if (map? (first message))
                  (merge (first message)
                         {:ns ns})
                  {:ns ns})
        message (if (map? (first message))
                  (rest message)
                  message)]
    (if (or (= level :error)
            (= level :fatal))
      (sentry/send-event! {:message message
                           :level level
                           :context context})
      (sentry/send-breadcrumb! {:message message
                                :level level
                                :context context}))))
