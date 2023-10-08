(ns automaton-core.log.be-log-test
  (:require
   [automaton-core.log :as l]
   [automaton-core.log.be-log :as sut]
   [automaton-core.log.registry]
   [automaton-core.log.be-registry]
   [automaton-core.log.tracking.be-error-tracking]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [automaton-core.log.impl.log4j2]))

(deftest log-test
  (testing "Log expand to text-based log4j2. Meaning the strategy applied at compile time, and as expected"
    (is (= (macroexpand `(l/trace "Hey"))
           (macroexpand `(sut/log [:automaton-core.log.registry/text-based
                                   :automaton-core.log.registry/error-tracking] :trace "Hey"))))
    (is (= `((juxt
              ~automaton-core.log.be-registry/no-op-fn)
             ~*ns*
             :trace "Hey" "Ho")
           (macroexpand `(sut/log [:automaton-core.log.registry/no-op] :trace "Hey" "Ho")))))
  (testing "Fatal expand to print. Meaning the strategy applied at compile time, and as expected"
    (is (= `((juxt
              ~automaton-core.log.impl.log4j2/log-fn
              ~automaton-core.log.tracking.be-error-tracking/log-fn)
             ~*ns* :fatal "Hey")
           (macroexpand `(l/fatal "Hey")))))
  (testing "Fatal is happening and printing the expected string"
    (is (= "Hey\n"
           (with-out-str
             (sut/log [:automaton-core.log.registry/print] :fatal "Hey")))))
  (testing "Trace is happening and printing the expected string"
    (is (= "Hey\n"
           (with-out-str
             (sut/log [:automaton-core.log.registry/print] :trace "Hey"))))))

(deftest fatal-exception-test
  (testing "Printed strategy applies to exception when fatal level is caught\n"
    (is (str/starts-with? (with-out-str
                            (sut/log-exception [:automaton-core.log.registry/print]
                                               :fatal
                                               (ex-info "msg"
                                                        {:data :foo})))
                          "#error {\n :cause msg"))))

(deftest fatal-format-test
  (testing "Expansion"
    (is (=
         `((juxt
            ~automaton-core.log.be-registry/simple-print)
           ~*ns*
           :fatal
           (format "Hello %s and %s" "mati" "anthony"))
         (macroexpand `(sut/log-format
                        [:automaton-core.log.registry/print]
                        :fatal
                        "Hello %s and %s" "mati" "anthony")))))
  (testing "Printed strategy applies to exception when fatal level is caught"
    (is (= "Hello mati and anthony\n"
           (with-out-str
             (sut/log-format
              [:automaton-core.log.registry/print]
              :fatal
              "Hello %s and %s" "mati" "anthony"))))))
