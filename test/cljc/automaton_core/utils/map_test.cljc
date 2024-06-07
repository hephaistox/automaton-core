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

(deftest maps-to-k-test
  (is (= {:b {:a :b}
          :a {:c :d
              :a :a}}
         (sut/maps-to-key [{:a :b}
                           {:c :d
                            :a :a}]
                          :a)))
  (testing "Maps with no value for the key are removed."
    (is (= {:b {:a :b}
            :a {:c :d
                :a :a}}
           (sut/maps-to-key [{:a :b}
                             {:c :d
                              :a :a}
                             {}]
                            :a)))))

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

(deftest remove-nil-vals-test
  (testing "Remove nil values"
    (is (= {} (sut/remove-nil-vals nil)))
    (is (= {} (sut/remove-nil-vals {})))
    (is (= {} (sut/remove-nil-vals {:foo nil})))
    (is (= {:barfoo :foobar}
           (sut/remove-nil-vals {:foo nil
                                 :bar nil
                                 :barfoo :foobar})))))

(deftest remove-nil-submap-vals-test
  (testing "Remove nil sub values"
    (is (= {} (sut/remove-nil-submap-vals nil)))
    (is (= {:foobar :barfoo}
           (sut/remove-nil-submap-vals {:foo nil
                                        :foobar :barfoo})))
    (is (= {:foo {:bar :foo}}
           (sut/remove-nil-submap-vals {:foo {:bar :foo
                                              :foo nil}})))))

(deftest map-difference-test
  (testing "The same maps are returning no difference"
    (is (= (sut/map-difference {} {}) {}))
    (is (= (sut/map-difference {:a 1} {:a 1}) {}))
    (is (= (sut/map-difference {:a 1
                                :d 6
                                :b {:c 2
                                    :d 6}}
                               {:a 1
                                :b {:c 2
                                    :d 6}
                                :d 6})
           {}))
    (is (= (sut/map-difference {:a 2
                                :b 5}
                               {:b 5
                                :a 2})
           {})))
  (testing "Difference is found"
    (is (= (sut/map-difference {:a 2} {}) {:a 2}))
    (is (= (sut/map-difference {} {:a 2}) {:a 2}))
    (is (= (sut/map-difference {:a 1}
                               {:a 1
                                :b 2})
           {:b 2}))
    (is (= (sut/map-difference {:a 1
                                :d 6
                                :b {:c 2
                                    :d 6}}
                               {:a 1
                                :b {:c 2
                                    :d 5}
                                :d 6})
           {:b {:c 2
                :d 6}}))
    (is (= (sut/map-difference {:a 2
                                :b 5}
                               {:b 5
                                :a 3})
           {:a 2}))))

(deftest keys->sequence-number-test
  (testing "Happy path"
    (is (= {:a 1
            :b 2}
           (sut/keys->sequence-number {:a 12
                                       :b 15})
           (sut/keys->sequence-number [[:a 12] [:b 15]]))))
  (testing "Empty maps returns empty map"
    (is (= {}
           (sut/keys->sequence-number nil)
           (sut/keys->sequence-number {})
           (sut/keys->sequence-number [])))))

(deftest translation-keys-test
  (testing "Standard case"
    (is (= {1 12
            2 15}
           (sut/translate-keys {:a 12
                                :b 15}
                               {:a 1
                                :b 2}))))
  (testing "Missing translation are not modified"
    (is (= {:a 12
            :b 15}
           (sut/translate-keys {:a 12
                                :b 15}
                               {}))))
  (testing "Empty maps are ok"
    (is (= {} (sut/translate-keys nil {})))
    (is (= {} (sut/translate-keys {} nil)))
    (is (= {} (sut/translate-keys nil nil)))))

(deftest submap?
  (testing "submap is found correctly"
    (is (sut/submap? {:a {:b :B1}}
                     {:a {:b :B1
                          :c :C1}}))
    (is (sut/submap? {:c 5}
                     {:a {:b :B1}
                      :c 5}))
    (is (sut/submap? {:a 1} {:a 1}))
    (is (sut/submap? {} {})))
  (testing "submap is not found cases"
    (is (false? (sut/submap? {:a {:b :B1}} {:c 5})))
    (is (false? (sut/submap? nil {})))
    (is (false? (sut/submap? {:c 5} nil)))))

(deftest get-key-or-before-test
  (testing "Test if latest key is returned, even if doesn't exist"
    (is (nil? (sut/get-key-or-before (into (sorted-map)
                                           {1 :a
                                            32 :b
                                            2 :c})
                                     0)))
    (is (= 1
           (sut/get-key-or-before (into (sorted-map)
                                        {1 :a
                                         32 :b
                                         2 :c})
                                  1)))
    (is (= 1
           (sut/get-key-or-before (into (sorted-map)
                                        {1 :a
                                         32 :b
                                         2 :c})
                                  1.3)))
    (is (= 2
           (sut/get-key-or-before (into (sorted-map)
                                        {1 :a
                                         32 :b
                                         2 :c})
                                  2)))
    (is (= 2
           (sut/get-key-or-before (into (sorted-map)
                                        {1 :a
                                         32 :b
                                         2 :c})
                                  30)))
    (is (= 32
           (sut/get-key-or-before (into (sorted-map)
                                        {1 :a
                                         32 :b
                                         2 :c})
                                  32)))
    (is (nil? (sut/get-key-or-before (into (sorted-map) {}) 32)))))
