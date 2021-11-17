(ns se.w3t.site.pages.contact
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

(defsc ContactPage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::ContactPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["contact"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 ;; (comp/transact! app [`(mutations/load-url {:url ~(str utils/site-url "/md/dev_page.md")
                 ;;                                            :c ~LandingPage
                 ;;                                            :state-map {:page 'body}})])
                 (dr/route-immediate [:component/id ::ContactPage]))}
  
  (g/container {:my 0
                :spacing 4
                :justifyContent :center
                ;:alignItems :center
                }
               (g/item {:xs 6}
                       (dom/h1 {:style {:margin-top "6rem"
                                        :color "#e8a761"
                                        }} "W3T AB"))
               (g/item {:xs 6})
               (g/item {:xs 6}
                       (dom/iframe {:width 500 :height 400
                                    :style {:border 0}
                                    :loading "lazy"
                                    :allowfullscreen true
                                    :src "https://www.google.com/maps/embed/v1/place?key=AIzaSyBoqk02RBThPjYn09OQqaLzxE2h49OrNjM&q=Dymlingsgränd+5,Hägersten"}))
               (g/item {:xs 6}
                       (markdown/render {:body "## Please do not hesitate to reach out to us. We are happy to help and eager to answer your questions about our Service Areas and Offerings!"})
                       (markdown/render {:body "#### Adress  
Dymlingsgränd 5  
129 30 Hägersten  
Sweden
#### Email: info@w3t.se
#### Phone: +46730322499
#### Organisation number: [559324-8452](https://www.bolagsfakta.se/5593248452-W3T_AB)"}))))
