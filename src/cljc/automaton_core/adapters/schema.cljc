(ns automaton-core.adapters.schema
  "Validate the data against the schema.

  Is a proxy for malli"
  (:require
   [malli.core  :as malli]
   [malli.error :as malli-error]))

(defn validate-data
  "Return true if the data is matching the schema
  Params:
  * `schema` schema to match
  * `data` data to check appliance to schema"
  [schema data]
  (malli/validate schema data))

(defn validate-data-humanize
  "Returns nil if valid, the error message otherwise.

  Params:
  * `schema` schema to match
  * `data` data to check appliance to schema"
  [schema data]
  (when-not (validate-data schema data)
    (-> (malli/explain schema data)
        malli-error/with-spell-checking
        malli-error/humanize)))

(defn validate
  "Test the schema parameter is valid

  Params:
  * `schema` schema to test"
  [schema]
  (try (malli/schema schema)
       true
       (catch #?(:clj Exception
                 :cljs :default)
         _
         false)))

(defn validate-humanize
  "Test the schema parameter is valid
  Returns a humanize message about the error
  Returns nil if valid

  Params:
  * `schema` schema to test"
  [schema]
  (try (malli/schema schema)
       nil
       (catch #?(:clj Exception
                 :cljs :default)
         _
         (str "Schema not valid" (str schema)))))
