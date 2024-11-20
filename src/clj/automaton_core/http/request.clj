(ns automaton-core.http.request
  "Namespace for http requests"
  (:require
   [org.httpkit.client :as http]))

(defn http-get ([url] (get url nil)) ([url opts] (http/get url opts)))
