(ns automaton-core.user
  (:require
   [clojure.tools.namespace.repl :as tn]
   [mount.core :as mount]))

#_(defn start []
  ;(with-logging-status)
    #_(mount/start #'app.conf/config
                   #'app.db/conn
                   #'app.www/nyse-app
                   #'app.example/nrepl))             ;; example on how to start app with certain states

(defn stop []
  (mount/stop))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn refresh []
  (stop)
  (tn/refresh))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn refresh-all []
  (stop)
  (tn/refresh-all))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (stop)
  #_(tn/refresh :after 'dev/go))
