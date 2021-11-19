(ns se.w3t.site.pages.codo
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
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
  (g/container {:spacing 6}
               (g/item {:xs 3}
                       (dom/h2 {:style {:color "#e8a761"}} "CODO"))
               (g/item {:xs 8}
                       (markdown/render {:body "With the introduction of Ethereum and related Decentralized technologies (IPFS, ENS) we belive that the future of the Web is going to be Decentralized. Web 3.0 technologies will be taking over classical Client-Server architectures and along with Blockchain this will enable new types of decentralized Organisations governed by Democratic rules written in Code rather than in natural language."})
                       (dom/iframe {:width "700" :height "394"
                                    :src "https://www.youtube.com/embed/c2xzpi28oC8"
                                    :title "YouTube video player"
                                    :frameborder 0
                                    :allow "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                                    :allowfullscreen true
                                    :style {:margin-top "4rem"
                                            :margin-bottom "4rem"}})
                       (markdown/render {:body "  
With Codo we want to make running any kind of remote Project as simple as possible by connecting Contributors togehter on one Online Hub. Codo is a Web App that includes Project management features as well as a Social Component where Individuals can create Skill Profiles and apply to join any Project with a click of a button. Codo is Open, Transparent with Decentralized Project Governance (voting of Decisions) and Shared Ownership.

The Codo Project is our way of enabling the Decentralized Future." }))))
