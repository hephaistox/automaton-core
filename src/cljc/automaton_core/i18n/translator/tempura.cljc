(ns automaton-core.i18n.translator.tempura
  "Implementation of `automaton-core.i18n.translator/Translator` protocol with [tempura](https://github.com/taoensso/tempura)

  Is an adapter for translation in `automaton-core`, so no web related technology is mentionned there, even if tempura provides them.
  It will be the job of `automton-web` to provide that features"
  (:require [automaton-core.i18n.dict.text :as core-dict]
            [automaton-core.i18n.translator :as core-translator]
            [automaton-core.log :as core-log]
            [automaton-core.utils.map :as utils-map]
            [taoensso.tempura :as tempura]))

(def tempura-missing-text
  "Necessary for tempura,  a missing key is expected for all languages marked with `:core-dict?` in `automaton-core.i18n.language`"
  {:en {:missing "The text is missing! :( Please let us know at info@hephaistox.com"}
   :fr {:missing "Le texte est manquant! :( Veuillez nous en informer à l'adresse info@hephaistox.com"}})

(defn- append-dictionaries
  "Appends dictionaries
  Params:
  * `dicts` list of dictionaries to append together, the default keys for missing keys and the core dictionary are defaulted"
  [dicts]
  (apply utils-map/deep-merge tempura-missing-text core-dict/dict dicts))

(defn create-opts
  "Create the options for tempura/tr
  Params:
  * `dicts` list of dictionaries to append together, the default keys for missing keys and the core dictionary are defaulted"
  [& dicts]
  (let [debug true ;; (= :dev (conf-core/read-param [:env]))
       ]
    {:dict (append-dictionaries dicts)
     :cache-dict? (not debug)
     :default-local :fr
     :cache-locales (not debug)}))

(defrecord TempuraTranslator [opts main-langs]
  core-translator/Translator
    (default-languages [_] main-langs)
    (translate [_ langs-id tr-id resources]
      (let [locales (vec (concat langs-id main-langs))
            translated-text (tempura/tr opts locales [tr-id] resources)]
        (core-log/trace "Translate key `" tr-id "`,with locales `" locales "`, -> `" translated-text "`")
        translated-text)))

(defn make-translator
  "Build a TempuraTranslator
  Params:
  * `dicts` ordered list of dictionaries, the last is lower priority. A least priority dictionary is added with missing keys for tempura in it. You can overide in your own app
  * `main-langs` language defaulted by tempura if not found is a list to accept locales"
  [main-langs & dicts]
  (-> (apply create-opts dicts)
      (->TempuraTranslator main-langs)))
