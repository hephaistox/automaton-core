(ns automaton-core.utils.date "Utility function for date management")

(def year-format (java.text.SimpleDateFormat. "yyyy"))

(defn this-year
  ([date] (.format year-format date))
  ([] (this-year (java.util.Date.))))
