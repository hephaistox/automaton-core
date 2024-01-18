(ns automaton-core.utils.uuid-gen
  "Generate uuid, is a proxy to `http://danlentz.github.io/clj-uuid/`"
  (:require
   #?(:clj [clj-uuid :as uuid])))

(defn time-based-uuid
  "Generate a time based uuid, so sorting uuid is sorting chronologically"
  []
  #?(:clj (uuid/v1)
     :cljs (throw (ex-info "Not implemented yet" {}))))

(defn unguessable
  "When the uuid should not be guessed"
  []
  #?(:clj (uuid/v4)
     :cljs (random-uuid)))
