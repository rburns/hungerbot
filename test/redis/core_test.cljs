(ns test.redis.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.test :refer-macros [deftest testing is]]
            [helpers :refer [unordered-equal]]
            [cljs.core.async :refer [<!]]
            [redis.core :as r]))

(deftest sanity
  (let [client (r/redis-connect)]
    (go
      (is (= "OK" (<! (client :set "key" "value"))))
      (is (= "value" (<! (client :get "key"))))
      (is (= 1 (<! (client :del "key"))))
      (client :quit))))

(deftest wildcard-keys
  (let [client (r/redis-connect)]
    (go
      (is (= "OK" (<! (client :set "one:two" "value"))))
      (is (= "OK" (<! (client :set "one:three" "value"))))
      (is (unordered-equal ["one:three" "one:two"] (<! (client :keys "one:*"))))
      (is (= 1 (<! (client :del "one:two"))))
      (is (= 1 (<! (client :del "one:three"))))
      (client :quit))))

