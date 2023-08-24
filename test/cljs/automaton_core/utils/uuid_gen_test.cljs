(ns automaton-core.utils.uuid-gen-test
  (:require
   [clojure.test :refer [testing deftest is]]

   [automaton-core.utils.uuid-gen :as sut]))

(deftest unguessable
  (testing "check that generates proper uuid"
    (is (every? uuid?
                (repeatedly 10 #(sut/unguessable))))))
