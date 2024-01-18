(ns automaton-core.log.log-levels-test
  (:require
   [automaton-core.log.log-levels :as sut]
   #?(:clj [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer [deftest is testing] :include-macros true])))

(deftest execute-min-level?
  (testing "Logs of same or higher level are accepted"
    (is (sut/execute-min-level? :trace :trace))
    (is (sut/execute-min-level? :fatal :fatal)))
  (testing "Logs of strictly lower level are rejected"
    (is (not (sut/execute-min-level? nil :trace)))
    (is (not (sut/execute-min-level? :debug :trace)))
    (is (not (sut/execute-min-level? :fatal :trace)))
    (is (not (sut/execute-min-level? :debug :trace)))))

(deftest execute-max-level?
  (testing "Logs of same or lower level are accepted"
    (is (sut/execute-max-level? :trace :trace))
    (is (sut/execute-max-level? :fatal :debug)))
  (testing "Logs of strictly higher level are rejected"
    (is (not (sut/execute-max-level? nil :trace)))
    (is (not (sut/execute-max-level? :trace :debug)))
    (is (not (sut/execute-max-level? :trace :fatal)))
    (is (not (sut/execute-max-level? :debug :error)))))

(deftest execute-level?
  (testing "Logs between max and min are accepted"
    (is (sut/execute-level? {:min-level :trace
                             :max-level :error
                             :level :trace}))
    (is (sut/execute-level? {:min-level :trace
                             :max-level :error
                             :level :error}))
    (is (sut/execute-level? {:min-level :trace
                             :max-level :error
                             :level :warn}))
    (is (not (sut/execute-level? {:min-level :debug
                                  :max-level :error
                                  :level :trace})))
    (is (not (sut/execute-level? {:min-level :debug
                                  :max-level :error
                                  :level :fatal})))
    (is (not (sut/execute-level? {:min-level :warn
                                  :max-level :fatal
                                  :level :trace}))))
  (testing "Logs when max is not defined works according to min-level"
    (is (sut/execute-level? {:min-level :trace
                             :level :trace}))
    (is (sut/execute-level? {:min-level :trace
                             :level :error}))
    (is (not (sut/execute-level? {:min-level :debug
                                  :level :trace}))))
  (testing "Logs when min is not defined works according to max-level"
    (is (sut/execute-level? {:max-level :trace
                             :level :trace}))
    (is (sut/execute-level? {:max-level :fatal
                             :level :error}))
    (is (not (sut/execute-level? {:max-level :debug
                                  :level :warn})))))
