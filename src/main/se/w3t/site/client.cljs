(ns se.w3t.site.client
  (:require
    [com.fulcrologic.fulcro.application :as app]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1]]
    [se.w3t.site.mutations :as m]
    [se.w3t.site.router :as r]
    [se.w3t.site.pages.landing-page :as landing-page]
    [com.fulcrologic.rad.routing :as rroute]
    [com.fulcrologic.rad.routing.html5-history :as hist5 :refer [html5-history]]
    [com.fulcrologic.rad.routing.history :as history]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [se.w3t.site.ui :refer [Root]]
    [se.w3t.site.components.footer :refer [ui-footer]]
    [se.w3t.site.components.blog-entry :refer [ui-blog-entry BlogEntry]]
    [se.w3t.site.markdown :as markdown])
  (:require-macros
   ;[se.w3t.relaxed.components.searchable-list :as sl :refer [defsc-searchable-list]]
   ))

(defonce app (app/fulcro-app))

(defsc User [this {:user/keys [name] :as props}]
  {:ident :user/id
   :query [:user/id :user/name]
   :initial-state (fn [{:keys [id name] :as params}] {:user/id id :user/name name})}
  (dom/div name))

(def ui-user (comp/computed-factory User {:keyfn :user/id}))

(defsc Project [this {:project/keys [name] :as props}]
  {:ident :project/id
   :query [:project/id :project/name]
   :initial-state (fn [{:keys [id name] :as params}] {:project/id id :project/name name})}
  (dom/div "b " name))

(def ui-project (comp/computed-factory Project {:keyfn :project/id}))

