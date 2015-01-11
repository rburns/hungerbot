(ns test.hunger.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [deftest testing is]]
            [cljs.core.async :refer [<!]]
            [redis.core :refer [redis-connect]]
            [hunger.redis-store :as r]
            [hunger.store :refer [delete destroy]]
            [hunger.core :as h]))


(deftest url->feed
  (let [client (redis-connect)
        store  (r/store "p")]
    (go
      (client :set "p:feed:foo:bar" "val4")
      (client :set "p:feed:foo:baz" "val5")
      (client :set "p:feed:foo:zot" "val6")
      (is (= (<! (h/url->feed store "foo")) (h/Feed. "foo" {:bar "val4" :baz "val5" :zot "val6"})))
      (client :quit)
      (destroy store))))

(deftest add-feed
  (let [store      (r/store "redis-hunger-test")
        feed       (h/Feed. "http://foo" {:bar "baz"})
        finish     (fn [error result]
                     (is (= result [feed]))
                     (delete store "feeds")
                     (delete store ["feed" "http://foo" "bar"])
                     (destroy store))
        list-feeds (fn [error result]
                     (is (= result feed))
                     (h/list-feeds store (fn [] true) finish))]
    (h/add-feed store feed list-feeds)))

(deftest filtered-feed-list
  (let [store      (r/store "redis-hunger-test-a")
        feed-one   (h/Feed. "http://a" {:detail "zot"})
        feed-two   (h/Feed. "http://b" {:detail "bar"})
        finish     (fn [error result]
                     (is (= [feed-one] result))
                     (destroy store))
        list-feeds (fn [] (h/list-feeds store #(= "zot" (:detail (:info %))) finish))
        add-second (fn [] (h/add-feed store feed-one list-feeds))
        add-first  (fn [] (h/add-feed store feed-two add-second))]
    (add-first)))

