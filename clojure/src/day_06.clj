(ns day-06
  (:require
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]))

(defn pad-seq
  [n pad coll]
  (take n (concat coll (repeat pad))))

(defn transpose
  [pad colls]
  (let [l (apply max (map count colls))]
    (apply mapv vector (map (partial pad-seq l pad) colls))))

(tests
  (transpose nil [[1 2] [3 4]]) := [[1 3] [2 4]])

(defn parse
  [s]
  (let [lines (str/split-lines s)
        operands (butlast lines)
        operators (re-seq #"\S" (last lines))
        problems (transpose
                   nil
                   (mapv
                     #(map parse-long
                        (re-seq #"\d+" %))
                     operands))]
    {:problems  problems
     :operators operators}))

(defn solve-part-1
  [s]
  (let [{:keys [operators problems]} (parse s)]
    (reduce +
      (for [[problem operator] (map vector problems operators)]
        (let [op (case operator "*" * "+" +)]
          (reduce op problem))))))

(tests
  (solve-part-1 (slurp "../rust/resources/input_06_example.txt")) := 4277556)

(defn solve-part-2
  [s]
  (reduce +
    (for [[problem] (partition 1 2
                      (partition-by str/blank?
                        (mapv #(apply str %) (transpose " " (str/split-lines s)))))]
      (let [operator (case (last (first problem))
                       \* * \+ +)
            operands (map #(parse-long (str/trim (apply str (butlast %)))) problem)]
        (reduce operator operands)))))

(tests
  (solve-part-2 (slurp "../rust/resources/input_06_example.txt")) := 3263827)

(comment
  (solve-part-1 (slurp "../rust/resources/input_06.txt"))
  (solve-part-1 (slurp "../rust/resources/input_06_example.txt"))
  (solve-part-2 (slurp "../rust/resources/input_06.txt"))
  (solve-part-2 (slurp "../rust/resources/input_06_example.txt")))
