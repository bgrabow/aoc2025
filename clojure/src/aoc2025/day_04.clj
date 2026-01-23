(ns aoc2025.day-04
  (:require
    [clojure.string :as str]
    [aoc2025.util :as util]
    [hyperfiddle.rcf :refer [tests]]))

(defn neighbors
  [[y x]]
  (for [dx [-1 0 1]
        dy [-1 0 1]
        :when (not= [0 0] [dx dy])]
    [(+ y dy) (+ x dx)]))

(defn parse
  [s]
  (vec
    (for [line (str/split-lines s)]
      (vec line))))

(defn occupied-neighbors
  [grid p]
  (for [p' (neighbors p)
        :when (= \@ (get-in grid p'))]
    p'))

(defn solve-part-1
  [s]
  (let [grid (parse s)]
    (->> (for [y (range (count grid))
               x (range (count (first grid)))
               :when (= \@ (get-in grid [y x]))]
           (occupied-neighbors grid [y x]))
      (filter #(< (count %) 4))
      (count))))

(tests
  (solve-part-1 (slurp "../rust/resources/input_04_example.txt")) := 13)

(defn take-until
  [f coll]
  (concat
    (take-while (complement f) coll)
    (take 1 (drop-while (complement f) coll))))

(defn iterate-until-fixed
  [f x]
  (map first
    (util/take-until #(apply = %)
      (partition 2 1
        (iterate f x)))))

(defn removable?
  [grid p]
  (< (count (occupied-neighbors grid p)) 4))

(defn solve-part-2
  [s]
  (let [grid (parse s)
        final-grid (last
                     (iterate-until-fixed
                       (fn [g]
                         (vec
                           (for [y (range (count g))]
                             (vec
                               (for [x (range (count (first g)))]
                                 (cond
                                   (and (= \@ (get-in g [y x]))
                                     (removable? g [y x]))
                                   \.
                                   :else (get-in g [y x])))))))
                       grid))]
    (- (get (frequencies (flatten grid)) \@)
      (get (frequencies (flatten final-grid)) \@))))

(tests
  (solve-part-2 (slurp "../rust/resources/input_04_example.txt")) := 43)

(comment
  (solve-part-1 (slurp "../rust/resources/input_04.txt"))
  (solve-part-2 (slurp "../rust/resources/input_04.txt")))
