(ns hunger.redis-store
  (:require [cljs.nodejs :as nodejs]
            [goog.crypt.base64 :as b64]
            [hunger.store :refer [IStore]]))

(def redis (nodejs/require "redis"))

(defn key->string
  [string]
  (b64/encodeString string))

(defn string->key
  [string]
  (b64/decodeString string))

(defrecord RedisStore [prefix client]
  IStore

  (fetch
    [this id cb]
    (.get client (str prefix ":" id) cb))

  (write
    [this id item cb]
    (.set client (str prefix ":" id) item cb))

  (delete
    [this id cb]
    (.del client (str prefix ":" id) cb))

  (collection-fetch
    [this id cb]
    (.smembers client (str prefix ":" id) cb))

  (collection-add
    [this id item cb]
    (.sadd client (str prefix ":" id) item cb))

  (collection-remove
    [this id item cb]
    (.srem client (str prefix ":" id) item cb))

  (destroy
    [this]
    (.quit client)))

(defn store
  ([] (store "hunger"))
  ([prefix] (RedisStore. prefix (.createClient redis))))

