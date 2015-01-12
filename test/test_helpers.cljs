(ns test-helpers
  (:require [cljs.test :refer-macros [deftest is]]))

(defn unordered-equal
  [container terms]
  (= (sort container) (sort terms)))

(deftest containing-test
  (is (= false (unordered-equal [1 2 3] [4])))
  (is (= true (unordered-equal [1 2 3] [3 2 1])))
  (is (= true (unordered-equal [1 2 3] [1 3 2])))
  (is (= true (unordered-equal [1 2 3] [3 1 2]))))
