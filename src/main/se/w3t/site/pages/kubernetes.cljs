(ns se.w3t.site.pages.kubernetes
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [se.w3t.site.markdown :as markdown]))

(defsc KubernetesPage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::KubernetesPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["kubernetes"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 ;; (comp/transact! app [`(mutations/load-url {:url ~(str utils/site-url "/md/dev_page.md")
                 ;;                                            :c ~LandingPage
                 ;;                                            :state-map {:page 'body}})])
                 (dr/route-immediate [:component/id ::KubernetesPage]))}
  (g/container {:spacing 6}
               (g/item {:xs 3}
                       (dom/h2 {:style {:color "#e8a761"}} "KUBERNETES"))
               (g/item {:xs 8}
                       (l/stack {:direction :row
                                 :jusifyContent :center
                                 :alignItems :center
                                 :spacing 14
                                 :style {:color "#b2b4bf"}}
                                (h2 "DEPLOY") (h2 "OPERATE") (h2 "SECURE") (h2 "MONITOR"))
                       (markdown/render {:body "Maintaining a Kubernetes platform can be challening. With packaged alternatives such as [OKD](https://okd.io/) and [OpenShift](https://www.redhat.com/en/technologies/cloud-computing/openshift) operational expenses can be reduced. W3T has developed a packaged Kubernetes solution based on the most up to date Open Source tools from the [CNCF](https://www.cncf.io/) (Cloud Native Computing Foundtion)."}))
               ;; (g/item {:xs 3}
               ;;         (dom/h3 {:style {:color "#a57aeb"}} "TECH"))
               ;; (g/item {:xs 8}
               ;;         (markdown/render {:body "For us, Infrastucture as Code is a must."}))
               ))
