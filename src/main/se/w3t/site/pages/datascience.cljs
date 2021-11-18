(ns se.w3t.site.pages.datascience
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2 h3 h4]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.mutations :as mutations]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils]
            [se.w3t.site.markdown :as markdown]
            [mui.layout.grid :as g]
            [mui.layout :as l]))

(defsc DataSciencePage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::DataSciencePage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["datascience-page"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (dr/route-immediate [:component/id ::DataSciencePage]))}
  (g/container {:my 0
                :spacing 4
                :justifyContent :center
                :alignItems :center}
               (dom/h1 {:style {:margin-top "6rem"
                                :color "#e8a761"}} "DATA")
               (g/item {:xs 8}
                       (l/stack {:class "category-heading"
                                 :direction :row
                                 :jusifyContent :center
                                 :alignItems :center
                                 :spacing 7
                                 :style {:color "#b2b4bf"}}
                                (h2 "COLLECT") (h2 "STORE") (h2 "TRANSFORM") (h2 "ANALYZE") (h2 "PRESENT"))
                       
                       (markdown/render {:body "# We are building a modern Data platform that can be tailor made to fit your Analytics needs
We have multiple years of experience within data related areas (Bioinformatics, Engineering)."}))
               (g/item {:xs 8}
                       (dom/h3 {:style {:color "#a57aeb"}} "TECH"))))
