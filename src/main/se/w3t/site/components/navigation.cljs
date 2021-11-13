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
  {
   :initLocalState (fn [] {:open false})}
  ;; t/slide {:appear true
  ;;           :direction "down"}
  ;; n/drawer {:anchor :top
  ;;           :open true}
  
(t/collapse {:in (comp/get-state this :open)
             :collapsedSize "96px"
             :onMouseOver #(comp/set-state! this {:open true})
             :onMouseLeave #(comp/set-state! this {:open false})}

         (g/container {:height "200px"
                       :width "100vw"
                       :px 6
                       :spacing 0 ;:alignItems :center
                       }
                      (g/item {:xs 8}
                              (dom/img {:style {:width "6rem" :height "auto"}
                                        :src "/images/w3t.png"
                                        :onClick #(rroute/route-to! this landing-page/LandingPage {})}))
                      (g/item {:xs 4
                               :py 2}
                              (l/stack {:spacing 2 :direction :row}
                                        ;(fa/icon ["fal" "coffee"])
                                       (l/stack {:spacing 1}
                                                (dom/h4 "SERVICES")
                                        (a {:href ""
                                            :style {:color "grey"}
                                            :onClick #(rroute/route-to! this devops-page/DevOpsPage {})} "DevOps")
                                        (a {:href ""
                                            :style {:color "grey"}
                                            :onClick #(rroute/route-to! this datascience-page/DataSciencePage {})} "Data Science"))
                                       (l/stack {:spacing 1}
                                                (dom/h4 "PLATFORMS")
                                                (i/button {:color "info"
                                                           :onClick #(rroute/route-to! this datascience-page/DataSciencePage {})} "Data Science"))
                                       (l/stack {:spacing 1}
                                                (dom/h4 "CASES")
                                                (a {:onClick #(rroute/route-to! this web-development-page/WebDevelopmentPage {})} "Web")))))))

(def ui-navigation (comp/factory Navigation))
