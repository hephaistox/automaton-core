(ns automaton-core.utils.type-arg
  "Force arguments to comply to a protocol

  Design decision
  * Arguments typed can be asserted
     * Rationale:
         * By default functional programming does not require typing of arguments. But the assembly of many different components may lead to difficult debugging, as the failing will occur deep in the call stack. Without a good understanding of the implementation, the error's understanding may be difficult. So the solution is to assert the arguments type.
         * This is concept usually used in Object Oriented Programming, but Hephaistox believes OOP's principle are useful, the point is to apply it where we need it, and not everywhere as most of the OOP language requires.
         * We believe our components assembly solutions will need these assertion to robustify the code and accelerate the development cycle
     * Consequences:
        * the asserts functions below should be called by the implementations build function
        * if the argment is not compliant, it is explicitly refused and the build function returns nil
     * Limit
        * The assert is time consuming during optimization phase. So it is done only on development environment, through a macro mechanism which is skipping that assertion implementation
        * The `this` argument of a `defrecord` (i.e. the first argument) couldn't be tested in the implementation as clojure needs a valid object to be able to call the method on it.
        * As described below, this mechanism if implemented for clojure compiler only

    * Assert-protocol is not implemented on clojurescript
     * Rationale: The solution is based on `extends?` which is not compatible with clojurescript as for now (cf. [clojurescript doc](https://clojurescript.org/about/differences#_protocol))
     * Consequence:
         * This tests will only be checked during clojure test, which is not an issue if that assemblies are done in cljc side
         * All assert will return true on clojurescript
         * The `assert-protocol` function has a `:unused-binding` flag to prevent kondo warnings"
  #?(:clj (:require
           [automaton-core.configuration :as core-conf]
           [automaton-core.log           :as core-log])))

#?(:clj (defmacro assert-protocols
          "Assert the `args` to check if they all match the expected type"
          [caller-fn-name asserts & body]
          (if (core-conf/read-param [:test-type-arg?] false)
            `(do ~@body)
            `(when (->> (for [[expected-type# arg#] ~asserts]
                          (cond
                            (nil? arg#) (do (core-log/error-format
                                             "Protocol `%s`, expects `%s` - found `nil`"
                                             ~caller-fn-name
                                             (:on expected-type#))
                                            true)
                            (not (extends? expected-type# (type arg#)))
                            (do (core-log/error-format "Protocol `%s` expects `%s` - found `%s`"
                                                       ~caller-fn-name
                                                       (:on expected-type#)
                                                       (type arg#))
                                true)
                            :else nil))
                        (every? nil?))
               ~@body)))
   :cljs (defn assert-protocols [_ _ body & _] body))

#?(:clj
     (defmacro assert-positive-integer
       "Test if the parameter is a positive Interger
  Params:
  * `val` value to test
  * `msg` message to display, the `%s` in the string is replaced with the value"
       [val msg & body]
       (if (core-conf/read-param [:test-type-arg?] false)
         `(do ~@body)
         `(if (and (int? ~val) (or (zero? ~val) (pos? ~val)))
            (do ~@body)
            (core-log/error-format ~msg ~val))))
   :cljs (defn assert-positive-integer [_ _ body & _] body))
