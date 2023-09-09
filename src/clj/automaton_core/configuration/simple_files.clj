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
        (let [val (get-in config-edn
                          key-path
                          :value-not-set)]
          (when (= val :value-not-set)
            (throw (ex-info "Parameter is not found"
                                {:key-path key-path
                                 :config-edn config-edn})))
          val)
        (catch Exception e
          (throw (ex-info "Parameter not defined: " {:error e
                                                     :key-path key-path}))))
      (catch Exception e
        (throw (ex-info "Unexpected error in parameter " {:error e
                                                          :key-path key-path}))))))
