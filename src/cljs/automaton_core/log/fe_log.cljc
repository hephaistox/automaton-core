(ns automaton-core.log.fe-log
  "Factory generating log function"
  (:require [automaton-core.log.strategy]
            [automaton-core.log.fe-registry]
            [automaton-core.log.strategy.static-ns-level]
            #?(:cljs [automaton-core.log.tracking.fe-error-tracking :as exs]))
  #?(:cljs (:require-macros [automaton-core.log.fe-log])))

#?(:cljs (defn log-init! [params] (exs/init-error-tracking! params)))

(defn logger-ids-to-logger-fns
  [logger-ids]
  (reduce (fn [acc logger-id]
            (conj acc
                  (get-in
                    #?(:cljs automaton-core.log.fe-registry/strategies-registry
                       :clj {})
                    [logger-id :impl])))
    []
    logger-ids))

(defmacro log
  [logger-id level & message]
  (let [ns (str *ns*)
        log-fns `(automaton-core.log.fe-log/logger-ids-to-logger-fns
                   ~logger-id)]
    `((apply juxt ~log-fns) ~ns (str ~level) ~@message)))

(defmacro log-exception
  [logger-id level exception & additional-message]
  (when additional-message (log logger-id level additional-message))
  (let [ns (str *ns*)
        log-fns `(automaton-core.log.fe-log/logger-ids-to-logger-fns
                   ~logger-id)]
    `((apply juxt ~log-fns) ~ns (str ~level) ~exception)))

(defmacro log-data
  [logger-id level data & additional-message]
  (when additional-message (log logger-id additional-message))
  (let [ns (str *ns*)
        log-fns `(automaton-core.log.fe-log/logger-ids-to-logger-fns
                   ~logger-id)]
    `((apply juxt ~log-fns) ~ns (str ~level) ~data)))

(defmacro log-format
  [logger-id level fmt & data]
  (let [ns (str *ns*)
        log-fns `(automaton-core.log.fe-log/logger-ids-to-logger-fns
                   ~logger-id)]
    `((apply juxt ~log-fns) ~ns (str ~level) (format ~fmt ~@data))))
