(ns automaton-core.log.registry "List all known strategies")

(def strategies-registry
  "List of predefined strategies
   Look at be-registry and fe-registry for implementations."
  {::print {:description "Simple print"}
   ::text-based
   {:description
    "Basic one - sends everything to flatten text (js/console for clojurescript and log4j2 for clojure)"}
   ::no-op {:description "Deactivate that log"}
   ::error-tracking-context
   {:description
    "Strategy for exception monitoring logging type, that gathers context of upcoming error alerts"}
   ::error-tracking-alert {:description
                           "Strategy for alerting about existing problem."}})
