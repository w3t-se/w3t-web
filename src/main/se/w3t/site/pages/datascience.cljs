(ns se.w3t.site.pages.datascience
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.mutations :as mutations]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils]
            [se.w3t.site.markdown :as markdown]
            [mui.layout.grid :as g]))

(defsc DataSciencePage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::DataSciencePage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["datascience-page"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (dr/route-immediate [:component/id ::DataSciencePage]))}
  (g/container {:height "500px"
                :style {:background-color "#f7f8fa"}
                :spacing 0
                :alignItems :center}
               (g/item {:xs 6}
                       (markdown/render {:body "# We are building a modern Data platform that can be tailor made to fit your Analytics needs
We have multiple years of experience within data related areas (Bioinformatics, Engineering)."}))))
