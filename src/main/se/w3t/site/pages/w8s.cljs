(ns se.w3t.site.pages.w8s
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [se.w3t.site.markdown :as markdown]
            [se.w3t.site.components.team-item :as team-item]))

(defsc W8sPage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::ContactPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["contact"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (let [app-element (.getElementById js/document "app")]
                   (.scrollTo app-element 0 0))
                 ;; (comp/transact! app [`(mutations/load-url {:url ~(str utils/site-url "/md/dev_page.md")
                 ;;                                            :c ~LandingPage
                 ;;                                            :state-map {:page 'body}})])
                 (dr/route-immediate [:component/id ::W8sPage]))}

  (g/container {:my 0
                :spacing 5
                :justifyContent :center
                :alignItems :center}

               ))
