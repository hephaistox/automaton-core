(ns automaton-core.i18n.translate.translate-core
  "This namespace is for reducing boilerplate code and already predefine which implementation of translate protocol is used. If needed, can be replaced per customer app."
  (:require
   [automaton-core.i18n.translate.tempura :as tempura]
   [automaton-core.log :as log]))

(defn start-translate []
  (log/info "Starting translation component")
  (let [trans (tempura/->TempuraTranslate {})]
    (log/trace "Translation component is started")
    trans))

(def translate-state
  (atom (start-translate)))
