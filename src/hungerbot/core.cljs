(ns hungerbot.core
  (:require [cljs.nodejs :as nodejs]
            [hungerbot.slack :refer [slack]]
            [hunger.core :refer [list-feeds add-feed remove-feed]]
            [hunger.store :refer [destroy]]
            [hunger.redis-store :refer [store]]))

(nodejs/enable-util-print!)

(defn -main []
  (let [store (store)
        slack (slack)]))

(set! *main-cli-fn* -main)
