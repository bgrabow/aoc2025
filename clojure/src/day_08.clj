(ns day-08
  (:require
    [clojure.set :as set]
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]
    [clojure.math.numeric-tower :as math]))

(defn parse
  [s]
  (mapv #(mapv parse-long (re-seq #"\d+" %)) (str/split-lines s)))

(defn solve-part-1
  [s]
  (let [boxes (parse s)
        adjacency-queue (vec
                          (dedupe
                            (sort-by first
                              (for [a boxes
                                    b boxes
                                    :when (not= a b)]
                                (let [distance-squared (reduce + (mapv (comp #(* % %) -) a b))]
                                  [distance-squared (vec (sort [a b]))])))))]
    (reductions
      (fn [circuits next-candidate]
        (let [[distance] next-candidate
              [a b] (seq (second next-candidate))
              a-group (first (filter #(contains? % a) circuits))
              b-group (first (filter #(contains? % b) circuits))]
          (if (and a-group b-group
                (= a-group b-group))
            circuits
            (conj (disj circuits a-group b-group)
              (set/intersection a-group b-group)))))
      #{}
      adjacency-queue)))

(let [[a b] (seq #{1 2})]
  [a b])

(defn solve-part-2
  [s])

(comment
  (parse (slurp "../rust/resources/input_08_example.txt"))
  (solve-part-1 (slurp "../rust/resources/input_08.txt"))
  (solve-part-1 (slurp "../rust/resources/input_08_example.txt"))
  (solve-part-2 (slurp "../rust/resources/input_08.txt"))
  (solve-part-2 (slurp "../rust/resources/input_08_example.txt")))
