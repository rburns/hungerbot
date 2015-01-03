(ns hungerbot.core
  (:require [cljs.nodejs :as nodejs]
            [hungerbot.slack :refer [slack]]
            [hunger.core :refer [list-feeds add-feed remove-feed]]
            [hunger.store :refer [destroy]]
            [hunger.redis-store :refer [store]]))

(nodejs/enable-util-print!)

(defn join-cmd
  [message slack]
  (.send (:channel message) "I'll be able to join channels shortly!"))

(defn leave-cmd
  [message slack]
  (.send (:channel message) "I'll be able to leave channels shortly!"))

(defn subscribe-cmd
  [message slack]
  (.send (:channel message) "I'll be able to subscribe to feeds shortly!"))

(defn list-cmd
  [message slack]
  (.send (:channel message) "I'll be able to list feeds shortly!"))

(defn remove-cmd
  [message slack]
  (.send (:channel message) "I'll be able to remove feed subscriptions shortly!"))

(defn default-response
  [message slack]
  (.send (:channel message) "I have no idea what you are talking about."))

(defn -main []
  (let [store (store)
        slack (slack {:commands {:join join-cmd
                                 :leave leave-cmd
                                 :subscribe subscribe-cmd
                                 :list list-cmd
                                 :remove remove-cmd}
                      :default-response default-response})]))

(set! *main-cli-fn* -main)
