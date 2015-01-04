(ns test.hungerbot.slack
  (:require [cljs.test :refer-macros [deftest testing is]]
            [hungerbot.slack :as s]))

(deftest help-for-command
  (is (= (s/help-for-command :foo {:description "a foo"})
          "*foo* - _a foo_"))
  (is (= (s/help-for-command :foo {:description "a foo" :params [:bar]})
         "*foo* <bar> - _a foo_")))

(deftest help-cmd-content
  (is (= (s/help-cmd-content {})
         ""))
  (is (= (s/help-cmd-content {:description "a description" :commands {}})
         "`a description`"))
  (is (= (s/help-cmd-content {:description "a description"
                              :commands {:one {}
                                         :two {:description "a"}}})
         "`a description`\n*one*\n*two* - _a_")))
