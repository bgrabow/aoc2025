(ns day-02
  (:require
    [hyperfiddle.rcf :refer [tests]]
    [clojure.math.numeric-tower :as math]))

(defn parse
  [s]
  (for [[_ low high] (re-seq #"(\d+)-(\d+)" s)]
    [(parse-long low) (parse-long high)]))

(defn tens-magnitude
  [x]
  (count (take-while pos? (iterate #(quot % 10) x))))

(tests
  (tens-magnitude -1) := 0
  (tens-magnitude 0) := 0
  (tens-magnitude 1) := 1
  (tens-magnitude 11) := 2
  (tens-magnitude 111) := 3)

(defn prefix
  [x]
  (quot x (math/expt 10 (quot (tens-magnitude x) 2))))

(defn repeated
  [x]
  (+ x (* x (math/expt 10 (tens-magnitude x)))))

(defn range-sum
  [lb-prefix ub-prefix]
  (reduce + (map repeated (range lb-prefix (inc ub-prefix)))))

(defn prefix-range
  [lb-prefix ub-prefix]
  (map repeated (range lb-prefix (inc ub-prefix))))

(tests
  (prefix-range 1 2) := [11 22])

(defn ids-sum-in-mag
  [lb ub magnitude]
  (let [lb (max lb (math/expt 10 (dec magnitude)))
        ub (min ub (dec (math/expt 10 magnitude)))
        lb-prefix (prefix lb)
        ub-prefix (prefix ub)
        lowest-invalid-id (if (>= (repeated lb-prefix) ub)
                            (repeated lb-prefix)
                            (repeated (inc lb-prefix)))
        highest-invalid-id (if (<= (repeated ub-prefix) ub)
                             (repeated ub-prefix)
                             (repeated (dec ub-prefix)))]
    (if (< highest-invalid-id lowest-invalid-id)
      0
      (range-sum (prefix lowest-invalid-id) (prefix highest-invalid-id)))))

(defn ids-in-mag
  [lb ub magnitude]
  (let [lb (max lb (math/expt 10 (dec magnitude)))
        ub (min ub (dec (math/expt 10 magnitude)))
        lb-prefix (prefix lb)
        ub-prefix (prefix ub)
        lowest-invalid-id (if (>= (repeated lb-prefix) lb)
                            (repeated lb-prefix)
                            (repeated (inc lb-prefix)))
        highest-invalid-id (if (<= (repeated ub-prefix) ub)
                             (repeated ub-prefix)
                             (repeated (dec ub-prefix)))]
    (if (< highest-invalid-id lowest-invalid-id)
      []
      (prefix-range (prefix lowest-invalid-id) (prefix highest-invalid-id)))))

(defn invalid-ids
  [[lb ub]]
  (let [lb-mag (tens-magnitude lb)
        ub-mag (tens-magnitude ub)]
    (->> (range lb-mag (inc ub-mag))
      (filter even?)
      (mapcat (partial ids-in-mag lb ub)))))

(tests
  (invalid-ids [11 22]) := [11 22])

(tests
  (invalid-ids [11 22]) := [11 22])

(defn solve-part-1
  [s]
  (->> (parse s)
    (mapcat invalid-ids)
    (reduce +)))

(comment
  (let [input (slurp "../rust/resources/input_02.txt")
        example (slurp "../rust/resources/input_02_example.txt")]
    [(solve-part-1 input)
     #_(solve-part-2 input)]))
