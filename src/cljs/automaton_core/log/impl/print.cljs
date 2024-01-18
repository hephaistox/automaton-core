(ns automaton-core.log.impl.print
  "Simple print namespace, most likely used just temporarily.
trace -> debug -> info -> warn -> error -> fatal"
  (:require
   [clojure.string :as str]))

(defn get-logger
  "The logger version for js console is based in the order found in that [article](https://www.atatus.com/blog/javascript-logging-basic-tips/)

  Params
  * `level`"
  [level]
  (case level
    :trace js/console.log
    :debug js/console.debug
    :info js/console.info
    :warn js/console.warn
    (:error :fatal) js/console.error
    js/console.log))

(defn log-fn
  "
  Params:
  * ignored
  * `level`
  * `message`
  "
  [_ level & message]
  (let [logger (get-logger level)] (logger (str/join " " message))))
