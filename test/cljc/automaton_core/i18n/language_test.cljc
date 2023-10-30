(ns automaton-core.i18n.language-test
  (:require [automaton-core.i18n.language :as sut]
            #?(:clj [clojure.test :refer [deftest is testing]]
               :cljs [cljs.test :refer [deftest is testing] :include-macros
                      true])))

(deftest merge-languages-map-test
  (testing "Non selected languages are removed"
    (is (= {:fr {:id :fr}}
           (sut/merge-languages-map {:fr {}, :en {}} {:fr {}}))))
  (testing "Supplementary key values are copied"
    (is (= {:fr {:lvl 2, :id :fr, :lvl2 3}}
           (sut/merge-languages-map {:fr {:lvl 1}, :en {:lvl 1}}
                                    {:fr {:lvl 2, :lvl2 3}}))))
  (testing "Non existing languages in previous steps are removed"
    (is (= {}
           (sut/merge-languages-map {:fr {:lvl 1}, :en {:lvl 1}} {:pl {}})))))

(deftest selected-languages-test
  (testing "All language data are retrieved"
    (is (= {:fr {:core-dict? true, :ui-text "FR", :desc "Français", :id :fr}}
           (:languages (sut/make-automaton-core-languages {:fr {}})))))
  (testing "Keys specified are found"
    (is (= :bar
           (-> (sut/make-automaton-core-languages {:fr {:foo :bar}})
               :languages
               :fr
               :foo))))
  (testing "Multiple maps arguments are merged "
    (is (= {:fr {:lvl 2, :id :fr}}
           (sut/merge-languages-map {:fr {:lvl 1}, :en {:lvl 1}}
                                    {:fr {:lvl 2}})))))

(deftest get-lang-test
  (testing "Returning a language"
    (let [lang (sut/make-automaton-core-languages {:fr {:lvl 1}, :en {:lvl 1}}
                                                  {:fr {:lvl 2}})]
      (is (map? (sut/language lang :fr))))))
