(ns automaton-core.adapters.deps-edn-test
  (:require
   [automaton-core.adapters.deps-edn :as sut]
   [clojure.test :refer [deftest is testing]]))

(deftest is-hephaistox-deps-test
  (testing "Hephaistox lib find"
    (is (sut/is-hephaistox-deps ['hephaistox/automaton-core
                                 {:mvn/version ""}])))
  (testing "Non hephaistox lib is skipped"
    (is (not (sut/is-hephaistox-deps [:is-a-dep {:mvn/version ""}])))))

(deftest hephaistox-deps-test
  (testing "Hephaistox deps are selected"
    (is (= ['hephaistox/automaton-core]
           (sut/hephaistox-deps {:deps {'hephaistox/automaton-core {}
                                        'clojure.core {}}})))))

(deftest update-dep-local-root-test
  (testing "Not concerned deps are unchanged"
    (is (= {} (sut/update-dep-local-root "" {})))
    (is (= nil (sut/update-dep-local-root "" nil)))
    (is (= {:foo :bar} (sut/update-dep-local-root "" {:foo :bar}))))
  (testing "local roots are updated"
    (is (= {:local/root "../../automaton/"}
           (sut/update-dep-local-root ".." {:local/root "../automaton"})))
    (is (= {:local/root "../../automaton/"
            :foo :bar}
           (sut/update-dep-local-root ".."
                                      {:local/root "../automaton"
                                       :foo :bar})))))

(deftest update-alias-local-root-test
  (testing "No modification if the alias contains no local/root"
    (is (= {:extra-deps {}
            :deps {}}
           (sut/update-alias-local-root ".."
                                        {:extra-deps {}
                                         :deps {}})))
    (is (= {:deps {}} (sut/update-alias-local-root ".." {:deps {}})))
    (is (= {:extra-deps {}}
           (sut/update-alias-local-root ".." {:extra-deps {}}))))
  (testing "deps and extra-deps local-root's are updated "
    (is (= {:deps {'my-lib1 {:local/root "../../my-lib1/"
                             :foo :bar}}
            :extra-deps {'my-lib2 {:local/root "../../my-lib2/"
                                   :bar :foo}}}
           (sut/update-alias-local-root
            ".."
            {:deps {'my-lib1 {:local/root "../my-lib1"
                              :foo :bar}}
             :extra-deps {'my-lib2 {:local/root "../my-lib2"
                                    :bar :foo}}})))))

(deftest update-aliases-local-root-test
  (testing "All aliases are updated"
    (is (= {:alias-1 {:extra-deps {'lib1 {:local/root "../../lib1/"}}
                      :deps {'lib2 {:local/root "../../lib2/"}}}
            :alias-2 {:extra-deps {'lib2 {:local/root "../../lib2/"}}
                      :deps {'lib3 {:local/root "../../../lib3/"}}}}
           (sut/update-aliases-local-root
            ".."
            {:alias-1 {:extra-deps {'lib1 {:local/root "../lib1"}}
                       :deps {'lib2 {:local/root "../lib2"}}}
             :alias-2 {:extra-deps {'lib2 {:local/root "../lib2"}}
                       :deps {'lib3 {:local/root "../../lib3"}}}})))))

(deftest update-deps-edn-local-root-test
  (testing
    "Comprehensive test with all use case of local/root to update in the deps.edn file"
    (is (= {:aliases {:alias-1 {:extra-deps {'lib1 {:local/root "../../lib1/"}}
                                :deps {'lib2 {:local/root "../../lib2/"}}}
                      :alias-2 {:extra-deps {'lib2 {:local/root "../../lib2/"}}
                                :deps {'lib3 {:local/root "../../../lib3/"}}}}
            :deps {'lib5 {:local/root "../../lib5/"}}}
           (sut/update-deps-edn-local-root
            ".."
            {:aliases {:alias-1 {:extra-deps {'lib1 {:local/root "../lib1"}}
                                 :deps {'lib2 {:local/root "../lib2"}}}
                       :alias-2 {:extra-deps {'lib2 {:local/root "../lib2"}}
                                 :deps {'lib3 {:local/root "../../lib3"}}}}
             :deps {'lib5 {:local/root "../lib5"}}})))))
