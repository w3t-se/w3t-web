(ns se.w3t.site.pages.blog
  (:require [com.fulcrologic.fulcro.application :as app]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.mutations :as mutations]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :as utils :refer [md->html]]
            [se.w3t.site.markdown :as markdown]
            [se.w3t.site.components.blog-entry :refer [ui-blog-entry BlogEntry]]
            [mui.layout :as l]
            [mui.layout.grid :as g]))

(defsc BlogListPage [this {:keys [blogs active-blog]}]
  {:query         [{:blogs (comp/get-query BlogEntry)}
                   :active-blog]
   :ident         (fn [] [:component/id ::Blog])
   :initial-state (fn [{:keys [blogs] :as params}] {:blogs (or blogs [])})
   :route-segment ["blog" :blog-id]
   :will-enter (fn [app {:keys [blog-id] :as route-params}]
                 (when-let  [blog-id (some-> blog-id (js/parseInt))]
                   (swap! (::app/state-atom app) assoc-in [:component/id ::BlogPage :active-blog blog-id])
                   ;(react-ref )
                   )
                 (dr/route-immediate [:component/id ::Blog]))}
  (g/container {}
               (g/item {:xs 2}
                       (dom/h1 {:style {:color "#ebb871"}} "BLOG"))
               (g/item {:xs 10})
               (for [blog blogs]
                 (ui-blog-entry blog
                                (if (= (:id blog) active-blog) {:show-rest? true})))))
