(ns automaton-core.log.tracking.sentry "Sentry shareable code")

(defn event-environment
  [event]
  #?(:clj (. event getEnvironment)
     :cljs event.environment))

(defn silence-development-events [event] (let [environment (event-environment event)] (when-not (= environment "development") event)))
