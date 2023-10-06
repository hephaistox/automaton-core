(ns automaton-core.i18n.translator
  "Protocol for translation logic")

(defprotocol Translator
  (default-languages [this] "sequence of ids of the default languages")
  (translate [this lang-ids tr-id resources] "translate the `:tr-id` with the resources as parameters (first resource is %1, second is %2, ...), trying to translate with first language lang-ids, then second, ..."))
