(ns automaton-core.utils.uuid-gen-test
  (:require [automaton-core.utils.uuid-gen :as sut]
            [clojure.test :refer [testing deftest is]]))

(deftest time-based-uuid
  (testing "uuid and chonological orders are the same"
    (let [vecs (map (fn [_n] (sut/time-based-uuid)) (range 10))] (is (= (sort (map str vecs)) (map str vecs)))))
  (testing "Is a uuid" (is (uuid? (sut/time-based-uuid))))
  (testing "Timed based uuid are in the right order"
    (dotimes [_ 10] (let [uuid1 (sut/time-based-uuid) uuid2 (sut/time-based-uuid)] (is (compare (str uuid1) (str uuid2)))))))
