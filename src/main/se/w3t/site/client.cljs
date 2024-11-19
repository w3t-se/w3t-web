(ns se.w3t.site.client
  (:require
    [com.fulcrologic.fulcro.application :as app-default]
    [com.fulcrologic.fulcro.react.version18 :refer [with-react18]]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
    [com.fulcrologic.rad.routing.html5-history :as hist5 :refer [html5-history]]
    [com.fulcrologic.rad.routing.history :as history]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [se.w3t.site.components.blog-entry :refer [ui-blog-entry BlogEntry]]
    [se.w3t.site.blog-entries :as blog-entries :refer [blogs]]
    [se.w3t.site.mutations]
    [se.w3t.site.ui :refer [Root]]))

(defonce app (with-react18 (app-default/fulcro-app {})))

(comment 
  (map #(merge/merge-component! app BlogEntry (comp/get-initial-state BlogEntry %)
                                               :append [:component/id :se.w3t.site.pages.blog/Blog :blogs])
               blogs))

(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []

  (app-default/set-root! app Root {:initialize-state? true})
  (dr/initialize! app)
  
  (dr/change-route! app ["home"])
  (history/install-route-history! app (html5-history))

  (app-default/mount! app Root "app" {:initialize-state? false}))

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  [] 
  (comp/refresh-dynamic-queries! app)
  (app-default/mount! app Root "app")
  (js/console.log "Hot reload"))
