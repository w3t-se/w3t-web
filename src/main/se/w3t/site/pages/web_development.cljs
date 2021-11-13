(ns se.w3t.site.pages.web-development
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.mutations :as mutations]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :as utils :refer [md->html]]
            [se.w3t.site.markdown :as markdown]
            [mui.layout.grid :as g]
            [mui.surfaces :as s]))

(defsc WebDevelopmentPage [this {:keys []}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::WebDevelopment])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["web-development"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (dr/route-immediate [:component/id ::WebDevelopment]))}
  (g/container {:height "500px"
                :style {:background-color "#f7f8fa"}
                :spacing 0
                :alignItems :center}
               (g/item {:xs 6}
                       (dom/h1 {:style {:font-size "64"
                                        :color "#1a1103"}} "Responsive "
                               (dom/text {:style {:color "#e69f35"}} "Single Page Applications") " for Web and Mobile"))
               (g/item {:xs 6}
                       (dom/text "We are developing Single Page Applications (SPAs) and have built up expertise over the past few years with a focus on the Clojure(Script) universe.")

                       (dom/iframe {:width "560" :height "315"
                                    :src "https://www.youtube.com/embed/c2xzpi28oC8"
                                    :title "YouTube video player"
                                    :frameborder 0 :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                    :allowfullscreen true}))
               (markdown/render {:body "# The future is decentralized  
               With the introduction of Ethereum and related Decentralized technologies (IPFS, ENS) we belive that the future of the Web is going to be Decentralized. Web 3.0 technologies will be taking over classical Client-Server architectures and along with Blockchain this will enable new types of decentralized Organisations governed by Democratic rules written in Code rather than in natural language.
"})))
