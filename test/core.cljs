(ns test.core
  (:require [cljs.nodejs :as nodejs]
            [cljs.test :refer-macros [run-tests]]
            [test.carcass.core]))

(nodejs/enable-util-print!)

(run-tests 'test.carcass.core)

