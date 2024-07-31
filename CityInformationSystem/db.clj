(ns db
  (:require [clojure.string :as str]))

(defn load-data [filename]
  (let [content (slurp filename)
        lines (str/split-lines content)
        parse-line (fn [line]
                     (let [[city province size population area] (str/split line #"\|")]
                       {:city city :province province :size size :population (read-string population) :area (read-string area)}))]
    (map parse-line lines)))
