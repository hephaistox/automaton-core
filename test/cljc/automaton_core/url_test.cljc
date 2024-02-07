(ns automaton-core.url-test
  (:require
   #?@(:clj [[clojure.test :refer [deftest is testing]]]
       :cljs [[cljs.test :refer [deftest is testing] :include-macros true]])
   [automaton-core.url :as sut]))

(deftest extract-tld-from-host-test
  (testing "Find existing tld"
    (is (= "com" (sut/extract-tld-from-host "http://hephaistox.com")))
    (is (= "fr" (sut/extract-tld-from-host "http://hephaistox.fr")))
    (is (= "fr" (sut/extract-tld-from-host "hephaistox.fr"))))
  (testing "Compatible with ports"
    (is (nil? (sut/extract-tld-from-host "localhost:3000")))
    (is (= "com" (sut/extract-tld-from-host "http://hephaistox.com:3000")))
    (is (= "uk" (sut/extract-tld-from-host "hephaistox.co.uk")))
    (is (= "fr" (sut/extract-tld-from-host "http://hephaistox.fr"))))
  (testing "Compatible with multiple domaines"
    (is (= "fr"
           (sut/extract-tld-from-host "http://www.subdomain.hephaistox.fr"))))
  (testing "Localhost are compatible"
    (is (nil? (sut/extract-tld-from-host "localhost")))
    (is (nil? (sut/extract-tld-from-host "192.168.0.01")))))

(deftest compare-locations-test
  (testing "Exact same are accepted"
    (is (sut/compare-locations "http://www.hephaistox.com/foo'bar?lang=en"
                               "http://www.hephaistox.com/foo'bar?lang=en"))
    (is (sut/compare-locations "http://www.hephaistox.com/foo'bar?lang=en"
                               "http://www.hephaistox.com/foo'bar?lang=en"
                               "http://www.hephaistox.com/foo'bar?lang=en")))
  (testing "Exact same are discarded"
    (is (not (sut/compare-locations
              "http://www.hephaistox.com/foo'bar?lang=en"
              "http://www.hephaistox.com/foo'bar?lang=fr"
              "http://www.hephaistox.com/foo'bar?lang=en"))))
  (testing "Compare relative and fullpath"
    (is (sut/compare-locations "http://www.hephaistox.com/foo'bar?lang=en"
                               "/foo'bar?lang=en"))))

(deftest parse-queries-test
  (testing "Simple params"
    (is (= {:par "foo"
            :bar "barfoo"}
           (sut/parse-queries "?par=foo&bar=barfoo")))
    (is (= {:par ""} (sut/parse-queries "?par="))))
  (testing "No params"
    (is (nil? (sut/parse-queries "?")))
    (is (nil? (sut/parse-queries "")))
    (is (nil? (sut/parse-queries nil))))
  (testing "Complete url analysis"
    (is (= {:par "foo"
            :bar "barfoo"}
           (sut/parse-queries
            "http://hephaistox.com:3000?par=foo&bar=barfoo#foobar")))))
