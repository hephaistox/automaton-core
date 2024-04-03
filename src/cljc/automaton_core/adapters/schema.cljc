(ns automaton-core.adapters.schema
  "Validate the data against the schema.

  Is a proxy for malli"
  (:require
   [malli.core :as malli]
   [malli.error :as malli-error]
   [automaton-core.log :as core-log]))

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
  (try
    (malli/schema schema)
    true
    (catch #?(:clj Exception
              :cljs :default) _
      false)))

(defn validate-humanize
  "Test the schema parameter is valid
  Returns a humanize message about the error
  Returns nil if valid

  Params:
  * `schema` schema to test"
  [schema]
  (try
    (malli/schema schema)
    nil
    (catch #?(:clj Exception
              :cljs :default) _
      (str "Schema not valid"
           (str schema)))))

(defn- schema-valid-with-log
  [schema data message]
  (if (validate-data schema data)
    true
    (do
      (core-log/error (str message ": " (validate-data-humanize schema data)))
      false)))

(defn assert-schema
  "Assert the `data` is complying the `schema`
  Params:
  * `schema`
  * `data`
  * `message`"
  [schema data message]
  (assert (schema-valid-with-log schema data message)))

(defn assert-schemas
  "Assert the `data` is complying the `schema`
  Params:
  * `schema-datas` sequence of schema data"
  [& schema-datas]
  (doseq [[schema data message] (partition 3 schema-datas)]
    (assert-schema schema data message)))
