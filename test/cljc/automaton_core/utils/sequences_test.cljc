(ns automaton-core.utils.sequences-test
  (:require
   [automaton-core.utils.sequences :as sut]
   #?(:clj [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer [deftest is testing] :include-macros true])
   [clojure.string                 :as str]))

(deftest trim-leading-nil-test
  (testing "basic example"
    (is (= ["a" "b"] (sut/trim-leading-nil ["a" "b"])))
    (is (= ["a" "b"] (sut/trim-leading-nil ["a" "b" nil])))
    (is (= ["a" "b"] (sut/trim-leading-nil ["a" "b" nil nil nil nil])))
    (is (= [nil "a" nil nil "b"] (sut/trim-leading-nil [nil "a" nil nil "b" nil nil nil nil]))))
  (testing "edge cases"
    (is (empty? (sut/trim-leading-nil [nil nil nil nil nil])))
    (is (empty? (sut/trim-leading-nil [])))
    (is (empty? (sut/trim-leading-nil nil))))
  (testing "Test other fn usage"
    (is (= ["a" "b"] (sut/trim-leading-nil ["a" "b" nil nil nil nil] nil?)))
    (is (empty? (sut/trim-leading-nil ["a" "b"] string?)))
    (is (= ["a" "b"] (sut/trim-leading-nil ["a" "b" nil nil "" ""] str/blank?)))))

(deftest index-of-test
  (testing "Element found in the sequence"
    (is (= 2 (sut/index-of [1 2 :foo 3] #{:foo})))
    (is (= 0 (sut/index-of [:foo 1 2 3] #{:foo})))
    (is (= 3 (sut/index-of [1 2 3 :foo] #{:foo}))))
  (testing "Element not found in the sequence" (is (nil? (sut/index-of [1 2 3] :foo)))))

(deftest concat-at-test
  (is (= [:a :b :c :d :e :f] (sut/concat-at [:a :b :x :e :f] :x [:c :d])))
  (testing "If replacing `kw` is not found, do nothing."
    (is (= [:a :b :x :e :f :c :d] (sut/concat-at [:a :b :x :e :f] :y [:c :d])))))

(deftest position-by-values-test
  (testing "Non vector are ok"
    (is (= {1 [0]
            2 [1]
            3 [2]}
           (sut/position-by-values '(1 2 3)))))
  (testing "Empty vectors"
    (is (= {} (sut/position-by-values [])))
    (is (= {} (sut/position-by-values nil))))
  (testing "Return vectors"
    (is (every? vector?
                (-> (sut/position-by-values [1 2 3 1 1 2])
                    vals))))
  (testing "Happy path"
    (is (= {1 [0 3 4]
            2 [1 5]
            3 [2]}
           (sut/position-by-values [1 2 3 1 1 2])))
    (is (= {1 [0]
            2 [1]
            3 [2]}
           (sut/position-by-values [1 2 3])))))
