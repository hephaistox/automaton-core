(ns automaton-core.log
  "Main entrypoint to automaton core logging, defines basic levels that we use to log.

  Logical order of logs is:
  trace -> debug -> info -> warn -> error -> fatal

  Design decision:
  * the log is relying on configuration namespace, so nothing will be logged before that
  * the log api is hosted in a `cljc` file so log is accessible to namespaces in cljc
     * Rationle:
        * deciding to push code in cljc should not prevent to log
        * postponing the decision to host code in frontend or backend is an objective
     * Consequence:
        * This namespace is the entrypoint for both clj and cljs implementations
"
  (:require
   [automaton-core.configuration :as conf]
   [automaton-core.log.strategy.static-ns-level
    :as
    log-static-ns-level-strategy]
   [automaton-core.log.strategy :as log-strategy]
   #?(:clj [automaton-core.log.be-log]
      :cljs [automaton-core.log.fe-log]))
  #?(:cljs (:require-macros [automaton-core.log])))

(def stgy
  "Decides which strategy implementation to use for choosing the loggers."
  (log-static-ns-level-strategy/make-static-ns-level-strategy
   {:env (conf/read-param [:env])}))

(defn cljs-env?
  "Take the &env from a macro, and tell whether we are expanding into cljs."
  [env]
  (boolean (:ns env)))

(defmacro trace
  "Records all of the application's behaviour details. Its purpose is primarily diagnostic, and it is more granular and finer than the DEBUG log level. When you need to know what happened in your application or the third-party libraries you're using, utilise this log level. The TRACE log level can be used to query code parameters or analyse algorithm steps."
  [& message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :trace)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log ~logger-id# :trace ~@message)
      `(automaton-core.log.be-log/log ~logger-id# :trace ~@message))))

(defmacro trace-exception
  "Like trace, but focused on exceptions."
  [exception & additional-message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :trace)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-exception ~logger-id#
                                                :trace
                                                ~exception
                                                ~@additional-message)
      `(automaton-core.log.be-log/log-exception ~logger-id#
                                                :trace
                                                ~exception
                                                ~@additional-message))))

(defmacro trace-data
  "Like trace, but first argument is expected to be a map with usefull more detailed data."
  [data & additional-message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :trace)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-data ~logger-id#
                                           :trace
                                           ~data
                                           ~@additional-message)
      `(automaton-core.log.be-log/log-data ~logger-id#
                                           :trace
                                           ~data
                                           ~@additional-message))))

(defmacro trace-format
  "Like trace, but uses clojure format function, so first argument is string to translate and rest is arguments to supply it with."
  [fmt & args]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :trace)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-format ~logger-id# :trace ~fmt ~@args)
      `(automaton-core.log.be-log/log-format ~logger-id# :trace ~fmt ~@args))))

(defmacro debug
  "You are providing diagnostic information in a thorough manner with DEBUG. It's long and contains more information than you'll need when gg using the application. The DEBUG logging level is used to retrieve data that is required to debug, troubleshoot, or test an application. This guarantees that the application runs smoothly."
  [& message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :debug)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log ~logger-id# :debug ~@message)
      `(automaton-core.log.be-log/log ~logger-id# :debug ~@message))))

(defmacro debug-exception
  "Like debug, but focused on exceptions."
  [exception & additional-message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :debug)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-exception ~logger-id#
                                                :debug
                                                ~exception
                                                ~@additional-message)
      `(automaton-core.log.be-log/log-exception ~logger-id#
                                                :debug
                                                ~exception
                                                ~@additional-message))))

