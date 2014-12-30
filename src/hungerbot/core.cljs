(ns hungerbot.core
  (:require [cljs.nodejs :as nodejs]
            [hunger.core :refer [list-feeds]]
            [hunger.redis-store :refer [store destroy]]))

(nodejs/enable-util-print!)

(defn -main []
  (let [store (store)]
    (list-feeds store (fn [error feeds]
                        (println "callback called")
                        (println feeds)
                        (destroy store)))))

(set! *main-cli-fn* -main)
