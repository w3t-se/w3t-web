(ns se.w3t.site.components.navigation
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom :refer [div a p]]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.pages.landing-page :as landing-page]
            [se.w3t.site.pages.datascience :as datascience-page]
            [se.w3t.site.pages.devops :as devops-page]
            [se.w3t.site.pages.development :as development-page]
            [se.w3t.site.pages.codo :as codo-page]
            [se.w3t.site.pages.kubernetes :as kubernetes-page]
            [se.w3t.site.pages.blockchain :as blockchain-page]
            [se.w3t.site.pages.contact :as contact-page]
            [se.w3t.site.pages.w8s :as w8s-page]
            [se.w3t.site.pages.team :as team-page]
            [se.w3t.site.pages.blog :as blog-page]
            [se.w3t.site.pages.partnerships :as partnerships-page]
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            ["@mui/material/Menu" :default Menu]
            ["@mui/material/MenuItem" :default MenuItem]
            [se.w3t.site.utils :as u]
            [mui.inputs :as i]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [mui.surfaces :as s]
            [mui.navigation :as n]
            [mui.transitions :as t]
            [se.w3t.site.fontawesome.icons :as fa]))

(def ui-menu (interop/react-factory Menu))
(def ui-menu-item (interop/react-factory MenuItem))
(def last-scroll-y (atom 0))

