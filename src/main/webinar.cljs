(ns webinar
  "The webinar: its countdown (shared with the home page) and the /webinar
   page with the signup form. Details come from webinar.json, which the
   server reads too, so the date lives in one place."
  (:require ["solid-js" :refer [createSignal onMount onCleanup]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./starfield.cljs" :refer [Starfield]]
            ["./source.cljs" :as source]
            ["../../webinar.json$default" :as webinar])
  (:require-macros [sqeave :refer [defc]]))

(defn- pad [n] (.padStart (str n) 2 "0"))

(defn- remaining [start now]
  (let [s (if start
            (js/Math.max 0 (js/Math.floor (/ (- (.getTime (js/Date. start)) now) 1000)))
            0)]
    [(pad (js/Math.floor (/ s 86400)))
     (pad (js/Math.floor (/ (mod s 86400) 3600)))
     (pad (js/Math.floor (/ (mod s 3600) 60)))
     (pad (mod s 60))]))

(defn format-start [start]
  (str (.toLocaleString (js/Date. start) "en-GB"
                        {:weekday "long" :day "numeric" :month "long" :year "numeric"
                         :hour "2-digit" :minute "2-digit" :timeZone "Europe/Stockholm"})
       ", Stockholm time"))

(def short-title (:shortTitle webinar))

;; The webinar lives in the Sqeave store under [:webinar/id 0]. Every
;; Countdown reads the same entity, so the home page and /webinar agree.
(defc Countdown [this {:webinar/keys [id start] :or {id 0 start nil}}]
  (let [[now set-now] (createSignal (js/Date.now))]
    (onMount
     (fn []
       (when-not (start)
         (sqeave/set! this :webinar/start (:start webinar)))
       (let [iv (js/setInterval #(set-now (js/Date.now)) 1000)]
         (onCleanup #(js/clearInterval iv)))))
    #jsx [:<>
          [:div {:class "countdown" :aria-label "Time until the webinar"}
           [:div {} [:b {} (nth (remaining (start) (now)) 0)] [:small {} "days"]]
           [:div {} [:b {} (nth (remaining (start) (now)) 1)] [:small {} "hours"]]
           [:div {} [:b {} (nth (remaining (start) (now)) 2)] [:small {} "minutes"]]
           [:div {} [:b {} (nth (remaining (start) (now)) 3)] [:small {} "seconds"]]]
          [:p {:class "countdown-note"}
           (if (start) (format-start (start)) "Date announced soon")]]))

;; --- Signup form -------------------------------------------------------------

(defn- post-signup [body]
  (-> (js/fetch "/api/webinar/signup"
                {:method "POST"
                 :headers {"Content-Type" "application/json"}
                 :body (js/JSON.stringify body)})
      (.then (fn [res]
               (-> (.json res)
                   (.catch (fn [_] {}))
                   (.then (fn [data]
                            (if (.-ok res)
                              {:ok true}
                              {:ok false
                               :error (or (:error data)
                                          "We could not register you just now. Try again, or email info@w3t.se.")}))))))
      (.catch (fn [_] {:ok false :error "Could not reach the server. Check your connection and try again."}))))

(defn SignupForm []
  (let [[name set-name] (createSignal "")
        [email set-email] (createSignal "")
        [company set-company] (createSignal "")
        [role set-role] (createSignal "")
        [consent set-consent] (createSignal false)
        [website set-website] (createSignal "")
        [status set-status] (createSignal "idle")
        [error set-error] (createSignal "")
        submit (fn [e]
                 (.preventDefault e)
                 (set-status "sending")
                 (set-error "")
                 (-> (post-signup {:name (name) :email (email) :company (company) :role (role)
                                   :consent (consent) :website (website)
                                   :src (source/current)})
                     (.then (fn [r]
                              (if (:ok r)
                                (set-status "done")
                                (do (set-error (:error r)) (set-status "idle")))))))]
    #jsx [:div {:class "signup"}
          (if (= (status) "done")
            #jsx [:div {:class "signup-done" :role "status"}
                  [:h3 {} "You're registered"]
                  [:p {} "We've sent a confirmation with a calendar invite to "
                   [:b {} (email)] ". The join link follows by email before we start."]]
            #jsx [:form {:onSubmit submit :novalidate true}
                  [:h3 {} "Save your seat"]
                  [:p {:class "form-note"} "Free, online, 45 minutes. We send the join link by email."]
                  [:label {:for "su-name"} "Name"]
                  [:input {:id "su-name" :name "name" :autocomplete "name" :required true
                           :value (name) :onInput #(set-name (.. % -target -value))}]
                  [:label {:for "su-email"} "Work email"]
                  [:input {:id "su-email" :name "email" :type "email" :autocomplete "email" :required true
                           :value (email) :onInput #(set-email (.. % -target -value))}]
                  [:div {:class "row2"}
                   [:div {}
                    [:label {:for "su-company"} "Company " [:span {:class "opt"} "(optional)"]]
                    [:input {:id "su-company" :name "company" :autocomplete "organization"
                             :value (company) :onInput #(set-company (.. % -target -value))}]]
                   [:div {}
                    [:label {:for "su-role"} "Role " [:span {:class "opt"} "(optional)"]]
                    [:input {:id "su-role" :name "role" :autocomplete "organization-title"
                             :value (role) :onInput #(set-role (.. % -target -value))}]]]
                  [:div {:class "hp" :aria-hidden "true"}
                   [:label {:for "su-website"} "Website"]
                   [:input {:id "su-website" :name "website" :tabindex "-1" :autocomplete "off"
                            :value (website) :onInput #(set-website (.. % -target -value))}]]
                  [:label {:class "check" :for "su-consent"}
                   [:input {:id "su-consent" :type "checkbox" :checked (consent)
                            :onChange (fn [e] (set-consent (.. e -target -checked)) (set-error ""))}]
                   [:span {} "W3T may store my name and email to send me the join link and the recording. I can ask to be removed at any time."]]
                  (when (not= "" (error))
                    #jsx [:p {:class "form-error" :role "alert"} (error)])
                  [:button {:class "btn btn-signal" :type "submit" :disabled (= (status) "sending")}
                   (if (= (status) "sending") "Registering…" "Register")]])]))

;; --- The page ------------------------------------------------------------------

(defn WebinarPage []
  #jsx [:<>
        [:section {:class "hero page-hero"}
         [Starfield]
         [:div {:class "wrap hero-inner"}
          [:span {:class "pill"} "Webinar"]
          [:h1 {:class "page-title"} (:title webinar)]
          [:p {:class "lead"} (:summary webinar)]
          [Countdown {:ident [:webinar/id 0]}]]]
        [:section {:class "band"}
         [:div {:class "wrap webinar"}
          [:div {:class "stack"}
           [:h2 {} "What we'll cover"]
           [:ul {:class "agenda"}
            (mapv (fn [[t d]] #jsx [:li {} [:b {} t] [:span {} d]]) (:agenda webinar))]
           [:p {:class "form-note"}
            "For IT and platform teams weighing what comes after VMware. Can't make it? Register anyway and we'll send you the recording."]]
          [SignupForm]]]])
