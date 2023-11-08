(ns automaton-core.configuration.simple-files
  "Simple configuration based on files
  Data can be set in two files:
  * one set in `heph-common-conf` java property for all values not specific to an environment
  * one set in `heph-conf` java property for all values specific to that environment"
  (:require [automaton-core.log :as log]
            [babashka.fs :as fs]
            [clojure.edn :as edn]
            [automaton-core.adapters.java-properties :as java-properties]
            [automaton-core.configuration.protocol :as configuration-prot]))

(defn read-edn
  "Read the `.edn` file,
  Params:
  * `edn-filename` name of the edn file to load
  Errors:
  * throws an exception if the file is not found
  * throws an exception if the file is a valid edn
  * `file` could be a string representing the name of the file to load
  or a (io/resource) object representing the name of the file to load"
  [edn-filename]
  (let [edn-filename (when edn-filename (str (fs/absolutize edn-filename)))
        _ (log/trace "Load file:" edn-filename)
        edn-content (try (slurp edn-filename) (catch Exception _ (log/warn-format "Unable to load the file `%s`" edn-filename) nil))]
    (try (edn/read-string edn-content) (catch Exception _ (log/warn-format "File `%s` is not a valid edn" edn-filename) nil))))

(defrecord SimpleConf [config-map]
  configuration-prot/Conf
    (read-conf-param [_this key-path] (get-in config-map key-path)))

(defn- load-conf
  [property-name]
  (-> (java-properties/get-java-property property-name)
      read-edn))

(defn make-simple-conf
  "Create the simple configuration"
  []
  (let [config-map (->> ["heph-conf" "heph-common-conf"]
                        (mapv load-conf)
                        (filterv some?)
                        (apply merge))]
    (->SimpleConf config-map)))
