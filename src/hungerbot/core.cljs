(ns hungerbot.core
  (:require [cljs.nodejs :as nodejs]
            [carcass.core :refer [animate token->url]]
            [hunger.core :refer [list-feeds add-feed remove-feed]]
            [hunger.store :refer [destroy]]
            [hunger.redis-store :as redis-store]))

(nodejs/enable-util-print!)

(def config (:config (js->clj (nodejs/require "../config.js") :keywordize-keys true)))

(def store (atom nil))

(def subscribe-cmd
  {:description "Add a feed to the current channel."
   :params [:url]
   :handler (fn [message slack]
              (let [channel (:channel message)]
                (if-let [url (token->url (-> message :params first))]
                  (add-feed url @store (fn [error, result]
                                         (if (= nil error)
                                           (.send channel "Duly noted, sir.")
                                           (.send channel "Something went wrong."))))
                  (.send channel "Hardly seems you should be looking at that."))))})

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
  (reset! store (redis-store/store))
  (animate {:description "I'll give you the feeds"
            :config config
            :commands {:subscribe subscribe-cmd
                       :list list-cmd
                       :remove remove-cmd}
            :default-response default-response}))

(set! *main-cli-fn* -main)
