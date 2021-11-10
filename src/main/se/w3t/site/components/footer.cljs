(ns se.w3t.site.components.footer
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom :refer [div i a p]]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :refer [img-url]]))

(defsc Footer [this {}]
  {}
  (div {:class "ui text menu"
                        :style {:margin "0px"
                                :height "10px"
                                :max-height "10px"
                                :padding "0px"
                                }}
       (div :.right.menu         
            (dom/span {:class "item"
                       :style {:height "1rem" 
                                        ;:position "relative"
                                        ;:bottom "10px"
                                        ;:left "230px"
                               :color "#babcbf"}}
                      (dom/i :.icon.copyright)
                      " Web3 Technologies 2021 "
                      (div {:class "item link"
                            :target "_blank"
                            :href "https://github.com/w3t-se"
                            :style {:padding "3px"}}
                           (dom/i {:class "icon github"
                                   :style {:padding 0
                                           :margin 0}}))
                      ;; (div {:class "item link"
                      ;;       :href "https://fulcro.fulcrologic.com/"
                      ;;       :style {:padding "3px"}}
                      ;;      (dom/i {:class "icon linkedin"
                      ;;              :style {:padding 0
                      ;;                      :margin 0}}))
                      (div {:class "item link"
                            :target "_blank"
                            :href "https://twitter.com/w3t_se"
                            :style {:padding "3px"}}
                           (dom/i {:class "icon twitter"
                                   :style {:padding 0
                                           :margin 0}}))
                      " Powered by "
                      (a {:class "item link"
                          :target "_blank"
                          :href "http://ethereum.org"
                          :style {:padding "3px"}}
                         (dom/img {:src (str img-url "ethereum.png")
                                   :style {:padding 0
                                           :margin 0
                                           :width "10px"
                                           :height "auto"
                                           }}))
                      (a {:class "item link"
                          :target "_blank"
                          :href "http://clojure.org"
                          :style {:padding "3px"}}
                         (dom/img {:src (str img-url "clojure.svg")
                                   :style {:padding 0
                                           :margin 0
                                           :width "17px"
                                           :height "auto"
                                           }}))
                      (a {:class "item link"
                          :target "_blank"
                          :href "https://fulcro.fulcrologic.com/"
                          :style {:padding "3px"}}
                         (dom/img {:src (str img-url "fulcro.svg")
                                   :style {:padding 0
                                           :margin 0
                                           :width "17px"
                                           :height "auto"
                                           }}))))))

(def ui-footer (comp/factory Footer))
