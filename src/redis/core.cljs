(ns redis.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [chan put! close!]]))

(def redis (nodejs/require "redis"))

(defn cb-channel
  ([output] (cb-channel output identity))
  ([output transform]
   (fn [error response]
     (if (= nil error)
       (when (not (= nil response))
         (put! output (transform (js->clj response :keywordize-keys true))))
       (put! output (js->clj error :keywordize-keys true)))
     (close! output))))

(defn redis-client
  [client]
  (fn
    [cmd & args]
    (let [cmd (aget client (name cmd))
          output (chan)]
      (if (= nil args)
        (.apply cmd client)
        (.apply cmd client (clj->js (concat args [(cb-channel output)]))))
      output)))

(defn redis-connect [] (redis-client (.createClient redis)))

