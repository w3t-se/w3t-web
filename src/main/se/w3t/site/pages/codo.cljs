(ns se.w3t.site.pages.codo
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.mutations :as mutations]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :as utils :refer [md->html]]
            [se.w3t.site.markdown :as markdown]
            [mui.layout :as l]
            [mui.layout.grid :as g]))

(defsc CodoPage [this {:keys []}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::Codo])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["codo"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (dr/route-immediate [:component/id ::Codo]))}
  (g/container {:spacing 8
                :justifyContent :center
                :alignItems :center}
               ;; (g/item {:xs 6}
               ;;         ;; (dom/h1 {:style {:font-size "64"
               ;;         ;;                  :color "#1a1103"}} "Responsive "
               ;;         ;;         (dom/text {:style {:color "#e69f35"}} "Codo") " for Web and Mobile")
               ;;         )
               (g/item {:xs 8}
                       (dom/h1 {:style {:margin-top "6rem"
                                        :color "#e8a761";"#c640de"
                                        }} "CODO")
                       (markdown/render {:body "# 
With the introduction of Ethereum and related Decentralized technologies (IPFS, ENS) we belive that the future of the Web is going to be Decentralized. Web 3.0 technologies will be taking over classical Client-Server architectures and along with Blockchain this will enable new types of decentralized Organisations governed by Democratic rules written in Code rather than in natural language.
"}))
               (g/item {:xs 8}
                       (dom/iframe {:width "700" :height "394"
                                    :src "https://www.youtube.com/embed/c2xzpi28oC8"
                                    :title "YouTube video player"
                                    :frameborder 0
                                    :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                    :allowfullscreen true}))))
