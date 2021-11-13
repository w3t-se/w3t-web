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
  (g/container {:mt 24
                :p 8
                :px 12
                :style {:background-color "#f0f0f0"
                        :color "black"
                        :width "100vw"}}
               (g/item {:xs 6}
                       ;(dom/h1 {:style {:color "orange"}} "W3T AB")
                       (markdown/render {:body "# W3T AB

#### Adress
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
                       (markdown/render {:body "# Social"}))))

(def ui-footer (comp/factory Footer))
