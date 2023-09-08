(ns automaton-core.i18n.translate.tempura
  "Tempura library implementation of Translate protocol."
  (:require
   [automaton-core.utils.map :as crusher]
   [automaton-core.i18n.translate.protocol :as prot]
   [automaton-core.i18n.dictionary :as automaton-dict]
   [taoensso.tempura :as tempura]))

(def tempura-missing-text
  {:en {:missing "The text is missing! :( Please let us know at info@hephaistox.com"}
   :fr {:missing "Le texte est manquant! :( Veuillez nous en informer à l'adresse info@hephaistox.com"}})

(defn tempura-tr
  [dictionary lang & text]
  (tempura/tr {:dict dictionary} (vector lang) (vector (first text))
              (vec (rest text))))

(defn translate-fn
  [& args]
  (apply tempura-tr args))

(defrecord TempuraTranslate [params]
  prot/Translate
  (create-dictionary [_ dict]
    (crusher/deep-merge
     tempura-missing-text
     automaton-dict/automaton-dictionary
     dict))
  (-translate [_ dictionary lang text]
    (apply translate-fn dictionary lang text)))
