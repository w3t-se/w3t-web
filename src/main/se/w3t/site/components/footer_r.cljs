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
  (dom/div {:style {:overflow "visible"
                    :position "relative"}}
           (dom/div {:class "container"
                     :style {:overflow "visible"
                             :width "100vw"}})
           (g/container {:p 5
                         :style {:z-index 20
                                 :opacity 0.8}}
                        (g/item {:xs 6}
                                (dom/h1 "W3T AB")
                                (markdown/render {:body "#### Email: info@w3t.se
#### Organisation number: [559324-8452](https://www.allabolag.se/foretag/w3t-ab/h%C3%A4gersten/konsulter/2KI2Q4KI5YF3I)"}))
                        (g/item {:xs 6}
                                (l/stack {:spacing 6}
                                         (dom/h1 "Social")
                                         (l/stack {:spacing 6
                                                   :direction "row"}
                                                  (dom/a {:href "https://twitter.com/w3t_se"}
                                                         (dom/img {:style {:width "auto" :height "3rem"}
                                                                   :src (str img-url "twitter_black.svg")}))
                                                  (dom/a {:href "https://www.linkedin.com/company/w3t"}
                                                         (dom/img {:style {:width "auto" :height "3rem"}
                                                                   :src (str img-url "linkedin_black.svg")}))
                                                  (dom/a {:href "https://github.com/w3t-se"}
                                                         (dom/img {:style {:width "auto" :height "3rem"}
                                                                   :src (str img-url "github_black.svg")})))
                                         (markdown/render {:body "**Red Hat® and OpenShift® are trademarks of Red Hat, Inc., registered in the United States and other countries.*"}))))))

(def ui-footer (comp/factory Footer))
