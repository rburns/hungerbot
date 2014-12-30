(ns hunger.store)

(defprotocol IStore
  (fetch [this item cb])
  (record [this id item cb])
  (destroy [this]))
