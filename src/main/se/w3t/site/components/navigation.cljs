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
            [se.w3t.site.pages.team :as team-page]
            [se.w3t.site.pages.blog :as blog-page]
            [se.w3t.site.pages.partnerships :as partnerships-page]
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
                            :alignItems "center"
                            :justifyContent "center"}
                           (g/container {:height "96px"
                                         :width "80vw"
                                         :alignItems "center"
                                         :justifyContent "center"
                                         :spacing 0}
                                        (g/item {:xs 1}
                                                (dom/a {:style {:cursor "pointer"}}
                                                       (dom/img {:style {:width "6rem" :height "auto"}
                                                                 :src "/images/w3t-white.png"
                                                                 :onClick #(rroute/route-to! this landing-page/LandingPage {})})))
                                        (l/stack {:direction "row"
                                                  :spacing 8
                                                  :justifyContent "flex-end"}
                                                 (dom/text :.navbar-heading "SERVICES")
                                                 (dom/text :.navbar-heading "COMMUNITY")
                                                 (a {:onClick #(rroute/route-to! this blog-page/BlogListPage {})} (dom/text :.navbar-heading "BLOG")))
                                        (g/item {:xs 7}
                                                (l/stack {:direction "row"
                                                          :spacing 8
                                                          :justifyContent "flex-end"}
                                                         (a {:class "navbar-link no-select"
                                                             :style {:color "#ebb871"}
                                                             :onClick #(rroute/route-to! this team-page/TeamPage {})} "TEAM")
                                                         (a {:class "navbar-link no-select"
                                                             :style {:color "#ebb871"}
                                                             :onClick #(rroute/route-to! this contact-page/ContactPage {})} "CONTACT")))))
              (g/container {:style {:padding-bottom "4rem"}
                            :spacing 0}
                      (g/item {:xs 2.76})
                      (g/item {:xs 0.68}
                              (l/stack {:spacing 2}
                                       (a {:class "navbar-link no-select"
                                           :onClick #(rroute/route-to! this kubernetes-page/KubernetesPage {})} "Kubernetes")
                                       (a {:class "navbar-link no-select"
                                           :onClick #(rroute/route-to! this devops-page/DevOpsPage {})} "DevOps")
                                       (a {:class "navbar-link no-select"
                                           :onClick #(rroute/route-to! this datascience-page/DataSciencePage {})} "Data Science")
                                       (a {:class "navbar-link no-select"
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
                                        (a {:class "navbar-link no-select"
                                            :onClick #(rroute/route-to! this codo-page/CodoPage {})} "Codo")
                                        (a {:class "navbar-link no-select"
                                            :onClick #(rroute/route-to! this partnerships-page/PartnershipsPage {})} "Partners")))
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
