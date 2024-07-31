(ns menu
  (:require [db]
            [clojure.string :as str]))

(defn list-cities [cities]
  (sort-by :city cities))

(defn list-cities-by-province [cities province]
  (let [filtered-cities (filter #(= province (:province %)) cities)]
    (sort-by (juxt :size :city) filtered-cities)))

(defn list-cities-by-density [cities province]
  (let [filtered-cities (filter #(= province (:province %)) cities)
        calc-density (fn [city] (/ (:population city) (:area city)))]
    (sort-by calc-density filtered-cities)))

(defn display-city-info [cities city-name]
  (let [city (first (filter #(= city-name (:city %)) cities))]
    (if city
      (println (str "[" (str/join " " (map #(str "\"" % "\"") [(:city city) (:province city) (:size city) (:population city) (:area city)])) "]"))
      (println "City not found"))))

(defn list-provinces [cities]
  (let [grouped (group-by :province cities)
        count-cities (fn [[province cities]] [province (count cities)])
        province-counts (sort-by (comp - second) (map count-cities grouped))
        total-cities (reduce + (map second province-counts))]
    (doseq [[index [province count]] (map-indexed vector province-counts)]
      (println (str (inc index) ": [\"" province "\" " count "]")))
    (println (str "Total: " (count province-counts) " provinces, " total-cities " cities on file."))))

(defn display-province-info [cities]
  (let [grouped (group-by :province cities)
        sum-population (fn [[province cities]] [province (reduce + (map :population cities))])]
    (sort-by first (map sum-population grouped))))

(defn menu [cities]
  (loop []
    (println "*** City Information Menu ***")
    (println "-----------------------------")
    (println "1. List Cities")
    (println "2. Display City Information")
    (println "3. List Provinces")
    (println "4. Display Province Information")
    (println "5. Exit")
    (flush)
    (let [choice (read-line)]
      (case choice
        "1" (do
              (println "1.1 List all cities")
              (println "1.2 List all cities for a given province")
              (println "1.3 List all cities for a given province by density")
              (flush)
              (let [sub-choice (read-line)]
                (case sub-choice
                  "1.1" (do
                          (let [result (list-cities cities)
                                city-names (map :city result)
                                formatted-names (str "[" (str/join " " (map #(str "\"" % "\"") city-names)) "]")]
                            (println formatted-names))
                          (recur))
                  "1.2" (do
                          (println "Enter province name:")
                          (flush)
                          (let [province (read-line)
                                result (list-cities-by-province cities province)]
                            (doseq [[index city] (map-indexed vector result)]
                              (let [formatted-city (str (inc index) ": [" (str/join " " (map #(str "\"" % "\"") [(:city city) (:size city) (:population city)])) "]")]
                                (println formatted-city))))
                          (recur))
                  "1.3" (do
                          (println "Enter province name:")
                          (flush)
                          (let [province (read-line)
                                result (list-cities-by-density cities province)]
                            (doseq [[index city] (map-indexed vector result)]
                              (let [formatted-city (str (inc index) ": [" (str/join " " (map #(str "\"" % "\"") [(:city city) (:size city) (:population city)])) "]")]
                                (println formatted-city))))
                          (recur))
                  (recur))))
        "2" (do
              (println "Enter city name:")
              (flush)
              (let [city-name (read-line)]
                (display-city-info cities city-name))
              (recur))
        "3" (do
              (list-provinces cities)
              (recur))
        "4" (do
              (let [result (display-province-info cities)]
                (doseq [province result]
                  (println province)))
              (recur))
        "5" (do (println "Good Bye"))
        (recur)))))
