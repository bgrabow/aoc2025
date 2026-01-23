(ns aoc2025.util)

(defn take-until
  [f coll]
  (concat
    (take-while (complement f) coll)
    (take 1 (drop-while (complement f) coll))))
