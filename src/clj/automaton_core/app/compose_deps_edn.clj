(ns automaton-core.app.compose-deps-edn
  "The composed `deps.edn` file is generated here, especially to store at the root of the monorepo.

  Design decision,
  * monorepo is targeting repl, so all deps and paths are set in the root

  Design decision, the workflow is the following:
  * A bb task called in `clojure` dir is done
  * The monorepo `build_config.edn` is reading to detect the project is monorepo,
  * The composite files are generated again
  * If their content are different from the one stored in the files the task is started again,
  * Otherwise, the execution is started again"
  (:require [automaton-core.app.compose-deps-edn.compose-deps-edn-impl :as compose-deps-edn-impl]))

(defn compose
  [compose-deps-edn-dir apps]
  (-> {}
      (assoc :aliases (compose-deps-edn-impl/aliases compose-deps-edn-dir #{:run} apps)
             :deps (compose-deps-edn-impl/update-deps #{:run} apps)
             :paths (compose-deps-edn-impl/path compose-deps-edn-dir #{:run} apps))))
