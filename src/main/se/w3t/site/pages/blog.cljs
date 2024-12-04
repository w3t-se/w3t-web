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
   :route-segment ["blog"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (let [app-element (.getElementById js/document "app")]
                   (.scrollTo app-element 0 0))
                 (dr/route-deferred
                  [:component/id ::BlogListPage]
                  (fn []
                    (comp/transact! app `[(se.w3t.site.mutations/load-blogs)
                                          (dr/target-ready ~{:target [:component/id ::BlogListPage]})] {:parallel? false}))))}
  (dom/div {:style {:display "flex"
                    :flex-direction "column"}}
   #_(g/container {:spacing 1}
                (g/item {:xs 3}
                        #_(dom/h1 {:class "navbar-heading"} "BLOG"))
                (g/item {:xs 8}))
   (for [blog blogs]
     (dom/div {
               :style {:margin-bottom "2rem"
                       :cursor "pointer"}
               :onClick #(rroute/route-to! this be/BlogEntry {:blog-id (:blog/id blog)})}
       (ui-blog-entry blog
                      {:show-rest? false
                       :back? false})))))
