(ns site
  "The W3T landing page."
  (:require ["solid-js" :refer [createSignal onMount onCleanup]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./typewriter.cljs" :refer [Typewriter]]
            ["./starfield.cljs" :refer [Starfield]]
            ["./config.cljs" :as config])
  (:require-macros [sqeave :refer [defc]]))

(defn Arrow []
  #jsx [:svg {:viewBox "0 0 12 12" :fill "none" :stroke "currentColor" :stroke-width "1.8" :aria-hidden "true"}
        [:path {:d "M3 9l6-6M4 3h5v5"}]])

(defn Header []
  #jsx [:header {}
        [:div {:class "wrap nav"}
         [:a {:class "logo" :href "#top" :aria-label "W3T home"} "W3T"]
         [:ul {}
          [:li {:class "hide-sm"} [:a {:href "#services"} "Services"]]
          [:li {:class "hide-sm"} [:a {:href "#work"} "Blog"]]
          [:li {} [:a {:class "btn btn-accent" :href "#contact"} "Book a call"]]]]])

;; --- Countdown -------------------------------------------------------------
;; The webinar lives in the Sqeave store under [:webinar/id 0]. The start
;; comes from config.cljs on mount; anything that later calls
;; (sqeave/set! this :webinar/start ...) moves every view with it.

(defn- pad [n] (.padStart (str n) 2 "0"))

(defn- remaining [start now]
  (let [s (if start
            (js/Math.max 0 (js/Math.floor (/ (- (.getTime (js/Date. start)) now) 1000)))
            0)]
    [(pad (js/Math.floor (/ s 86400)))
     (pad (js/Math.floor (/ (mod s 86400) 3600)))
     (pad (js/Math.floor (/ (mod s 3600) 60)))
     (pad (mod s 60))]))

(defn- format-start [start]
  (.toLocaleString (js/Date. start) "en-GB"
                   {:dateStyle "long" :timeStyle "short" :timeZone "Europe/Stockholm"}))

