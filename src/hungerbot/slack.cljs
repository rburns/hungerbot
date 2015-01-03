(ns hungerbot.slack
  (:require [cljs.nodejs :as nodejs]
            [clojure.string :refer [split]]
            [hungerbot.config :refer [config]]))

(def Slack (nodejs/require "slack-client"))

(defn my-id
  [slack]
  (-> slack .-self .-id))

(def user-token-re #"^<@(U[0-9A-Z]+)>:?$")

(defn token->user-id
  [token]
  (last (re-find user-token-re token)))

(defn message-is-for-me
  [text channel slack]
  (cond
    (= (my-id slack) (token->user-id (first text))) true
    (= true (.-is_im channel)) true
    :else false))

(defn parse-message
  [message slack]
  (let [text     (split (.-text message) #" ")
        channel  (.getChannelGroupOrDMByID slack (.-channel message))
        prefixed (not (= nil (token->user-id (first text))))]
    {:command        (if prefixed (first (drop 1 text)) (first text))
     :params         (if prefixed (rest (drop 1 text)) (rest text))
     :channel        channel
     :should-respond (message-is-for-me text channel slack)}))

(defn handle-open
  [slack]
  (fn
    []
    (println "I am connected!")))

(defn do-subscribe
  [message slack]
  (.send (:channel message) "I'll be able to subscribe to feeds shortly!"))

(defn do-list
  [message slack]
  (.send (:channel message) "I'll be able to list feeds shortly!"))

(defn do-remove
  [message slack]
  (.send (:channel message) "I'll be able to remove feed subscriptions shortly!"))

(defn do-join
  [message slack]
  (.send (:channel message) "I'll be able to join channels shortly!"))

(defn do-leave
  [message slack]
  (.send (:channel message) "I'll be able to leave channels shortly!"))

(defn do-help
  [message slack]
  (.send (:channel message) (str "I'll give you the feeds.\n"
                                 "*join* <channel-name> - _Join a channel._\n"
                                 "*leave* - _Leave the current channel._\n"
                                 "*subscribe* <url> - _Add a feed to the current channel._\n"
                                 "*list* - _List the feeds in the current channel._\n"
                                 "*remove* <url> - _Removes a feed from the current channel._\n")))

(defn do-default
  [message slack]
  (.send (:channel message) "I have no idea what you are talking about."))

(defn handle-message
  [slack]
  (fn
    [message]
    (let [parsed-message (parse-message message slack)]
      (println (str "message: " parsed-message))
      (when (:should-respond parsed-message)
        (condp = (:command parsed-message)
          "subscribe" (do-subscribe parsed-message slack)
          "list" (do-list parsed-message slack)
          "remove" (do-remove parsed-message slack)
          "join" (do-join parsed-message slack)
          "leave" (do-leave parsed-message slack)
          "help" (do-help parsed-message slack)
          (do-default parsed-message slack))))))

(defn handle-error
  [slack]
  (fn
    [error]
    (println "slack error:")
    (.log js/console error)))

(defn slack
  []
  (let [slack (Slack. (:token config) (:auto-reconnect config) (:auto-mark config))]
    (.on slack "open" (handle-open slack))
    (.on slack "message" (handle-message slack))
    (.on slack "error" (handle-error slack))
    (.login slack)
    slack))


