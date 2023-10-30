(ns automaton-core.utils.fallback-test
  (:require [automaton-core.utils.fallback :as sut]
            #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer [deftest is testing] :include-macros
                      true])))

(deftest always-return-test
  (testing "When there is no exception function value is returned."
    (is (= (sut/always-return (fn [] 34) 15) 34))))
