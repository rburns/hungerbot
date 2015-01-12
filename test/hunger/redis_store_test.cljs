(ns hunger.redis-store-test
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [deftest testing is]]
            [test-helpers :refer [unordered-equal]]
            [cljs.core.async :refer [<!]]
            [hunger.store :refer [fetch write destroy collection-add collection-fetch delete
                                  collection-contains?]]
            [hunger.redis-store :as r]))

(deftest normalize-key
  (is (= (r/normalize-key "prefix" "foo")
         "prefix:foo"))
  (is (= (r/normalize-key "prefix" ["foo"])
         "prefix:foo"))
  (is (= (r/normalize-key "prefix" ["foo" "bar"])
         "prefix:foo:bar")))

(deftest collection-contains?-test
  (let [store (r/store "redis-store-test-e")]
    (go
      (is (= 1 (<! (collection-add store "key" "value1"))))
      (is (= true (<! (collection-contains? store "key" "value1"))))
      (is (= false (<! (collection-contains? store "key" "value2"))))
      (<! (delete store "key"))
      (destroy store))))

(deftest write-fetch
  (let [store (r/store "redis-store-test")]
    (go
      (is (= "OK" (<! (write store "key" "value"))))
      (is (= "value" (<! (fetch store "key"))))
      (is (= 1 (<! (delete store "key"))))
      (destroy store))))

(deftest composite-write-fetch
  (let [store (r/store "redis-store-test")]
    (go
      (is (= "OK" (<! (write store ["key1" "key2"] "value"))))
      (is (= "value" (<! (fetch store ["key1" "key2"]))))
      (delete store ["key1" "key2"])
      (destroy store))))

(deftest wildcard-fetch
  (let [store (r/store "redis-store-test")]
    (go
      (write store ["foo" "bar"] "val1")
      (write store ["foo" "baz"] "val2")
      (is (= {:bar "val1" :baz "val2"} (<! (fetch store ["foo" "*"]))))
      (is (= 1 (<! (delete store ["foo" "bar"]))))
      (is (= 1 (<! (delete store ["foo" "baz"]))))
      (destroy store))))

(deftest wildcard-delete
  (let [store (r/store "redis-store-test-b")]
    (go
      (write store ["foo" "bar"] "val1")
      (write store ["foo" "baz"] "val2")
      (is (= 2 (<! (delete store ["foo" "*"]))))
      (is (= {} (<! (fetch store ["foo" "*"]))))
      (destroy store))))

(deftest collection-add-fetch-remove
  (let [store (r/store "redis-store-test")]
    (go
      (is (= 1 (<! (collection-add store "key1" "value1"))))
      (is (= 1 (<! (collection-add store "key1" "value2"))))
      (is (unordered-equal ["value1" "value2"] (<! (collection-fetch store "key1"))))
      (is (= 1 (<! (delete store ["key1"]))))
      (destroy store))))

(deftest composite-collection-add-fetch-remove
  (let [store (r/store "redis-store-test")]
    (go
      (is (= 1 (<! (collection-add store ["key3" "key4"] "value1"))))
      (is (= 1 (<! (collection-add store ["key3" "key4"] "value2"))))
      (is (unordered-equal ["value1" "value2"] (<! (collection-fetch store ["key3" "key4"]))))
      (is (= 1 (<! (delete store ["key3" "key4"]))))
      (destroy store))))
