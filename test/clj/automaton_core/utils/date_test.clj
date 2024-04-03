(ns automaton-core.utils.date-test
  (:require
   [automaton-core.utils.date :as sut]
   [clojure.test              :refer [deftest is testing]]))

(deftest this-year
  (testing "Check now"
    (is (>= (Integer/parseInt (sut/this-year)) 2022))
    (is (>= (Integer/parseInt (sut/this-year (java.util.Date.))) 2022))))
