(ns se.w3t.site.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.rad.routing :as rroute]    
            [se.w3t.site.router :as r]
            [se.w3t.site.pages.landing-page :as landing-page]
            [se.w3t.site.pages.datascience :as datascience-page]
            [se.w3t.site.pages.devops :as devops-page]
            [se.w3t.site.pages.web-development :as web-development-page]
            ;[se.w3t.site.pages.blog :as blog]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [mui.inputs :as i]
            [mui.navigation :as n]
            
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]))

(defsc Root [this {:root/keys [router] :as props}]
  {:query [{:root/router (comp/get-query r/MainRouter)}]
   :initial-state (fn [{:keys [] :as props}]
                    {:root/router (comp/get-initial-state r/MainRouter)})}
  (div {:style {:width "1000px"}}
   (g/container {:spacing 0 :alignItems :center
                 }
     (g/item {:xs 8}
       (dom/img {:style {:width "6rem"
                         :height "auto"}
                 :src "/images/w3t.png"
                 :onClick #(rroute/route-to! this landing-page/LandingPage {})}))
      (g/item {:xs 4
               }
              (l/stack {:spacing 2 :direction :row
                        }
               (i/button {:color "info"
                          :onClick #(rroute/route-to! this devops-page/DevOpsPage {})} "DevOps")
               (i/button {:color "info"
                          :onClick #(rroute/route-to! this datascience-page/DataSciencePage {})} "Data Science")
               (i/button {:color "info"
                          :onClick #(rroute/route-to! this web-development-page/WebDevelopmentPage {})} "Web")
               (i/button {:color "info"
                          :onClick #(rroute/route-to! this web-development-page/WebDevelopmentPage {})} "Contact"))))
   (r/ui-main-router router)))
