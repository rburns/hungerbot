(ns hunger.core
  (:require [cljs.nodejs :as nodejs]
            [hunger.redis-store :refer [RedisStore store IStore.fetch]]
            [hunger.store :refer [IStore:fetch]]))

(defn add-feed
  [url store]
  )

(defn remove-feed
  [id store]
  )

(defn list-feeds
  [store cb]
  (fetch store :feeds cb))

(defn last-item-in-feed
  [id store]
  )

(defn update-last-item-in-feed
  [id item store]
  )
