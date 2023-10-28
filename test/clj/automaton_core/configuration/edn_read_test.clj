(ns automaton-core.configuration.edn-read-test
  (:require [automaton-core.configuration.edn-read :as sut]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest is testing]]))

(deftest read-edn-test
  (testing "Find the test configuration stub"
    (is (= {:foo "bar"
            :bar 10}
           (sut/read-edn (io/resource "configuration/stub.edn"))))))
