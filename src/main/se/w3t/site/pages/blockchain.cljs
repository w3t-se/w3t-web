(ns se.w3t.site.pages.blockchain
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
            [se.w3t.site.utils :as utils :refer [img-url]]
            [mui.layout.grid :as g]
            [se.w3t.site.markdown :as markdown]))

(defsc BlockchainPage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::BlockchainPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["partners"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (let [app-element (.getElementById js/document "app")]
                   (.scrollTo app-element 0 0))
                 (dr/route-immediate [:component/id ::BlockchainPage]))}
  (dom/div
   (g/container {:spacing 6
                 :alignItems "center"}
                (g/item {:xs 3}
                        (dom/h2 {:style {:margin 0
                                         :color "#ffa500ff"}} "WEB3"))
                (g/item {:xs 8}
                        (dom/img {:style {:width "800px" :height "300px"
                                         :object-fit "cover"
                                         ;:object-position "-10% 0"
                                         }
                                 :src (str img-url "data-top.jpg")}))
                (g/item {:xs 3}
                        #_(dom/img {:style {:width "auto" :height "6rem"
                                          :margin-left "1rem"}
                                  :src (str "Logo-Red_Hat-Ready-Business_Partner-Solution_Provider-A-Reverse-RGB.svg")}))
                (g/item {:xs 8}
                        (markdown/render {:body "Web3 Technologies and dApps are quickly changing the way modern young businesses operate and build innovations on the modern web. Gone are the days of large entities building platforms where they take the larger share of the profits. Enter a era of new Open Marketplaces where the main content Contributors get the larger share of the cake. Enter Web3.

[What is web3?](https://hbr.org/2022/05/what-is-web3)

We have experience within the Web3 and Blockchaion areas having developed our own solutions on top of decentralized Technologies. We can aid in breaking down and developing your Blockchain project including:
 - Smart Contract development (Solidity, cairo)
 - dApp (frontend) development
 - Integration with decentralized databases and storage (IPFS, Ceramic, OrbisDB, ComposeDB, Aarweave)
 - Integration with exisiting Smart Contracts and Web3 ecosystem (finance, governance, deFi)
 - Recommendations on how to integrate Blockchain and decentralized Governance in your business
 - Business formation and Tokenization examples (non-legal recommendations)"})))))
