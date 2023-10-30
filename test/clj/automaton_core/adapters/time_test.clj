(ns automaton-core.adapters.time-test
  (:require [clojure.test :refer [deftest is testing]]
            [automaton-core.adapters.time :as sut]))

(deftest now-str
  (testing "Check date is generated"
    (let [date-str (sut/now-str)]
      (is (string? date-str))
      (is (> (count date-str) 20)))))
