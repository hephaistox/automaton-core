(ns automaton-core.adapters.commands
  "Library to execute a set of commands"
  (:require [automaton-core.adapters.files :as files]
            [automaton-core.adapters.schema :as schema]
            [automaton-core.log :as log]
            [babashka.process :as process]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def size-command "Size of the command line to be managed, measured on mcbook pro" 185)

(def commands-schema [:vector [:or [:tuple [:vector [:string]] :map] [:tuple [:vector [:string]]]]])

(def glob-command-params
  "Command params globals"
  {:in :inherit
   :blocking? true})

(defn stream-execute-command*
  "Command processing function, that's redirecting output to a log fn as a stream, to allow information to be viewed during operation.

  It's additionally focused on both stderr and stdout as in some situations (like docker build that is outputing to stderr) you read from stdout it will block, but when you also want to read from stderr, you'll have to do the first in a future or so, to not block that.
  https://clojurians.slack.com/archives/CLX41ASCS/p1694600409401029"
  [command dir]
  (let [proc (process/process command
                              {:dir dir
                               :shutdown process/destroy-tree})]
    (future (with-open [rdr (io/reader (:out proc))]
              (binding [*in* rdr]
                (loop []
                  (when-let [line (read-line)] (log/trace line))
                  (when (or (.ready rdr) (.isAlive (:proc proc))) (recur))))))
    (with-open [rdr (io/reader (:err proc))]
      (binding [*in* rdr]
        (loop []
          (when-let [line (read-line)] (log/trace line))
          (when (or (.ready rdr) (.isAlive (:proc proc))) (recur)))))))

(defn execute-command*
  "execute-command core function that's used to process the command."
  [command dir out file in blocking?]
  (let [proc (process/process command
                              {:dir dir
                               :shutdown process/destroy-tree
                               :out out
                               :out-file file
                               :err out
                               :err-file file
                               :in in})]
    (if blocking? (process/check proc) proc)))

(defn execute-command
  "Execute a command, described with
  Params:
  * `command` - tokenize command
  * `cmd-params` list of parameters for that particular command
  * `cmds-params` list of parmeters set once for the global command"
  [[command cmd-params] cmds-params]
  (let [merged-params (merge glob-command-params cmd-params cmds-params)
        execute-command-params {:command command
                                :merged-params merged-params
                                :cmd-params cmd-params
                                :cmds-params cmds-params}
        {:keys [dir out in blocking? stream?]} merged-params
        filename out
        str-command (str/join " " command)
        file (if (or (nil? out) (= out :string)) nil (io/file filename))
        out (cond (= out :string) :string
                  out :write
                  :else :inherit)
        blocking? (or (= out :string) blocking?)]
    (files/create-dirs dir)
    (log/debug-format "%s - in directory `%s`" str-command (files/absolutize dir))
    (when (and (not= out :string) filename) (log/info "  -> output = " filename))
    (try (if stream? (stream-execute-command* command dir) (execute-command* command dir out file in blocking?))
         (catch java.io.IOException e (throw (ex-info (str "Directory does not exist") (merge {:exception e} execute-command-params))))
         (catch clojure.lang.ExceptionInfo e
           (let [{:keys [exit type]} (ex-data e)]
             (if (= type :babashka.process/error)
               (throw (ex-info (str "Command `" str-command "` failed on exit code " exit) (merge execute-command-params {:exception e})))
               (throw (ex-info (str "Command `" str-command "` failed ") (merge execute-command-params {:exception e}))))))
         (catch Exception e (throw (ex-info (str "Command `" str-command "` failed ") (merge execute-command-params {:exception e})))))))

(defn exec-cmds
  "Execute commands with their default parameters
   is a vector of two elements:
   * the first is a vector of string, each representing an argument on the cli
   * the second is the set of parameters, which allow to be specific for each command
  Params:
  * `commands` tokenize command"
  ([commands] (exec-cmds commands {}))
  ([commands default-params]
   (schema/schema-valid-or-throw commands-schema commands "Malformed command")
   (apply str (doall (for [command commands] (:out (execute-command command default-params)))))))
