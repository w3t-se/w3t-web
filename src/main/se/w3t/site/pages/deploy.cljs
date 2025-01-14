(ns se.w3t.site.pages.deploy
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2]]
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils :refer [md->html img-url]]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [se.w3t.site.markdown :as markdown]
            ["@mui/material/Tabs" :default Tabs]
            ["@mui/material/Tab" :default Tab]))

#_(def stripe (js/Stripe "your-publishable-key"))

(def api-url "https://api.w3t.se")

(defsc DeployPage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::DeployPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "") :selected :provider})
   :initLocalState (fn [_] {:selected :provider
                            :distro nil
                            :cloud nil
                            :features #{}})
   :route-segment ["deploy"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (let [app-element (.getElementById js/document "app")]
                   (.scrollTo app-element 0 0))
                 (dr/route-immediate [:component/id ::DeployPage]))}
  (let [{:keys [selected cloud distro features]} (comp/get-state this)]
    (g/container {:spacing 6
                  :style {:min-height "70vh"}}
                 (g/item {:xs 3}
                         (dom/h2 {:style {:margin 0
                                          :color "#ffa500ff"}} "DEPLOY")
                         (l/stack {:spacing 2
                                   :style {:margin-top "2rem"}}
                                  (for [s ["Provider" "Features" "Details"]]
                                    (dom/a {:rel "noopener" :href (str "#" s)} s))))

                 (g/item {:xs 9}
                         (dom/form {:onSubmit (fn [e]
                                                (.preventDefault e)
                                                (.then (js/fetch (str api-url "/stripe-checkout") {:method "POST"})
                                                       (fn [response] (.then (.json response) (fn [content])))))}
                                   (markdown/render {:body "Thank your for choosing **W8S** our turnkey Kuberenetes platform. Let's get started!"})
                                   (dom/h3 {} "Select Kubernetes Distribution:")
                                   (l/stack {:direction "row"
                                             :spacing 2}
                                            (dom/img {:style (conj {:width "auto" :height "6rem" :padding "1rem" :cursor "pointer"}
                                                                   (when (= distro :kubernetes) {:border "1px solid"
                                                                                                 :border-color "blue"}))
                                                      :onClick #(comp/set-state! this {:distro :kubernetes})
                                                      :src (str img-url "kubernetes.svg")})
                                            (dom/img {:style (conj {:width "auto" :height "6rem" :padding "1rem" :cursor "pointer"}
                                                                   (when (= distro :openshift) {:border "1px solid"
                                                                                                :border-color "blue"}))
                                                      :onClick #(comp/set-state! this {:distro :openshift})
                                                      :src (str img-url "OpenShift-LogoType.svg")}))
                                   (when distro
                                     (condp = selected
                                       :provider (comp/fragment {}
                                                                (dom/h3 {} "Select preferred Cloud Provider:")
                                                                (l/stack {:direction "row"
                                                                          :spacing 2}
                                                                         (dom/img {:style (conj {:width "auto" :height "6rem" :padding "1rem" :cursor "pointer"}
                                                                                                (when (= cloud :gcp) {:border "1px solid"
                                                                                                                      :border-color "blue"}))
                                                                                   :onClick #(comp/set-state! this {:cloud :gcp})
                                                                                   :src (str img-url "google-cloud.svg")})))
                                       :features (dom/form {}
                                                           (dom/input {}))
                                       :details (comp/fragment {})

                                       ""))
                                   (when cloud
                                     (comp/fragment {}
                                                    (dom/h3 {} "Select Features:")
                                                    (dom/h4 {:style {:margin 0}} "Addons")
                                                    (l/stack {:direction "row"
                                                              :spacing 2}
                                                             (dom/img {:style (conj {:width "auto" :height "6rem" :padding "1rem" :cursor "pointer"}
                                                                                    (when (contains? features :devops) {:border "1px solid"
                                                                                                                        :border-color "blue"}))
                                                                       :onClick #(comp/set-state! this {:features (conj features :devops)})
                                                                       :src (str img-url "devops.svg")}))
                                                    (dom/h4 {:style {:margin 0}} "Databases")
                                                    (l/stack {:direction "row"
                                                              :spacing 2}
                                                             (dom/img {:style (conj {:width "auto" :height "6rem" :padding "1rem" :cursor "pointer"}
                                                                                    (when (contains? features :psql) {:border "1px solid"
                                                                                                                      :border-color "blue"}))
                                                                       :onClick #(comp/set-state! this {:features (conj features :psql)})
                                                                       :src (str img-url "psql_logo.svg")}))))
                                   (when (and selected cloud)
                                     (dom/div {:class "flex flex-row gap-1.5"}
                                     (dom/button {:type "submit"
                                                  :class "button-9"} "Deploy!")
                                     (dom/span {} "(Sorry! Coming soon). In the meantime please reach out to us at sales@w3t.se with your desired options and contact details. We will deploy your desired configuration as swiftly as possible."))))))))
