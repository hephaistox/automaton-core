(ns automaton-core.utils.sequences-test
  (:require [automaton-core.utils.sequences :as sut]
            #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer [deftest is testing] :include-macros true])
            [clojure.string :as str]))

(deftest trim-leading-nil-test
  (testing "basic example"
    (is (= ["a" "b"] (sut/trim-leading-nil ["a" "b"])))
    (is (= ["a" "b"] (sut/trim-leading-nil ["a" "b" nil])))
    (is (= ["a" "b"] (sut/trim-leading-nil ["a" "b" nil nil nil nil])))
    (is (= [nil "a" nil nil "b"] (sut/trim-leading-nil [nil "a" nil nil "b" nil nil nil nil]))))
  (testing "edge cases"
    (is (empty? (sut/trim-leading-nil [nil nil nil nil nil])))
    (is (empty? (sut/trim-leading-nil [])))
    (is (empty? (sut/trim-leading-nil nil))))
  (testing "Test other fn usage"
    (is (= ["a" "b"] (sut/trim-leading-nil ["a" "b" nil nil nil nil] nil?)))
    (is (empty? (sut/trim-leading-nil ["a" "b"] string?)))
    (is (= ["a" "b"] (sut/trim-leading-nil ["a" "b" nil nil "" ""] str/blank?)))))
