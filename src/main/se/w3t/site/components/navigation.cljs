(ns se.w3t.site.components.navigation
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom :refer [div a p]]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.pages.landing-page :as landing-page]
            [se.w3t.site.pages.datascience :as datascience-page]
            [se.w3t.site.pages.devops :as devops-page]
            [se.w3t.site.pages.web-development :as web-development-page]
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
         (l/container {:height "200px"
                       :spacing 0}
                      (g/container {:height "96px"
                                    ;:px 3
                                    :spacing 0
                                    :alignItems :center}
                                   (g/item {:xs 2;:spacing 2 :direction :row
                                             :alignItems :center}
                                     (dom/img {:style {:width "6rem" :height "auto"}
                                               :src "/images/w3t-white.png"
                                               :onClick #(rroute/route-to! this landing-page/LandingPage {})}))
                                   (g/item {:xs 1.5}
                                           (dom/h4 {:style {:color "orange"}} "SERVICES"))
                                   (g/item  {:xs 1.5}
                                      (dom/h4 {:style {:color "orange"}}  "SOLUTIONS"))
                                   (g/item {:xs 7}
                                           (l/stack {
                                                     :direction "row"
                                                     :justifyContent "flex-end"}
                                                    (dom/h4 {:style {:color "orange"}}  "CONTACT")))
                                    )
                      (g/container {:height "104px"
                                    :spacing 0
                                    ;:alignItems :center
                                    }
                                   (g/item {:xs 2
                                             ;:alignItems :center
                                            })
                                   (g/item {:xs 1.5}
                                    (l/stack {:spacing 2}
                                        (a {:href ""
                                            :style {:color "#f0f0f0"
                                                    :text-decoration "none"
                                                    :user-select "none"
                                                    :onMouseDown (fn [e] nil)}
                                            :onClick #(rroute/route-to! this devops-page/DevOpsPage {})} "DevOps")
                                        (a {:href ""
                                            :style {:color "#f0f0f0"
                                                    :text-decoration "none"}
                                            :onClick #(rroute/route-to! this datascience-page/DataSciencePage {})} "Data Science")
                                        (a {:href ""
                                            :style {:color "#f0f0f0"
                                                    :text-decoration "none"}
                                            :onClick #(rroute/route-to! this datascience-page/DataSciencePage {})} "Web Development")))
                                   (g/item  {:xs 1.5}
                                           (l/stack {:spacing 2}
                                                    (a {:href ""
                                                        :style {:color "#f0f0f0"
                                                                :text-decoration "none"}
                                                        :onClick #(rroute/route-to! this web-development-page/WebDevelopmentPage {})} "Flow")
                                                    (a {:href ""
                                                        :style {:color "#f0f0f0"
                                                                :text-decoration "none"}
                                                        :onClick #(rroute/route-to! this web-development-page/WebDevelopmentPage {})} "Logical")
                                                    (a {:href ""
                                                        :style {:color "#f0f0f0"
                                                                :text-decoration "none"}
                                                        :onClick #(rroute/route-to! this web-development-page/WebDevelopmentPage {})} "Codo")))))))

(def ui-navigation (comp/factory Navigation))
