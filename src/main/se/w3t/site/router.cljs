(ns se.w3t.site.router
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :refer [defrouter]]
            [com.fulcrologic.fulcro.dom :as dom]
            [se.w3t.site.pages.landing-page :refer [LandingPage]]
            [se.w3t.site.pages.development :refer [DevelopmentPage]]
            [se.w3t.site.pages.devops :refer [DevOpsPage]]
            [se.w3t.site.pages.datascience :refer [DataSciencePage]]
            [se.w3t.site.pages.codo :refer [CodoPage]]
            [se.w3t.site.pages.kubernetes :refer [KubernetesPage]]
            [se.w3t.site.pages.contact :refer [ContactPage]]
            [se.w3t.site.pages.team :refer [TeamPage]]
            [se.w3t.site.pages.blog :refer [BlogListPage]]
            [se.w3t.site.pages.blockchain :refer [BlockchainPage]]
            [se.w3t.site.components.blog-entry :refer [BlogEntry]]
            [se.w3t.site.pages.partnerships :refer [PartnershipsPage]]
            [se.w3t.site.pages.deploy :refer [DeployPage]]
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [mui.layout.grid :as g]
            [mui.layout :as l]
            ["react-scroll-parallax" :refer [ParallaxProvider ParallaxBanner]]))

(def ui-parallax-provider (interop/react-factory ParallaxProvider))
(def ui-parallax-banner (interop/react-factory ParallaxBanner))

(defrouter MainRouter [this {:keys [current-state route-factory route-props]}]
  {:always-render-body? true
   :router-targets      [LandingPage DevOpsPage DataSciencePage DevelopmentPage KubernetesPage CodoPage ContactPage BlogListPage BlogEntry TeamPage PartnershipsPage DeployPage BlockchainPage]
   :initLocalState (fn [] {:opacity 1
                           :scroll-y 0})
   :componentDidMount (fn [this]
                        (.addEventListener (.getElementById js/document "app") "scroll"
                                           (fn [e]
                                             (let [current-scroll-y (aget e "target" "scrollTop")
                                                   height (aget e "target" "height")]
                                               (comp/set-state! this {:scroll-y (* 0.4 current-scroll-y)})))))}
  (let [{:keys [scroll-y opacity]} (comp/get-state this)]
    (dom/div {:style {:position "relative"}}
             (dom/div {:id "parallax"
                       :class "container2"
                       :style {:position "absolute"
                               :width "100vw"
                               :height "100vh"
                               :transform (str "translateY(" scroll-y "px)")}})
             (l/container {:id "main-router"
                           :spacing 4
                           :style {:overflow-x "hidden"
                                   :overflow-y "visible"
                                   :width "85vw"
                                   :padding "0px"
                                   :min-height "70vh"
                                   :margin-bottom "4rem"
                                   :position "relative"
                                   :z-index 10}}

                          (dom/div :.ui.loader {:classes [(when-not (= :routed current-state) "active")]})
                          (when route-factory
                            (route-factory (comp/computed route-props (comp/get-computed this))))))))

(def ui-main-router (comp/computed-factory MainRouter))
