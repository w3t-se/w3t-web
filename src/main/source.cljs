(ns source
  "Which campaign a visitor came from: the ?src= tag on the page they
   landed on, kept for the session so it survives a click from the home
   page to /webinar. Sent with the webinar signup.")

(def ^:private storage-key "w3t-src")
(def ^:private state {:src ""})

(defn capture!
  "Call once at startup. A tag in the URL wins over a stored one."
  []
  (let [from-url (or (.get (js/URLSearchParams. js/window.location.search) "src") "")]
    (try
      (if (not= "" from-url)
        (.setItem js/sessionStorage storage-key from-url)
        (aset state :src (or (.getItem js/sessionStorage storage-key) "")))
      (catch :default _ nil))
    (when (not= "" from-url) (aset state :src from-url))))

(defn current [] (:src state))
