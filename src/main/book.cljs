(ns book
  "The /book page: a call booked through HubSpot Meetings, embedded."
  (:require ["./config.cljs" :as config]))

(defn- embed-url [url]
  (str url (if (.includes url "?") "&" "?") "embed=true"))

(defn BookPage []
  #jsx [:section {:class "band book"}
        [:div {:class "wrap"}
         [:div {:class "center"}
          [:span {:class "pill"} "Book a call"]
          [:h1 {:class "page-title"} "Talk to an engineer"]
          [:p {:class "lead"} "30 minutes on where you are today and what the first month could look like. Pick a time that suits you."]]
         (if (not= "" config/hubspot-meetings-url)
           #jsx [:div {:class "booking"}
                 [:iframe {:title "Book a call with W3T"
                           :src (embed-url config/hubspot-meetings-url)
                           :loading "lazy"}]
                 [:p {:class "form-note"}
                  "Calendar not showing? "
                  [:a {:href config/hubspot-meetings-url :target "_blank" :rel "noopener"} "Open the booking page"]
                  " or email " [:a {:href (str "mailto:" config/email)} config/email] "."]]
           #jsx [:div {:class "booking-fallback"}
                 [:p {} "Email us and we'll find a time:"]
                 [:p {:class "contact-lines"}
                  [:a {:href (str "mailto:" config/email)} config/email]
                  [:a {:href (str "tel:" config/phone-e164)} config/phone]]])]])
