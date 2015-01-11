(ns hunger.store)

(defprotocol IStore
  (fetch [this id])
  (write [this id item])
  (delete [this id])
  (collection-fetch [this id])
  (collection-add [this id item])
  (collection-remove [this id item])
  (destroy [this]))
