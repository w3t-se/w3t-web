(ns se.w3t.site.pages.devops
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.mutations :as mutations]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [se.w3t.site.markdown :as markdown]))

(defsc DevOpsPage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::DevOpsPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["devops-page"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (dr/route-immediate [:component/id ::DevOpsPage]))}
  (g/container {:my 0
                ;:py 8
                :spacing 4
                :justifyContent :center
                :alignItems :center}
               (g/item {:xs 8}
                       (l/stack {:direction :row
                                 :jusifyContent :center
                                 :alignItems :center
                                 :spacing 3
                                 :style {:color "#b2b4bf"}}
                                (h2 "DEVELOPERS") (h2 "CODE") (h2 "PIPELINES") (h2 "ARTIFACTS") (h2 "INFRASTRUCTURE"))
                       (dom/h1 {:style {:margin-top "6rem"
                                        :color "#e8a761"}} "DEVOPS")
                       (markdown/render {:body "We have multiple years of experience within data related areas (Bioinformatics, Engineering)."}))
               (g/item {:xs 8}
                       (dom/h3 {:style {:color "#a57aeb";"#c640de"
                            }} "TECH"))
               (g/item {:xs 8}
                       (dom/h3 {:style {:color "#a57aeb"}} "CASES")
                       
                       )))
