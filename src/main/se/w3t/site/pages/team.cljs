(ns se.w3t.site.pages.team
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils]
            [mui.layout :as l]
            [mui.navigation :as n]
            [mui.layout.grid :as g]
            [mui.transitions :as t]
            [se.w3t.site.markdown :as markdown]
            [mui.inputs :as i]
            [mui.icons :as ico]
            [se.w3t.site.components.team-item :as team-item]))

(defsc TeamPage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::TeamPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["team"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (dr/route-immediate [:component/id ::TeamPage]))}
  (g/container {:spacing 6}
               (g/item {:xs 3}
                       (dom/h2 {:style {:margin 0
                                        :color "#e8a761"}} "TEAM"))
               (l/image-list {:variant "quilted"
                              :sx {:width 800 :height 1200}
                              :cols 2
                              :rowHeight 221}
                             (team-item/ui-team-item {:name "Daniel Hermansson"
                                                      :source "https://storageapi.fleek.co/zertan-team-bucket/profiles/daniel.jpg"
                                                      :github "zertan"})
                             (team-item/ui-team-item
                              {:name "Robin Josef"
                               :source "https://storageapi.fleek.co/zertan-team-bucket/profiles/robin.jpg"
                               :github "16pj"}))))
