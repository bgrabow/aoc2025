(ns aoc2025.day-10
  (:require
    [aoc2025.util :as util]
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
  [largest-step current target]
  (/ (reduce + (map (comp abs -) current target))
    largest-step))

(tests
  (heuristic 3 [0 0 0] [10 15 20]) := 15
  (heuristic 3 [9 16 19] [10 15 20]) := 1)

(defn path-cost
  [{:keys [largest-step target]} {:keys [joltage steps]}]
  (+ steps (heuristic largest-step joltage target)))

(defn press-joltage-button
  [joltage button]
  (try (reduce #(update %1 %2 inc) joltage button)
       (catch Exception e
         (throw (ex-info "Failed to press joltage button"
                  {:joltage joltage :button button} e)))))

(tests
  (path-cost
    {:largest-step 2
     :target       [3 5 4 7]}
    {:joltage [0 0 0 0]
     :steps   0})
  := 19/2
  (path-cost
    {:largest-step 2
     :target       [3 5 4 7]}
    {:joltage [0 0 1 0]
     :steps   1})
  := 10)

#{[3] [1 3] [2] [2 3] [0 2] [0 1]}

(defn paths-heap
  [{:keys [joltage-requirements
           button-schematics]}
   step']
  (sorted-set-by (fn [a b]
                   (compare
                     [(:expected-cost a) (- (:steps a)) (:joltage a)]
                     [(:expected-cost b) (- (:steps b)) (:joltage b)]))
    (assoc step' :expected-cost (path-cost
                                  {:largest-step (apply max (map count button-schematics))
                                   :target       joltage-requirements}
                                  step'))))

(defn new-paths
  [{:keys [button-schematics
           joltage-requirements]}
   explored-paths]
  (let [candidate (first explored-paths)]
    (map
      (fn [button]
        (let [step' {:joltage (press-joltage-button (:joltage candidate) button)
                     :steps   (inc (:steps candidate))}]
          (assoc step' :expected-cost (path-cost
                                        {:largest-step (apply max (map count button-schematics))
                                         :target       joltage-requirements}
                                        step'))))
      button-schematics)))

(defn expand-search
  [params explored-paths]
  (let [candidate (first explored-paths)]
    (-> (reduce conj explored-paths
          (new-paths params explored-paths))
      (disj candidate))))

(tests
  (expand-search
    {:button-schematics    [[3] [1 3] [2] [2 3] [0 2] [0 1]]
     :joltage-requirements [3 5 4 7]}
    (paths-heap {:button-schematics    [[3] [1 3] [2] [2 3] [0 2] [0 1]]
                 :joltage-requirements [3 5 4 7]}
      {:joltage [0 0 0 0]
       :steps   0}))
  := (-> (paths-heap {:button-schematics    [[3] [1 3] [2] [2 3] [0 2] [0 1]]
                      :joltage-requirements [3 5 4 7]}
           {:joltage [0 0 0 0]
            :steps   0})
       (conj
         {:joltage [0 0 0 1] :steps 1 :expected-cost 10}
         {:joltage [0 1 0 1] :steps 1 :expected-cost 19/2}
         {:joltage [0 0 1 0] :steps 1 :expected-cost 10}
         {:joltage [0 0 1 1] :steps 1 :expected-cost 19/2}
         {:joltage [1 0 1 0] :steps 1 :expected-cost 19/2}
         {:joltage [1 1 0 0] :steps 1 :expected-cost 19/2})
       (disj {:joltage       [0 0 0 0]
              :steps         0
              :expected-cost 19/2})))

(defn solve-part-2
  [s]
  (let [machines (parse s)]
    (vec
      (for [{:keys [joltage-requirements button-schematics joltage-levels]} (take 1 machines)]
        (loop [explored-paths (paths-heap {:joltage-requirements joltage-requirements
                                           :button-schematics    button-schematics}
                                {:joltage joltage-levels
                                 :steps   0})]
          (println (count explored-paths))
          (let [new-paths (new-paths
                            {:joltage-requirements joltage-requirements
                             :button-schematics    button-schematics}
                            explored-paths)]
            (or (first (filter #(= joltage-requirements (:joltage %)) new-paths))
              (recur (disj (reduce conj explored-paths new-paths) (first explored-paths))))))))))

(comment
  (parse (slurp "../rust/resources/input_10_example.txt"))
  (parse (slurp "../rust/resources/input_10.txt"))
  (solve-part-1 (slurp "../rust/resources/input_10.txt"))
  (solve-part-1 (slurp "../rust/resources/input_10_example.txt"))
  (solve-part-2 (slurp "../rust/resources/input_10.txt"))
  (solve-part-2 (slurp "../rust/resources/input_10_example.txt")))
