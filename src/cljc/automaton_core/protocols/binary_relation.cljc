(ns automaton-core.protocols.binary-relation
  "Test registry elements for binary relation properties")

(defn- namespace-kw-to-operator-name
  "Return the operator name
  Params:
  * `x` a namespaced keyword"
  [x]
  (namespace (symbol x)))

(defn- pick-random-tuple
  "Returns a lazy sequence of `nb-iterations` of tuple of values pick in `values`
  Params:
  * `values` collection of values where to pick elements in
  * `tuple-size` Number of elements in the tuple to build (i.e. 2 returns pairs, 3 triples ....)
  * `nb-iterations` Number of tuple that a returnd"
  [values tuple-size nb-iterations]
  (repeatedly nb-iterations #((apply juxt (repeat tuple-size rand-nth)) values)))

(defn reflexive
  "Does the binary operator is reflexive? See [wikipedia article](https://en.wikipedia.org/wiki/Reflexive_relation)

  Params:
  * `binary-opeartor` to test
  * `values-to-test` values to test"
  [binary-operator values-to-test]
  {::reflexive
   {:msg (str "Operator " (namespace-kw-to-operator-name binary-operator) " is not reflexive")
    :result (->> values-to-test
                 (map #(binary-operator % %))
                 (filter false?))
    :expect-fn empty?}})

(defn strongly-connected
  "Is `binary-operator` strongly-connected , as defined in [wiki page](https://en.wikipedia.org/wiki/Connected_relation)
  Returns the list of pair of values in `values-to-test` which are not strongly connected

  Params:
  * `binary-operator`
  * `values-to-test` a set of values to test that we'll pick in
  * `nb-iterations` number of random pairs to pick in `values-to-test` to test the efficiency of it"
  [binary-operator values-to-test nb-iterations]
  {::totally-comparable
   {:msg
    (str "Operator " (namespace-kw-to-operator-name binary-operator) " is not totally comparable")
    :result (->> (pick-random-tuple values-to-test 2 nb-iterations)
                 (map (fn [[date1 date2]]
                        (if (or (binary-operator date1 date2) (binary-operator date2 date1))
                          false
                          [date1 date2])))
                 (filterv sequential?))
    :expect []}})

(defn transitive
  "Is `binary-operator` transitive, as defined in [wiki page](https://en.wikipedia.org/w/index.php?title=Transitive_relation&oldid=1178741565)
  Returns the list of triple of values in `values-to-test` for which transitivity property is not fullfilled

  Params:
  * `binary-operator`
  * `values-to-test` a set of values to test that we'll pick in
  * `nb-iterations` number of random pairs to pick in `values-to-test` to test the efficiency of it"
  [binary-operator values-to-test nb-iterations]
  {::totally-comparable
   {:msg
    (str "Operator " (namespace-kw-to-operator-name binary-operator) " is not totally comparable")
    :result (->> (pick-random-tuple values-to-test 3 nb-iterations)
                 (map (fn [triple]
                        (let [[date1 date2 date3] (sort (#?(:clj var-get
                                                            :cljs identity)
                                                         binary-operator)
                                                        triple)]
                          (if (binary-operator date1 date3) false [date1 date2 date3]))))
                 (filterv sequential?))
    :expect []}})

(defn asymmetric
  "Is the `binary-operator` asymetric, as defined in [wiki page](https://en.wikipedia.org/wiki/Asymmetric_relation)
  Returns the list of pair of values from `values-to-test` which are not fullfilling the asymmetric definition

  Params:
  * `binary-operator`
  * `values-to-test` a set of values to test that we'll pick in
  * `nb-iterations` number of random pairs to pick in `values-to-test` to test the efficiency of it"
  [binary-operator values-to-test nb-iterations]
  {::asymetric {:msg (str "Operator " binary-operator " is not asymetric")
                :result (->> (pick-random-tuple values-to-test 2 nb-iterations)
                             (map (fn [[date1 date2]]
                                    (if (not (and (binary-operator date1 date2)
                                                  (binary-operator date2 date1)))
                                      false
                                      [date1 date2])))
                             (filterv sequential?))
                :expect []}})

(defn symmetric
  "Is the `binary-operator` symetric, as defined in [wiki page](https://fr.wikipedia.org/wiki/Relation_sym%C3%A9trique)
  Returns the list of pair of values from `values-to-test` which are not fullfilling the symmetric definition

  Params:
  * `binary-operator`
  * `values-to-test` a set of values to test that we'll pick in
  * `nb-iterations` number of random pairs to pick in `values-to-test` to test the efficiency of it"
  [binary-operator values-to-test nb-iterations]
  {::asymetric {:msg (str "Operator " binary-operator " is not asymetric")
                :result (->> (pick-random-tuple values-to-test 2 nb-iterations)
                             (map (fn [[date1 date2]]
                                    (if (or (and (binary-operator date1 date2)
                                                 (not (binary-operator date2 date1)))
                                            (and (binary-operator date2 date1)
                                                 (not (binary-operator date1 date2))))
                                      [date1 date2]
                                      false)))
                             (filterv sequential?))
                :expect []}})

(defn antisymmetric
  "Is the `binary-operator` antisymmetric, as defined in [wiki page](https://en.wikipedia.org/wiki/Antisymmetric_relation)
  Returns the list of pair of values from `values-to-test` which are not fullfilling the antisymmetric definition

  Params:
  * `binary-operator`
  * `equality-operator` is the binary operator checking equality
  * `values-to-test` a set of values to test that we'll pick in
  * `nb-iterations` number of random pairs to pick in `values-to-test` to test the efficiency of it"
  [binary-operator equality-operator values-to-test nb-iterations]
  {::asymetric {:msg (str "Operator " binary-operator " is not asymetric")
                :result (->> (pick-random-tuple values-to-test 2 nb-iterations)
                             (map (fn [[date1 date2]]
                                    (when (and (binary-operator date1 date2)
                                               (binary-operator date2 date1))
                                      (if (equality-operator date1 date2) false [date1 date2]))))
                             (filterv sequential?))
                :expect []}})

(defn total-order
  "Is the `binary-operator` total order, as defined in [wiki page](https://en.wikipedia.org/wiki/Total_order)
  Returns the list of pair of values from `values-to-test` which are not fullfilling the total order definition
  * `binary-operator`
  * `equality-operator` is the binary operator checking equality
  * `values-to-test` a set of values to test that we'll pick in
  * `nb-iterations` number of random pairs to pick in `values-to-test` to test the efficiency of it"
  [binary-operator equality-operator values-to-test nb-iterations]
  (merge (reflexive binary-operator values-to-test)
         (transitive binary-operator values-to-test nb-iterations)
         (antisymmetric binary-operator equality-operator values-to-test nb-iterations)
         (strongly-connected binary-operator values-to-test nb-iterations)))

(defn equivalence
  "Is the `binary-operator` an equivalence, as defined in [wiki page](https://fr.wikipedia.org/wiki/Relation_d%27%C3%A9quivalence)
  Returns the list of pair of values from `values-to-test` which are not fullfilling the asymetric definition
  * `binary-operator`
  * `values-to-test` a set of values to test that we'll pick in
  * `nb-iterations` number of random pairs to pick in `values-to-test` to test the efficiency of it"
  [binary-operator values-to-test nb-iterations]
  (merge (reflexive binary-operator values-to-test)
         (symmetric binary-operator values-to-test nb-iterations)
         (transitive binary-operator values-to-test nb-iterations)))
