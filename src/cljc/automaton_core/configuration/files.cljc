(ns automaton-core.configuration.files
  "Namespace for simple configuration based on local file.
   Just like in core configuration, we are not using log nor outside dependencies to comply with the configuration requirements."
  (:require
   #?@(:clj [[clojure.edn :as edn]
             [clojure.java.io :as io]
             [automaton-core.adapters.java-properties :as java-properties]]
       :cljs [[cljs.reader :as edn]])
   [automaton-core.configuration.protocol :as core-conf-prot]
   [automaton-core.utils.keyword          :as utils-keyword]
   [automaton-core.utils.map              :as utils-map]))

#?(:cljs (def ^:private nodejs? (exists? js/require)))
#?(:cljs (def ^:private fs (when nodejs? (js/require "fs"))))

(defn slurp-file
  [f]
  (try #?(:clj (when-let [file (or (io/resource f) (io/file f))] (slurp file))
          :cljs (when ^js (.existsSync fs f) (str ^js (.readFileSync fs f))))
       (catch #?(:clj Exception
                 :cljs js/Error)
         _
         (println (str "Reading file " f " failed")))))

(defn read-config-file
  "Reads config file, on purpose fn defined here to keep dependencies as small as possible."
  [f]
  (when-let [content (slurp-file f)]
    (into {} (utils-keyword/sanitize-map-keys (edn/read-string content)))))

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
    (for [[id freq] (frequencies kseq)
          :when (> freq 1)]
      (println "WARNING: configuration keys are duplicated for:" id))))

(defn merge-configs [& m] (warn-on-overwrite m) (apply utils-map/deep-merge m))

(defn read-config
  "Reads configuration, currently it's based on config.edn file. On js part, if nodejs is not available to get the configuration from file. If used with js, config-js-reference variable is expected to be set publicly."
  []
  #?(:clj (->> config-file
               property->config-files
               (mapv read-config-file)
               (filterv some?)
               (apply merge-configs))
     :cljs (if nodejs? (read-config-file config-file) {})))

(defrecord FilesConf [conf]
  core-conf-prot/Conf
    (read-conf-param [_this key-path] (get-in conf key-path))
    (config [_this] conf))

(defn make-files-conf [] (->FilesConf (read-config)))
