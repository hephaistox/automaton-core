(ns automaton-core.utils.keyword-test
  (:require [automaton-core.utils.keyword :as sut]
            #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer [deftest is testing] :include-macros true])))

(deftest sanitize-key-test
  (testing "Basic case" (is (= :hello (sut/sanitize-key :hello))))
  (testing "capital letters turned into lower case" (is (= :hello (sut/sanitize-key :HeLLo))))
  (testing "_ turned into -" (is (= :hello-world (sut/sanitize-key :hello_world))))
  (testing ". turned into -" (is (= :hello-world (sut/sanitize-key :hello-world))))
  (testing "Works also on strings" (is (= :hello-my-world (sut/sanitize-key "hello.MY_wOrLd")))))

(deftest sanitize-map-keys-test
  (testing "Basic case"
    (is (= {:hello "world"
            :my-amazing :liFe}
           (sut/sanitize-map-keys {:heLLo "world"
                                   :my_amazing :liFe}))))
  (testing "Works on nested maps"
    (is (= {:hello "world"
            :my-amazing :liFe
            :three {:two-more {:another-one 1
                               :normal-one :true}
                    :one "hi"}}
           (sut/sanitize-map-keys {:Hello "world"
                                   :my_amazing :liFe
                                   :three {:two_more {:another.one 1
                                                      :normal-one :true}
                                           :ONE "hi"}})))))
