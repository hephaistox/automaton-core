(ns automaton-core.log.fe-log-test
  (:require [automaton-core.log.fe-log :as sut]
            [cljs.test :refer [deftest is testing] :include-macros true]))

(deftest fatal-test
  (testing "Fatal is always printing something"
    (is
      (= "foo bar"
         (with-out-str
           (sut/log [:automaton-core.log.registry/print] :fatal "foo" "bar")))))
  (testing "Macroexpansion is resolving until the key of the chosen method"
    (is
      (=
        '((clojure.core/apply
           clojure.core/juxt
           (automaton-core.log.fe-log/logger-ids-to-logger-fns
            [:automaton-core.log.registry/print]))
          "automaton-core.log.fe-log-test"
          :fatal
          "foo"
          "bar")
        (macroexpand '(sut/log
                       [:automaton-core.log.registry/print]
                       :fatal
                       "foo"
                       "bar"))))))

(deftest trace-test
  (testing "Trace is not accepted in log namespace test rule"
    (is (= ""
           (with-out-str (sut/log [:automaton-core.log.registry/no-op]
                                  :trace
                                  "foo"
                                  "bar"))))))
