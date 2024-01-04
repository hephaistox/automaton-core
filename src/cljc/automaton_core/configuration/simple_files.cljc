(ns automaton-core.configuration.simple-files
  "Namespace for simple configuration based on local file.
   Just like in core configuration, we are not using log nor outside dependencies to comply with the configuration requirements."
  (:require #?@(:clj [[clojure.edn :as edn] [clojure.java.io :as io] [automaton-core.adapters.java-properties :as java-properties]]
                :cljs [[cljs.reader :as edn] [goog.object :as obj]])
            [automaton-core.utils.map :as utils-map]
            [automaton-core.configuration.protocol :as core-conf-prot]
            [automaton-core.utils.keyword :as utils-keyword]))

#?(:cljs (def ^:private nodejs? (exists? js/require)))

#?(:cljs (def ^:private fs (when nodejs? (js/require "fs"))))

#?(:cljs (def ^:private process (when nodejs? (js/require "process"))))

(defn parse-number
  [^String v]
  (try #?(:clj (Long/parseLong v)
          :cljs (parse-long v))
       #?(:clj (catch NumberFormatException _ (BigInteger. v)))
       (catch #?(:clj Exception
                 :cljs js/Error)
         _
         v)))

(defn str->value
  "ENV vars and system properties are strings. str->value will convert:
   the numbers to longs, the alphanumeric values to strings, and will use Clojure reader for the rest
   in case reader can't read OR it reads a symbol, the value will be returned as is (a string)"
  [v]
  (cond (re-matches #"[0-9]+" v) (parse-number v)
        (re-matches #"^(true|false)$" v) #?(:clj (Boolean/parseBoolean v)
                                            :cljs (parse-boolean v))
        (re-matches #"\w+" v) v
        :else (try (let [parsed (edn/read-string v)] (if (symbol? parsed) v parsed))
                   (catch #?(:clj Exception
                             :cljs js/Error)
                     _
                     v))))

(defn read-system-env
  []
  (->> #?(:clj (System/getenv)
          :cljs (zipmap (obj/getKeys (.-env process)) (obj/getValues (.-env process))))
       (map (fn [[k v]] [(utils-keyword/keywordize k) (str->value v)]))
       (into {})))

(defn slurp-file
  [f]
  (try #?(:clj (when-let [file (or (io/resource f) (io/file f))] (slurp file))
          :cljs (when ^js (.existsSync fs f) (str ^js (.readFileSync fs f))))
       (catch #?(:clj Exception
                 :cljs js/Error)
         _
         (println (str "Reading file" f " failed")))))

(defn read-config-file
  "Reads config file, on purpose fn defined here to keep dependencies as small as possible."
  [f]
  (when-let [content (slurp-file f)] (into {} (utils-keyword/sanitize-map-keys (edn/read-string content)))))

(def config-file
  #?(:clj "heph-conf"
     :cljs "config.edn"))

#?(:clj (defn property->config-files
          [property-name]
          (some-> property-name
                  java-properties/get-java-property
                  java-properties/split-property-value)))

(defn- warn-on-overwrite
  [ms]
  (let [kseq (reduce (fn [acc m] (concat acc (keys m))) [] ms)]
    (for [[id freq] (frequencies kseq) :when (> freq 1)] (println "WARNING: configuration keys are duplicated for:" id))))

(defn merge-configs [& m] (warn-on-overwrite m) (apply utils-map/deep-merge m))

(defn read-config
  "Reads configuration, currently it's based on config.edn file. On js part, if nodejs is not available to get the configuration from file. If used with js, config-js-reference variable is expected to be set publicly."
  []
  #?(:clj (->> config-file
               property->config-files
               (mapv read-config-file)
               (filterv some?)
               (apply merge-configs (read-system-env)))
     :cljs (if nodejs?
             (->> (read-config-file config-file)
                  (merge-configs (read-system-env)))
             {})))

(def ^{:doc "A map of configuration variables."} conf (memoize read-config))

(defrecord SimpleConf []
  core-conf-prot/Conf
    (read-conf-param [_this key-path] (get-in (conf) key-path))
    (config [_this] (conf)))
