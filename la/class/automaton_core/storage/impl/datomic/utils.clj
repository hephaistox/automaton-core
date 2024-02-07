(ns automaton-core.storage.impl.datomic.utils
  "Datomic utility functions"
  (:require
   [datomic.api :as d]))

(defn entity [db q-result] (d/entity db q-result))

(defn temp-id [key] (d/tempid key))
