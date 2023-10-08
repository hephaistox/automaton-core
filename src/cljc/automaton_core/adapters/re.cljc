(ns automaton-core.adapters.re
  "Adapter to regular expressions
  Seamless access to regular expressions between cljs and clj
  Managing both back and frontend to have a seamless experience"
  #?(:cljs (:require [automaton-core.adapters.string :as aas])))

(def starts-a-string
  #?(:clj "\\A"
     :cljs "^"))

(def ends-a-string
  #?(:clj "\\z"
     :cljs "$"))

(defn stringify
  [re]
  #?(:clj (str re)
     :cljs (if (string? re)
             re
             (aas/remove-first-last-character (str re)))))

(defn assemble-re
  "Assemble regular expressions together
  Params:
  * `res` collection of regular expressions"
  [res]
  (->> (map stringify res)
       (apply str)
       re-pattern))

(defn full-sentence-re
  "Transform the re to match a whole string
  Is compatible both with clj and cljs"
  [re]
  (->> [starts-a-string (stringify re) ends-a-string]
       (apply str)
       re-pattern))

(defn assemble-re-optional
  "Assemble regular expressions
  Params:
  * `res` is a list of regular expressions (as #\"\" or string) and boolean, the boolean tells if the re is optional
  * `prefix` (Optional, default = \"\")
  * `suffix` (Optional, default = \"\")"
  ([res prefix suffix]
   (loop [[re optional? & nexts] res
          assembled ""]
     (let [re (stringify re)
           assembled-re (str assembled
                             (if optional?
                               (str re "?")
                               re))]
       (if nexts
         (recur nexts
                assembled-re)
         (re-pattern (str prefix assembled-re suffix))))))
  ([res]
   (assemble-re-optional res nil nil)))
