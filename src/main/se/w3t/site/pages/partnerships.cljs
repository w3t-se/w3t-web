(ns se.w3t.site.pages.partnerships
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils]
            [se.w3t.site.pages.blog :as blog-page]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [se.w3t.site.markdown :as markdown]))

(defsc PartnershipsPage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::PartnershipsPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["partners"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (let [app-element (.getElementById js/document "app")]
                   (.scrollTo app-element 0 0))
                 (dr/route-immediate [:component/id ::PartnershipsPage]))}
  (dom/div
   (g/container {:spacing 6
                 :alignItems "center"}
                (g/item {:xs 3}
                        (dom/h2 {:style {:color "#ffa500ff"}} "PARTNERS"))
                (g/item {:xs 8})
                (g/item {:xs 3}
                        (dom/img {:style {:width "auto" :height "6rem"
                                          :margin-left "1rem"}
                                  :src (str "Logo-Red_Hat-Ready-Business_Partner-Solution_Provider-A-Reverse-RGB.svg")}))
                (g/item {:xs 8}
                        (markdown/render {:body "We are an official Red Hat®* Partner within the Solution Provider area. We specialize in Kubernetes and specifically Red Hat® OpenShift®. Red Hat® has a prominent Partner Program and perform most of Solution Deployments via Partners. [Official Profile Pages](https://redhat.secure.force.com/finder/PFPartnerDetail?id=0013a00001qXOf8AAG)"}))
                (g/item {:xs 3}
                        (dom/a {:href "https://www.parallelconsulting.com/"}
                               (dom/img {:style {:width "auto" :height "6rem"
                                                 :margin-left "2rem"}
                                         :src "/images/parallel.jpeg"})))
                (g/item {:xs 8}
                        (markdown/render {:body "Parallel Consulting offer Services within their core areas Data, Infrastructure and Software Development and are active in the UK, USA, Netherlands and the Nordics. [Parallel Consulting](https://www.parallelconsulting.com/)"})))))
