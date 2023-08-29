(ns automaton-core.configuration.simple-files
  (:require
   [automaton-core.configuration.protocol :as protocol]
   [automaton-core.adapters.files :as files]
   [automaton-core.adapters.edn-utils :as edn-utils]))

(defrecord OutpaceConf
           [params]
  protocol/Conf
  (read-conf-param [_this key-path]
    (try
      (try
        (get-in (edn-utils/read-edn
                 (files/file-path "config.edn"))
                key-path)
        (catch Exception e
          (throw (ex-info "Parameter not defined: " {:error e
                                                     :key-path key-path}))))
      (catch Exception e
        (throw (ex-info "Unexpected error in parameter " {:error e
                                                          :key-path key-path}))))))
