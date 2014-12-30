(ns hunger.core
  (:require [cljs.nodejs :as nodejs]))

(defprotocol IStore
  (fetch [this item cb])
  (record [this id item cb])
  (destroy [this]))

(def redis (nodejs/require "redis"))

(defrecord RedisStore [prefix client]
  IStore
  (fetch [this item cb] (.smembers client (str prefix ":" item) cb))
  (record [this id item cb] (.smembers client (str prefix ":" item) cb))
  (destroy [this] (println "quiting") (.quit client)))

(defn store
  ([] (store "hunger"))
  ([prefix] (RedisStore. prefix (.createClient redis))))

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
