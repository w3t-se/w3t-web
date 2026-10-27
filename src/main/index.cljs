(ns index
  (:require ["solid-js/web" :refer [render]]
            ["@solidjs/router" :refer [Router Route]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./site.cljs" :refer [Layout Home]]
            ["./webinar.cljs" :refer [WebinarPage]]
            ["./book.cljs" :refer [BookPage]]
            ["./source.cljs" :as source]
            ["./styles.css"])
  (:require-macros [sqeave :refer [defc]]))

(defn NotFound []
  #jsx [:section {:class "band"}
        [:div {:class "wrap center"}
         [:h1 {:class "page-title"} "Page not found"]
         [:p {:class "lead"} "That page doesn't exist. Try the home page."]
         [:a {:class "btn btn-accent" :href "/"} "Go home"]]])

(defc Root [this {:root/keys [id] :or {id 0}}]
  #jsx [Router {:root Layout}
        [Route {:path "/" :component Home}]
        [Route {:path "/webinar" :component WebinarPage}]
        [Route {:path "/book" :component BookPage}]
        [Route {:path "*404" :component NotFound}]])

(source/capture!)

(let [e (js/document.getElementById "root")]
  (set! (.-innerHTML e) "")
  (render (fn [] #jsx [sqeave/SqeaveProvider {} [Root {:ident [:root/id 0]}]]) e))
