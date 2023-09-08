(ns automaton-core.i18n.translate.protocol
  "Protocol for text translation logic.")

(defprotocol Translate
  (create-dictionary [this dict] "Returns a map structure of the dictionary")
  (-translate [this dictionary lang text] "Translation function that returns translated text based on passed dictionary, language and text to translate"))

(defn translate
  "This function is used to enable passing variadic arguments to protocol translate fn, as it's unsupported."
  [this dictionary lang & text]
  (-translate this dictionary lang text))
