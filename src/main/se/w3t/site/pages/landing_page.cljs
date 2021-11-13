(ns se.w3t.site.pages.landing-page
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.markdown :as markdown]
            [mui.layout :as l]))

(defsc LandingPage [this {:keys []}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::LandingPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["home"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 ;; (comp/transact! app [`(mutations/load-url {:url ~(str utils/site-url "/md/landing_page.md")
                 ;;                                            :c ~LandingPage
                 ;;                                            :state-map {:page 'body}})])
                 (dr/route-immediate [:component/id ::LandingPage]))}
  (l/container {}
;;        (dom/img {:style {:flex "50%"
;; :class "ui large image"
;;                          :position "aboslute"
                         
;;                          :top "0px"
;;                          :width "auto"
;;                          :height "800px"}
;;              :src "/images/back1.jpg"})
   (markdown/render {:body "# We are geeks within DevOps, Data Science and Web Development!
## At W3T we strive to create ready packaged solutions from the latest Open Source components and frameworks that can then be tailor made and deployed in Customer systems. Our Professional Services arm have a combined several decades of experience of building, deploying and succesfully running Products within our core areas: DevOps, Data Science and "})

   (markdown/render {:body "# Collaborative Open-Source Projects built by Independent Contributor Communities
Our goal is to grow at a and to contribute as much as possible to the Open Source community b. Our main effort in this area is the builders of the Codo Community which aims to create a platform for independent individuals to contribute to great Projects while retaining a piece of the Cake through shared ownsership."}))
  )
