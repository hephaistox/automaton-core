(ns automaton-core.configuration
  "Configuration parameters, stored in configuration file.
   This namespace is the entry point to call conf

  Design decision:
  * Configuration used to be based on outpace, it was too complicated for a small benefit,
  * The `config.edn` file was first unique, it needs to be updatable by environment to allow different
  value between production and repl, test and dev, but also monorepo vs app
  * The different version of parameter `config.edn` was first based on classpath (differentitated
  with aliases). It is ok for one app, but it renders the monrepo build complicated as it was naturally
  gathering all classpath, so all `config.edn` versions. The solution was to be based on environment
  parameter. So each alias can tell which version it uses, especially monorepo could be different.

  For a parameter `p`:
  * Create the parameter, in the current implementation, in the `util/conf.clj`
  * Read the parameter with  `conf/read-param`"
  (:require [automaton-core.configuration.protocol :as configuration-prot]
            [automaton-core.configuration.simple-files :as simple-files]
            [automaton-core.log :as log]
            [mount.core :refer [defstate in-cljc-mode]]))

;; Force the use of `cljc mode` in mount library, so call to `@` will work
(in-cljc-mode)

(defn start-conf
  []
  (try (log/info "Starting configuration component")
       (let [conf (simple-files/make-simple-conf)]
         (log/trace "Configuration component is started")
         conf)
       (catch Throwable e (log/fatal (ex-info "Configuration component failed" {:error e})) (throw e))))

(defn stop-conf [] (log/debug "Stop configuration component"))

(defstate conf-state :start (start-conf) :stop (stop-conf))

(defn read-param
  ([key-path default-value]
   (cond (not (vector? key-path)) (do (log/warn-format "Key path should be a vector. I found " key-path) default-value)
         (instance? mount.core.NotStartedState @conf-state)
         (do (log/warn-format "Unexpected error in configuration, component configuration is not started" @conf-state) default-value)
         :else (let [value (configuration-prot/read-conf-param @conf-state key-path)]
                 (log/trace "Read key-path " key-path " = " value)
                 (or value default-value))))
  ([key-path] (read-param key-path nil)))
