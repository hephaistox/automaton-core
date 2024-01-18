#_{:heph-ignore {:forbidden-words ["tap>"]}}
(ns automaton-core.repl.portal
  (:require
   [automaton-core.configuration :as conf]
   [portal.api :as p]
   [portal.client.jvm :as p-client]))

(def default-port (conf/read-param [:dev :portal-port]))

(def submit #'p/submit)

(defn client-submit [port] (partial p-client/submit {:port port}))

(defn client-connect
  "Connects to existing portal (start fn).
   Params:
   * `port` (optional)  defaults to def `default-port`, it is a port on which portal app can be found."
  ([] (client-connect default-port))
  ([port]
   (conf/read-param [:app-name])
   (add-tap (client-submit port))
   (tap> "Client connected")))

(defn portal-connect "Regular portal add-tap fn proxy." [] (add-tap #'submit))

(defn start
  "Starts portal app
   Params:
   * port (optional) defaults to `default-port`, defines what port portal should be started."
  ([] (start default-port))
  ([port] (p/open {:port port}) (portal-connect) (tap> "Portal has started")))

(defn stop "Close portal app" [] (p/close))
