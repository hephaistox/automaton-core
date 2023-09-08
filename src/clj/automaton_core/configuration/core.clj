(ns automaton-core.configuration.core
  "Configuration parameters, stored in configuration file.
   This namespace is the entry point to call conf

  For a parameter `p`:
  * Create the parameter, in the current implementation, in the `util/conf.clj`
  * Read the parameter with  `conf/read-param`"
  (:require
   [automaton-core.configuration.simple-files :as simple-files]
   [automaton-core.configuration.protocol :as prot]
   [automaton-core.log :as log]

   [mount.core :refer [defstate in-cljc-mode]]))

;; Force the use of `cljc mode` in mount library, so call to `@` will work
(in-cljc-mode)

(defn start-conf []
  (log/info "Starting configuration component")
  (let [conf (simple-files/->SimpleConf {})]
    (log/trace "Configuration component is started")
    conf))

(defn stop-conf []
  (log/debug "Stop configuration component"))

(defstate conf-state
  :start (start-conf)
  :stop (stop-conf))

(defn read-param [key-path]
  (when-not (vector? key-path)
    (throw (ex-info "Key path should be a vector. I found " key-path)))
  (when (instance? mount.core.NotStartedState @conf-state)
    (throw (ex-info "Unexpected error in configuration, component configuration is not started" @conf-state)))
  (let [value (prot/read-conf-param @conf-state key-path)]
    (log/trace "Read key-path" key-path "= " value)
    value))
