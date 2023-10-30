(ns automaton-core.i18n.dict.text-test
  (:require #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer [deftest is testing] :include-macros
                      true])
            [automaton-core.i18n.dict.text :as sut]
            [automaton-core.i18n.language :as core-lang]
            [automaton-core.i18n.missing-translation-report :as b-language]
            [clojure.set :as set]
            [clojure.string :as str]))

(deftest dict-test
  (testing
    (apply str
      "Dictionary is matching all expected languages. List of languages to expect: "
      (str/join " " core-lang/dict-core-languages-ids))
    (is (= []
           (b-language/key-with-missing-languages
             sut/dict
             core-lang/dict-core-languages-ids
             #{:tongue/missing-key}))))
  (testing "All languages are known languages"
    (is (empty? (set/difference (set (keys sut/dict))
                                core-lang/get-core-languages-id)))))
