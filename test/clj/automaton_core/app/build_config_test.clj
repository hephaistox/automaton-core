(ns automaton-core.app.build-config-test
  (:require
   [automaton-core.app.build-config :as sut]
   [clojure.test                    :refer [deftest is testing]]))

(deftest load-build-config-test
  (testing "The configuration of the current project is found and valid"
    (is (= ((juxt map? (comp not empty?)) (sut/load-build-config "")) [true true])))
  (testing "The configuration of the current project is found and valid"
    (is (nil? (sut/load-build-config "non-existing-dir")))))

(deftest search-for-build-configs-test
  (testing "The configuration of the current project is found and valid"
    (is (= ((juxt (partial every? string?) (comp not empty?)) (sut/search-for-build-configs ""))
           [true true])))
  (testing "The configuration of the current project is found and valid"
    (is (empty? (sut/search-for-build-configs "non-existing-dir")))))
