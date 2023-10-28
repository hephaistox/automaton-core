(ns automaton-core.configuration.simple-files-test
  (:require
   [automaton-core.configuration.simple-files :as sut]
   [clojure.java.io :as io]
   [clojure.test :refer [deftest is testing]]))

(deftest read-edn-test
  (testing "Find the test configuration stub"
    (is (= {:foo "bar"
            :bar 10}
           (sut/read-edn (io/resource "configuration/stub.edn"))))))
