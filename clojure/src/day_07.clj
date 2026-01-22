(ns day-07
  (:require
    [clojure.set :as set]
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]))

(defn parse
  [s]
  (let [[beam-str & splitter-strs] (str/split-lines s)
        beams (set (keep-indexed (fn [i x] (when (= \S x) i)) beam-str))
        splitters (map #(set (keep-indexed (fn [i x] (when (= \^ x) i)) %)) splitter-strs)]
    {:beams beams :splitters splitters}))

(defn split
  [beams splitters]
  (let [splits (set/intersection beams splitters)]
    (-> beams
      (set/difference splits)
      (set/union (set (mapcat (fn [i] [(dec i) (inc i)]) splits))))))

(defn count-splits
  [beam-rows splitters]
  (reduce + (map #(count (set/intersection %1 %2)) beam-rows splitters)))

(defn draw-beams
  [beam-rows]
  (let [overall-length (apply max (map #(apply max %) beam-rows))]
    (map
      (fn [row]
        (apply str
          (reduce
            (fn [v i]
              (assoc v i \|))
            (vec (repeat (inc overall-length) \.)) row)))
      beam-rows)))

(defn solve-part-1
  [s]
  (let [{:keys [beams splitters]} (parse s)]
    (count-splits (reductions split beams splitters) splitters)))

(tests
  (solve-part-1 (slurp "../rust/resources/input_07_example.txt")) := 21)

(defn solve-part-2
  [s])

(comment
  (solve-part-1 (slurp "../rust/resources/input_07.txt"))
  (solve-part-1 (slurp "../rust/resources/input_07_example.txt"))
  (solve-part-2 (slurp "../rust/resources/input_07.txt"))
  (solve-part-2 (slurp "../rust/resources/input_07_example.txt")))
