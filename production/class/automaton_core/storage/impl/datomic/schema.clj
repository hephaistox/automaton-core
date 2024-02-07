(ns automaton-core.storage.impl.datomic.schema
  "Contains datomic schema"
  (:require
   [automaton-core.user.account.schema :refer [account-schema]]))

(def all-schema "All schemas, used to quickly recreate the db" account-schema)
