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
      (is (= (h/url->feed "foo" store)
             (h/Feed. "foo" {:bar "val4" :baz "val5" :zot "val6"})))
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
                     (h/list-feeds store #(%) finish))]
    (h/add-feed store feed list-feeds)))

