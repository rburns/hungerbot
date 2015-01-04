(ns carcass.core
  (:require [cljs.nodejs :as nodejs]
            [clojure.string :refer [split join]]))

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
  [slack engine]
  (fn
    []
    (println "I am connected!")))

(defn help-for-command
  [command info]
  (let [has-params       (not (= nil (:params info)))
        has-description  (not (= nil (:description info)))]
    (join " " (cond-> [(str "*" (name command) "*")]
                has-params      (conj (join "" (map #(str "<" (name %) ">") (:params info))))
                has-description (conj (str "- _" (:description info) "_"))))))

(defn help-cmd-content
  [engine]
  (let [has-description (not (= nil (:description engine)))
        has-commands    (> (count (seq (:commands engine))) 0)
        commands        (map #(help-for-command (first %) (last %)) (seq (:commands engine)))]
    (join "\n" (cond-> []
                 has-description (conj (str "`" (:description engine) "`"))
                 has-commands    (conj (join "\n" commands))))))

(defn help-cmd
  [engine]
  (let [content (help-cmd-content engine)]
    (fn
      [message slack]
      (.send (:channel message) content))))

(defn handle-message
  [slack engine]
  (fn
    [message]
    (let [parsed-message (parse-message message slack)]
      (println (str "message: " parsed-message))
      (when (:should-respond parsed-message)
        (if-let [handler (:handler ((keyword (:command parsed-message)) (:commands engine)))]
          (handler parsed-message slack)
          ((:default-response engine) parsed-message slack))))))

(defn handle-error
  [slack engine]
  (fn
    [error]
    (println "slack error:")
    (.log js/console error)))

(defn animate
  [config user-engine]
  (let [slack (Slack. (:token config) (:auto-reconnect config) (:auto-mark config))
        engine (assoc-in user-engine [:commands :help] {:handler (help-cmd user-engine)})]
    (.on slack "open" (handle-open slack engine))
    (.on slack "message" (handle-message slack engine))
    (.on slack "error" (handle-error slack engine))
    (.login slack)
    slack))


