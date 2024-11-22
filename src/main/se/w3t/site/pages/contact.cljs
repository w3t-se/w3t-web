(ns se.w3t.site.pages.contact
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2]]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [se.w3t.site.markdown :as markdown]
            [se.w3t.site.components.team-item :as team-item]))

(defsc ContactPage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::ContactPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :route-segment ["contact"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (let [app-element (.getElementById js/document "app")]
                   (.scrollTo app-element 0 0))
                 ;; (comp/transact! app [`(mutations/load-url {:url ~(str utils/site-url "/md/dev_page.md")
                 ;;                                            :c ~LandingPage
                 ;;                                            :state-map {:page 'body}})])
                 (dr/route-immediate [:component/id ::ContactPage]))}

  (g/container {:my 0
                :spacing 5
                :justifyContent :center}

               (g/item {:xs 3}
                       (dom/h2 {:style {:margin 0
                                        :color "#a57aeb"}} "PASSIONS"))
               (g/item {:xs 9})

               (g/item {:xs 12}
                       (markdown/render {:body "# We are a bunch of Geeks with a common passion for DevOps, Data Science and Software Development!
W3T strives to create **packaged solutions** from the latest Open Source components that can be tailor-made to your needs and deployed in the Cloud or your Container based infrastructure. Our Professional Services Team has a combined multiple years of experience of building, deploying and succesfully operating Projects within our core areas: DevOps, Data Science and Software Development on top of Kubernetes."}))
               (g/item {:xs 12}
                       #_(dom/h2 {:style {:margin-bottom "2rem"
                                        :color "#e8a761"}} "TEAM")
                       (l/image-list {:variant "quilted"
                                      :cols 2
                                      :rowHeight 221}
                                     (team-item/ui-team-item {:name "Daniel"
                                                              :source "https://storageapi.fleek.co/zertan-team-bucket/profiles/daniel.jpg"
                                                              :github "zertan"})
                                     (team-item/ui-team-item
                                      {:name "Robin"
                                       :source "https://storageapi.fleek.co/zertan-team-bucket/profiles/robin.jpg"
                                       :github "16pj"})))
               (g/item {:xs 3}
                       (dom/h2 {:style {:margin-bottom "2rem"
                                        :color "#a57aeb"}} "CONTACT")

                       #_(dom/iframe {:width 500 :height 400
                                      :style {:border 0}
                                      :loading "lazy"
                                      :allowfullscreen true
                                      :src "https://www.google.com/maps/embed/v1/place?key=AIzaSyBoqk02RBThPjYn09OQqaLzxE2h49OrNjM&q=Dymlingsgränd+5,Hägersten"}))
               (g/item {:xs 9}
                       (markdown/render {:body "## Please do not hesitate to reach out to us. We are happy to help and eager to answer your questions about our Service Areas and Offerings!"})
                       (markdown/render {:body "####
#### Email: info@w3t.se
#### Phone: +46730322499
#### Organisation number: [559324-8452](https://www.bolagsfakta.se/5593248452-W3T_AB)"}))))
