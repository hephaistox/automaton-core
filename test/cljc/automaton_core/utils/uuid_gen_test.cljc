(ns automaton-core.utils.uuid-gen-test
  (:require
   [automaton-core.utils.uuid-gen :as sut]
   [clojure.test                  :refer [deftest is testing]]))

(deftest unguessable
  (testing "check that generates proper uuid"
    (is (every? uuid? (repeatedly 10 #(sut/unguessable))))))
