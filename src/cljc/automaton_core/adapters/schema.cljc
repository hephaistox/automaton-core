(ns automaton-core.adapters.schema
  "Validate the data against the schema.

  Is a proxy for malli"
  (:require
   [malli.core      :as malli]
   [malli.error     :as malli-error]
   [malli.transform :as malli-transform]
   [malli.util      :as malli-util]))

(def registry (merge (malli/default-schemas) (malli-util/schemas)))

(defn close-map-schema
  "Turn a map schema into a closed one."
  [map-schema]
  (update map-schema
          1
          (fn [schema-params]
            (if (map? schema-params)
              (assoc schema-params :closed true)
              schema-params))))

(defn validate-data
  "Return true if the data is matching the schema
  Params:
  * `schema` schema to match
  * `data` data to check appliance to schema"
  [schema data]
  (-> schema
      (malli/schema {:registry registry})
      (malli/validate data)))

(defn validate-data-humanize
  "Returns nil if valid, the error message otherwise.

  Params:
  * `schema` schema to match
  * `data` data to check appliance to schema"
  [schema data]
  (when-not (-> schema
                (malli/schema {:registry registry})
                (validate-data data))
    {:error (-> (malli/explain schema data)
                malli-error/with-spell-checking
                malli-error/humanize)
     :schema schema
     :data data}))

(defn validate
  "Test the schema parameter is valid

  Params:
  * `schema` schema to test"
  [schema]
  (try (malli/schema schema {:registry registry})
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
  (try (malli/schema schema {:registry registry})
       nil
       (catch #?(:clj Exception
                 :cljs :default)
         _
         (str "Schema not valid: " (str schema)))))

(defn add-default
  "Adds to `data` default values defined in the `schema`."
  [schema data]
  (malli/decode schema
                data
                (malli-transform/default-value-transformer
                 {::malli-transform/add-optional-keys true})))
