(ns hunger.redis-store
  (:require [cljs.nodejs :as nodejs]
            [hunger.store :refer [IStore]]))

(def redis (nodejs/require "redis"))

(defrecord RedisStore [prefix client]
  IStore
  (fetch [this item cb] (.smembers client (str prefix ":" item) cb))
  (record [this id item cb] (.smembers client (str prefix ":" item) cb))
  (destroy [this] (println "quiting") (.quit client)))

(defn store
  ([] (store "hunger"))
  ([prefix] (RedisStore. prefix (.createClient redis))))

