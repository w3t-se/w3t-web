(ns se.w3t.site.pages.blog
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :as utils :refer [md->html]]
            [se.w3t.site.markdown :as markdown]
            [se.w3t.site.components.blog-entry :as be :refer [ui-blog-entry BlogEntry]]
            [se.w3t.site.blog-entries :as blog-entries]
            [mui.layout :as l]
            [mui.layout.grid :as g]))

(defsc BlogListPage [this {:keys [blogs active-blog]}]
  {:query         [{:blogs (comp/get-query BlogEntry)}
                   :active-blog]
   :ident         (fn [] [:component/id ::BlogListPage])
   :initial-state (fn [{:keys [blogs] :as params}] {:blogs (or blogs [])
                                                    :active-blog nil})
   :route-segment ["blogs"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (println "p:" route-params)
                 (comp/transact! app [`(se.w3t.site.mutations/load-blogs)])
                 ;; (when-let  [blog-id (some-> blog-id (js/parseInt))]
                 ;;   (swap! (:com.fulcrologic.fulcro.application/state-atom app) assoc-in [:component/id ::BlogPage :active-blog blog-id]))
                 (dr/route-immediate [:component/id ::BlogListPage]))}
  (dom/div {:style {:display "flex"
                    :flex-direction "column"}}
   (g/container {:spacing 1}
                (g/item {:xs 3}
                        (dom/h2 {:style {:color "#ebb871"}} "BLOG"))
                (g/item {:xs 8}))
   (for [blog blogs]
     (dom/div {:mouse "pointer"
               :onClick #(rroute/route-to! this be/BlogEntry {:blog-id (:blog/id blog)})}
       (ui-blog-entry blog
                      {:show-rest? false
                       :back? false})))))
