(ns automaton-core.adapters.build-config-test
  (:require
   [clojure.test :refer [deftest is testing]]

   [automaton-core.adapters.build-config :as sut]
   [automaton-core.adapters.files :as files]
   [automaton-core.adapters.edn-utils :as edn-utils]))

(deftest search-for-build-config-test
  (testing "At least current project should be found"
    (is (> (count (sut/search-for-build-config))
           0))))

(deftest spit-build-config-test
  (testing "Check spitted build config is found"
    (let [tmp-dir (files/create-temp-dir)
          content {:foo3 :bar3}]
      (sut/spit-build-config tmp-dir
                             content
                             ";; Hey!")
      (is (= content
             (edn-utils/read-edn-or-nil (files/create-file-path tmp-dir
                                                                sut/build-config-filename)))))))
