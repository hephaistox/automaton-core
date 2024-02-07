(ns automaton-core.user.account.persistence
  "Account access entrypoint functions."
  (:require
   [automaton-core.storage.component :as storage-entry]
   [automaton-core.user.account.datalog :as queries]))

(defn add-account!
  [storage first-name last-name email]
  (storage-entry/upsert storage
                        (queries/add-account first-name last-name email)))

(defn find-account-by-email
  [storage email]
  (storage-entry/select storage (queries/find-account-by-email email)))

(defn remove-account!
  [storage email]
  (storage-entry/delete storage (queries/remove-account email)))

(comment
  ;; This will be moved to test
  (add-account! (:connection @storage-entry/storage-state)
                "Mateuszek"
                "Mazurczak"
                "kaspazza@gmail.com")
  (find-account-by-email (:connection @storage-entry/storage-state)
                         "kaspazza@gmail.com")
  (remove-account! (:connection @storage-entry/storage-state)
                   "kaspazza@gmail.com")
  ;;
)
