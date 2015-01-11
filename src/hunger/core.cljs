(ns hunger.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [<!]]
            [hunger.store :refer [fetch write collection-fetch collection-add collection-remove]]))

(defrecord Feed [url info])

(defn url->feed
  [url store]
  (Feed. url {}))

(defn add-feed
  [store feed cb]
  (let [results (atom  [])]
    (go (swap! results conj (<! (collection-add store "feeds" (:url feed))))
        (doseq [[prop value] (seq (:info feed))]
          (swap! results conj (<! (write store ["feed" (:url feed) (name prop)] value))))
        (cb nil feed))))

(defn remove-feed
  [store feed cb]
  (collection-remove store "feeds" feed))

(defn list-feeds
  [store filter cb]
  (let [results (atom [])]
    (go
      (swap! results conj (map #(url->feed % store) (<! (collection-fetch store "feeds"))))
      (cb nil @results))))

(defn last-item-in-feed
  [store feed cb]
  (fetch store (str "last-item-of-" feed)))

(defn update-last-item-in-feed
  [store feed item cb]
  (write store (str "last-item-of-" feed) item))

