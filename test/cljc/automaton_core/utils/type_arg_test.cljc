(ns automaton-core.utils.type-arg-test
  (:require
   [automaton-core.utils.type-arg :as sut]
   #?@(:clj [[clojure.test :refer [deftest is testing]]]
       :cljs [[cljs.test :refer [deftest is testing] :include-macros true]])))

(defprotocol Bar
  #_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
  (a [_])
  #_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
  (b [_]))

(defrecord Foo [int]
  Bar
    (a [_] :a)
    (b [_] :b))

(def foo (->Foo 11))

#?(:clj (macroexpand '(sut/assert-protocol Bar "Should not be displayed" [] 3)))

(deftest assert-type-test
  (testing "Assert accepts no args"
    (is (= 3 (sut/assert-protocols "Should not be displayed" [] 3))))
  (testing "Assert refuses a nil value"
    (let [res (sut/assert-protocols "Should be displayed" [[Bar nil]] 3)]
      #?(:clj (is (nil? res))
         :cljs (is res))))
  (testing "Successful assert"
    (let [res (sut/assert-protocols "Should not be displayed" [[Bar foo]] 3)] (is (= 3 res))))
  (testing "Failing assert"
    (let [res (sut/assert-protocols "This assertion is expected to fail" [[Bar 10.0]] 5)]
      #?(:clj (is (nil? res))
         :cljs (is res))))
  (testing "If only one arg fail, all of them fail"
    (let [res (sut/assert-protocols "This assertion is expected to fail" [[Bar 10.0]] [Bar foo] 4)]
      #?(:clj (is (nil? res))
         :cljs (is res)))))
