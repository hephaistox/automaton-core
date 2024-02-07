(ns automaton-core.user.account.datalog
  "Usefull datalog queries to be used with access protocol functions")

(defn add-account
  "Adding new account to db"
  [first-name last-name email]
  [[:db/add email :account/first first-name]
   [:db/add email :account/last last-name]
   [:db/add email :account/email email]])

(defn find-account-by-email
  "Returns all the information available on the account by it's email"
  [email]
  {:schema '[:find (pull ?e [*]) :in $ ?email :where [?e :account/email ?email]]
   :values [email]})

(defn remove-account
  "Removes all information about account by email"
  [account-email]
  [[:db/retractEntity [:account/email account-email]]])
