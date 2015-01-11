(ns hunger.redis-store
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as nodejs]
            [goog.crypt.base64 :as b64]
            [cljs.core.async :refer [<!]]
            [clojure.string :refer [join replace]]
            [hunger.store :refer [IStore]]
            [redis.core :refer [redis-connect]]))

(def sep ":")

(defn key->string
  [string]
  (b64/encodeString string))

(defn string->key
  [string]
  (b64/decodeString string))

(defn normalize-key
  [prefix key]
  (str prefix sep (if (vector? key) (join sep key) key)))

(defn- wildcard?
  [id]
  (and (vector? id) (= "*" (last id))))

(defn- de-prefix
  [prefix string]
  (replace string (re-pattern (str "^" prefix)) ""))

(defrecord RedisStore [prefix client]
  IStore

  (fetch
    [this id]
    (if (wildcard? id)
      (go
        (let [result (atom {})
              root   (normalize-key prefix id)]
          (doseq [k (<! (client :keys (normalize-key prefix id)))]
            (swap! result conj [(keyword (de-prefix root k)) (<! (client :get k))]))
          @result))
      (client :get (normalize-key prefix id))))

  (write
    [this id item]
    (client :set (normalize-key prefix id) item))

  (delete
    [this id]
    (if (wildcard? id)
      (go
        (let [result (atom [])
              root   (normalize-key prefix id)]
          (doseq [k (<! (client :keys (normalize-key prefix id)))]
            (swap! result conj [(keyword (de-prefix root k)) (<! (client :del k))]))
          (reduce + (map #(last %) @result))))
      (client :del (normalize-key prefix id))))

  (collection-fetch
    [this id]
    (client :smembers (normalize-key prefix id)))

  (collection-add
    [this id item]
    (client :sadd (normalize-key prefix id) item))

  (collection-remove
    [this id item]
    (client :srem (normalize-key prefix id) item))

  (destroy
    [this]
    (client :quit)))

(defn store
  ([] (store "hunger"))
  ([prefix] (RedisStore. prefix (redis-connect))))

