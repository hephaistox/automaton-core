(ns automaton-core.adapters.edn-utils
  "Edn file manipulation"
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]

   [automaton-core.adapters.files :as files]
   [automaton-core.adapters.log :as log]
   [automaton-core.adapters.time :as time]))

(defn read-edn
  "Read the `.edn` file,
  * throws an exception if the file is not found
  * throws an exception if the file is a valid edn
  * `file` could be a string representing the name of the file to load
  or a (io/resource) object representing the name of the file to load"
  ([edn-file-name loader-fn]
   (let [edn-file-name (files/absolutize edn-file-name)]
     (log/trace "Load file:" edn-file-name)
     (let [edn-content (try
                         (loader-fn edn-file-name)
                         (catch Exception e
                           (throw (ex-info (format "Unable to load the file `%s`" edn-file-name)
                                           {:caused-by e
                                            :file-name edn-file-name}))))]
       (try
         (edn/read-string edn-content)
         (catch Exception e
           (throw (ex-info (format "File `%s` is not an edn" edn-file-name)
                           {:caused-by e
                            :file-name edn-file-name})))))))
  ([edn-file-name]
   (read-edn edn-file-name slurp)))

(defn read-edn-or-nil
  "Read the `.edn` file,
  * return nil if the file does not exist or is invalid
  * `file` could be a string representing the name of the file to load
  or a (io/resource) object representing the name of the file to load"
  ([edn-file-name loader-fn]
   (try
     (read-edn edn-file-name loader-fn)
     (catch Exception _
       nil)))
  ([edn-file-name]
   (read-edn-or-nil edn-file-name slurp)))

(defn spit-edn
  "Spit the `content` in the edn file called `deps-edn-filename`.
  If any, the header is added at the top of the file"
  ([edn-filename content header]
   (try
     (spit edn-filename
           (str/join [(with-out-str
                        (when header
                          (println header (time/now-str))))
                      content]))
     content
     (catch Exception e
       (throw (ex-info "Impossible to rename"
                       {:deps-edn-filename edn-filename
                        :content content
                        :exception e})))))
  ([deps-edn-filename content]
   (spit-edn deps-edn-filename content nil)))

(defn update-edn-content
  "Update the edn file content with the `params-to-merge` map"
  ([edn-filename update-fn header]
   (let [bb-config (read-edn edn-filename)]
     (spit-edn edn-filename
               (update-fn bb-config)
               header)))
  ([edn-filename update-fn]
   (update-edn-content edn-filename update-fn nil)))
