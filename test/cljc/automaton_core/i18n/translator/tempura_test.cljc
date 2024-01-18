(ns automaton-core.i18n.translator.tempura-test
  (:require
   #?(:clj [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer [deftest is testing] :include-macros true])
   [automaton-core.i18n.language :as core-lang]
   [automaton-core.i18n.translator :as core-translator]
   [automaton-core.i18n.translator.tempura :as sut]))

(def dict-stub
  {:en {:foo "bar-en"}
   :fr {:foo "bar-fr"}})

(deftest tempura-missing-text-test
  (testing "All languages in the dictionary have a missing key"
    (is (= core-lang/dict-core-languages-ids
           (set (keys sut/tempura-missing-text))))
    (is (= core-lang/dict-core-languages-ids
           (->> (filter (fn [[_k v]] (:missing v)) sut/tempura-missing-text)
                (map first)
                set)))))

(deftest TempuraTranslator-test
  (testing "Is the specified language working"
    (is (= (get-in sut/tempura-missing-text [:en :missing])
           (let [test-translator
                 (sut/make-translator [:fr] sut/tempura-missing-text dict-stub)]
             (core-translator/translate test-translator [:en] :missing []))))
    (is (= (get-in sut/tempura-missing-text [:fr :missing])
           (let [test-translator
                 (sut/make-translator [:en] sut/tempura-missing-text dict-stub)]
             (core-translator/translate test-translator [:fr] :missing [])))))
  (testing
    "If no language is specified during translation, is the default language used?"
    (is (= (get-in sut/tempura-missing-text [:fr :missing])
           (let [test-translator
                 (sut/make-translator [:fr] sut/tempura-missing-text dict-stub)]
             (core-translator/translate test-translator [] :missing []))))
    (is (= (get-in sut/tempura-missing-text [:en :missing])
           (let [test-translator
                 (sut/make-translator [:en] sut/tempura-missing-text dict-stub)]
             (core-translator/translate test-translator [] :missing [])))))
  (testing "Are the dictionaries found"
    (let [translator
          (sut/make-translator [:en] sut/tempura-missing-text dict-stub)]
      (is (= "bar-en"
             (-> translator
                 (core-translator/translate [] :foo []))
             (-> translator
                 (core-translator/translate [:en] :foo []))))
      (is (= "bar-fr"
             (-> (sut/make-translator [:en] sut/tempura-missing-text dict-stub)
                 (core-translator/translate [:fr] :foo [])))))))