(defmacro debug-data
  "Like debug, but first argument is expected to be a map with usefull more detailed data."
  [data & additional-message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :debug)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-data ~logger-id#
                                           :debug
                                           ~data
                                           ~@additional-message)
      `(automaton-core.log.be-log/log-data ~logger-id#
                                           :debug
                                           ~data
                                           ~@additional-message))))

(defmacro debug-format
  "Like debug, but uses clojure format function, so first argument is string to translate and rest is arguments to supply it with."
  [fmt & args]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :debug)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-format ~logger-id# :debug ~fmt ~@args)
      `(automaton-core.log.be-log/log-format ~logger-id# :debug ~fmt ~@args))))

(defmacro info
  "INFO messages are similar to how applications normally behave. They describe what occurred. For example, if a specific service was stopped or started, or if something was added to the database. During normal operations, these entries are unimportant. The information written in the INFO log is usually useful, and you don't have to do anything with it."
  [& message]
  (let [logger-id# (automaton-core.log.strategy/apply-strategy stgy *ns* :info)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log ~logger-id# :info ~@message)
      `(automaton-core.log.be-log/log ~logger-id# :info ~@message))))

(defmacro info-exception
  "Like info, but focused on exceptions."
  [exception & additional-message]
  (let [logger-id# (automaton-core.log.strategy/apply-strategy stgy *ns* :info)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-exception ~logger-id#
                                                :info
                                                ~exception
                                                ~@additional-message)
      `(automaton-core.log.be-log/log-exception ~logger-id#
                                                :info
                                                ~exception
                                                ~@additional-message))))

(defmacro info-data
  "Like info, but first argument is expected to be a map with usefull more detailed data."
  [data & additional-message]
  (let [logger-id# (automaton-core.log.strategy/apply-strategy stgy *ns* :info)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-data ~logger-id#
                                           :info
                                           ~data
                                           ~@additional-message)
      `(automaton-core.log.be-log/log-data ~logger-id#
                                           :info
                                           ~data
                                           ~@additional-message))))

(defmacro info-format
  "Like info, but uses clojure format function, so first argument is string to translate and rest is arguments to supply it with."
  [fmt & args]
  (let [logger-id# (automaton-core.log.strategy/apply-strategy stgy *ns* :info)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-format ~logger-id# :info ~fmt ~@args)
      `(automaton-core.log.be-log/log-format ~logger-id# :info ~fmt ~@args))))

(defmacro warn
  "When an unexpected application issue has been identified, the WARN log level is used. This indicates that you are unsure if the issue will recur or not. At this time, you may not notice any negative effects on your application. This is frequently an issue that prevents some processes from operating. However, this does not necessarily imply that the application has been affected. The code should, in fact, continue to function normally. If the issue recurs, you should examine these warnings at some point."
  [& message]
  (let [logger-id# (automaton-core.log.strategy/apply-strategy stgy *ns* :warn)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log ~logger-id# :warn ~@message)
      `(automaton-core.log.be-log/log ~logger-id# :warn ~@message))))

(defmacro warn-exception
  "Like warn, but focused on exceptions."
  [exception & additional-message]
  (let [logger-id# (automaton-core.log.strategy/apply-strategy stgy *ns* :warn)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-exception ~logger-id#
                                                :warn
                                                ~exception
                                                ~@additional-message)
      `(automaton-core.log.be-log/log-exception ~logger-id#
                                                :warn
                                                ~exception
                                                ~@additional-message))))

(defmacro warn-data
  "Like warn, but first argument is expected to be a map with usefull more detailed data."
  [data & additional-message]
  (let [logger-id# (automaton-core.log.strategy/apply-strategy stgy *ns* :warn)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-data ~logger-id#
                                           :warn
                                           ~data
                                           ~@additional-message)
      `(automaton-core.log.be-log/log-data ~logger-id#
                                           :warn
                                           ~data
                                           ~@additional-message))))

(defmacro warn-format
  "Like warn, but uses clojure format function, so first argument is string to translate and rest is arguments to supply it with."
  [fmt & args]
  (let [logger-id# (automaton-core.log.strategy/apply-strategy stgy *ns* :warn)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-format ~logger-id# :warn ~fmt ~@args)
      `(automaton-core.log.be-log/log-format ~logger-id# :warn ~fmt ~@args))))

(defmacro error
  "This ERROR indicates that something critical in your application has failed. This log level is used when a serious issue is preventing the application's functionalities from functioning properly. The application will continue to run for the most part, but it will need to be handled at some point."
  [& message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :error)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log ~logger-id# :error ~@message)
      `(automaton-core.log.be-log/log ~logger-id# :error ~@message))))

(defmacro error-exception
  "Like error, but focused on exceptions."
  [exception & additional-message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :error)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-exception ~logger-id#
                                                :error
                                                ~exception
                                                ~@additional-message)
      `(automaton-core.log.be-log/log-exception ~logger-id#
                                                :error
                                                ~exception
                                                ~@additional-message))))

(defmacro error-data
  "Like error, but first argument is expected to be a map with usefull more detailed data."
  [data & additional-message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :error)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-data ~logger-id#
                                           :error
                                           ~data
                                           ~@additional-message)
      `(automaton-core.log.be-log/log-data ~logger-id#
                                           :error
                                           ~data
                                           ~@additional-message))))

(defmacro error-format
  "Like error, but uses clojure format function, so first argument is string to translate and rest is arguments to supply it with."
  [fmt & args]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :error)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-format ~logger-id# :error ~fmt ~@args)
      `(automaton-core.log.be-log/log-format ~logger-id# :error ~fmt ~@args))))

(defmacro fatal
  "FATAL indicates that the application is about to prevent a major problem or corruption. The FATAL level of logging indicates that the application's situation is critical, such as when a critical function fails. If the application is unable to connect to the data store, for example, you can utilise the FATAL log level."
  [& message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :fatal)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log ~logger-id# :fatal ~@message)
      `(automaton-core.log.be-log/log ~logger-id# :fatal ~@message))))

(defmacro fatal-exception
  "Like fatal, but focused on exceptions."
  [exception & additional-message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :fatal)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-exception ~logger-id#
                                                :fatal
                                                ~exception
                                                ~@additional-message)
      `(automaton-core.log.be-log/log-exception ~logger-id#
                                                :fatal
                                                ~exception
                                                ~@additional-message))))

(defmacro fatal-data
  "Like fatal, but first argument is expected to be a map with usefull more detailed data."
  [data & additional-message]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :fatal)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-data ~logger-id#
                                           :fatal
                                           ~data
                                           ~@additional-message)
      `(automaton-core.log.be-log/log-data ~logger-id#
                                           :fatal
                                           ~data
                                           ~@additional-message))))

(defmacro fatal-format
  "Like fatal, but uses clojure format function, so first argument is string to translate and rest is arguments to supply it with."
  [fmt & args]
  (let [logger-id#
        (automaton-core.log.strategy/apply-strategy stgy *ns* :fatal)]
    (if (cljs-env? &env)
      `(automaton-core.log.fe-log/log-format ~logger-id# :fatal ~fmt ~@args)
      `(automaton-core.log.be-log/log-format ~logger-id# :fatal ~fmt ~@args))))
