(ns automaton-core.adapters.string "String manipulation usable both in clj and cljs")

(defn remove-last-character
  "Remove the last character of a string"
  [s]
  (let [s (str s)] (subs s 0 (max 0 (- (count s) 1)))))

(defn remove-first-last-character
  "Remove the first and last character of a string"
  [s]
  (let [s (str s) count-s (count s)] (if (< 2 count-s) (subs s 1 (max 0 (- (count s) 1))) "")))

(def ellipsis "...")

(defn limit-length
  "Limit the length of the string
  Params:
  * `s` string to limit
  * `limit` maximum numbers of character of the resulting string, with prefix and suffix included, with an ellipsis of string s if necessary
  * `on-ellipsis` a function executed when the ellipsis is done"
  ([s limit] (limit-length s limit nil nil identity))
  ([s limit prefix suffix on-ellipsis]
   (let [line (str prefix s suffix)]
     (if (<= (count line) limit)
       line
       (do (on-ellipsis s)
           (apply str
                  (concat prefix
                          (take (- limit (count ellipsis) (count prefix) (count suffix)) s)
                          ellipsis
                          suffix)))))))

(defn remove-trailing-character
  "Remove last character if it is matching char
  Params:
  * `s` string
  * `char` a character to compare to last character of `s`"
  [s char]
  (if (= char (last s))
    (subs s
          0
          (-> s
              count
              dec))
    s))
