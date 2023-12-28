(ns automaton-core.configuration.protocol)

(defprotocol Conf
  (read-conf-param [this key-path]
   "Read the value of key")
  (config [this]
   "Returns whole configuration"))
