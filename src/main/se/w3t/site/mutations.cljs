(ns se.w3t.site.mutations
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [se.w3t.site.utils :as utils]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [se.w3t.site.components.blog-entry :refer [ui-blog-entry BlogEntry]]
            [se.w3t.site.blog-entries :as blog-entries :refer [blogs]]))

(defmutation load-url [{:keys [url c state-map]}]
  (action [{:keys [app state]}]
          (go (let [response (<!  (http/get url {:with-credentials? false}))
                    body (clojure.string/replace (:body response) #"\n|$" "")]
                (merge/merge-component! app c (comp/get-initial-state c {:page body}))))))

(defmutation load-blogs [{:keys []}]
  (action [{:keys [app state]}]
          (let [b (blog-entries/blogs)]
            (mapv #(merge/merge-component! app BlogEntry (comp/get-initial-state BlogEntry %)
                                           :append [:component/id :se.w3t.site.pages.blog/BlogListPage :blogs])
                  b))))

(defmutation load-blog [{:keys [id]}]
  (action [{:keys [app state]}]
          (.then (js/fetch (str "/blogs/" id ".md"))
                 (fn [res]
                   (.then (.text res)
                          (fn [text]
                            (js/console.log "res:" text)
                            (swap! state assoc-in [:blog/id id :blog/content] text)))))))
