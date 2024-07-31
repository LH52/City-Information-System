(ns app
  (:require [db]
            [menu]))

(defn -main []
  (let [cities (db/load-data "MyFile.txt")] 
    (menu/menu cities)))

(-main)
