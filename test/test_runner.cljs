(ns test-runner
  (:require [cljs.nodejs :as nodejs]
            [cljs.test :refer-macros [run-tests]]
            [test-helpers]
            [carcass.core-test]
            [redis.core-test]
            [hunger.redis-store-test]
            [hunger.core-test]))

(nodejs/enable-util-print!)

(run-tests 'test-helpers)
(run-tests 'carcass.core-test)
(run-tests 'redis.core-test)
(run-tests 'hunger.redis-store-test)
(run-tests 'hunger.core-test)

