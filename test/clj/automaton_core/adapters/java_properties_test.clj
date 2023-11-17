(ns automaton-core.adapters.java-properties-test
  (:require [automaton-core.adapters.java-properties :as sut]
            [clojure.test :refer [deftest is testing]]))

(deftest get-java-properties-test
  (testing "Java properties are valid"
    (is (map? (sut/get-java-properties)))
    (is (< 2
           (-> (sut/get-java-properties)
               count)))))

(comment
  (sut/get-java-property "heph-conf")
  ;
)
