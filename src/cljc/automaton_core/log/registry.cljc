(ns automaton-core.log.registry "List all known strategies")

(def strategies-registry
  "List of predefined strategies
   Look at be-registry and fe-registry for implementations."
  {::print {:description "Simple print"},
   ::text-based
     {:description
        "Basic one - sends everything to flatten text (js/console for clojurescript and log4j2 for clojure)"},
   ::no-op {:description "Deactivate that log"},
   ::error-tracking {:description
                       "Strategy for exception monitoring logging type"}})
