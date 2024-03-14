(ns automaton-core.portal.common
  "Common setup for portal"
  (:require
   [automaton-core.configuration :as core-conf]))

(defn default-port
  "Port where the server is started"
  []
  (core-conf/read-param [:dev :portal-port] 8351))

(defn app-name
  "Application name as displayed in the portal"
  []
  (core-conf/read-param [:app-name] "Non defined"))
