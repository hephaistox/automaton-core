(ns automaton-core.i18n.language.primary-language
  "Primary language implementation of language protocol. Main-lang that is passed is defining which will be the primary one, but other languages may also be supported, just with lower priority."
  (:require [automaton-core.i18n.utils :as i18n-core]
            [automaton-core.i18n.language.protocol :as lang-prot]))

(defrecord PrimaryLanguage [main-lang]
  lang-prot/Language
  (language-accepted
    [_ lang]
    (case lang
      "fr" i18n-core/french
      ("en" "com") i18n-core/english
      nil))
  (default-language [_]
    main-lang)
  (choose-language
    [_ {:keys [path-lang cookies-lang other-lang]}]
    (if-let [path-language path-lang]
      path-language
      (if-let [cookies-language cookies-lang]
        cookies-language
        (if-let [other-language other-lang]
          other-language
          main-lang)))))
