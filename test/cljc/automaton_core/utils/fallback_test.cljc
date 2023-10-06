(ns automaton-core.utils.fallback-test
  (:require [automaton-core.utils.fallback :as sut]
            #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer [deftest is testing] :include-macros true])))

(deftest always-return-test
  (testing "ret-val is returned when there is exception"
    (is (= (sut/always-return #(throw (ex-info "sututu" {})) 15)
           15)))
  #?(:clj (testing "ret-val is returned when there is java error"
            (is (= (sut/always-return #(throw (AssertionError. "Wrong input")) 15)
                   15))))
  (testing "When there is no exception function value is returned."
    (is (=
         (sut/always-return (fn [] 34) 15)
         34))))
