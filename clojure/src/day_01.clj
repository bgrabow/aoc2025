(ns day-01
  (:require
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]))

(def DIAL_START 50)
(def DIAL_MAX 99)
(def DIAL_RANGE (inc DIAL_MAX))

(defn parse-line
  [line]
  (let [[dir & distance] line]
    (case dir
      \L (- (parse-long (apply str distance)))
      \R (parse-long (apply str distance)))))

(defn rotations
  [s]
  (map parse-line (str/split-lines (str/trim s))))

(defn div-euclid
  [x y]
  (long (Math/floor (/ x y))))

(tests
  (div-euclid -1 DIAL_RANGE) := -1
  (div-euclid 100 DIAL_RANGE) := 1
  (div-euclid 99 DIAL_RANGE) := 0
  (div-euclid -1 DIAL_RANGE) := -1
  (div-euclid -1 DIAL_RANGE) := -1
  (div-euclid -1 DIAL_RANGE) := -1)

(defn zeroes-between
  [from to]
  (if (<= from to)
    (- (div-euclid to DIAL_RANGE) (div-euclid from DIAL_RANGE))
    (zeroes-between (- from) (- to))))

(defn normalize-dial
  [x]
  (mod x (inc DIAL_MAX)))

(defn solve-part-1
  [input]
  (->> (rotations input)
    (reductions + DIAL_START)
    (map normalize-dial)
    (filter zero?)
    (count)))

(defn solve-part-2
  [input]
  (->> (rotations input)
    (reductions + DIAL_START)
    (partition 2 1)
    (map #(apply zeroes-between %))
    (reduce +)))

(comment
  (let [input (slurp "../rust/resources/input_01.txt")
        example (slurp "../rust/resources/input_01_example.txt")]
    #_(solve-part-1 input)
    ;(solve-part-2 example)
    (solve-part-2 input)))