;; (sl/defsc-searchable-list UserList (comp/get-query User) ui-user 'se.w3t.relaxed.mutations/some-mut)
;; (def ui-user-list (comp/factory UserList {:keyfn :list/id}))

;; (sl/defsc-searchable-list ProjectList (comp/get-query Project) ui-project 'se.w3t.relaxed.mutations/some-mut)
;; (def ui-project-list (comp/factory ProjectList {:keyfn :list/id}))

(comment
  (merge/merge-component! app BlogEntry (comp/get-initial-state BlogEntry {:id 0 :type "CASE" :date "01-11-21" :heading "Developing a Release Automation flow for PrimeKey AB!" :first-paragraph "PrimeKey has been developing Public Key Insfrasructure (PKI) products for almost two decades and was in a position to renew some of it's development practices with a focus on automation. Since we know how closely coupled the DevOps and underlying IT Infrastructure are, we proposed the move furter into the adoption of Kubernetes." :rest "## Site 

## Merger  
There was an ongoing merger with the now parent Company Keyfactor."}) :append [:component/id :se.w3t.site.pages.blog/Blog :blogs])
  (merge/merge-component! app ItemList (comp/get-initial-state ItemList {:id 1 :title "Baby"}) :replace [:root/items])
  (merge/merge-component! app Item (comp/get-initial-state Item {:id 0 :title "Baby0"}) :append [:list/id 1 :list/items])
  (merge/merge-component! app User (comp/get-initial-state User {:id 3 :name "Baby"}) :append [:list/id :users :list/items])
  (merge/merge-component! app Project (comp/get-initial-state Project {:id 0 :name "Codo"}) :append [:list/id :users :list/items])

  (merge/merge-component! app UserList (comp/get-initial-state UserList {:id :users
                                                                         :items [(comp/get-initial-state User {:id 0 :name "Marge"})]}) :replace [:root/users])
  (comp/get-query UserList)
  (defsc-searchable-list `UserList (comp/get-query User) ui-user)
  )

;; (defsc Root [this {:root/keys [users projects] :as props}]
;;   {:query [{:root/users (comp/get-query UserList)}
;;            {:root/projects (comp/get-query ProjectList)}
;;            ]
;;    :initial-state (fn [param] {:root/users (comp/get-initial-state UserList {:id :users
;;                                                                              :items [(comp/get-initial-state User {:id 0 :name "Marge"})]})
;;                                :root/projects (comp/get-initial-state ProjectList {:id :projects
;;                                                                                    :items [(comp/get-initial-state Project {:id 0 :name "Codo"})]})
;;                                })}
;;   (dom/div {:class "content-segment"
;;             :style {:margin "0 auto;"
;;                     :width "500px"}}
;;            (dom/button {:class "ui button black"
;;                         :onClick #(merge/merge-component! app User (comp/get-initial-state User {:id 1 :name "Daniel"}) :append [:list/id :users :list/items])}
;;                        "Add user")
;;            (dom/div
;;             (when users
;;               (ui-user-list users))
;;             (when projects
;;               (ui-project-list projects)
;;               ))))


(defsc Root2 [this {:root/keys [router] :as props}]
  {:query [{:root/router (comp/get-query r/MainRouter)}
           ;; { ;(comp/get-query draggable/ItemList)
           ;;  }
            ]
   :initial-state (fn [{:keys [] :as props}]
                    {:root/router (comp/get-initial-state r/MainRouter)})
   ;; :initial-state (fn [param] { ;; (comp/get-initial-state draggable/ItemList {:id 1
   ;;                                         ;;                                             :title "Cards"})
   ;;                             })
                    }
  (dom/section {:class "fixed body"
               :style {;:fontFamily "'Rubik', sans-serif"
                       :display "flex"; 
                       :width "calc(100hh)"
                       :height "calc(100vh)"
                       :background-color "#2e2e2e"
                       }}
   (dom/main {:style {;:background-color "white" ;"#ededed";"#F5F5F5"
                                        ;:flex-wrap "wrap"
                                    :position "relative"
                                    :display "flex"
                                    :flex "1 1 auto"
                                    :overflow "auto"
                                        ;:min-height "800px"
                                    :flex-direction "column"
                                        ;:margin-top "2rem"
                                        ;:margin-left "0.5rem"
                                        ;:height "100%"
                                    :height "calc(100vh)"}}
             (dom/div {;:class "ui container"
                       :style {
                               :width "calc(220px - 100hw)"
                                        ;:padding "2rem"
                               }}
                      (dom/header  {:style {;:background-color "white";"#13181c";"#d7d8e0"
                                            :margin "0px"
                                            :height "60px"
                                            :max-height "60px"
                                        ;:display "flex"
                       ;:border-bottom "1px solid #e3e3e3"
                                            }
                                    }
                                   (dom/div {:class "ui secondary text menu"
                                             :style {:margin "0px"
                                                     :height "60px"
                                                     :max-height "60px"
                                                     :padding "0px"
                                                     }}
                                            (dom/img {:class "ui small image"
                                                      :style {:width "6rem"
                                                              :height "auto"
                                                              }
                                                      :src "/images/w3t.png"
                                                      :onClick #(rroute/route-to! this landing-page/LandingPage {})})
                                            (dom/div :.ui.right.menu)
                                            (div :.right.menu
                                                 (div {:onClick #(rroute/route-to! this landing-page/LandingPage {})} "Products")
                                                 (div {:onClick #(rroute/route-to! this landing-page/LandingPage {})} "Services")
                                                 (div {:onClick #(rroute/route-to! this landing-page/LandingPage {})} "Team")
                                                 (div {:onClick #(rroute/route-to! this landing-page/LandingPage {})} "Contact")))))
             
             (div {:style {:position "relative"
                           :display "flex"
                           :flex "1 1 auto"
                           :overflow "hidden"
                                        ;:min-height "800px"
                           :flex-direction "column"
                                        ;:margin-top "2rem"
                                        ;:margin-left "0.5rem"
                                        ;:height "100%"
                           :height "calc(100vh - 100px)"}}
                  (r/ui-main-router router))
             (ui-footer)))
   ;; (dom/button {:class "ui button black"
   ;;              ;:onClick #(merge/merge-component! app User (comp/get-initial-state User {:id 1 :name "Daniel"}) :append [:list/id :users :list/items])
  ;;              }
   ;;             "Add card")
   )
  


(defn ^:export init
  "Shadow-cljs sets this up to be our entry-point function. See shadow-cljs.edn `:init-fn` in the modules of the main build."
  []
  (app/set-root! app Root {:initialize-state? true})
  (dr/initialize! app)
  
  (dr/change-route! app ["home"])
  (history/install-route-history! app (html5-history))

  (merge/merge-component! app BlogEntry (comp/get-initial-state BlogEntry {:id 0 :type "DEVOPS" :date "18-11-21" :heading "Cool DevOps" :first-paragraph "none" :rest "```clojure
(defn asd []
  (+ 1 1))
```"}) :append [:component/id :se.w3t.site.pages.blog/Blog :blogs])
  (merge/merge-component! app BlogEntry (comp/get-initial-state BlogEntry {:id 1 :type "CASE" :date "01-11-21" :heading "Developing a Release Automation flow for PrimeKey AB!" :first-paragraph "PrimeKey has been developing Public Key Insfrasructure (PKI) products for almost two decades and was in a position to renew some of it's development practices with a focus on automation. Since we know how closely coupled the DevOps and underlying IT Infrastructure are, we proposed the move furter into the adoption of Kubernetes." :rest "## Site 

## Merger  
There was an ongoing merger with the now parent Company Keyfactor."}) :append [:component/id :se.w3t.site.pages.blog/Blog :blogs])

  (app/mount! app Root "app" {:initialize-state? false})
  ;(app/mount! app Root "app")
  ;(js/console.log "Loaded")
  )

(defn ^:export refresh
  "During development, shadow-cljs will call this on every hot reload of source. See shadow-cljs.edn"
  []
  ;(log/info "Reinstalling controls")
  
  (comp/refresh-dynamic-queries! app)
  (app/mount! app Root "app")
  (js/console.log "Hot reload"))
