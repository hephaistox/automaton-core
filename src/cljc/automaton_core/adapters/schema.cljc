(ns automaton-core.adapters.schema
  "Validate the data against the schema.
  Is a proxy for malli"
  (:require
   [malli.core  :as malli]
   [malli.error :as malli-error]))

(defn schema-valid
  "Return true if the data is matching the schema
  Params:
  * `schema` schema to match
  * `data` data to check appliance to schema"
  [schema data]
  (malli/validate schema data))

(defn schema-valid-humanize
  "Returns nil if valid, the error message otherwise.

  Params:
  * `schema` schema to match
  * `data` data to check appliance to schema"
  [schema data]
  (when-not (schema-valid schema data)
    (-> (malli/explain schema data)
        malli-error/with-spell-checking
        malli-error/humanize)))
