(ns automaton-core.configuration.environment
  "Get environment data stored in the configuration"
  (:require
   #?@(:clj [[clojure.edn :as edn]]
       :cljs [[cljs.reader] [goog.object :as obj]])
   [automaton-core.configuration.protocol :as core-conf-prot]
   [automaton-core.utils.keyword          :as core-keyword]
   [clojure.string                        :as str]))

#?(:cljs (def ^:private nodejs? (exists? js/require)))

#?(:cljs (def ^:private process (when nodejs? (js/require "process"))))

(defn env-key-path
  "Turns key-path ([:a :b :c] -> 'a-b-c') into environment type key."
  [key-path]
  (let [path-str (str/join "-" (map name key-path))]
    (when-not (str/blank? path-str) (core-keyword/keywordize path-str))))

(defn parse-number
  [^String v]
  (try #?(:clj (Long/parseLong v)
          :cljs (parse-long v))
       #?(:clj (catch NumberFormatException _ (BigInteger. v)))
       (catch #?(:clj Exception
                 :cljs js/Error)
         _
         v)))

(defn parse-system-env
  "Turns string type into number. In case of failure in parsing it's returned in a format as it was (a string)."
  [v]
  (cond
    (re-matches #"[0-9]+" v) (parse-number v)
    (re-matches #"^(true|false)$" v) #?(:clj (Boolean/parseBoolean v)
                                        :cljs (parse-boolean v))
    (re-matches #"\w+" v) v
    :else (try (let [f #?(:clj edn/read-string
                          :cljs cljs.reader/read-string)
                     parsed (f v)]
                 (if (symbol? parsed) v parsed))
               (catch #?(:clj Exception
                         :cljs js/Error)
                 _
                 v))))

(defn read-all
  "Reads all system env properties and converts to appropriate type."
  []
  (->> #?(:clj (System/getenv)
          :cljs (if process
                  (let [env (.-env process)] (zipmap (obj/getKeys env) (obj/getValues env)))
                  {}))
       (map (fn [[k v]] [(core-keyword/keywordize k) v]))
       (into {})))

(defrecord EnvConf [conf]
  core-conf-prot/Conf
    (read-conf-param [_this key-path] (get conf (env-key-path key-path)))
    (config [_this] conf))

(defn make-env-conf [] (->EnvConf (read-all)))
