(ns automaton-core.configuration.core-test
  (:require
   [clojure.test :refer [deftest is testing]]

   [mount.core :as mount]

   [automaton-core.configuration.core :as sut]))

(deftest check-conf-working
  (testing "Check test environment is dev"
    (let [_ (mount/only #{#'automaton-core.configuration.core/conf-state})]
      (is (= :dev
             (sut/read-param [:env]))))

    (mount/stop)))
