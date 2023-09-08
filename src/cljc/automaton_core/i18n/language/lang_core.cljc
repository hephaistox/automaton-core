(ns automaton-core.i18n.language.lang-core
  "This namespace is for reducing boilerplate code and already predefine which implementation of language protocol is used. If needed, can be replaced per customer app."
  (:require
   [automaton-core.i18n.language.primary-language :as primary-language]
   [automaton-core.i18n.utils :as i18n-utils]
   [automaton-core.log :as log]))

(defn start-lang []
  (log/info "Starting language component")
  (let [lang (primary-language/->PrimaryLanguage i18n-utils/french)]
    (log/trace "Language component is started")
    lang))

(def lang-state
  (atom (start-lang)))
