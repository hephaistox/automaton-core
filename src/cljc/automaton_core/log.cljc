(ns automaton-core.log
  "Entry point for log.
   This namespace is unique in the way it's implemented, as usually we try to avoid reader conditionals for   whole functions, but it's extremely usefull for consistency and usage of logs in the entire application.
  As previous attempt of keeping them apart was problematic.


   On clj part:
   Is now a simple redirection to clojure tools logging
   Set to log4j2 [check setup in](clojure/deps.edn)
   Check https://logging.apache.org/log4j/2.x/manual/configuration.html for configuration details."
  (:require
   #?@(:clj [[clojure.tools.logging :as l]
             [clojure.pprint :as pp]
             [automaton-core.adapters.string :as bas]
             [clojure.string :as str]])))

#?(:clj (defn prettify-elt
          "Prepare the element `elt` to display in the log
           Params:
            * `elt` data to show, which type will be checked"
          [elt]
          (if (or (map? elt)
                  (set? elt)
                  (vector? elt))
            (-> elt
                pp/pprint
                with-out-str
                bas/remove-last-character)
            elt)))

#?(:clj
   (defmacro prettify
     [& args]
     `(str/join ""
                (map prettify-elt
                     [~@args]))))

#?(:clj (defmacro trace [& message]
          `(l/trace (prettify ~@message)))
   :cljs (defn trace
           [& message]
           (apply js/console.log "T:" message)))

#?(:clj (defmacro debug [& message]
          `(l/debug (prettify ~@message)))
   :cljs (defn debug [& message]
           (apply js/console.log "D:" message)))

#?(:clj (defmacro info [& message]
          `(l/info (prettify ~@message)))
   :cljs (defn info
           [& message]
           (apply js/console.log "I:" message)))

#?(:clj (defmacro warn [& message]
          `(l/warn (prettify ~@message)))
   :cljs (defn warn
           [& message]
           (apply js/console.log "W:" message)))

#?(:clj (defmacro error [& message]
          `(l/error (prettify ~@message)))
   :cljs (defn error
           [& message]
           (apply js/console.log "E:" message)))

#?(:clj (defmacro fatal [& message]
          `(l/error (prettify ~@message)))
   :cljs (defn fatal [& message]
           (apply js/console.log "F:" message)))
