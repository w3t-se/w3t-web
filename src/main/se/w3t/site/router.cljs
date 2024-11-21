(ns se.w3t.site.router
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.routing.dynamic-routing :refer [defrouter]]
            [com.fulcrologic.fulcro.dom :as dom]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
            [se.w3t.site.pages.landing-page :refer [LandingPage]]
            [se.w3t.site.pages.development :refer [DevelopmentPage]]
            [se.w3t.site.pages.devops :refer [DevOpsPage]]
            [se.w3t.site.pages.datascience :refer [DataSciencePage]]
            [se.w3t.site.pages.codo :refer [CodoPage]]
            [se.w3t.site.pages.kubernetes :refer [KubernetesPage]]
            [se.w3t.site.pages.contact :refer [ContactPage]]
            [se.w3t.site.pages.team :refer [TeamPage]]
            [se.w3t.site.pages.blog :refer [BlogListPage]]
            [se.w3t.site.components.blog-entry :refer [BlogEntry]]
            [se.w3t.site.pages.partnerships :refer [PartnershipsPage]]
            [se.w3t.site.pages.deploy :refer [DeployPage]]
            [mui.layout.grid :as g]
            [mui.layout :as l]))

(defrouter MainRouter [this {:keys [current-state route-factory route-props]}]
  {:always-render-body? true
   :router-targets      [LandingPage DevOpsPage DataSciencePage DevelopmentPage KubernetesPage CodoPage ContactPage BlogListPage BlogEntry TeamPage PartnershipsPage DeployPage]}
  (l/container {:id "main-router"
                :spacing 4
                :style {:overflow-x "hidden"
                        :overflow-y "visible"
                        :width "85vw"
                        :padding "0px"
                        :min-height "70vh"
                        :margin-bottom "4rem"
                        :position "relative"
                        :z-index 10}}
               (dom/div :.ui.loader {:classes [(when-not (= :routed current-state) "active")]})
               (when route-factory
                 (route-factory (comp/computed route-props (comp/get-computed this))))))

(def ui-main-router (comp/computed-factory MainRouter))
