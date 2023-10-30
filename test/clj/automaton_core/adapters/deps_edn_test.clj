(ns automaton-core.adapters.deps-edn-test
  (:require [automaton-core.adapters.deps-edn :as sut]
            [automaton-core.adapters.files :as files]
            [clojure.test :refer [deftest is testing]]))

(def tmp-dir (files/create-temp-dir))

(deftest get-deps-filename-test
  (testing "Get deps is working"
    (is (string? (sut/get-deps-filename tmp-dir)))))

(deftest update-commit-id-test
  (testing "The commit id of a lib is updated when exists"
    (is (= {} (sut/update-commit-id 'foo "1234" {})))
    (is (= {:deps {'foo {:git/sha "1234"}}}
           (sut/update-commit-id 'foo "1234" {:deps {'foo {:git/sha ""}}})))))

(deftest extract-paths-test
  (testing "Extract paths from a deps.edn file"
    (is (= ["a" "b" "c" "d" "e" "f" "g"]
           (sut/extract-paths {:paths ["a" "b" "c"],
                               :aliases {:repl {:extra-paths ["d" "e"]},
                                         :runner {:extra-paths ["f" "g"]}}}
                              #{})))
    (is (= ["a" "b" "c" "d" "e" "f" "g"]
           (sut/extract-paths {:paths ["a" "b" "c"],
                               :aliases {:repl {:extra-paths ["d" "e"]},
                                         :runner {:extra-paths ["f" "g"]}}}))))
  (testing "Exclusion of aliases is working"
    (is (= ["a" "b" "c" "d" "e"]
           (sut/extract-paths {:paths ["a" "b" "c"],
                               :aliases {:repl {:extra-paths ["d" "e"]},
                                         :runner {:extra-paths ["f" "g"]}}}
                              #{:runner})))
    (is (= ["a" "b" "c" "f" "g"]
           (sut/extract-paths {:paths ["a" "b" "c"],
                               :aliases {:repl {:extra-paths ["d" "e"]},
                                         :runner {:extra-paths ["f" "g"]}}}
                              #{:repl}))))
  (testing "Dedupe works"
    (is (= ["a" "b" "c" "f" "g"]
           (sut/extract-paths {:paths ["f" "g"],
                               :aliases {:repl {:extra-paths ["d" "e" "f"]},
                                         :runner {:extra-paths ["a" "b" "c"
                                                                "f"]}}}
                              #{:repl})))))

(deftest extract-deps-test
  (testing "`:deps` key is extracted"
    (is (= [['org.clojure/clojure #:mvn{:version "1.11.1"}]]
           (sut/extract-deps {:deps {'org.clojure/clojure {:mvn/version
                                                             "1.11.1"}}}
                             #{}))))
  (testing "aliases are extracted"
    (is
      (= [['com.clojure-goes-fast/clj-memory-meter #:mvn{:version "0.2.1"}]
          ['foo/bar #:mvn{:version "1.1.0"}] ['bar/foo #:mvn{:version "1.1.0"}]]
         (sut/extract-deps
           {:deps {'com.clojure-goes-fast/clj-memory-meter {:mvn/version
                                                              "0.2.1"}},
            :aliases {:repl {:extra-deps {'foo/bar {:mvn/version "1.1.0"}}},
                      :runner {:extra-deps {'bar/foo {:mvn/version "1.1.0"}}}}}
           #{}))))
  (testing "aliases are excluded"
    (is (= [['com.clojure-goes-fast/clj-memory-meter #:mvn{:version "0.2.1"}]
            ['foo/bar #:mvn{:version "1.1.0"}]]
           (sut/extract-deps
             {:deps {'com.clojure-goes-fast/clj-memory-meter {:mvn/version
                                                                "0.2.1"}},
              :aliases {:repl {:extra-deps {'foo/bar {:mvn/version "1.1.0"}}},
                        :runner {:extra-deps {'bar/foo {:mvn/version
                                                          "1.1.0"}}}}}
             #{:runner})))))

(deftest remove-deps-test
  (testing "Remove one dep is working"
    (is (= {:deps {'com.clojure-goes-fast/clj-memory-meter #:mvn{:version
                                                                   "0.2.1"},
                   'bar/foo #:mvn{:version "1.1.0"}}}
           (sut/remove-deps {:deps {'com.clojure-goes-fast/clj-memory-meter
                                      #:mvn{:version "0.2.1"},
                                    'foo/bar #:mvn{:version "1.1.0"},
                                    'bar/foo #:mvn{:version "1.1.0"}}}
                            ['foo/bar])))
    (is (= {:deps {'com.clojure-goes-fast/clj-memory-meter #:mvn{:version
                                                                   "0.2.1"},
                   'foo/bar #:mvn{:version "1.1.0"}}}
           (sut/remove-deps {:deps {'com.clojure-goes-fast/clj-memory-meter
                                      #:mvn{:version "0.2.1"},
                                    'foo/bar #:mvn{:version "1.1.0"},
                                    'bar/foo #:mvn{:version "1.1.0"}}}
                            ['bar/foo]))))
  (testing "Remove unexisting deps"
    (is (= {:deps {'com.clojure-goes-fast/clj-memory-meter #:mvn{:version
                                                                   "0.2.1"},
                   'foo/bar #:mvn{:version "1.1.0"},
                   'bar/foo #:mvn{:version "1.1.0"}}}
           (sut/remove-deps {:deps {'com.clojure-goes-fast/clj-memory-meter
                                      #:mvn{:version "0.2.1"},
                                    'foo/bar #:mvn{:version "1.1.0"},
                                    'bar/foo #:mvn{:version "1.1.0"}}}
                            ['bar/foo2])))))
