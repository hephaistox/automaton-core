(ns automaton-core.utils.uuid-gen
  "Generate uuid, is a proxy to `http://danlentz.github.io/clj-uuid/`.
  Time based version is based on [collosal squuid](https://github.com/yetanalytics/colossal-squuid)."
  (:require
   [com.yetanalytics.squuid]
   #?(:clj [clj-uuid :as uuid])))

(defn time-based-uuid
  "Generate a time based `uuid`, so sorting uuid is sorting chronologically"
  []
  (com.yetanalytics.squuid/generate-squuid))

(defn unguessable
  "Generate a `uuid`, when the uuid should not be guessed"
  []
  #?(:clj (uuid/v4)
     :cljs (random-uuid)))
