(ns automaton-core.data-structure.graph-test
  (:require [clojure.test :refer [deftest is testing]]
            [automaton-core.data-structure.graph.graph-stub :as graph-stub]
            [automaton-core.data-structure.graph :as sut]))

(deftest topologically-ordered-test-copy
  (testing "Check doseq is ordered in the right order"
    (is (= ['a 'b 'c 'd]
           (sut/topologically-ordered graph-stub/nodes-fn
                                      graph-stub/edges-fn
                                      graph-stub/dst-in-edge
                                      graph-stub/remove-nodes
                                      graph-stub/graph-data))))
  (testing "Work with graph with useless dependencies"
    (is (= ['a 'e 'b 'c 'd]
           (sut/topologically-ordered graph-stub/nodes-fn
                                      graph-stub/edges-fn
                                      graph-stub/dst-in-edge
                                      graph-stub/remove-nodes
                                      (assoc graph-stub/graph-data 'e {:foo5 :bar5}))))))
