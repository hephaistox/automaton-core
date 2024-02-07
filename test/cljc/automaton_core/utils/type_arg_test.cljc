(ns automaton-core.utils.type-arg-test
  (:require
   #?@(:clj [[automaton-core.utils.type-arg :as sut]
             [clojure.test :refer [deftest is testing]]])))

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

#?(:clj
     (deftest assert-type-test
       (testing "Assert accepts no args"
         (is (= 3 (sut/assert-protocols "Should not be displayed" [] 3))))
       (testing "Assert refuses a nil value"
         (is (nil?
              (sut/assert-protocols "Should not be displayed" [[Bar nil]] 3))))
       (testing "Successful assert"
         (is
          (= 3 (sut/assert-protocols "Should not be displayed" [[Bar foo]] 3))))
       (testing "Failing assert"
         (is (nil? (sut/assert-protocols "This assertion is expected to fail"
                                         [[Bar 10.0]]
                                         5))))
       (testing "If only one arg fail, all of them fail"
         (is (nil? (sut/assert-protocols "This assertion is expected to fail"
                                         [[Bar 10.0]]
                                         [Bar foo]
                                         4))))))
