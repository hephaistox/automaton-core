(ns automaton-core.storage.component
  "Entrypoint to storage"
  (:require [automaton-core.storage.persistent :as storage]
            [automaton-core.storage.impl.datomic.datomic :as datomic]
            [automaton-core.storage.impl.datomic.schema :as datomic-schema]
            [automaton-core.configuration :as conf]
            [automaton-core.log :as core-log]
            [mount.core :refer [defstate]]))

(defn start-storage
  []
  (try (core-log/info "Starting storage component")
       (let [dc (datomic/make-datomic-client datomic-schema/all-schema)
             db-uri (or (conf/read-param [:storage :datomic :url]) (conf/read-param [:storage-datomic-url]))
             _db-uri-valid? (when-not db-uri (throw (ex-info "Database uri was not found." {})))
             conn (storage/connection dc db-uri)
             access (datomic/make-datomic-access)]
         (core-log/trace "Storage component is started")
         {:connection conn
          :access access})
       (catch Throwable e (core-log/fatal (ex-info "Storage component failed." {:error e})))))

(defstate storage-state :start (start-storage) :stop (.release storage-state))

(defn upsert [storage update-fn] (core-log/trace "Executed: " update-fn) (storage/upsert (:access storage) (:connection storage) update-fn))

(defn select [storage select-fn] (core-log/trace "Selected: " select-fn) (storage/select (:access storage) (:connection storage) select-fn))

(defn delete
  [storage delete-fn]
  (core-log/trace "Removed with:" delete-fn)
  (storage/delete (:access storage) (:connection storage) delete-fn))
