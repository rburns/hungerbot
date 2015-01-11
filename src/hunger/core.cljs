(ns hunger.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [<!]]
            [hunger.store :refer [fetch write collection-fetch collection-add collection-remove]]))

(defrecord Feed [url info])

(defn url->feed
  [store url]
  (go (Feed. url (<! (fetch store ["feed" url "*"])))))

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
  [store sieve cb]
  (let [results (atom [])]
    (go
      (doseq [url (<! (collection-fetch store "feeds"))]
        (swap! results conj (<! (url->feed store url))))
      (cb nil (filter sieve @results)))))

(defn last-item-in-feed
  [store feed cb]
  (fetch store (str "last-item-of-" feed)))

(defn update-last-item-in-feed
  [store feed item cb]
  (write store (str "last-item-of-" feed) item))

