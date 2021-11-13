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
  (g/container {:p 8
                :px 12
                :style {:background-color "#000000"
                        :color "white"
                        :width "100vw"}}
               (g/item {:xs 6}
                       (markdown/render {:body "# W3T AB

#### Adress
W3T AB  
Dymlingsgränd 5  
129 30 Hägersten  
Sweden
#### Phone +46730322499
#### Organisation number: [559324-8452](https://www.bolagsfakta.se/5593248452-W3T_AB)"}))
               (g/item {:xs 6}
                       (markdown/render {:body "# "}))))

(def ui-footer (comp/factory Footer))
