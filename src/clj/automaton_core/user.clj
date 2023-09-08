(ns automaton-core.user
  (:require
   [mount.core :as mount]))

#_(defn start []
  ;(with-logging-status)
    #_(mount/start #'app.conf/config
                   #'app.db/conn
                   #'app.www/nyse-app
                   #'app.example/nrepl))             ;; example on how to start app with certain states

(defn stop []
  (mount/stop))
