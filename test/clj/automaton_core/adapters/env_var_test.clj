(ns automaton-core.adapters.env-var-test
  (:require
   [automaton-core.adapters.env-variables :as sut]
   [clojure.test :refer [deftest is testing]]))

(deftest get-env-var-test
  (testing "Find the shell name in environment variable"
    (is (string? (sut/get-env "TERM")))))

(deftest get-env-vars-test
  (testing "Env vars are ok"
    (is (map? (sut/get-envs)))
    (is (< 2 (-> (sut/get-envs)
                 count)))))

(comment
  (sut/get-env "SHELL")
;
  )
