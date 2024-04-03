(ns automaton-core.adapters.schema-test
  (:require
   [automaton-core.adapters.schema :as sut]
   [clojure.test                   :refer [deftest is testing]]))

(deftest schema
  (testing "Valid schema"
    (is (sut/validate-data [:tuple :string :int] ["hey" 12])))
  (testing "Invalid schema, throws an exception"
    (is (not (sut/validate-data [:tuple :string :int] [12 12])))))

(deftest schema-test-test
  (testing "Valid schema returns true"
    (is (nil? (sut/validate-humanize [:vector :int]))))
  (testing "Invalid schema returns false"
    (is (some? (sut/validate-humanize nil)))
    (is (some? (sut/validate-humanize 12)))))

(deftest assert-schema-test
  (testing "Test succesful assertions"
    (is (nil? (sut/assert-schema :int 2 "Error"))))
  (testing "Test unsuccesful assertions"
    (is (thrown-with-msg? java.lang.AssertionError
                          #"Assert"
                          (sut/assert-schema :int "foo" "Error")))))

(deftest assert-schemas-test
  (testing "Succesful assertions"
    (is (nil? (sut/assert-schemas :int 2 :string "foo"))))
  (testing "Unsuccesful assertions"
    (is (thrown-with-msg? java.lang.AssertionError
                          #"Assert"
                          (sut/assert-schemas :int "foo" :string "foo")))))
