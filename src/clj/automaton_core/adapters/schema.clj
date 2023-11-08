(ns automaton-core.adapters.schema
  "Validate the data against the schema.
  Is a proxy for malli"
  (:require [malli.core :as malli]
            [malli.error :as malli-error]))

(defn schema-valid
  "Return true if the data is not matching the schema
  Params:
  * `schema` schema to match
  * `data` data to check appliance to schema"
  [schema data]
  (malli/validate schema data))

(defn schema-valid-or-throw
  "True or throw an exception if the data is not matching the schema.
  Params:
  * `schema` schema to match
  * `data` data to check appliance to schema
  * `msg` to send in the exception if thrown"
  [schema data msg]
  (when-not (schema-valid schema data)
    (throw (ex-info msg
                    {:data data
                     :reason (-> schema
                                 (malli/explain data)
                                 (malli-error/humanize))})))
  data)
