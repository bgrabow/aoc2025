(ns aoc2025.day-08
  (:require
    [clojure.set :as set]
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]))

(defn parse
  [s]
  (mapv #(mapv parse-long (re-seq #"\d+" %)) (str/split-lines s)))

(defn solve-part-1
  [n s]
  (let [boxes (parse s)
        adjacency-queue (vec
                          (dedupe
                            (sort-by first
                              (for [a boxes
                                    b boxes
                                    :when (not= a b)]
                                (let [distance-squared (reduce + (mapv (comp #(* % %) -) a b))]
                                  [distance-squared (vec (sort [a b]))])))))]
    (->> (reductions
           (fn [circuits next-candidate]
             (let [[_distance] next-candidate
                   [a b] (seq (second next-candidate))
                   a-circuit (or (first
                                   (filter
                                     #(contains? % a)
                                     circuits))
                               #{a})
                   b-circuit (or (first
                                   (filter
                                     #(contains? % b)
                                     circuits))
                               #{b})]
               (if (= a-circuit b-circuit)
                 circuits
                 (conj (disj circuits a-circuit b-circuit)
                   (set/union a-circuit b-circuit)))))
           #{}
           (take n adjacency-queue))
      (last)
      (sort-by #(- (count %)))
      (take 3)
      (map count)
      (reduce *))))

(tests
  (solve-part-1 10 (slurp "../rust/resources/input_08_example.txt")) := 40)

(defn solve-part-2
  [s]
  (let [boxes (parse s)
        target-circuit-size (count boxes)
        adjacency-queue (vec
                          (dedupe
                            (sort-by first
                              (for [a boxes
                                    b boxes
                                    :when (not= a b)]
                                (let [distance-squared (reduce + (mapv (comp #(* % %) -) a b))]
                                  [distance-squared (vec (sort [a b]))])))))
        [_circuits [a b]] (->> (map vector
                                (drop 1
                                  (reductions
                                    (fn [circuits next-candidate]
                                      (let [[_distance] next-candidate
                                            [a b] (seq (second next-candidate))
                                            a-circuit (or (first
                                                            (filter
                                                              #(contains? % a)
                                                              circuits))
                                                        #{a})
                                            b-circuit (or (first
                                                            (filter
                                                              #(contains? % b)
                                                              circuits))
                                                        #{b})]
                                        (if (= a-circuit b-circuit)
                                          circuits
                                          (conj (disj circuits a-circuit b-circuit)
                                            (set/union a-circuit b-circuit)))))
                                    #{}
                                    adjacency-queue))
                                (map second adjacency-queue))
                           (filter (fn [[circuits]]
                                     (and (= 1 (count circuits))
                                       (= target-circuit-size (count (first circuits))))))
                           (first))]
    (* (first a) (first b))))

(tests
  (solve-part-2 (slurp "../rust/resources/input_08_example.txt")) := 25272)

(comment
  (parse (slurp "../rust/resources/input_08_example.txt"))
  (solve-part-1 1000 (slurp "../rust/resources/input_08.txt"))
  (solve-part-1 10 (slurp "../rust/resources/input_08_example.txt"))
  (solve-part-2 (slurp "../rust/resources/input_08.txt"))
  (solve-part-2 (slurp "../rust/resources/input_08_example.txt")))
