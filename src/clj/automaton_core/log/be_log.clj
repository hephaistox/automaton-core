(ns automaton-core.log.be-log
  "This namespace works as a backend proxy for chosing the logging implementation.
   Macros in this namespace to log are chosen from `automaton-core.log`.
   Current structure is generic for logging level, as they are the same right now in sense of this proxy.
   In future it may develop if needed to e.g. have the same number of macros as in `automaton-core.log`."
  (:require [automaton-core.log.be-registry :as log-be-registry]
            [automaton-core.log.tracking.be-error-tracking :as exs]))

(defn log-init!
  [{:keys [dsn env]}]
  (exs/init-error-tracking! {:dsn dsn, :env env}))

(defn- logger-ids-to-logger-fns
  "Based on logger-id chooses from registered strategies which logging function to use."
  [logger-ids]
  (reduce (fn [acc logger-id]
            (conj acc
                  (get-in log-be-registry/strategies-registry
                          [logger-id :impl])))
    []
    logger-ids))

(defmacro log
  [logger-ids level & message]
  (let [ns *ns*
        log-fns (logger-ids-to-logger-fns logger-ids)]
    `((juxt ~@log-fns) ~ns ~level ~@message)))

(defmacro log-exception
  [logger-ids level exception & additional-message]
  (when additional-message `(log ~logger-ids ~level ~@additional-message))
  (let [ns *ns*
        log-fns (logger-ids-to-logger-fns logger-ids)]
    `((juxt ~@log-fns) ~ns ~level ~exception)))

(defmacro log-data
  [logger-ids level data & additional-message]
  (when additional-message `(log ~logger-ids level ~@additional-message))
  (let [ns *ns*
        log-fns (logger-ids-to-logger-fns logger-ids)]
    `((juxt ~@log-fns) ~ns ~level ~data)))

(defmacro log-format
  [logger-ids level fmt & data]
  (let [ns *ns*
        log-fns (logger-ids-to-logger-fns logger-ids)]
    `((juxt ~@log-fns) ~ns ~level (format ~fmt ~@data))))
