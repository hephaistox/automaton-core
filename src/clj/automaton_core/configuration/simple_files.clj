(ns automaton-core.configuration.simple-files
  (:require
   [automaton-core.configuration.protocol :as protocol]
   [automaton-core.configuration.edn-read :as conf-edn-read]
   [clojure.java.io :as io]))

(def config-edn
  (conf-edn-read/read-edn
   (or (io/resource "config.edn")
       (io/file "config.edn"))))

(defrecord SimpleConf
           [params]
  protocol/Conf
  (read-conf-param [_this key-path]
    (try
      (try
        (get-in config-edn
                key-path)
        (catch Exception e
          (throw (ex-info "Parameter not defined: " {:error e
                                                     :key-path key-path}))))
      (catch Exception e
        (throw (ex-info "Unexpected error in parameter " {:error e
                                                          :key-path key-path}))))))
