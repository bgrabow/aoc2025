(ns day-09
  (:require
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]))

(defn parse
  [s]
  (->> (str/split-lines s)
    (mapv #(mapv parse-long (re-seq #"\d+" %)))))

(defn rectangle-area
  [[a b]]
  (reduce * (map (comp inc abs -) a b)))

(defn solve-part-1
  [s]
  (let [red-tiles (parse s)]
    (->> (set
           (for [a red-tiles
                 b red-tiles
                 :when (not= a b)]
             [a b]))
      (map (juxt identity rectangle-area))
      (sort-by (comp - second))
      (first)
      (second))))

(tests
  (solve-part-1 (slurp "../rust/resources/input_09_example.txt")) := 50)

(defn solve-part-2
  [s])

(comment
  (parse (slurp "../rust/resources/input_09_example.txt"))
  (solve-part-1 (slurp "../rust/resources/input_09.txt"))
  (solve-part-1 (slurp "../rust/resources/input_09_example.txt"))
  (solve-part-2 (slurp "../rust/resources/input_09.txt"))
  (solve-part-2 (slurp "../rust/resources/input_09_example.txt")))
