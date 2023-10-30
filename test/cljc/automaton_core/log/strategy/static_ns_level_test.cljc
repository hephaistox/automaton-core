(ns automaton-core.log.strategy.static-ns-level-test
  (:require #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer [deftest is testing] :include-macros
                      true])
            [automaton-core.log.strategy :as log-strategy]
            [automaton-core.log.registry :as log-registry]
            [automaton-core.log.strategy.static-ns-level :as sut]))

(deftest ns-rules-test
  (let [ns-chooser-stub (sut/make-static-ns-level-strategy sut/ns-rules)
        ns-rules-map-stub (into {}
                                (map (fn [{:keys [rule-id], :as rule}] [rule-id
                                                                        rule])
                                  (:ns-rules ns-chooser-stub)))
        test-ns (fn [rule-id test-ns]
                  (some-> rule-id
                          (ns-rules-map-stub)
                          :re
                          (re-find test-ns)))]
    (testing
      "Check all used strategies are known strategies in `automaton-core.log.strategy/registry`"
      (is (every? (set (keys log-registry/strategies-registry))
                  (log-strategy/rule-ids ns-chooser-stub))))
    (testing "List of rule application we want to enforce through tests"
      (is (test-ns :rule1 "automaton-core"))
      (is (test-ns :rule1 "automaton-core.log"))
      (is (test-ns :rule2 "automaton-core.log")))))

(deftest apply-ns-rule-test
  (let [ns-chooser-stub (sut/make-static-ns-level-strategy sut/ns-rules)
        ns-rules-map-stub (into {}
                                (map (fn [{:keys [rule-id], :as rule}] [rule-id
                                                                        rule])
                                  (:ns-rules ns-chooser-stub)))
        get-stubbed-rule (fn [rule-id] (rule-id ns-rules-map-stub))]
    (testing "ns outside the scope are rejected"
      (is (nil? (sut/apply-ns-rule "other ns" (get-stubbed-rule :rule1))))
      (is (nil? (sut/apply-ns-rule "automaton-core"
                                   (get-stubbed-rule :rule2)))))
    (testing "ns matching the scope are accepted"
      (is (map? (sut/apply-ns-rule "automaton-core" (get-stubbed-rule :rule1))))
      (is (map? (sut/apply-ns-rule "automaton-core.log"
                                   (get-stubbed-rule :rule2))))
      (is (map? (sut/apply-ns-rule "automaton-core.log"
                                   (get-stubbed-rule :rule1)))))))

(deftest choose-logger-test
  (let [ns-chooser-stub (sut/make-static-ns-level-strategy sut/ns-rules)
        apply-strategy* (partial log-strategy/apply-strategy ns-chooser-stub)]
    (testing "One matching is returning the expected match"
      (is (= [:automaton-core.log.registry/print]
             (apply-strategy* "automaton-core" :fatal))))
    (testing "Among many matches the first one is returned"
      (is (= [:automaton-core.log.registry/print]
             (apply-strategy* "automaton-core.log" :fatal))))
    (testing "Default logger is text-based and error-tracking"
      (is (= [:automaton-core.log.registry/text-based
              :automaton-core.log.registry/error-tracking]
             (apply-strategy* "non-existing-namespace" :fatal))))))

