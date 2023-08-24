(ns automaton-core.configuration.outpace
  "Implement the conf protocol for outpace"
  (:require
   [clojure.string :as str]

   [outpace.config :refer [defconfig]]

   [automaton-core.configuration.protocol :as protocol]))

(defconfig
  ^{:validate [#(< 1000 %) "Should be greater than 1000"
               #(>= 10000 %) "Should be less or equal than 9999"]}
  dev!clj-nrepl-port
  4000)

(defconfig
  app-name
  "App name")

(defconfig
  ^{:validate [#(< 1000 %) "Should be greater than 1000"
               #(>= 10000 %) "Should be less or equal than 9999"]}
  http-server!port
  3000)

(defconfig
  ^{:validate [string? "Should be a string"]
    :required true}
  http-server!url
  "http://localhost")

(defconfig
  ^{:required true}
  env
  :prod)

(defrecord OutpaceConf
           [params]
  protocol/Conf
  (read-conf-param [_this key-path]
    (try
      (let [n (str "automaton-core.configuration.outpace/"
                   (str/join "!" (map name key-path)))]
        (try
          (eval (symbol n))
          (catch Exception e
            (throw (ex-info "Parameter not defined: " {:symbol n
                                                       :error e
                                                       :key-path key-path})))))
      (catch Exception e
        (throw (ex-info "Unexpected error in parameter " {:error e
                                                          :key-path key-path}))))))
