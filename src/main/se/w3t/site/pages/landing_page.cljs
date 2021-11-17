(ns se.w3t.site.pages.landing-page
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.markdown :as markdown]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [se.w3t.site.components.nodes-graph :refer [ui-nodes]]
            [se.w3t.site.pages.datascience :as datascience-page]
            [se.w3t.site.pages.devops :as devops-page]
            [se.w3t.site.pages.development :as development-page]
            [se.w3t.site.pages.codo :as codo-page]
            [se.w3t.site.pages.kubernetes :as kubernetes-page]))

;; (defn setup []
;;   (q/frame-rate 35))

;; (defn t []
;;   (* 0.001 (q/millis)))

;; (defn calc-y [x mid amp]
;;   (+ mid (* (q/sin (+ (t) x)) amp)))

;; (defn wave [step mid-y amp]
;;   (let [w (q/width)
;;         h (q/height)
;;         mult (q/map-range w
;;                           700 200
;;                           0.01 0.03)]
;;     (q/begin-shape)
;;     (q/vertex 0 h) ; lower left corner
;;     (doseq [x (range (- w) (+ step w) step)]
;;       (let [t (* x mult)
;;             y (calc-y t mid-y amp)]
;;         (q/vertex x y)))
;;     (q/vertex w h) ; lower right corner
;;     (q/end-shape)))

;; (defn draw []
;;   (q/background 255)
;;   (q/stroke 255 250)
;;   (q/fill 50 230 (+ (* 20 (q/sin (t))) 230) 40)
;;   (let [h (q/height)
;;         move-down (/ h 5)
;;         amp (/ h 8)]
;;     (doseq [y (range move-down (+ amp h) 8)]
;;       (let [x-step (- (* y 0.8) move-down)]
;;         (wave x-step y amp)))))

;; (q/defsketch waves
;;    :host "abc"
;;    :size [1200 500]
;;    :setup setup
;;    :draw draw)

