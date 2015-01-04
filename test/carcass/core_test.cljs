(ns test.carcass.core
  (:require [cljs.test :refer-macros [deftest testing is]]
            [carcass.core :as c]))

(deftest help-for-command
  (is (= (c/help-for-command :foo {:description "a foo"})
          "*foo* - _a foo_"))
  (is (= (c/help-for-command :foo {:description "a foo" :params [:bar]})
         "*foo* <bar> - _a foo_")))

(deftest help-cmd-content
  (is (= (c/help-cmd-content {})
         ""))
  (is (= (c/help-cmd-content {:description "a description" :commands {}})
         "`a description`"))
  (is (= (c/help-cmd-content {:description "a description"
                              :commands {:one {}
                                         :two {:description "a"}}})
         "`a description`\n*one*\n*two* - _a_")))
