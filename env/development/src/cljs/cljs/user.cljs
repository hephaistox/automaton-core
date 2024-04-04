#_{:heph-ignore {:forbidden-words ["tap>"]}}
(ns cljs.user
  (:require
   [automaton-core.portal.client :refer [client-connect tst]]))

(comment
  (client-connect)
  (tst)
  ;
)
