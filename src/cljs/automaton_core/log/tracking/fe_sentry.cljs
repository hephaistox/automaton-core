(ns automaton-core.log.tracking.fe-sentry
  "Sentry frontend logging."
  (:require ["@sentry/browser" :as Sentry]))

(defn send-breadcrumb!
  "Sends breadcrumb, which will not be shown in sentry untill event is sent.
   You can read more here: https://docs.sentry.io/platforms/javascript/guides/react/enriching-events/breadcrumbs/"
  [{:keys [message level context]}]
  (.addBreadcrumb Sentry
                  (clj->js {:level level
                            :message message
                            :data context})))

(defn send-event!
  "Sends an event that is registered in sentry."
  [{:keys [message level context]}]
  (.captureEvent Sentry
                 (clj->js {:level level
                           :message message
                           :extra context})))
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn init-sentry!
  "Initialize sentry for frontend (js), which is recording browser/js unhandled exceptions that happens inside and enables to send events manually.
  'development' as an environment is ignored, so no event is sent from it."
  [{:keys [dsn traced-website env]}]
  (.init Sentry
         #js {:dsn dsn
              :environment env
              :integrations #js [(new (.BrowserTracing Sentry))]
              :replaysSessionSampleRate 0
              :replaysOnErrorSampleRate 0
              :tracesSampleRate 1.0
              :tracePropagationTargets #js ["localhost" traced-website]}))
