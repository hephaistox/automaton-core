(ns automaton-core.adapters.bb-edn-test
  (:require
   [clojure.test :refer [deftest is testing]]

   [automaton-core.adapters.bb-edn :as sut]
   [automaton-core.adapters.files :as files]))

(deftest update-bb-edn-test
  (testing "Update edn keep `:init` and `:requires` keys, and add what is in tasks"
    (let [tmp-bb-dir (files/create-temp-dir)]
      (files/spit-file (files/create-file-path tmp-bb-dir sut/bb-edn-filename)
                       {:paths []
                        :deps {}
                        :tasks {:init "don't touch"
                                :requires "neither"
                                :a "should disappear"}})
      (is (= {:paths [],
              :deps {},
              :tasks
              {:init "don't touch",
               :requires "neither",
               'foo4 {:doc "Test", :task '(execute)}
               'foo2 {:doc "Test2", :task '(execute)}}}
             (sut/update-bb-edn  tmp-bb-dir
                                 {"foo4" {:cli-params-mode :none
                                          :doc "Test"
                                          :exec-task 'bar}
                                  "foo2" {:cli-params-mode :none
                                          :doc "Test2"
                                          :exec-task 'bar2}}))))))
