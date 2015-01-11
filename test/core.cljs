(ns test.core
  (:require [cljs.nodejs :as nodejs]
            [cljs.test :refer-macros [run-tests]]
            [test.helpers]
            [test.carcass.core]
            [test.redis.core]
            [test.hunger.redis-store]
            [test.hunger.core]))

(nodejs/enable-util-print!)

(run-tests 'test.helpers)
(run-tests 'test.carcass.core)
(run-tests 'test.redis.core)
(run-tests 'test.hunger.redis-store)
(run-tests 'test.hunger.core)

