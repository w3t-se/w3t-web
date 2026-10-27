(ns index
  (:require ["solid-js/web" :refer [render]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./site.cljs" :refer [Site]]
            ["./styles.css"])
  (:require-macros [sqeave :refer [defc]]))

(defc Root [this {:root/keys [id] :or {id 0}}]
  #jsx [Site])

(let [e (js/document.getElementById "root")]
  (set! (.-innerHTML e) "")
  (render (fn [] #jsx [sqeave/SqeaveProvider {} [Root {:ident [:root/id 0]}]]) e))
