(ns automaton-core.i18n.language
  "All known languages in `automaton-core`, subsequent namespaces are forced to use only known languages there.
  Language concept could be enriched with language definition or other data, for instance, the flag of a language, ...

  It is only a place for language description, not to tell where it is supposed to be used
  See cust-app themselves to know what language they use"
  (:require [automaton-core.utils.map :as utils-map]
            [clojure.set :as set]))

(defprotocol Languages
  (language [this lang-id]
   "return the language\nParams:\n  * `lang-id`: language id")
  (languages [this]
   "return the list of known languages")
  (languages-ids [this]
   "Set of known ids (set of keywords)")
  (dict-languages-ids [this]
   "Set of known language ids keywords"))

(defrecord AutomatonCoreLanguages [languages]
  Languages
    (language [_ lang-id] (get languages lang-id))
    (languages [_] languages)
    (languages-ids [_] (set (keys languages)))
    (dict-languages-ids [_] (set (map first (filter (fn [[_ language]] (:core-dict? language)) languages)))))

(def ^:private core-languages-dict
  "Defines list of known language, the keys are internal identifiers and values are defined with:
  * `core-dict?` tells if that language should be used in dictionary of autamaton-core. This value is a decision, it will be used to check that the dictionary contains all of them
  * `ui-text` the text to show in UI as an identifier of the language.
  * `desc` description of the language"
  {:fr {:core-dict? true
        :ui-text "FR"
        :desc "Français"}
   :en {:core-dict? true
        :ui-text "EN"
        :desc "English"}
   :pl {:ui-text "PL"
        :desc "Polski"}})

(defn merge-languages-map
  "Merge maps in the argument order, so all their data are merged and the ones on the left are lower priority

  Each map is language definition
  Only languages existing in all dictionaries are kept,
  Params:
  * sequence of languages matching a language id to the language description"
  [& selected-languages-seq]
  (let [languages-ids (apply set/intersection (mapv (comp set keys) selected-languages-seq))
        languages (-> (apply utils-map/deep-merge (map #(select-keys % languages-ids) selected-languages-seq))
                      utils-map/add-ids)]
    languages))

(defn make-automaton-core-languages
  "Create a `Languages` instance
  Params:
  * `selected-languages` is a dictionary which can add data and restrict the use of some languages in subpart of the app
  The final map consists in the languages defined in both `selected-languages` `core-lang/base-languages`
  The language data map are merged, see `merge-languages-map` for details"
  [& selected-languages-seq]
  (->AutomatonCoreLanguages (apply merge-languages-map core-languages-dict selected-languages-seq)))

(def core-languages "Languages available in `automaton-core`, instance of `Languages`" (make-automaton-core-languages))

(def get-core-languages-id "Known language ids in `automaton-core`" (languages-ids core-languages))

(def dict-core-languages-ids "Known languages ids in `automaton-core`'s" (dict-languages-ids core-languages))
