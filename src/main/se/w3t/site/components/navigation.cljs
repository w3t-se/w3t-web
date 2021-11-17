(ns se.w3t.site.components.navigation
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom :refer [div a p]]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.pages.landing-page :as landing-page]
            [se.w3t.site.pages.datascience :as datascience-page]
            [se.w3t.site.pages.devops :as devops-page]
            [se.w3t.site.pages.development :as development-page]
            [se.w3t.site.pages.codo :as codo-page]
            [se.w3t.site.pages.kubernetes :as kubernetes-page]
            [se.w3t.site.pages.contact :as contact-page]
            [mui.inputs :as i]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [mui.surfaces :as s]
            [mui.navigation :as n]
            [mui.transitions :as t]
            [se.w3t.site.fontawesome.icons :as fa]))

(defsc Navigation [this {}]
  {:initLocalState (fn [] {:open false})}
  ;; t/slide {:appear true
  ;;           :direction "down"}
  ;; n/drawer {:anchor :top
  ;;           :open true}
  
  (t/collapse {:in (comp/get-state this :open)
               :collapsedSize "96px"
               :onMouseOver #(comp/set-state! this {:open true})
               :onMouseLeave #(comp/set-state! this {:open false})}
              (g/container {:spacing 0
                            :style {:width "100vw"
                             :alignItems "center"
                             :justifyContent "center"
                             :overflow "hidden"}}
                           (g/container {:height "96px"
                                         :width "80vw"
                                         :alignItems "center"
                                         :justifyContent "center"
                                         :spacing 0}
                                        (g/item {:xs 1
                                                 :alignItems :center}
                                                (dom/a {:href "#"}
                                                       (dom/img {:style {:width "6rem" :height "auto"}
                                                                 :src "/images/w3t-white.png"
                                                                 :onClick #(rroute/route-to! this landing-page/LandingPage {})})))
                                        (g/item {:xs 1}
                                                (dom/h4 {:style {:user-select "none"
                                                                 :color "#e8a761"
                                                                 }} "SERVICES"))
                                        (g/item  {:xs 1}
                                                 (dom/h4 {:style {:user-select "none"
                                                                  :color "#e8a761"
                                                                  }}  "COMMUNITY"))
                                        (g/item {:xs 7}
                                                (l/stack {
                                                          :direction "row"
                                                          :justifyContent "flex-end"}
                                                         (a {:href "#"
                                                             :style {
                                                                     :color "#f0f0f0"
                                        ;:hover {:color "#000"}
                                                                     :text-decoration "none"
                                                                     :onMouseDown (fn [e] nil)}
                                                             :onClick #(rroute/route-to! this contact-page/ContactPage {})} (dom/h4 {:style {:color "#e8a761"}} "CONTACT"))
                                                         ))
                                        ))
         (g/container {    ;:height "104px"
                                        ;:py 2
                       :style {:padding-bottom "4rem"}
                       :spacing 0
                                        ;:alignItems :center
                       }
                      (g/item {:xs 2.8
                                        ;:alignItems :center
                               })
                      (g/item {:xs 0.82}
                              (l/stack {:spacing 2}
                                       (a {:href "#"
                                           :style {:color "#f0f0f0"
                                        ;:hover {:color "#000"}
                                                   :text-decoration "none"
                                                   :user-select "none"}
                                           :onClick #(rroute/route-to! this kubernetes-page/KubernetesPage {})} "Kubernetes")
                                       (a {:href "#"
                                           :style {:color "#f0f0f0"
                                        ;:hover {:color "#000"}
                                                   :text-decoration "none"
                                                   :user-select "none"}
                                           :onClick #(rroute/route-to! this devops-page/DevOpsPage {})} "DevOps")
                                       (a {:href "#"
                                           :style {:color "#f0f0f0"
                                                   :text-decoration "none"}
                                           :onClick #(rroute/route-to! this datascience-page/DataSciencePage {})} "Data Science")
                                       (a {:href "#"
                                           :style {:color "#f0f0f0"
                                                   :text-decoration "none"}
                                           :onClick #(rroute/route-to! this development-page/DevelopmentPage {})} "Development")))
                      (g/item  {:xs 1}
                               (l/stack {:spacing 2}
                                        ;; (a {:href "#"
                                        ;;     :style {:color "#f0f0f0"
                                        ;;             :text-decoration "none"}
                                        ;;     :onClick #(rroute/route-to! this web-development-page/WebDevelopmentPage {})} "Flow")
                                        ;; (a {:href "#"
                                        ;;     :style {:color "#f0f0f0"
                                        ;;             :text-decoration "none"}
                                        ;;     :onClick #(rroute/route-to! this web-development-page/WebDevelopmentPage {})} "Logical")
                                        (a {:href "#"
                                            :style {:color "#f0f0f0"
                                                    :text-decoration "none"}
                                            :onClick #(rroute/route-to! this codo-page/CodoPage {})} "Codo")))
                      ;; (g/item {:xs 7}
                      ;;         (l/stack {:direction "row"
                      ;;                   :justifyContent "flex-end"}
                      ;;                  (l/stack {:spacing 2
                      ;;                            :style {:text-align "right"}}
                      ;;                           (dom/a {:href "mailto:info@w3t.se"
                      ;;                                   :style {:color "#ebb871"; "orange"
                      ;;                                           }} "info@w3t.se")
                      ;;                           (dom/text "+46730322499"))))
                      )))

(def ui-navigation (comp/factory Navigation))
