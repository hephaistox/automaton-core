(ns automaton-core.adapters.edn-utils
  "Edn file manipulation"
  (:require [automaton-core.os.code-formatter :as code-formatter]
            [automaton-core.adapters.files :as files]
            [automaton-core.log :as core-log]
            [automaton-core.utils.uuid-gen :as uuid-gen]
            [clojure.edn :as edn]))

(defn is-clojure-like-file
  "Returns true if the file's extension is clojure like"
  [filename]
  (boolean (re-find #"\.clj[sc]?$" (or filename ""))))

(defn read-edn
  "Read the `.edn` file,
  Params:
  * `edn-filename` name of the edn file to load
  * `loader-fn` (Optional, default: files/read-file) a function returning the content of the file

  Errors:
  * throws an exception if the file is not found
  * throws an exception if the file is a valid edn
  * `file` could be a string representing the name of the file to load
  or a (io/resource) object representing the name of the file to load"
  ([edn-filename loader-fn]
   (let [edn-filename (files/absolutize edn-filename)
         edn-content (try (loader-fn edn-filename)
                          (catch Exception _ (core-log/warn (format "Unable to load the file `%s`" edn-filename))))]
     (try (edn/read-string edn-content)
          (catch Exception e (core-log/warn-exception e (format "File `%s` is not an edn" edn-filename)) nil))))
  ([edn-filename] (read-edn edn-filename files/read-file)))

(defn spit-edn
  "Spit the `content` in the edn file called `deps-edn-filename`.
  If any, the header is added at the top of the file
  Params:
  * `edn-filename` Filename
  * `content` What is spitted
  * `header` the header that is added to the content, followed by the timestamp - is automatically preceded with ;;
  Return the content of the file"
  ([edn-filename content header]
   (try (core-log/trace-format "Spit edn file: `%s`" edn-filename)
        (->> content
             (code-formatter/format-content header)
             (files/write-file edn-filename))
        content
        (catch Exception e
          (core-log/warn-exception (ex-info "Impossible to update the .edn file"
                                            {:deps-edn-filename edn-filename
                                             :content content
                                             :caused-by e})))))
  ([deps-edn-filename content] (spit-edn deps-edn-filename content nil)))

(defn update-edn-content
  "Update the edn file content with the `params-to-merge` map
  Returns the content of the file
  Params:
  * `edn-filename` the name of the file to update
  * `update-fn` function updating the content (fn [edn-content] (assoc edn-content ...))
  * `header` optional is a string added at the top of the file
  Note: the content will be formatted thanks to `automaton.core.adapters.code-formatter`
  "
  ([edn-filename update-fn header] (let [bb-config (read-edn edn-filename)] (spit-edn edn-filename (update-fn bb-config) header)))
  ([edn-filename update-fn] (update-edn-content edn-filename update-fn nil)))

(defn create-tmp-edn
  "Create a temporary file directory string with edn extension"
  []
  (let [edn-file (files/create-file-path (files/create-temp-dir) (str (uuid-gen/time-based-uuid) ".edn"))]
    (files/create-dirs (files/extract-path edn-file))
    edn-file))
