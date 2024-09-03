(ns automaton-core.adapters.schema-test
  (:require
   [automaton-core.adapters.schema :as sut]
   [clojure.test                   :refer [deftest is testing]]))

(deftest schema
  (testing "Valid schema" (is (sut/validate-data [:tuple :string :int] ["hey" 12])))
  (testing "Invalid schema, throws an exception"
    (is (not (sut/validate-data [:tuple :string :int] [12 12])))))

(deftest schema-test-test
  (testing "Valid schema returns true" (is (nil? (sut/validate-humanize [:vector :int]))))
  (testing "Invalid schema returns false"
    (is (some? (sut/validate-humanize nil)))
    (is (some? (sut/validate-humanize 12)))))

(deftest add-default-test
  (testing "Adds default values."
    (is (sut/add-default [:map
                          [:foo {:default "bar"}
                           :string]]
                         {}))))
