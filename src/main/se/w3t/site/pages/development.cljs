(ns se.w3t.site.pages.development
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.mutations :as m]
            [se.w3t.site.utils :as utils :refer [md->html]]
            [se.w3t.site.markdown :as markdown]
            [mui.layout.grid :as g]
            [mui.layout :as l]
            [mui.surfaces :as s]))

(defsc DevelopmentPage [this {:keys []}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::DevelopmentPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["development"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (dr/route-immediate [:component/id ::DevelopmentPage]))}
  (g/container {:spacing 6}
               (g/item {:xs 3}
                       (dom/h2 {:style {:color "#e8a761"}} "DEVELOPMENT"))
               (g/item {:xs 8}
                       (l/stack {:direction :row
                                 :jusifyContent :center
                                 :alignItems :center
                                 :spacing 10
                                 :style {:color "#b2b4bf"}}
                                (h2 "DESIGN") (h2 "PLAN") (h2 "BUILD") (h2 "DEPLOY") (h2 "MAINTAIN"))
                       (markdown/render {:body "# We are passionate about building Software Products efficiently by creating the right Team and equipping them with the right Tools."})
                       (markdown/render {:body "We have worked in Enterprise settings as well as in Start up Projects and are taking on most Software Projects."}))
               (g/item {:xs 3}
                       (dom/h3 {:style {:color "#a57aeb"}} "WEB"))
               (g/item {:xs 8}
                       (markdown/render {:body "We are developing Single Page Applications (SPAs) and have built up expertise over the past few years with a focus on the Clojure(Script) universe. We feel that Clojure is the best option for building User Space applications, Web Applications, Backend Services. We are actively learning in this space, feel free to head over to our blog for more info."}))
               ;; (g/item {:xs 3}
               ;;         (dom/h3 {:style {:color "#a57aeb"}} "CASES"))
               ))
