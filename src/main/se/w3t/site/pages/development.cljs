(ns se.w3t.site.pages.development
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.mutations :as mutations]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :as utils :refer [md->html]]
            [se.w3t.site.markdown :as markdown]
            [mui.layout.grid :as g]
            [mui.layout :as l]
            [mui.surfaces :as s]))

(defsc DevelopmentPage [this {:keys []}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::DevelopmentPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["development"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (dr/route-immediate [:component/id ::DevelopmentPage]))}
  (g/container {:my 0
                :spacing 4
                :justifyContent :center
                :alignItems :center}
               (g/item {:xs 8}
                       (l/stack {:direction :row
                                 :jusifyContent :center
                                 :alignItems :center
                                 :spacing 10
                                 :style {:color "#b2b4bf"}}
                                (h2 "DESIGN") (h2 "PLAN") (h2 "BUILD") (h2 "DEPLOY") (h2 "MAINTAIN"))
                       (dom/h1 {:style {:margin-top "6rem"
                                        :color "#e8a761";"#c640de"
                                        }} "DEVELOPMENT")
                       (markdown/render {:body "We have multiple years of experience within data related areas (Bioinformatics, Engineering)."}))
               (g/item {:xs 8}
                       (dom/h3 {:style {:color "#a57aeb"}} "WEB")
                       (markdown/render {:body "We are developing Single Page Applications (SPAs) and have built up expertise over the past few years with a focus on the Clojure(Script) universe." }))
               (g/item {:xs 8}
                       (dom/h3 {:style {:color "#a57aeb";"#c640de"
                            }} "CASES")
                       
                       )))
