(ns aoc2025.day-03
  (:require
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]))

(defn split-num
  [x]
  (mapv (comp parse-long str) (str x)))

(defn largest-joltage
  [xs]
  (let [result (reduce
                 (fn [{:keys [a b]} x]
                   (cond
                     (> b a) {:a b :b x}
                     (> x b) {:a a :b x}
                     :else {:a a :b b}))
                 {:a (first xs)
                  :b (second xs)}
                 (drop 2 xs))]
    (+ (* 10 (:a result)) (:b result))))

(tests
  (largest-joltage (split-num 987654321111111)) := 98
  (largest-joltage (split-num 811111111111119)) := 89
  (largest-joltage (split-num 234234234234278)) := 78
  (largest-joltage (split-num 818181911112111)) := 92)

(defn largest-n-battery-joltage
  [n xs]
  (reduce #(+ %2 (* 10 %1))
    (reduce
      (fn [joltage x]
        (let [augmented-joltage (conj joltage x)]
          (or (first (for [i (range n)
                           :when (< (get augmented-joltage i) (get augmented-joltage (inc i)))]
                       (vec (concat (take i augmented-joltage) (drop (inc i) augmented-joltage)))))
            joltage)))
      (vec (take n xs))
      (drop n xs))))

(tests
  (largest-n-battery-joltage 12 (split-num 987654321111111)) := 987654321111
  (largest-n-battery-joltage 12 (split-num 811111111111119)) := 811111111119
  (largest-n-battery-joltage 12 (split-num 234234234234278)) := 434234234278
  (largest-n-battery-joltage 12 (split-num 818181911112111)) := 888911112111)

(defn parse
  [s]
  (for [line (str/split-lines s)]
    (mapv (comp parse-long str) line)))

(defn solve-part-1
  [s]
  (->> (parse s)
    (map largest-joltage)
    (reduce +)))

(tests
  (solve-part-1 (slurp "../rust/resources/input_03_example.txt")) := 357)

(defn solve-part-2
  [s]
  (->> (parse s)
    (map (partial largest-n-battery-joltage 12))
    (reduce +)))

(tests
  (solve-part-2 (slurp "../rust/resources/input_03_example.txt")) := 3121910778619)

(comment
  (let [input (slurp "../rust/resources/input_03.txt")
        example (slurp "../rust/resources/input_03_example.txt")]
    [(solve-part-1 input)
     (solve-part-2 example)]))
