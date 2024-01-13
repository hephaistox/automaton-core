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
  parameter. So each alias can tell which version it uses, especially monorepo could be different."
  (:require [automaton-core.configuration.protocol :as core-conf-prot]
            [automaton-core.configuration.files :as core-conf-files]
            [automaton-core.configuration.environment :as core-conf-env]
            [mount.core :refer [defstate in-cljc-mode]]))

;; Force the use of `cljc mode` in mount library, so call to `@` will work
(in-cljc-mode)

(defn start-conf
  []
  (try (println "Starting configuration component")
       (let [conf (core-conf-files/->FilesConf)
             env-conf (core-conf-env/->EnvConf)]
         (println "Configuration component is started")
         [conf env-conf])
       (catch #?(:clj Throwable
                 :cljs :default)
         e
         (println "Configuration failed" e))))

(defn stop-conf [] (println "Stop configuration component"))

(defstate conf-state :start (start-conf) :stop (stop-conf))

(defn read-param
  "Returns value under `key-path` vector."
  ([key-path default-value]
   (let [value (or (core-conf-prot/read-conf-param (first @conf-state) key-path)
                   (core-conf-prot/read-conf-param (second @conf-state) key-path))]
     (when (nil? value) (println "Value for " key-path " is not set, use default value") default-value)))
  ([key-path] (read-param key-path nil)))