(defsc LandingPage [this {:keys []}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::LandingPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["home"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 ;; (comp/transact! app [`(mutations/load-url {:url ~(str utils/site-url "/md/landing_page.md")
                 ;;                                            :c ~LandingPage
                 ;;                                            :state-map {:page 'body}})])
                 (dr/route-immediate [:component/id ::LandingPage]))}
  ;; (dom/div {:style {:position "absolute"
  ;;                   :z-index "-1"
  ;;                   :top 0
  ;;                   :width "100vw"}
  ;;           :id "abc"})
  (g/container {;:py 8
                :spacing 4
                :alignItems "center"
                :justifyContent "center"
                }
   ;;        (dom/img {:style {:flex "50%"
   ;; :class "ui large image"
   ;;                          :position "aboslute"
   
   ;;                          :top "0px"
   ;;                          :width "auto"
   ;;                          :height "800px"}
   ;;              :src "/images/back1.jpg"})
               (g/item {:xs 6}
                       (dom/h1 {:style {:font "64 IBM Plex Sans, sans-serif"
                                        :font-size 64
                                        :color "white";"#8fde40"
                                        }} "meet {
           'breed of geeks'.next() }"))
   (g/item {:xs 6
            :id "nodes"}
           (ui-nodes)
           ;; (dom/img {:style {;:flex "50%"
           ;;                   ;:class "ui large image"
           ;;                   ;:position "aboslute"
                             
           ;;                   ;:top "0px"
           ;;                   :width "auto"
           ;;                   :height "20rem"
           ;;                   }
           ;;           :src "/images/kubernetes.svg"})
           )
   (g/item {:xs 12}
           (dom/h3 {:style {:color "#a57aeb";"#c640de"
                            }} "PASSIONS")
           (markdown/render {:body "# We are a bunch of Geeks with a common passion for DevOps, Data Science and Software Development!
W3T strives to create **packaged solutions** from the latest Open Source components that can be tailor-made to your needs and deployed in the Cloud or your Container based infrastructure. Our Professional Services Team has a combined multiple years of experience of building, deploying and succesfully operating Projects within our core areas: DevOps, Data Science and Software Development on top of Kubernetes."}))
   ;(g/item {:xs 6})
;;    (g/item {:xs 12}
;;            (markdown/render {:body "```clojure
;; (defn [x] (+ 1 1))
;; ```"}))
   (g/item {:xs 12}
           (dom/h3 {:style {:color "#a57aeb";"#c640de"
                            }} "USE CASES"))
   (g/item {:xs 6}
           "I need expertise on how to Deploy and Operate a modern Container platform. "
           (dom/h4 {:style {:color "#bdeb71"; "orange"
                            :float "right"}
                    :onClick #(rroute/route-to! this kubernetes-page/KubernetesPage {})} (a {:href "#"
                                                                                             :style {:color "#bdeb71"
                                                                                                     :text-decoration "none"}} "Kubernetes >")))
   (g/item {:xs 6}
           (l/stack {:direction "row"
                     :spacing 2
                     ;:justifyContent "flex-end"
                     ;:alignItems "center"
                     }
                    (dom/img {:style {:width "auto" :height "6rem"}
                              :src "/images/kubernetes-icon-white.png"})
                    ;; (dom/img {:style {:width "auto" :height "6rem"}
                    ;;           :src "/images/OpenShift-LogoType.svg"})
                    ))
   (g/item {:xs 6}
           "I need to deploy a proper DevOps flow for my Team and Corresponding IT Infrastructure. "
           (dom/h4 {:style {:color "#bdeb71";"orange"
                           :href ""
                           :float "right"}
                    :onClick #(rroute/route-to! this devops-page/DevOpsPage {})} (a {:href "#"
                                                                                     :style {:color "#bdeb71"
                                                                                             :text-decoration "none"}} "DevOps >")))
   (g/item {:xs 6}
           (l/stack {:direction "row"
                     :spacing 2
                     ;:justifyContent "flex-end"
                     ;:justifyContent "center"
                     :alignItems "center"}
                    (dom/img {:style {:width "auto" :height "6rem"}
                              :src "/images/devops_white.png"})
                    ;; (dom/img {:style {:width "auto" :height "6rem"}
                    ;;           :src "/images/OpenShift-LogoType.svg"})
                    ))
   (g/item {:xs 6}
           (l/stack {:direction "row"
                     :spacing 2
                     :justifyContent "flex-end"
                     :alignItems "center"}
                    (dom/img {:style {:width "auto" :height "6rem"}
                              :src "/images/dna_white.png"})))
   (g/item {:xs 6}
           (markdown/render {:body "I want to deploy an Analytics Stack to transform my Data into actionable Knowledge."})
           
           (dom/h4 {:style {:color "#bdeb71"; "orange"
                            }
                    :onClick #(rroute/route-to! this datascience-page/DataSciencePage {})} (a {:href "#"
                                   :style {:color "#bdeb71"
                                           :text-decoration "none"}} "Data >")))
   (g/item {:xs 6}
           (l/stack {:direction "row"
                     :spacing 2
                     :justifyContent "flex-end"
                     :alignItems "center"}
                    (dom/img {:style {:width "auto" :height "6rem"}
                              :src "/images/code_white.svg"})))
   (g/item {:xs 6}
           (markdown/render {:body "I have a Web or other Software Project that I need to get started on."})
           (dom/h4 {:style {:color "#bdeb71"; "orange"
                            }
                    :onClick #(rroute/route-to! this development-page/DevelopmentPage {})} (a {:href "#"
                                   :style {:color "#bdeb71"
                                           :text-decoration "none"}} "Development >")))
   
   (g/item {:xs 12}
           (dom/h3 {:style {:color "#a57aeb";"#c640de"
                            }} "OPEN SOURCE")
           (markdown/render {:body "# Collaborative Projects built by Independent Contributor Communities
Our goal is to grow with and contribute to the Open Source Community by making Open Collaboration a built in feature of how we work. This is why we base our main recruiting around Codo, a Web Platform and Online Community of independent Contributors. Using Codo individuals can come together to work on Open Projects while retaining a piece of the Cake through shared ownsership."})
              (g/item {:my "2rem"
                       :xs 6}
                      (l/stack {:direction "row"
                                :spacing 2}
                               (dom/img {:style {:width "auto" :height "4rem"}
                                         :src "/images/codo3_2_web.svg"})))
              (dom/h4 {:style {:color "#bdeb71"
                               }} (a {:href "#"
                                      :style {:color "#bdeb71"
                                              :text-decoration "none"}} "Codo >")))))
