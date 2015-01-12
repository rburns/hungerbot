(ns hungerbot.core
  (:require [cljs.nodejs :as nodejs]
            [clojure.string :refer [join]]
            [carcass.core :as carcass :refer [token->url]]
            [hunger.core :refer [Feed list-feeds fetch-feed add-feed remove-feed poll-for-feeds]]
            [hunger.store :refer [destroy]]
            [hunger.redis-store :as redis-store]))

(nodejs/enable-util-print!)

(def config (:config (js->clj (nodejs/require "../config.js") :keywordize-keys true)))

(defn Feed->desc
  [feed]
  (:url feed))

(defn subscribe-cmd
  [store]
  {:description "Add a feed to the current channel."
   :params [:url]
   :handler (fn [message slack]
              (let [channel (:channel message)]
                (if-let [url (token->url (-> message :params first))]
                  (let [feed (Feed. url {:channel (.-name channel)})]
                    (add-feed store feed (fn [error, result]
                                           (if (= nil error)
                                             (.send channel "Duly noted, sir.")
                                             (.send channel "Something went wrong.")))))
                  (.send channel "Hardly seems you should be looking at that."))))})

(defn list-cmd
  [store]
  {:description "List the feeds in the current channel."
   :handler  (fn [message slack]
               (let [sieve   (fn [feed] (= (-> message :channel .-name) (-> feed :info :channel)))
                     done (fn [error, result]
                            (if (= nil error)
                              (if (= 0 (count result))
                                (.send (:channel message) "Got no feeds, sorry.")
                                (.send (:channel message) (join "\n" (map Feed->desc result))))
                              (.send (:channel message) "Not sure what to say.")))]
                 (list-feeds store sieve done)))})

(defn remove-cmd
  [store]
  {:description "Removes a feed from the current channel."
   :params [:url]
   :handler (fn [message slack]
              (let [feed  (Feed. (token->url (-> message :params first)) {})
                    sieve (fn [feed] (= (-> message :channel .-name) (-> feed :info :channel)))
                    done  (fn [error result]
                            (.send (:channel message) "Scrubbed clean."))
                    scrub (fn [error result]
                            (if (= nil result)
                              (.send (:channel message) "Uh, can't find that one.")
                              (remove-feed store result done)))]
                (fetch-feed store feed sieve scrub)))})

(defn default-response
  [message slack]
  (.send (:channel message) "I have no idea what you are talking about."))

(defn post-items-to-channel
  [slack]
  (fn [items]))

(defn -main []
  (let [store (redis-store/store)
        slack (carcass/animate {:description "I'll give you the feeds."
                                :config (:slack config)
                                :commands {:subscribe (subscribe-cmd store)
                                           :list (list-cmd store)
                                           :remove (remove-cmd store)}
                                :default-response default-response})]
    (poll-for-feeds (-> config :hunger :default-interval) (post-items-to-channel slack))))

(set! *main-cli-fn* -main)
