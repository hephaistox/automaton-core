(ns automaton-core.app.compose-deps-edn.compose-deps-edn-impl-test
  (:require [automaton-core.app.compose-deps-edn.compose-deps-edn-impl :as sut]
            [clojure.test :refer [deftest is testing]]))

(deftest create-test-alias-src-path-test
  (testing "No app or no alias leads to empty src paths" (is (= [] (sut/create-test-alias-src-path [{:app-dir ""}]))))
  (testing "App dir is added to src-paths of the test alias"
    (is (= ["foo/test/clj/" "foo/test/cljc/"]
           (sut/create-test-alias-src-path [{:app-dir "foo"
                                             :deps-edn {:aliases {:common-test {:extra-paths ["test/clj" "test/cljc"]}}}}]))))
  (testing "Two apps are concatenated"
    (let [res (sut/create-test-alias-src-path [{:app-dir "foo"
                                                :deps-edn {:aliases {:common-test {:extra-paths ["test/clj" "test/cljc"]}}}}
                                               {:app-dir "bar"
                                                :deps-edn {:aliases {:common-test {:extra-paths ["test/clj"]}}}}])]
      (is (vector? res))
      (is (= #{"foo/test/clj/" "foo/test/cljc/" "bar/test/clj/"} (set res))))))

(deftest aliases-test
  (testing "Nil values are ok" (is (= {:env-development-test {:main-opts ()}} (sut/aliases "" #{} nil))))
  (testing "One alias is ok"
    (is (= {:env-development-test {:main-opts ()}
            :foo {:foo2 :bar}}
           (sut/aliases "."
                        #{}
                        [{:app-dir "."
                          :app-name "monorepo-app"
                          :deps-edn {:aliases {:foo {:foo2 :bar}}}}])))))

(deftest compare-deps-test
  (testing "First one is lower"
    (is (= {:mvn/version "0.1.3"} (sut/compare-deps {:mvn/version "0.1.2"} {:mvn/version "0.1.3"})))
    (is (= {:mvn/version "0.2.0"} (sut/compare-deps {:mvn/version "0.1.2"} {:mvn/version "0.2.0"})))
    (is (= {:mvn/version "1.0.0"} (sut/compare-deps {:mvn/version "0.1.2"} {:mvn/version "1.0.0"}))))
  (testing "Are identical" (is (= {:mvn/version "0.1.2"} (sut/compare-deps {:mvn/version "0.1.2"} {:mvn/version "0.1.2"}))))
  (testing "Second one is lower" (is (= {:mvn/version "0.1.4"} (sut/compare-deps {:mvn/version "0.1.4"} {:mvn/version "0.1.3"})))))

(deftest update-deps-test
  (testing "Empty apps create no deps" (is (= {} (sut/update-deps #{} [{:app-dir "."} {:app-dir "foo"}]))))
  (testing "Some lib are found"
    (is (= {'mount/mount #:mvn{:version "0.1.1"}}
           (sut/update-deps #{}
                            [{:app-dir ""
                              :deps-edn {:aliases {:foo {:extra-deps {'mount/mount {:mvn/version "0.1.1"}}}}}} {:app-dir "foo"}]))))
  (testing "The higher version is kepts when conflicts are found"
    (is (= {'mount/mount #:mvn{:version "0.1.2"}}
           (sut/update-deps #{}
                            [{:app-dir ""
                              :deps-edn {:aliases {:foo {:extra-deps {'mount/mount {:mvn/version "0.1.1"}}}}}}
                             {:app-dir "foo"
                              :deps-edn {:aliases {:foo {:extra-deps {'mount/mount {:mvn/version "0.1.2"}}}}}}])))
    (is (= {'mount/mount #:mvn{:version "0.1.3"}}
           (sut/update-deps #{}
                            [{:app-dir ""
                              :deps-edn {:aliases {:foo {:extra-deps {'mount/mount {:mvn/version "0.1.3"}}}}}}
                             {:app-dir "foo"
                              :deps-edn {:aliases {:foo {:extra-deps {'mount/mount {:mvn/version "0.1.2"}}}}}}])))))
