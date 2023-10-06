(ns automaton-core.log.strategy
  "Strategy to choose what logger is used where and when")

(defprotocol Strategy
  (apply-strategy [this ns level] "Return the seq of ids of the chosen rule according to that strategy")
  (rule-ids [this] "List of known rule ids in that strategy, adding the `no-op` strategy defaulting your parameters"))
