(ns automaton-core.i18n.language.protocol
  "Protocol for language related business logic")

(defprotocol Language
  (language-accepted [this lang] "Returns lang if it's in used languages otherwise nil. So if e.g. hispanic is not supported, nil will be returned.")
  (choose-language [this lang-map] "Returns language that is a prioritarised to use. Contains business logic of what is the order of actions to choose which language to use from.")
  (default-language [this] "Returns default language"))
