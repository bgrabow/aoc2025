(ns day-10
  (:require
    [clojure.string :as str]
    [clojure.math.combinatorics :as combo]
    [hyperfiddle.rcf :refer [tests]]))

(defn machine
  [s]
  (let [target-lights (mapv {\. 0 \# 1} (second (re-find #"\[([\.#]+)\]" s)))
        button-schematics (mapv #(mapv parse-long (re-seq #"\d+" %))
                            (re-seq #"\(\d+(?:,\d+)*\)" s))
        joltage-requirements (mapv parse-long (re-seq #"\d+" (re-find #"\{\d+(?:,\d+)*\}" s)))]
    {:target-lights        target-lights
     :initial-lights       (vec (repeat (count target-lights) 0))
     :button-schematics    button-schematics
     :button-combinations  (sort-by count (remove empty? (combo/subsets button-schematics)))
     :joltage-requirements joltage-requirements
     :joltage-levels       (vec (repeat (count joltage-requirements) 0))}))

(defn parse
  [s]
  (map machine (str/split-lines s)))

(defn press-button
  [lights button]
  (reduce #(update %1 %2 {0 1 1 0}) lights button))

(defn solve-part-1
  [s]
  (let [machines (parse s)]
    (reduce +
      (for [{:keys [button-combinations target-lights initial-lights]} machines]
        (count (first
                 (filter (fn [buttons]
                           (= target-lights (reduce press-button initial-lights buttons)))
                   button-combinations)))))))

(tests
  (solve-part-1 (slurp "../rust/resources/input_10_example.txt")) := 7)

(defn heuristic
  [current target]
  (reduce + (map (comp abs -) current target)))

(tests
  (heuristic [0 0 0] [10 15 20]) := 45
  (heuristic [9 16 19] [10 15 20]) := 3)

(defn a-star
  [init steps target]
  {})

(defn solve-part-2
  [_s])

(comment
  (parse (slurp "../rust/resources/input_10_example.txt"))
  (parse (slurp "../rust/resources/input_10.txt"))
  (solve-part-1 (slurp "../rust/resources/input_10.txt"))
  (solve-part-1 (slurp "../rust/resources/input_10_example.txt"))
  (solve-part-2 (slurp "../rust/resources/input_10.txt"))
  (solve-part-2 (slurp "../rust/resources/input_10_example.txt")))
