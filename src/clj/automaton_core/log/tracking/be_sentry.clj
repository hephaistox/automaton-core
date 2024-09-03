(ns automaton-core.log.tracking.be-sentry
  "Sentry backend implementation"
  (:require
   [automaton-core.utils.map          :as utils-map]
   [automaton-core.utils.pretty-print :as pretty-print]
   [sentry-clj.core                   :as sentry])
  (:import [io.sentry Breadcrumb Sentry SentryLevel]
           [java.util Date HashMap Map]))

(defn- keyword->level
  "Converts a keyword into an event level."
  [level]
  (case level
    :debug SentryLevel/DEBUG
    :info SentryLevel/INFO
    :warning SentryLevel/WARNING
    :error SentryLevel/ERROR
    :fatal SentryLevel/FATAL
    SentryLevel/INFO))

(defn- map->breadcrumb
  "Converts a map into a Breadcrumb."
  ^Breadcrumb [{:keys [type level message category data timestamp]}]
  (let [breadcrumb (if timestamp (Breadcrumb. ^Date timestamp) (Breadcrumb.))]
    (when type (.setType breadcrumb type))
    (when level (.setLevel breadcrumb (keyword->level level)))
    (when message (.setMessage breadcrumb message))
    (when category (.setCategory breadcrumb category))
    (when data
      (doseq [[k v] (utils-map/map-util-hashmappify-vals data #(HashMap. ^Map %))]
        (.setData breadcrumb k v)))
    breadcrumb))

(defn send-breadcrumb!
  "Sends breadcrumb, which will not be shown in sentry until event is sent.
   You can read more here: https://docs.sentry.io/platforms/java/enriching-events/breadcrumbs/"
  [{:keys [message level context]}]
  (Sentry/addBreadcrumb (map->breadcrumb {:message (pretty-print/seq->string message)
                                          :level level
                                          :data context})))

(defn send-event!
  "Sends an event that is registered in sentry."
  [{:keys [message level context]}]
  (sentry/send-event {:message (pretty-print/seq->string message)
                      :level level
                      :extra context}))

(defn init-sentry!
  "Initialize sentry for jvm, so events can be recorded.
   'development' as an environment is ignored, so no event is sent from it."
  [{:keys [dsn env]}]
  (if (every? some? [dsn env])
    (sentry/init! dsn {:environment env})
    (prn "Sentry initialization is skipped, paremeters are missing")))
