(ns automaton-core.endpoint.web.dev-handlers
  "Handlers made for development purposes"
  (:require
   [ring.middleware.reload :as mr]
   [prone.middleware :as prone]

   [automaton-core.adapters.deps-edn :as deps-edn]))

(def runnables
  "List of directories"
  (->
   (deps-edn/load-deps)
   (deps-edn/extract-paths)))

(defn wrap-nocache
  "Dev wrapper to prevent caching of all requests, helpfull to run multiple app locally on the same port for instance"
  [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(defn wrap-reload
  "Reload clj as they are saved"
  [handler]
  (-> handler
      (mr/wrap-reload {:dirs runnables})))

(defn wrap-no-exception
  "Show exceptions with prone library

  See [prone documentation ](https://github.com/magnars/prone#how-does-prone-determine-what-parts-of-a-stack-trace-belongs-to-the-application)
  to get how to set it up"
  [handler]
  (-> handler
      (prone/wrap-exceptions {:app-namespaces runnables})))

(def middlewares
  "Middlewares specific for development environment"
  [;;wrap-reload ;; Is not working, some namespace are auto referencing
   wrap-nocache
   wrap-no-exception])