(defsc Navigation [this props]
  {:initLocalState (fn [] {:services-anchor  nil
                           :community-anchor nil})
   :componentDidMount (fn []
                        (let [bar (.getElementById js/document "stickyBar")]
                          (.addEventListener (.getElementById js/document "app") "scroll"
                                             (fn [e]
                                               (let [current-scroll-y (aget e "target" "scrollTop")]
                                                 (if (> current-scroll-y 20)
                                                   (do
                                                     ;; If scrolled down past 200px and scrolling down
                                                     (.classList.add bar "fade-out")
                                                     (.classList.remove bar "fade-in"))
                                                   (do
                                                     ;; If scrolling up
                                                     (.classList.add bar "fade-in")
                                                     (.classList.remove bar "fade-out")))
                                                 (reset! last-scroll-y current-scroll-y))))))}
  (let [{:keys [services-anchor community-anchor]} (comp/get-state this)]
    (dom/nav {:class "no-select sticky-bar"
              :id "stickyBar"
              :onMouseLeave      #(comp/set-state! this {:services-anchor nil})
              :style {:display "flex"
                      :position "sticky"
                      :top 0
                      :flex-direction "row"
                      :alignItems "center"
                      :justifyContent "center"
                      :padding-top "2rem"
                      :padding-bottom "2rem"
                      :margin-bottom "2rem"}}
             #_(dom/img {:class "select-none"
                         :src "/images/top.svg"
                         :style {:position "absolute"
                                 :top 0
                                 :z-index 0
                                 :pointer-events "none"
                                 :width "100vw"}})
             (l/stack {:direction "row"
                       :style {:min-width "85vw"
                               :max-width 2048
                               :alignItems "center"
                               :justifyContent "center"}}
                      (l/stack {:direction      "row"
                                :spacing        8
                                :font-weight    700
                                :alignItems     "center"
                                :width "100%"
                                :justifyContent "flex-start"}
                        ;; Logo
                               (dom/a {:style {:cursor       "pointer"
                                               :margin-right "2rem"}}
                                      (dom/img {:style   {:width "6rem" :height "auto" :drag false}
                                                :src     (str u/img-url "w3t_one.jpg")
                                                :onClick #(rroute/route-to! this landing-page/LandingPage {})}))
                               (dom/div {:onMouseOver      #(comp/set-state! this {:services-anchor (.-currentTarget %)})}
                                        (dom/span
                                         {:aria-controls (when services-anchor "services-menu")
                                          :aria-haspopup "true"
                                          :class (str "navbar-heading " (when (comp/get-state this :services-anchor) "navbar-heading-always"))
                                          :style (conj {:font-weight 700
                                                        :font-style "bold"})}

                                         "SERVICES")
                                        (when (comp/get-state this :services-anchor)
                                          (l/stack {:id           "services-menu"
                                                    :anchorEl     services-anchor
                                                    :style {:position "absolute"
                                                            :padding "0.5rem"
                                                            :border-radius "4px"
                                                            :background-color "black"
                                                            :top "6rem"}
                                                    :keepMounted  true
                                                    :onMouseLeave      #(comp/set-state! this {:services-anchor nil})
                                                    :open         (boolean services-anchor)}

                                                   (dom/a {:class "navbar-heading"
                                                           :onClick #(do (rroute/route-to! this kubernetes-page/KubernetesPage {})
                                                                         (comp/set-state! this {:services-anchor nil}))}
                                                     "Kubernetes")
                                                   (dom/a {:class "navbar-heading"
                                                           :onClick #(do (rroute/route-to! this blockchain-page/BlockchainPage {})
                                                                         (comp/set-state! this {:services-anchor nil}))}
                                                          "Web3 and Blockchain")
                                                   (dom/a {:class "navbar-heading"
                                                           :onClick #(do (rroute/route-to! this devops-page/DevOpsPage {})
                                                                         (comp/set-state! this {:services-anchor nil}))}
                                                     "DevOps")
                                                   (dom/a {:class "navbar-heading"
                                                           :onClick #(do (rroute/route-to! this datascience-page/DataSciencePage {})
                                                                         (comp/set-state! this {:services-anchor nil}))}
                                                          "Data Science")
                                                   (dom/a {:class "navbar-heading"
                                                           :onClick #(do (rroute/route-to! this development-page/DevelopmentPage {})
                                                                         (comp/set-state! this {:services-anchor nil}))}
                                                          "Development"))))
                               (dom/a {:onClick #(rroute/route-to! this kubernetes-page/KubernetesPage {})}
                                      (dom/span {:className "navbar-heading no-select"} "W8S")))
                      (l/stack {:direction      "row"
                                :display "flex"
                                :spacing        8
                                :font-weight    700
                                :font-style "bold"
                                :alignItems     "center"
                                :justifyContent "flex-end"}

                        ;; COMMUNITY Heading with MUI Menu
                               #_(dom/div {}
                                          (dom/span
                                           {:className    "navbar-heading"
                                            :aria-controls (when community-anchor "community-menu")
                                            :aria-haspopup "true"
                                            :onClick      #(comp/set-state! this {:community-anchor (.-currentTarget %)})}
                                           "COMMUNITY")
                            ;; MUI Menu for COMMUNITY
                                          (ui-menu {:id           "community-menu"
                                                    :anchorEl     community-anchor
                                                    :keepMounted  true
                                                    :open         (boolean community-anchor)
                                                    :onClose      #(comp/set-state! this {:community-anchor nil})}
                                                   (ui-menu-item {:onClick #(do (rroute/route-to! this codo-page/CodoPage {})
                                                                                (comp/set-state! this {:community-anchor nil}))}
                                                                 "Codo")
                                                   (ui-menu-item {:onClick #(do (rroute/route-to! this partnerships-page/PartnershipsPage {})
                                                                                (comp/set-state! this {:community-anchor nil}))}
                                                                 "Partners")))
                        ;; Other Links
                               (dom/a {:onClick #(rroute/route-to! this blog-page/BlogListPage {})}
                                      (dom/span {:className "navbar-heading no-select"} "BLOG"))
                               #_(dom/a {:class   "navbar-link no-select"
                                         :style   {:color "#ebb871"}
                                         :onClick #(rroute/route-to! this team-page/TeamPage {})} "TEAM")
                               (dom/a {:onClick #(rroute/route-to! this contact-page/ContactPage {})}
                                      (dom/span {:className "navbar-heading no-select"} "WHO?")))))))


(def ui-navigation (comp/factory Navigation))
