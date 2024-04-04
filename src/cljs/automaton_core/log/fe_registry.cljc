(ns automaton-core.log.fe-registry
  "List of all known frontend strategies"
  (:require
   [automaton-core.log.registry :as log-registry]
   #?@(:cljs [[automaton-core.log.impl.print]
              [automaton-core.log.tracking.fe-error-tracking]])))

(defn no-op-fn [& _] nil)

(defn simple-print [_ns _level & msg] (apply print msg))

(def strategies-registry
  "List of predefined strategies
  Look at clj and cljs implementations to have an understanding of that strategies implementation which may be different on both technologies.
  For instance, `:text-based` will be based on log4j2 on backedn and console on frontend"
  {::log-registry/print {:description "Simple print"
                         :impl simple-print}
   ::log-registry/text-based
   {:description
    "Basic one - sends everything to flatten text (js/console for clojurescript and log4j2 for clojure)"
    #?@(:cljs [:impl automaton-core.log.impl.print/log-fn])}
   ::log-registry/no-op {:description "Deactivate that log"
                         :impl no-op-fn}
   ::log-registry/error-tracking-context
   {:description
    "For registering information for monitoring exceptions in the application"
    #?@(:cljs [:impl automaton-core.log.tracking.fe-error-tracking/add-context])}
   ::log-registry/error-tracking-alert
   {:description "For alerting about monitoring exceptions in the application"
    #?@(:cljs [:impl automaton-core.log.tracking.fe-error-tracking/error-alert])}})
