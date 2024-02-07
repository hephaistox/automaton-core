(ns automaton-core.utils.date-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [automaton-core.utils.date :as sut]))

(deftest this-year
  (testing "Check now"
    (is (>= (Integer/parseInt (sut/this-year)) 2022))
    (is (>= (Integer/parseInt (sut/this-year (java.util.Date.))) 2022))))
