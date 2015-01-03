(ns hungerbot.core
  (:require [cljs.nodejs :as nodejs]
            [hungerbot.slack :refer [slack]]
            [hunger.core :refer [list-feeds add-feed remove-feed]]
            [hunger.store :refer [destroy]]
            [hunger.redis-store :refer [store]]))

(nodejs/enable-util-print!)

(def join-cmd
  {:description "Join a channel."
   :params [:channel]
   :handler (fn [message slack]
              (.send (:channel message) "I'll be able to join channels shortly!"))})

(def leave-cmd
  {:description "Leave the current channel."
   :handler (fn [message slack]
              (.send (:channel message) "I'll be able to leave channels shortly!"))})

(def subscribe-cmd
  {:description "Add a feed to the current channel."
   :params [:url]
   :handler (fn [message slack]
              (.send (:channel message) "I'll be able to subscribe to feeds shortly!"))})

(def list-cmd
  {:description "List the feeds in the current channel."
   :handler  (fn [message slack]
               (.send (:channel message) "I'll be able to list feeds shortly!"))})

(def remove-cmd
  {:description "Removes a feed from the current channel."
   :params [:url]
   :handler (fn [message slack]
              (.send (:channel message) "I'll be able to remove feed subscriptions shortly!"))})

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
