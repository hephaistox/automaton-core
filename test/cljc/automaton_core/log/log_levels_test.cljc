(ns automaton-core.log.log-levels-test
  (:require [automaton-core.log.log-levels :as sut]
            #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer [deftest is testing] :include-macros true])))

(deftest execute-level?
  (testing "Logs of same or higher level are accepted" (is (sut/execute-level? :trace :trace)) (is (sut/execute-level? :fatal :fatal)))
  (testing "Logs of strictly lower level are rejected"
    (is (not (sut/execute-level? nil :trace)))
    (is (not (sut/execute-level? :debug :trace)))
    (is (not (sut/execute-level? :fatal :trace)))
    (is (not (sut/execute-level? :debug :trace)))))
