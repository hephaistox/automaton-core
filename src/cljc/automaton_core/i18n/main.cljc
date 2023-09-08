(ns automaton-core.i18n.main
  (:require
   [automaton-core.i18n.language.lang-core :as language-core]
   [automaton-core.i18n.language.protocol :as lang-prot]
   [automaton-core.i18n.translate.protocol :as trans-prot]
   [automaton-core.i18n.translate.translate-core :as translate-core]
   [automaton-core.utils.url :as url]))

(defn path-language
  "Get language from url path. Return nil if it's not there"
  [pathname]
  (let [paths (url/split-slash pathname)
        lang (first (filter #(>= (count %) 2) paths))]
    (lang-prot/language-accepted @language-core/lang-state lang)))

(def automaton-translate
  "Automaton translation function instantiated, consists only of automaton dictionary. So that dictionary is not created each time."
  (partial trans-prot/translate @translate-core/translate-state (trans-prot/create-dictionary @translate-core/translate-state {})))
