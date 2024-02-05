(ns automaton-core.adapters.schema-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [automaton-core.adapters.schema :as sut]))

(deftest schema
  (testing "Valid schema"
    (is (sut/schema-valid [:tuple :string :int] ["hey" 12])))
  (testing "Invalid schema, throws an exception"
    (is (not (sut/schema-valid [:tuple :string :int] [12 12])))))

