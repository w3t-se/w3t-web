(ns se.w3t.site.components.footer-r
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom :refer [div i a p]]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :refer [img-url]]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [se.w3t.site.markdown :as markdown]))

(defsc Footer [this {}]
  {}
  (g/container {:p 5
                :width "80vw"}
               (g/item {:xs 6}
                       (dom/h1 "W3T AB")
                       (markdown/render {:body "#### Adress
W3T AB  
Dymlingsgränd 5  
129 30 Hägersten  
Sweden
#### Email
info@w3t.se
#### Phone
+46730322499
#### Organisation number: [559324-8452](https://www.bolagsfakta.se/5593248452-W3T_AB)"}))
               (g/item {:xs 6}
                       (dom/h1 "Social")
                       (l/stack {:spacing 10
                                 :direction "row"}
                                (dom/a {:href "https://twitter.com/w3t_se"}
                                       (dom/img {:style {:width "auto" :height "3rem"}
                                                 :src "/images/twitter_black.svg"}))
                                (dom/a {:href "https://www.linkedin.com/company/w3t"}
                                       (dom/img {:style {:width "auto" :height "3rem"}
                                                 :src "/images/linkedin_black.svg"}))
                                (dom/a {:href "https://github.com/w3t-se"}
                                       (dom/img {:style {:width "auto" :height "3rem"}
                                                 :src "/images/github_black.svg"}))))))

(def ui-footer (comp/factory Footer))
