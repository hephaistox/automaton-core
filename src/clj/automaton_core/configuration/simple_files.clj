(ns automaton-core.configuration.simple-files
  "Simple configuration based on files
  Data can be set in two files:
  * one set in `heph-common-conf` java property for all values not specific to an environment
  * one set in `heph-conf` java property for all values specific to that environment"
  (:require
   [automaton-core.adapters.java-properties :as java-properties]
   [automaton-core.configuration.edn-read :as conf-edn-read]
   [automaton-core.configuration.protocol :as protocol]))

(defrecord SimpleConf
           [config-map]
  protocol/Conf
  (read-conf-param [_this key-path]
    (get-in config-map
            key-path)))

(defn- load-conf
  [property-name]
  (-> (java-properties/get-java-property property-name)
      conf-edn-read/read-edn))

(defn make-simple-conf
  "Create the simple configuration"
  []
  (let [config-map (->> ["heph-conf" "heph-common-conf"]
                        (mapv load-conf)
                        (filterv some?)
                        (apply merge))]
    (->SimpleConf config-map)))
