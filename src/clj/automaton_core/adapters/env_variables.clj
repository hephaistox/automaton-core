(ns automaton-core.adapters.env-variables
  "Operating enviroment variables")

(defn get-env
   "Get the environment variable
  Params:
  * `var-name` name of the environment variable to be retrieved"
  [var-name]
  (System/getenv var-name))

(defn get-envs
  "A map of all available environment variables"
  []
  (into {}
        (System/getenv)))
