(ns automaton-core.i18n.language
  (:require
   [automaton-core.i18n.main :as bil]
   [automaton-core.i18n.language.protocol :as i18n-prot]
   [automaton-core.i18n.utils :as i18n-core]
   [clojure.string :as cs]
   [automaton-core.i18n.language.lang-core :as language-core]))

(defn hostname
  "Get host from req headers"
  [req]
  (get (:headers req) "host"))

(defn tld->lang
  "Turns string into keyword for accepted languages.
   Returns keyword, defaults to english (:en)"
  [lang-str]
  (case lang-str
    "fr" i18n-core/french
    "com" i18n-core/english
    nil))

(defn tld-language
  "Returns language first two letters if it matches with TLD used in our website.
   com is mapped to english (en)
   If doesn't match returns nil"
  [host]
  (let [tld (last (cs/split (str host) #"\."))]
    (tld->lang tld)))

(defn accept-language
  "Get Accept-Language from req headers"
  [req]
  (get (:headers req) "accept-language"))

(defn browser-accept-language
  "Returns language first two letters if it matches with languages used in our website.
   If doesn't match returns nil"
  [accept-lang]
  (let [user-lang (str (first accept-lang) (second accept-lang))]
    (i18n-prot/language-accepted @language-core/lang-state user-lang)))

(defn cookies-language
  "Get cookies value under 'lang' key from req"
  [req]
  (:value (get (:cookies req) "lang")))

(defn select-language
  "Selects language based on request data.
   Decision is made in this order:
   path (e.g. /fr/) -> cookies -> browser Accept-Language -> TLD -> defaults to 'en'"
  [req]
  (let [path-lang (bil/path-language (:uri req))
        cookies-lang (cookies-language req)
        user-lang (browser-accept-language (accept-language req))
        tld-lang (tld-language (hostname req))
        other-lang (if user-lang
                     user-lang
                     tld-lang)]
    (i18n-prot/choose-language @language-core/lang-state {:path-lang path-lang
                                                          :cookies-lang cookies-lang
                                                          :other-lang other-lang})))

(defn atr
  "Automaton tr, it's called to translate text in automaton - using only automaton dictionary.
   If you want to translate text with custom dictionary you need to recreate automaton-translate and atr."
  [req & args]
  (apply
   bil/automaton-translate
   (select-language req)
   args))
