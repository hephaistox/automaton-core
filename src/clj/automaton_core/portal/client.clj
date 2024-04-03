#_{:heph-ignore {:forbidden-words ["tap>"]}}
(ns automaton-core.portal.client
  (:require
   [automaton-core.portal.common :as core-portal-common]
   [portal.client.jvm            :as p-client]))

(defn- client-submit [port] (partial p-client/submit {:port port}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start
  "Connects to existing portal (start fn).
   Params:
   * `port` (optional)  defaults to def `default-port`, it is a port on which portal app can be found."
  ([] (start (core-portal-common/default-port)))
  ([port]
   (add-tap (client-submit port))
   (tap> (format "Client connected for app `%s`"
                 (core-portal-common/app-name)))))
