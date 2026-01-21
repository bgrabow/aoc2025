(ns day-01
  (:require [clojure.string :as str]))

(defn parse-line
  [line]
  (let [[dir & distance] line]
    (case dir
      \L (- (parse-long (apply str distance)))
      \R (parse-long (apply str distance)))))

(defn rotations
  [s]
  (map parse-line (str/split-lines (str/trim s))))

(defn zeroes-between
  [x y])

(def DIAL_START 50)
(def DIAL_MAX 99)
(def DIAL_MIN 0)

(defn normalize-dial
  [x]
  (mod x (inc DIAL_MAX)))

(defn solve-part-1
  [input]
  (->> (rotations input)
    (reductions + DIAL_START)
    (map normalize-dial)
    (filter zero?)
    (count))
  )

#_(defn solve-part-2
  [input]
  (->> (rotations input)
            (partition 2 1)
            (map #(apply zeroes-between %))
            ;(reduce +)
    ))

(comment
  (let [input (slurp "../rust/resources/input_01.txt")
        example (slurp "../rust/resources/input_01_example.txt")]
    #_(solve-part-1 input)
    (solve-part-2 example)))
