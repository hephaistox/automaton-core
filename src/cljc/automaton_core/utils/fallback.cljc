(ns automaton-core.utils.fallback
  "Fallback utilties"
  (:require [automaton-core.log :as core-log])
  #?(:cljs (:require-macros [automaton-core.utils.fallback])))

(defn cljs-env? "Take the &env from a macro, and tell whether we are expanding into cljs." [env] (boolean (:ns env)))

(defmacro always-return
  "To be used with great caution, as this is not a good practice to ignore exceptions. This should be used only in if it's really important for some application part to always return the value and should be used as a last resort.
  Like e.g. it's used in not-found-page for it to be displayed without a problem and inform a user that something is wrong.
  `expr` fails returns `ret-val`"
  [expr-fn ret-val]
  (let [catch-level# (if (cljs-env? &env)
                       :default
                       #?(:clj Throwable
                          :cljs :default))
        ns (str *ns*)]
    `(try (~expr-fn)
          (catch ~catch-level# e#
            (core-log/error-exception (ex-info "Failed but defaulted to ret-val"
                                               {:error e#
                                                :data {:ret-val ~ret-val
                                                       :expr-fn ~expr-fn
                                                       :ns ~ns}}))
            ~ret-val))))
