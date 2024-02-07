(ns automaton-core.storage.persistent
  "Protocol namespace defining what is required from persistent storage implementation.")

(defprotocol PersistentStorageClient
  "Protocol for persisten storage connection mechanism"
  (connection [this config]
   "Creates connection to databse"))

(defprotocol PersistentStorageAccess
  "Protocol for persistent storage access"
  (upsert [this storage update-fn]
   "Mutate the storage with the new value in provided function")
  (select [this storage select-fn]
   "Find in the storage with the provided function")
  (delete [this storage delete-fn]
   "Delete in the storage with the provided function"))
