(ns se.w3t.site.components.team-item
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
            [mui.icons :as ico]))

(defsc TeamItem [this {:keys [name source github]}]
  {:initLocalState (fn [] {:hover false})}
  (let [hover? (comp/get-state this :hover)]
    (l/image-list-item {:onMouseOver #(comp/set-state! this {:hover true})
                        :onMouseLeave #(comp/set-state! this {:hover false})}
                       (dom/img {:loading "lazy"
                                 :src source})
                       (when hover?
                         (l/image-list-item-bar {:title name
                                                 :subtitle (a {:href (str "https://github.com/" github)} (str "@" github))
                                                 :actionIcon (i/icon-button {:sx {:color "rgba(255, 255, 255, 0.54)"}} (ico/chevron-right {}))})))))

(def ui-team-item (comp/factory TeamItem))
