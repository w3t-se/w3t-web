(ns se.w3t.site.pages.web-development
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.mutations :as mutations]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :as utils :refer [md->html]]
            [se.w3t.site.markdown :as markdown]))

(defsc WebDevelopmentPage [this {:keys []}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::WebDevelopment])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["web-development"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 ;; (comp/transact! app [`(mutations/load-url {:url ~(str utils/site-url "/md/landing_page.md")
                 ;;                                            :c ~LandingPage
                 ;;                                            :state-map {:page 'body}})])
                 (dr/route-immediate [:component/id ::WebDevelopment]))}
  (div {:style {:display "flex"}}
       (dom/img {:style {:flex "50%"
                                        ;:class "ui large image"
                         :position "aboslute"
                         
                         :top "0px"
                         :width "auto"
                         :height "800px"}
             :src "/images/back1.jpg"})
   (div {;:class "content-segment"
         :style {:flex "50%"
                 :background-color "#2e2e2e"
                 :width "800px"
                 ;:margin-left "40rem"
                 :font-size "3rem"
                 }}
        (markdown/render {:body "# Responsive Single Page Applications for Web and Mobile
                                 We are developing Single Page Applications (SPAs) and have built up expertise over the past few years with a focus on the Clojure(Script) universe."})
        (dom/h2 "Cases")))
  )