(defc Countdown [this {:webinar/keys [id start title] :or {id 0 start nil title "Bare Metal K8S"}}]
  (let [[now set-now] (createSignal (js/Date.now))]
    (onMount
     (fn []
       (when config/webinar-start
         (sqeave/set! this :webinar/start config/webinar-start))
       (let [iv (js/setInterval #(set-now (js/Date.now)) 1000)]
         (onCleanup #(js/clearInterval iv)))))
    #jsx [:<>
          [:div {:class "countdown" :aria-label "Time until the webinar"}
           [:div {} [:b {} (nth (remaining (start) (now)) 0)] [:small {} "days"]]
           [:div {} [:b {} (nth (remaining (start) (now)) 1)] [:small {} "hours"]]
           [:div {} [:b {} (nth (remaining (start) (now)) 2)] [:small {} "minutes"]]
           [:div {} [:b {} (nth (remaining (start) (now)) 3)] [:small {} "seconds"]]]
          [:p {:class "countdown-note"}
           (if (start) (str (format-start (start)) ", Stockholm time") "Date announced soon")]
          [:a {:class "btn btn-signal" :href "#webinar"}
           (str "Webinar Signup: " (title)) [Arrow]]]))

;; --- Hero --------------------------------------------------------------------

(defn Hero []
  #jsx [:section {:class "hero"}
        [Starfield]
        [:div {:class "wrap hero-inner"}
         [:h1 {}
          [:span {} "Your"]
          [Typewriter {:class "typed-line"
                       :words ["Kubernetes" "DevOps" "Operations"]
                       :label "Kubernetes, DevOps and Operations"}]
          [:span {} "partner"]]
         [:div {:class "ctas"}
          [:a {:class "btn btn-accent" :href "#contact"} "Get in touch" [Arrow]]
          [:a {:class "btn btn-ghost" :href "#services"} "View services"]]
         [Countdown {:ident [:webinar/id 0]}]]])

(defn Trust []
  (let [names ["Länsförsäkringar" "KEYFACTOR" "SMHI" "agro"]]
    #jsx [:section {:class "trust"}
          [:div {:class "wrap"}
           [:p {} "Businesses who already trust us"]
           [:div {:class "marquee" :aria-label (.join names ", ")}
            [:div {:class "track" :aria-hidden "true"}
             (mapv (fn [n] #jsx [:span {} n]) (vec (concat names names)))]]]]))

;; --- Services ----------------------------------------------------------------

(def services
  [{:tag "Kubernetes"
    :title "The base of any modern IT stack"
    :body ["Deploy Kubernetes at speed and manage your platforms across any environment, using your current container platform or one of our enterprise partner solutions."]
    :chips ["Bare metal" "VMware exit" "OpenShift"]
    :panel-title "kubectl get nodes"
    :panel ["NAME        STATUS   ROLES           VERSION"
            "metal-01    Ready    control-plane   v1.33.4"
            "metal-02    Ready    control-plane   v1.33.4"
            "metal-03    Ready    control-plane   v1.33.4"
            "metal-04    Ready    <none>          v1.33.4"
            "metal-05    Ready    <none>          v1.33.4"]}
   {:tag "DevSecOps"
    :title "From code to deployment, standardized"
    :body ["We have long-standing experience building efficient software factories that bridge technology and teams, and adapting the development process to your organization."]
    :chips ["GitOps" "Policy as code" "Supply chain"]
    :panel-title "pipeline · main · 4f2a91c"
    :panel ["✓ 10-test.sh        unit tests      38s"
            "✓ 20-scan.sh        image scan      12s"
            "✓ 30-build.sh       build image     54s"
            "✓ 40-deploy.sh      helm upgrade    9s"
            ""
            "deployed w3t-web @ 4f2a91c"]}
   {:tag "Automation"
    :title "AI-powered automation"
    :body ["Train and deploy your models, and your integrations across your data."
           "Data needs to flow efficiently and securely, and be accessible in the right place at the right time."]
    :chips ["Data pipelines" "Model serving" "Integrations"]
    :panel-title "flow.yaml"
    :panel ["source:  erp.orders        # every 5 min"
            "steps:"
            "  - clean: drop-pii"
            "  - enrich: customer-360"
            "  - score: churn-model:v7"
            "sink:    warehouse.orders_scored"]}])

(defn Service [{:keys [s flip]}]
  #jsx [:div {:class (if flip "svc flip" "svc")}
        [:figure {:class "panel" :aria-hidden "true"}
         [:figcaption {} (:panel-title s)]
         [:pre {} (.join (:panel s) "\n")]]
        [:div {:class "svc-text"}
         [:span {:class "pill"} (:tag s)]
         [:h3 {} (:title s)]
         (mapv (fn [p] #jsx [:p {} p]) (:body s))
         [:ul {:class "chips"}
          (mapv (fn [c] #jsx [:li {} c]) (:chips s))]]])

(defn Services []
  #jsx [:section {:class "band" :id "services"}
        [:div {:class "wrap"}
         [:div {:class "center"}
          [:span {:class "pill"} "Our Services"]
          [:h2 {} "We develop IT Solutions for any and all business needs"]
          [:p {:class "lead"} "We co-develop and deploy solutions within all IT areas, from the platform all the way to development and data analysis."]]
         [:div {:class "svcs"}
          (vec (map-indexed (fn [i s] #jsx [Service {:s s :flip (odd? i)}]) services))]]])

;; --- Webinar, work, contact -------------------------------------------------

(def agenda
  [["10 min" "Why teams are leaving VMware, and what it really costs to stay or go."]
   ["15 min" "Migration paths: VMs on KubeVirt, containerise, or retire."]
   ["10 min" "Live demo: the landing zone, with segmentation and access built in."]
   ["10 min" "Questions."]])

(defn Webinar []
  #jsx [:section {:class "band" :id "webinar"}
        [:div {:class "wrap webinar"}
         [:div {:class "stack"}
          [:span {:class "pill"} "Webinar"]
          [:h2 {} "Leaving VMware for bare metal Kubernetes"]
          [:p {:class "lead left"} "45 minutes on what to move, what to keep, and what the platform looks like when you land."]
          [:a {:class "btn btn-signal" :href "#contact"} "Save my seat" [Arrow]]]
         [:ul {}
          (mapv (fn [[t d]] #jsx [:li {} [:b {} t] [:span {} d]]) agenda)]]])

(def work
  [{:who "Länsförsäkringar" :title "Containerizing a core insurance platform"
    :body "Moved a legacy application onto containers and Kubernetes, so releases became routine instead of events."}
   {:who "PrimeKey / Keyfactor" :title "A software factory for PKI products"
    :body "CI with Jenkins, packaging with Helm, and a release flow the whole team could run."}
   {:who "Agronod" :title "Data sovereignty by design"
    :body "A secure platform for agricultural data, built around who owns the data and where it may go."}
   {:who "Open source" :title "Sqeave"
    :body "Our SolidJS and squint-cljs framework. This site is built with it."}])

(defn Work []
  #jsx [:section {:class "band" :id "work"}
        [:div {:class "wrap"}
         [:div {:class "center"}
          [:span {:class "pill"} "Work"]
          [:h2 {} "What we have built"]]
         [:div {:class "work"}
          (mapv (fn [w] #jsx [:article {}
                              [:span {:class "who"} (:who w)]
                              [:h3 {} (:title w)]
                              [:p {} (:body w)]])
                work)]]])

(defn Contact []
  #jsx [:section {:class "band" :id "contact"}
        [:div {:class "wrap center"}
         [:h2 {} "Talk to an engineer"]
         [:p {:class "lead"} "Tell us where you are today. We'll tell you what the first month looks like."]
         [:a {:class "btn btn-accent" :href (str "mailto:" config/email)} "Book a call" [Arrow]]
         [:p {:class "contact-lines"}
          [:a {:href (str "mailto:" config/email)} config/email]
          [:a {:href (str "tel:" config/phone-e164)} config/phone]]]])

(defn Footer []
  #jsx [:footer {}
        [:div {:class "wrap"}
         [:span {} "© 2026 W3T AB"]
         [:a {:href "https://github.com/w3t-se"} "GitHub"]]])

(defn Site []
  #jsx [:<>
        [Header]
        [:main {:id "top"}
         [Hero]
         [Trust]
         [Services]
         [Webinar]
         [Work]
         [Contact]]
        [Footer]])
