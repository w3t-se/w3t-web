(ns se.w3t.site.router
  (:require 
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.routing.dynamic-routing :refer [defrouter]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
            [se.w3t.site.pages.landing-page :refer [LandingPage]]
            [se.w3t.site.pages.web-development :refer [WebDevelopmentPage]]
            [se.w3t.site.pages.devops :refer [DevOpsPage]]
            [se.w3t.site.pages.datascience :refer [DataSciencePage]]
            [mui.layout :as l]
            ))

(defrouter MainRouter [this {:keys [current-state route-factory route-props]}]
  {:always-render-body? true
   :router-targets      [LandingPage DevOpsPage DataSciencePage WebDevelopmentPage]}
  (l/container {:id "main-router"
                :style {:overflow-x "hidden"
                        ;:height "calc(100vh-96)"
                        }
                ;:width "100vw"
                }
   (dom/div :.ui.loader {:classes [(when-not (= :routed current-state) "active")]})
   (when route-factory
     (route-factory (comp/computed route-props (comp/get-computed this))))))

(def ui-main-router (comp/computed-factory MainRouter))
