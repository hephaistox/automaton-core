(ns automaton-core.utils.namespace-test
  (:require
   [automaton-core.utils.namespace :as sut]
   [clojure.test                   :refer [deftest is testing]]))

(deftest namespaced-keyword-test
  (testing "Test from symbols"
    (is (= 'foo/bar (sut/namespaced-keyword 'foo 'bar))))
  (testing "Test from kw" (is (= 'foo/bar (sut/namespaced-keyword :foo :bar))))
  (testing "Test from string"
    (is (= 'foo/bar (sut/namespaced-keyword "foo" "bar"))))
  (testing "Resist to nul value "
    (is (= 'bar (sut/namespaced-keyword nil :bar)))
    (is (= 'foo (sut/namespaced-keyword :foo nil)))))
