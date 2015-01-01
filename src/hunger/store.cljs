(ns hunger.store)

(defprotocol IStore
  (fetch [this id cb])
  (write [this id item cb])
  (delete [this id cb])
  (collection-fetch [this id cb])
  (collection-add [this id item cb])
  (collection-remove [this id item cb])
  (destroy [this]))
