(ns hunger.consume
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer [<!]]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]
            [cljs-time.coerce :as tc]
            [hunger.store :refer [fetch]]))

(def FeedParser (nodejs/require "feedparser"))
(def request    (nodejs/require "request"))

(defn handle-req-error
  [err]
  (println (str "request error: " err)))

(defn handle-response
  [parser]
  (fn [res]
    (this-as this
      (if (not (= (.-statusCode res) 200))
        (.emit this "error" (js/Error "Bad status code"))
        (.pipe this parser)))))

(defn handle-feed-error
  [err]
  (println (str "feed error: " err)))

(defn last-seen-item
  [store feed]
  (fetch store ["feed" (:url feed) "last-seen"]))

; (defn update-last-seen-item-in-feed
;   [store feed item]
;   (write store (str "last-item-of-" feed) item))

(defn handle-feed
  [feed context]
  (fn []
    (this-as this
             (go
               (let [recent      (t/date-time (<! (last-seen-item (:store context) feed)))
                     next-item   (fn [] (js->clj (.read this) :keywordize-keys true))
                     ; most-recent (fn [item new-recent]
                     ;               (if (t/after? (t/date-time (:pubdate item) new-recent))
                     ;                             (do
                     ;                               )
                     ;                             new-recent))
                     new?        (fn [item] (t/after? (t/date-time (:pubdate item)) recent))
                     items       (loop [item       (next-item)
                                        ; new-recent recent
                                        new-items  []]
                                   (if (= nil item)
                                     new-items
                                     (recur (next-item)
                                            ; (most-recent item new-recent)
                                            (if (new? item) (conj new-items item) new-items))))]
                 (println (map #(:title %) items)))))))

(defn fetch-feed
  [feed context]
  (fn []
    (let [req    (request feed)
          parser (FeedParser)]
      (.on req "error" handle-req-error)
      (.on req "response" (handle-response parser))
      (.on parser "readable" (handle-feed feed context))
      (.on parser "error", handle-feed-error))))

(defn init-polling
  [context]
  (fn [feed]
    [(:url feed) (js/setInterval (fetch-feed feed context) (:default-interval context))]))

(defn consume
  [context]
    (fn [error feeds]
      (println "going to be polling")
      (println feeds)
      (println (str "every " (:default-interval context) " minutes"))
      (swap! (:intervals context) conj (map (init-polling context) feeds))))
