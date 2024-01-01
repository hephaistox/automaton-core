(ns automaton-core.adapters.re-test
  (:require [automaton-core.adapters.regexp :as sut]
            #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer [deftest is testing] :include-macros true])))

(deftest assemble-re-test
  (testing "Test assembly of strings" (is (= "abc" (sut/stringify (sut/assemble-re ["a" "b" "c"])))))
  (testing "Test assembly of re" (is (= "abc" (sut/stringify (sut/assemble-re [#"a" #"b" #"c"])))))
  (testing "Test assembly of mixed" (is (= "abc" (sut/stringify (sut/assemble-re [#"a" "b" #"c"]))))))

(deftest full-sentence-re-test
  (testing "Check terminators are working for clj and cljs"
    (let [res (sut/stringify (sut/full-sentence-re #"foo"))] (is (or (= res "\\Afoo\\z") (= res "^foo$"))))))

(deftest assemble-re-optional-test
  (testing "Check optionality is added"
    (is (= "foo?barfoo2" (sut/stringify (sut/assemble-re-optional [#"foo" true "bar" false "foo2" false])))))
  (testing "Test prefix and suffix"
    (is (= "afoo?barfoo2b" (sut/stringify (sut/assemble-re-optional [#"foo" true "bar" false "foo2" false] "a" "b"))))))
