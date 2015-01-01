(ns hunger.core
  (:require [cljs.nodejs :as nodejs]
            [hunger.store :refer [fetch write collection-fetch collection-add collection-remove]]))

(defn add-feed
  [url store cb]
  (collection-add store "feeds" url cb))

(defn remove-feed
  [url store cb]
  (collection-remove store "feeds" url cb))

(defn list-feeds
  [store cb]
  (collection-fetch store "feeds" cb))

(defn last-item-in-feed
  [url store cb]
  (fetch store (str "last-item-of-" url) cb))

(defn update-last-item-in-feed
  [url item store cb]
  (write store (str "last-item-of-" url) item cb))

