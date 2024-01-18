(ns automaton-core.utils.map-test
  (:require
   [automaton-core.utils.map :as sut]
   #?(:clj [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer [deftest is testing] :include-macros true])))

(def v ["a" "b" "c"])

(filter #(= "b" (second %)) (map-indexed vector v))

(deftest idx-of-test
  (testing "Basic case"
    (is (= 0 (sut/idx-of v "a")))
    (is (= 1 (sut/idx-of v "b")))
    (is (= 2 (sut/idx-of v "c"))))
  (testing "not found values return nil" (is (nil? (sut/idx-of v "z"))))
  (testing "nil values are ok"
    (is (nil? (sut/idx-of v nil)))
    (is (nil? (sut/idx-of nil "v")))))

(def v2 [{:foo :bar} {:foo :bar2}])

(deftest idx-of-pred-test
  (testing "Basic case" (is (= 0 (sut/idx-of-pred v2 #(= :bar (:foo %))))))
  (testing "not found values return nil"
    (is (nil? (sut/idx-of-pred v #(= :not-existing (:foo %))))))
  (testing "nil values are ok"
    (is (nil? (sut/idx-of-pred v nil)))
    (is (nil? (sut/idx-of-pred nil "v")))))

(deftest crush-test
  (testing "Crush function contract"
    (is (= {:foo.bar.go "???"
            :foo2.bar2.go2 "!!!"
            :foo2.bar2.go3 ")))"
            :foo2.bar3 "foo"}
           (sut/crush {:foo {:bar {:go "???"}}
                       :foo2 {:bar2 {:go2 "!!!"
                                     :go3 ")))"}
                              :bar3 "foo"}})))))
(deftest deep-merge-test
  (testing "last map has higher priority"
    (is (= {:one 1
            :two {}}
           (sut/deep-merge {:one 1
                            :two 2}
                           {:one 1
                            :two {}}))))
  (testing "Two level of nest"
    (is (= {:one 1
            :two {:three {:test true}
                  :four {:five 5}}}
           (sut/deep-merge {:one 1
                            :two {:three 3
                                  :four {:five 5}}}
                           {:two {:three {:test true}}}))))
  (testing "Two level of nest"
    (is (= {:one {:two {:three "three"
                        :nine 9}
                  :seven 7}
            :four {:five 5
                   :eight 8}
            :ten 10}
           (sut/deep-merge {:one {:two {:three 3}}
                            :four {:five {:six 6}}}
                           {:one {:seven 7
                                  :two {:three "three"
                                        :nine 9}}
                            :four {:eight 8
                                   :five 5}
                            :ten 10}))))
  (testing "Non conflicting keys are merged"
    (is (= {:one {:two 2
                  :three 3
                  :four 4
                  :five 5}}
           (sut/deep-merge {:one {:two 2
                                  :three 3}}
                           {:one {:four 4
                                  :five 5}}))))
  (testing "Nil is working as an empty map"
    (is (= {:one 1
            :two {:three 3}}
           (sut/deep-merge {:one 1
                            :two {:three 3}}
                           nil))))
  (testing "Nil is working as an empty map in complex list"
    (is (= {:one 1
            :two {:three 3
                  :fourth 4
                  :fifth 5}}
           (sut/deep-merge {:one 1
                            :two {:three 3}}
                           nil
                           {:one 1
                            :two {:fourth 4}}
                           nil
                           nil
                           {:one 1
                            :two {:fifth 5}}))))
  (testing "Multiple maps are manager, last one is higher priority"
    (is (= {:one 4
            :two {:three 6}}
           (sut/deep-merge {:one 1
                            :two {:three 3}}
                           {:one 2
                            :two {:three 4}}
                           {:one 3
                            :two {:three 5}}
                           {:one 4
                            :two {:three 6}})))))

(deftest add-ids-test
  (testing "Simple maps"
    (is (= {:foo {:bar "bar"
                  :id :foo}
            :bar {:foo "foo"
                  :id :bar}}
           (sut/add-ids {:foo {:bar "bar"}
                         :bar {:foo "foo"}}))))
  (testing "Non map values are unchanged"
    (is (= {:foo "bar"
            :bar "foo"}
           (sut/add-ids {:foo "bar"
                         :bar "foo"}))))
  (testing "Empty maps are ok" (is (= {} (sut/add-ids {})))))

(deftest update-kw-test
  (testing "Update is ok, non selected keys are excluded"
    (is (= {:foo "arg"
            :foo2 :bar2}
           (sut/update-kw {:foo :bar
                           :foo2 :bar2}
                          [:foo]
                          (fn [_] "arg")))))
  (testing "No keyword doesn't modify the map"
    (is (= {:foo :bar
            :foo2 :bar2}
           (sut/update-kw {:foo :bar
                           :foo2 :bar2}
                          []
                          (fn [_] "arg"))))))
