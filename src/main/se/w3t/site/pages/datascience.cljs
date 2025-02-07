(ns se.w3t.site.pages.datascience
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2 h3 h4]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils :refer [img-url]]
            [se.w3t.site.markdown :as markdown]
            [mui.layout.grid :as g]
            [mui.layout :as l]))

(defsc DataSciencePage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::DataSciencePage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["datascience-page"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (let [app-element (.getElementById js/document "app")]
                   (.scrollTo app-element 0 0))
                 (dr/route-immediate [:component/id ::DataSciencePage]))}
  (g/container {:spacing 6}
               (g/item {:xs 3}
                       (dom/h2 {:style {:color "#ffa500ff"}} "DATA"))
               (g/item {:xs 8}
                       (dom/img {:style {:width "800px" :height "300px"
                                         :object-fit "cover"
                                         ;:object-position "-10% 0"
                                         }
                                 :src (str img-url "data-top.jpg")})
                       (l/stack {:class "category-heading"
                                 :direction :row
                                 :jusifyContent :center
                                 :alignItems :center
                                 :spacing 7
                                 :style {:color "#b2b4bf"}}
                                (h2 "COLLECT") (h2 "STORE") (h2 "TRANSFORM") (h2 "ANALYZE") (h2 "PRESENT"))

                       (markdown/render {:body "# We are building a modern Data platform that can be tailor made to fit your Analytics needs
We have multiple years of experience within data related areas (Bioinformatics, Engineering) and are packaging that into a Data platform based on Open Source tools. Insights are only possible when the quality of the underlying Data is high which is why we employ proper Statistical analyses and pruning before applying any AI/ML techniques. We also have experience building *Small Data* continous models that can be put into Services to create Control System-like behaviours."}))))
