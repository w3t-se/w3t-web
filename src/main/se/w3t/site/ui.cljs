(ns se.w3t.site.ui
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.rad.routing :as rroute]    
            [se.w3t.site.router :as r]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [mui.navigation :as n]
            [se.w3t.site.components.footer-r :as footer]
            [se.w3t.site.components.navigation :as navigation]
))

(defsc Root [this {:root/keys [router] :as props}]
  {:query [{:root/router (comp/get-query r/MainRouter)}]
   :initial-state (fn [{:keys [] :as props}]
                    {:root/router (comp/get-initial-state r/MainRouter)})}
  (div {:style {:width "100vw"}}
       (navigation/ui-navigation)
       (r/ui-main-router router)
       (footer/ui-footer)))
