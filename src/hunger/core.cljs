(ns hunger.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [<! chan pipe]]
            [hunger.store :refer [fetch write delete collection-fetch collection-add
                                  collection-contains? collection-remove]]))

(defrecord Feed [url info])

(defn url->feed
  [store url]
  (println (str "url: " url))
  (go (Feed. url (<! (fetch store ["feed" url "*"])))))

(defn add-feed
  [store feed cb]
  (let [results (atom  [])]
    (go (swap! results conj (<! (collection-add store "feeds" (:url feed))))
        (doseq [[prop value] (seq (:info feed))]
          (swap! results conj (<! (write store ["feed" (:url feed) (name prop)] value))))
        (cb nil feed))))

(defn fetch-feed
  [store feed sieve cb]
  (let [result (atom nil)]
    (go
      (when (<! (collection-contains? store "feeds" (:url feed)))
        (let [feed (<! (url->feed store (:url feed)))]
          (reset! result (if (sieve feed) feed nil))))
      (cb nil @result))))

(defn remove-feed
  [store feed cb]
  (go (<! (collection-remove store "feeds" (:url feed)))
      (<! (delete store ["feed" (:url feed) "*"]))
      (cb nil "OK")))

(defn list-feeds
  [store sieve cb]
  (let [feeds-ch  (chan 1 (comp (filter sieve) (map (partial url->feed store))))]
    (pipe (collection-fetch store "feeds") feeds-ch)
    (go (cb nil (loop [feed  (<! feeds-ch)
                       feeds []]
                  (if (nil? feed) feeds (recur (<! feeds-ch)
                                               (conj feeds feed))))))))

(defn last-item-in-feed
  [store feed cb]
  (fetch store (str "last-item-of-" feed)))

(defn update-last-item-in-feed
  [store feed item cb]
  (write store (str "last-item-of-" feed) item))

