(ns aoc2025.day-05
  (:require
    [clojure.string :as str]
    [hyperfiddle.rcf :refer [tests]]))

(defn in-range?
  [[lb ub] x]
  (<= lb x ub))

(defn fresh?
  [fresh-ranges ingredient]
  (boolean (some #(in-range? % ingredient) fresh-ranges)))

(let [fresh-ranges [[3 5] [10 14] [16 20] [12 18]]]
  (tests
    (fresh? fresh-ranges 1) := false
    (fresh? fresh-ranges 5) := true
    (fresh? fresh-ranges 8) := false
    (fresh? fresh-ranges 11) := true
    (fresh? fresh-ranges 17) := true
    (fresh? fresh-ranges 32) := false))

(defn parse
  [s]
  (let [lines (str/split-lines s)]
    {:fresh-ranges (->> (take-while not-empty lines)
                     (mapv (fn [line]
                             (let [[_ lb ub] (re-find #"(\d+)-(\d+)" line)]
                               [(parse-long lb) (parse-long ub)]))))
     :ingredients  (->> (drop-while not-empty lines)
                     (drop 1)
                     (mapv (fn [line]
                             (parse-long (re-find #"\d+" line)))))}))

(defn solve-part-1
  [s]
  (let [{:keys [fresh-ranges ingredients]} (parse s)]
    (count (filter (partial fresh? fresh-ranges) ingredients))))

(defn solve-part-2
  [s]
  (let [{:keys [fresh-ranges]} (parse s)
        {:keys [frontier groups]} (reduce
                                    (fn [acc r]
                                      (let [[lb ub] (:frontier acc)
                                            [lb' ub'] r]
                                        (cond
                                          (not (:frontier acc)) (assoc acc :frontier r)
                                          (<= lb lb' ub) (assoc acc :frontier [(min lb lb') (max ub ub')])
                                          :else (-> acc
                                                  (update :groups conj [lb ub])
                                                  (assoc :frontier r)))))
                                    {:frontier nil :groups []}
                                    (sort-by (juxt first second) fresh-ranges))]
    (->> (conj groups frontier)
      (map #(inc (- (apply - %))))
      (reduce +))))

(tests
  (solve-part-1 (slurp "../rust/resources/input_05_example.txt")) := 3
  (solve-part-2 (slurp "../rust/resources/input_05_example.txt")) := 14)

(comment
  (let [input (slurp "../rust/resources/input_05.txt")
        example (slurp "../rust/resources/input_05_example.txt")]
    (solve-part-1 input)
    (solve-part-2 input)))


