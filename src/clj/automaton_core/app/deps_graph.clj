(ns automaton-core.app.deps-graph
  "Dependency graph of applications

  Is built based on `apps` as done in `monorepo-app.apps`"
  (:require [automaton-core.app.deps-graph.impl :as deps-graph-impl]
            [automaton-core.adapters.files :as files]))

(defn sorted-apps
  [apps]
  (->> apps
       deps-graph-impl/add-hephaistox-deps
       deps-graph-impl/topologically-sort))

(defn find-current-app
  [sorted-apps-graph]
  (->> sorted-apps-graph
       (filterv (fn [{:keys [app-dir]}] (files/compare-paths "." app-dir)))
       first))

(defn app-by-name
  "Search the graph to find an app with its name
  Params:
  * `sorted-apps-graph`
  * `app-name` string of the name of the app"
  [app-name sorted-apps-graph]
  (->> sorted-apps-graph
       (filter (fn [app] (= (:app-name app) app-name)))
       first))
