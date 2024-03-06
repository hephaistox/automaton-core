(ns automaton-core.portal.client
  "Client connection to the portal"
  (:require [portal.client.web :as p]))

(def submit (partial p/submit {:port 8351}))

(defn tst
  []
  (js/alert "from REPL"))

(defn client-connect
  []
  (add-tap #'submit))

(comment
 (client-connect)
;
  )
