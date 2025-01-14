(ns se.w3t.site.components.blog-entry
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :as utils :refer [md->html img-url]]
            [se.w3t.site.markdown :as markdown]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [mui.transitions :as t]
            [cljs-time.core :as tc]
            [cljs-time.format :as f]
            [clojure.string :as string]
            [com.fulcrologic.rad.type-support.date-time :as dt]))

(def formatter (f/formatter "yyyy-MM-dd"))

(defn get-reading-time [text]
  (int (/ (count (remove string/blank? (string/split text #"\s+"))) 230)))

(defsc BlogEntry [this {:blog/keys [id type date image heading first-paragraph reading-time content sections author tags og-title og-image] :as props}]
  {:query         [:blog/id :blog/type :blog/date :blog/heading :blog/first-paragraph :blog/image :blog/reading-time :blog/content :blog/sections :blog/author :blog/tags :blog/og-title :blog/og-image]
   :ident         :blog/id
   :route-segment ["blog" :blog-id]
   :will-enter (fn [app {:keys [blog-id] :as route-params}]
                 (let [app-element (.getElementById js/document "app")]
                   (.scrollTo app-element 0 0))
                 (dr/route-deferred
                  [:blog/id blog-id]
                  (fn []
                    (comp/transact! app `[(se.w3t.site.mutations/load-blog ~{:id blog-id})
                                          (dr/target-ready ~{:target [:blog/id blog-id]})] {:parallel? false}))))
   :initial-state (fn [{:keys [id type date heading first-paragraph image reading-time content sections author tags] :as params}] {:blog/id id
                                                                                                                                   :blog/date date
                                                                                                                                   :blog/type type
                                                                                                                                   :blog/heading heading
                                                                                                                                   :blog/image image
                                                                                                                                   :blog/reading-time reading-time
                                                                                                                                   :blog/first-paragraph first-paragraph
                                                                                                                                   :blog/content content
                                                                                                                                   :blog/author author
                                                                                                                                   :blog/tags tags
                                                                                                                                   :blog/sections sections})
   :initLocalState (fn [] {:show-rest? true
                           :back? true})}
  (let [show-rest? (if (not (nil? (comp/get-computed this :show-rest?)))
                     (comp/get-computed this :show-rest?)
                     (comp/get-state this :show-rest?))
        back? (if (not (nil? (comp/get-computed this :back?)))
                (comp/get-computed this :back?)
                (comp/get-state this :back?))]
    (comp/fragment {}
                   (dom/meta {:name "og:title" :content og-title})
                   (dom/meta {:name "og:image" :content og-image})
                   (g/container {:id (str "blog-" id)
                                 :width "100%"
                                 :overflow-y "visible"
                                 :spacing 2
                                        ;:class "space-mono-regular"

                                 :font-size "16px"}
                                (g/item {:xs 3
                                         :top "2rem"}
                                        (l/stack {:spacing 0.2
                                                  :style {:position (if show-rest? "fixed" "relative")}}
                                                 (dom/h3 {:style {:color (condp = type
                                                                           "CASE" "#a57aeb"
                                                                           "DEVELOPMENT" "#7a96eb"
                                                                           "ANNOUNCEMENT" "#e0b743"
                                                                           "TUTORIAL" "#75d964"
                                                                           "OPINION" "#b6b6bf"
                                                                           "RANT" "#e84d4d"
                                                                           "#a57aeb")}} type)
                                                 (dom/h4 (str (f/unparse formatter (tc/date-time date))))
                                                 (when author (dom/a {:href (str "https://github.com/" author)} (str "@" author)))
                                                 (dom/p {} (str (get-reading-time content) " min"))
                                                 (for [t tags]
                                                   (dom/span {:style {}} (str "#" (name t))))
                                                 (when show-rest?
                                                   (l/stack {:style {:margin-top "2rem"}}
                                                            (for [s sections]
                                                              (dom/a {:rel "noopener" :href (str "#" s)} s))))))
                                (g/item {:xs 8}
                                        (l/stack {:spacing 4}
                                                 (when image (dom/img {:style {:width "100%"
                                                                               :display "flex"}
                                                                       :src (str img-url image)}))
                                                 (markdown/render {:body heading}))
                                        (when-not show-rest?
                                          (markdown/render {:body first-paragraph}))
                                        (when show-rest?
                                          (markdown/render {:body content}))
                                        (l/stack {:direction "row"
                                                  :justifyContent "flex-end"}
                                                 (dom/a {:onClick #(when back? (rroute/back! this)) #_(comp/set-state! this {:show-rest? (not show-rest?)})}
                                                   (if back? (h1 "<") (h1 ">")))))))))

(def ui-blog-entry (comp/computed-factory BlogEntry {:keyfn :blog/id}))
