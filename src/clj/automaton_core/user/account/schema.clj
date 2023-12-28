(ns automaton-core.user.account.schema
  "Contains all schema related to user accounts"
  (:require [datomic.db]))

(def account-schema
  "Schema for creating account"
  [{:db/id #db/id [:db.part/db]
    :db/ident :account/first
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id #db/id [:db.part/db]
    :db/ident :account/last
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.install/_attribute :db.part/db}
   {:db/id #db/id [:db.part/db]
    :db/ident :account/email
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/unique :db.unique/identity
    :db.install/_attribute :db.part/db}])
