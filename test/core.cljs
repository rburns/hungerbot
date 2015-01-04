(ns test.core
  (:require [cljs.nodejs :as nodejs]
            [cljs.test :refer-macros [run-tests]]
            [test.hungerbot.slack]))

(nodejs/enable-util-print!)

(run-tests 'test.hungerbot.slack)

