(ns site
  "The shared layout and the home page."
  (:require ["solid-js" :refer [createEffect]]
            ["@solidjs/router" :refer [A useLocation]]
            ["./typewriter.cljs" :refer [Typewriter]]
            ["./starfield.cljs" :refer [Starfield]]
            ["./webinar.cljs" :refer [Countdown short-title]]
            ["./config.cljs" :as config]))

(defn Arrow []
  #jsx [:svg {:viewBox "0 0 12 12" :fill "none" :stroke "currentColor" :stroke-width "1.8" :aria-hidden "true"}
        [:path {:d "M3 9l6-6M4 3h5v5"}]])

(defn Header []
  #jsx [:header {}
        [:div {:class "wrap nav"}
         [A {:class "logo" :href "/" :aria-label "W3T home"} "W3T"]
         [:ul {}
          [:li {:class "hide-sm"} [A {:href "/#services"} "Services"]]
          [:li {:class "hide-sm"} [A {:href "/webinar" :activeClass "nav-current"} "Webinar"]]
          [:li {:class "hide-sm"} [A {:href "/#work"} "Blog"]]
          [:li {} [A {:class "btn btn-accent" :href "/book"} "Book a call"]]]]])

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
          [A {:class "btn btn-accent" :href "/book"} "Get in touch" [Arrow]]
          [A {:class "btn btn-ghost" :href "/#services"} "View services"]]
         [Countdown {:ident [:webinar/id 0]}]
         [A {:class "btn btn-signal" :href "/webinar"} (str "Webinar Signup: " short-title) [Arrow]]]])

(defn Trust []
  (let [names ["Länsförsäkringar" "KEYFACTOR" "SMHI" "agronod"]]
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

;; --- Work and contact --------------------------------------------------

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
         [A {:class "btn btn-accent" :href "/book"} "Book a call" [Arrow]]
         [:p {:class "contact-lines"}
          [:a {:href (str "mailto:" config/email)} config/email]
          [:a {:href (str "tel:" config/phone-e164)} config/phone]]]])

(defn Footer []
  #jsx [:footer {}
        [:div {:class "wrap"}
         [:span {} "© 2026 W3T AB"]
         [:a {:href "https://github.com/w3t-se"} "GitHub"]]])

(defn Home []
  #jsx [:<>
        [Hero]
        [Trust]
        [Services]
        [Work]
        [Contact]])

(defn- scroll-to-hash
  "After a route change, show the section the link named (/#services),
   or the top of the page."
  [hash]
  (js/requestAnimationFrame
   (fn []
     (if-let [el (and (not= "" hash) (js/document.getElementById (.slice hash 1)))]
       (.scrollIntoView el)
       (js/window.scrollTo 0 0)))))

(defn Layout
  "The router's root: header and footer around the current route."
  [props]
  (let [loc (useLocation)]
    (createEffect (fn [] (.-pathname loc) (scroll-to-hash (.-hash loc))))
    #jsx [:<>
          [Header]
          [:main {:id "top"} (:children props)]
          [Footer]]))
