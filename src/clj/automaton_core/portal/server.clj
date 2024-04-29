#_{:heph-ignore {:forbidden-words ["tap>"]}}
(ns automaton-core.portal.server
  "Portal server starting"
  (:require
   [automaton-core.portal.common :as core-portal-common]
   [portal.api                   :as p]))

(def ^:private submit "Sumbitting data to the portal" #'p/submit)
(defn- portal-connect "Regular portal add-tap fn proxy." [] (add-tap #'submit))

(defn start
  "Starts portal app
   Params:
   * port (optional) defaults to `default-port`, defines what port portal should be started."
  ([] (start (core-portal-common/default-port)))
  ([port]
   (let [res (p/open {:port port})]
     (portal-connect)
     (tap> (format "Portal server has started for app `%s` on port %d"
                   (core-portal-common/app-name)
                   port))
     res)))

(defn stop
  "Close portal app"
  ([] (p/clear) (p/close))
  ([portal-server] (p/clear) (p/close portal-server)))
