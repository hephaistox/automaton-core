(ns automaton-core.configuration.simple-files
  "Namespace for simple configuration based on local file.
   Just like in core configuration, we are not using log nor outside dependencies to comply with the configuration requirements."
  (:require #?(:clj [clojure.edn :as edn]
               :cljs [cljs.reader :as edn])
            #?@(:clj [[clojure.java.io :as io] [automaton-core.adapters.java-properties :as java-properties]
                      [automaton-core.utils.map :as utils-map]])
            [automaton-core.configuration.protocol :as core-conf-prot]
            [automaton-core.utils.keyword :as utils-keyword]))

#?(:cljs (def ^:private nodejs? (exists? js/require)))

#?(:cljs (def ^:private fs (when nodejs? (js/require "fs"))))

(defn slurp-file
  [f]
  #?(:clj (when-let [f (io/file f)] (when (.exists f) (slurp f)))
     :cljs (when ^js (.existsSync fs f) (str ^js (.readFileSync fs f)))))

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

(defn read-config
  "Reads configuration, currently it's based on config.edn file. On js part, if nodejs is not available to get the configuration from file. If used with js, config-js-reference variable is expected to be set publicly."
  []
  #?(:clj (->> config-file
               property->config-files
               (mapv read-config-file)
               (filterv some?)
               (apply utils-map/deep-merge))
     :cljs (when nodejs? (read-config-file config-file))))

(def ^{:doc "A map of configuration variables."} conf (memoize read-config))

(defrecord SimpleConf []
  core-conf-prot/Conf
    (read-conf-param [_this key-path] (get-in (conf) key-path))
    (config [_this] (conf)))
