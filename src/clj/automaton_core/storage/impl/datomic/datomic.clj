(ns automaton-core.storage.impl.datomic.datomic
  "Implementation of storage protocols in datomic"
  (:require
   [automaton-core.log                :as core-log]
   [automaton-core.storage.persistent :as storage]
   [datomic.api                       :as d]))

(defrecord DatomicClient [schema]
  storage/PersistentStorageClient
    (connection [_ config]
      (let [db-uri config]
        (d/create-database db-uri)
        (let [connection (d/connect db-uri)]
          @(d/transact connection schema)
          (core-log/trace "Storage component is started")
          connection))))

(defrecord DatomicAccess []
  storage/PersistentStorageAccess
    (upsert [_ storage add-schema] (d/transact storage add-schema))
    (select [_ storage {:keys [schema values]}] (apply d/q schema (d/db storage) values))
    (delete [_ storage delete-schema] (d/transact storage delete-schema)))

(defn make-datomic-client
  "Builds DatomicClient
  Params:
  * `schema` schema that the datomic db should be started with"
  [schema]
  (->DatomicClient schema))

(defn make-datomic-access "Builds DatomicAccess" [] (->DatomicAccess))
