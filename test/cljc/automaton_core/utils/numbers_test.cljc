(ns automaton-core.utils.numbers-test
  (:require
   [automaton-core.utils.numbers :as sut]
   #?@(:cljs [[cljs.test :refer-macros [testing is deftest]]]
       :clj [[clojure.test :refer [testing is deftest]]])))

(deftest check-val-in-range-test
  (testing "In the range integer are accepted"
    (is (nil? (sut/check-val-in-range 1 100 10)))
    (is (nil? (sut/check-val-in-range 10 100 10))))
  (testing "In the range floats are accepted"
    (is (nil? (sut/check-val-in-range 1.0 100.0 10.0)))
    (is (nil? (sut/check-val-in-range 10.0 100.0 10.0))))
  (testing "Out of range integer are accepted"
    (is (= -1 (sut/check-val-in-range 0 100 -1)))
    (is (= 100 (sut/check-val-in-range 1 100 100)))
    (is (= 0 (sut/check-val-in-range 1 100 0))))
  (testing "Out of range floats are accepted"
    (is (= 100.0 (sut/check-val-in-range 1.0 100.0 100.0)))
    (is (= 0.0 (sut/check-val-in-range 1.0 100.0 0.0)))
    (is (= 110.0 (sut/check-val-in-range 10.0 100.0 110.0)))))


(deftest check-vals-in-range-test
  (testing "In the range integer are accepted"
    (is (empty? (sut/check-vals-in-range 1 100 [10 1 99])))))
