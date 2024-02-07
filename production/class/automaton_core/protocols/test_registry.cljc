(ns automaton-core.protocols.test-registry
  "Test registry for a protocol.

  See ADR-0015"
  (:require
   [automaton-core.adapters.schema :as core-schema]
   [automaton-core.log :as core-log]))

(def ^:private test-registry-schema
  "Test registry schema"
  [:map-of
   :keyword
   [:or
    [:map [:msg :string] [:result :any] [:expect some?]]
    [:map [:msg :string] [:result :any] [:expect-fn fn?]]]])

(defn- validate-test-registry-schema
  "Returns the message if the parameter is not a valid registry regarding the schema

  Params:
  * `test-registry` registry to test"
  [test-registry]
  (core-schema/schema-valid-humanize test-registry-schema test-registry))

(defn- reports*
  "Execute the tests and creates a report of tests's execution

  The report will catch exception if it occurs and enrich the exception with the context of the registry entry that has failed

  The expected result could be described in two ways:
  * first if `expect-fn` is set, it is expected to be a one argument function called with `result`, this should return true if the test pass
  * then, `expect` is compared to result

  The first form is particularly useful to display a meaningful `result` value, even if the test is not able to tell the exact value

  Returns a collection of maps for each of failed tests

  Params:
  * `test-registry` map complying to `test-registry-schema`"
  [test-registry]
  (let [res (->> test-registry
                 (map
                  (fn [[test-name {:keys [msg expect expect-fn result]}]]
                    (try (if (fn? expect-fn)
                           (let [expect-fn-res (expect-fn result)]
                             (when-not expect-fn-res
                               {:msg msg
                                :test-name test-name
                                :expect-fn-res expect-fn-res
                                :result result}))
                           (when-not (= expect result)
                             {:msg msg
                              :test-name test-name
                              :expect expect
                              :result result}))
                         (catch #?(:clj Exception
                                   :cljs :default)
                           e
                           {:msg (str "Impossible to execute" test-name)
                            :exception e
                            :test-name test-name
                            :expect expect
                            :result result}))))
                 (filter some?)
                 set)]
    (when-not (empty? res) (core-log/error-data res "Some tests have failed:"))
    res))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn build-report
  "Validate the `test-registry` and returns the failed tests as a report

  Params:
    * `test-registry` Test registry to build report about"
  [test-registry]
  (-> test-registry
      (assoc ::registry-schema
             {:msg "Test the registry compliance with the schema"
              :result (validate-test-registry-schema test-registry)
              :expect-fn empty?})
      reports*))
