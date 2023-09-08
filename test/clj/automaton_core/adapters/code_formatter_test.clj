(ns automaton-core.adapters.code-formatter-test
  (:require
   [automaton-core.adapters.code-formatter :as sut]
   [clojure.test :refer [deftest is testing]]))

(deftest format-content-test
  (testing "Simple test"
    (is (= "{:a :b}"
           (sut/format-content {:a    :b})))
    (is (= "(do {:a {:b :c}} 1)"
           (sut/format-content '(do {:a {:b :c}} 1))))))

(comment
  ;; This test works only if some modifications are done in the bb.edn or in the task
  ;; Note that it will remove the comment line
  (sut/format-file "bb.edn")
;
  )
