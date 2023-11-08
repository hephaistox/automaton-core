(ns automaton-core.log.tracking.fe-error-tracking
  "Automaton web monitoring live exceptions for remote enviroments (e.g. production, local acceptance, global acceptance)."
  (:require [automaton-core.log.tracking.fe-sentry :as sentry]))

(defn log-fn
  [ns level & message]
  (let [context (if (map? (first message)) (merge (first message) {:ns ns}) {:ns ns})
        message (apply str (if (map? (first message)) (rest message) message))
        level (if (= :trace level) :debug level)]
    (if (or (= level :error) (= level :fatal))
      (sentry/send-event! {:message message
                           :level level
                           :context context})
      (sentry/send-breadcrumb! {:message message
                                :level level
                                :context context}))))
