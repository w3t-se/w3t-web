(ns se.w3t.site.components.blog-entry
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.mutations :as mutations]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :as utils :refer [md->html]]
            [se.w3t.site.markdown :as markdown]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [mui.transitions :as t]
            [com.fulcrologic.rad.type-support.date-time :as dt]))

(defsc BlogEntry [this {:blog/keys [id type date heading first-paragraph rest] :as props}]
  {:query         [:blog/id :blog/type :blog/date :blog/heading :blog/first-paragraph :blog/rest]
   :ident         :blog/id
   :initial-state (fn [{:keys [id type date heading first-paragraph rest] :as params}] {:blog/id id
                                                                                        :blog/date date
                                                                                        :blog/type type
                                                                                        :blog/heading heading
                                                                                        :blog/first-paragraph first-paragraph
                                                                                        :blog/rest rest})
   :initLocalState (fn [] {:show-rest? false})}
  (let [show-rest? (comp/get-computed this :show-rest?)
        show-rest? (comp/get-state this :show-rest?)]
    (g/container {:id (str "blog-" id)}
                 (g/item {:xs 2}
                         (l/stack {:spacing 0.1}
                                  (dom/h3 {:style {:color "#a57aeb"}} type)
                                  (dom/h4 date)))
                 (g/item {:xs 10}
                         (dom/h1 {:style {:margin-top "0.6rem"}} heading))
                 (g/item {:xs 2})
                 (g/item {:xs 10
                          :style (if-not show-rest? {:white-space "nowrap"
                                                     :overflow "hidden"
                                                     :text-overflow "ellipsis"})}
                         (dom/text first-paragraph)
                         (t/collapse {:in show-rest?}
                                     (markdown/render {:body rest})
                                     ;(markdown/ui-code-block {:language "bash" :value "export asd=asd"})
                                     ))
                 (g/item {:xs 12}
                         (l/stack {:direction "row"
                                   :justifyContent "flex-end"}
                                  (dom/a {:href "#"
                                          :onClick #(comp/set-state! this {:show-rest? (not show-rest?)})} 
                                         (if show-rest? (h1 "<") (h1 ">"))))))))

(def ui-blog-entry (comp/computed-factory BlogEntry))
