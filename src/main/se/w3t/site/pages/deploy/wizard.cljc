(ns se.w3t.site.pages.deploy.wizard
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            #?(:clj  [com.fulcrologic.fulcro.dom-server :as dom :refer [div i p a section h1 h2 text]]
               :cljs [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2 text]])

            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]

            [com.fulcrologic.rad.form :as form]

            [se.w3t.codo.wizards.new-project.basic-info-step :refer [BasicInfoStep]]

            [se.w3t.codo.routers.wizard :refer [ui-wizard-router WizardRouter]]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [se.w3t.blueprint.icons.solid :refer [arrow-left arrow-right clipboard-document-list cube]]

            [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
            [se.w3t.blueprint.stepper :refer [Stepper ui-stepper]]
            [se.w3t.flowbite.factories :as f]))

#?(:cljs
   (defmutation set-active-step! [{:keys [step]}]
     (action [{:keys [app state]}]
       (swap! state assoc-in [:component/id ::ProjectWizard :active] step))))

(defsc DeployWizard [this {:keys [wizard-router stepper step] :as props}]
  {:ident (fn [] [:component/id ::ProjectWizard])
   :query         [:step
                   {:stepper (comp/get-query Stepper)}
                   {:wizard-router (comp/get-query WizardRouter)}]
   :initial-state (fn [_] {:step :info
                           :stepper (comp/get-initial-state Stepper
                                                            {:steps [{:id :info
                                                                      :heading "Project Information"
                                                                      :details "Enter basic project information."
                                                                      :completed? false
                                                                      :active? true
                                                                      :icon :clipboard-document-list}
                                                                     {:id :contract
                                                                      :heading "Deploy Contract"
                                                                      :details "Deploy the Project to the Blockchain."
                                                                      :completed? false
                                                                      :active? false
                                                                      :icon :cube}]})
                           :wizard-router (comp/get-initial-state WizardRouter)})
   :initLocalState (fn [this _] {:step-route {:info {:back nil
                                                     :comp BasicInfoStep
                                                     :forward :contract
                                                     :onEnter form/edit!}
                                              #_:wallet #_{:back :contract
                                                       :comp WalletStep
                                                       :forward nil}}
                                 :id (dr/current-route this)
                                 :click-fns (fn [id]
                                              {:info #(form/edit! this BasicInfoStep [:project/id id])})
                                 :completed []})
   :route-segment ["deploy"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (dr/route-immediate [:component/id ::DeployWizard]))
   ;; :pre-merge (fn [{:keys [data-tree state-map]}]
   ;;              (merge (comp/get-initial-state ProjectWizard)
   ;;                     {:root/wizard-router (get-in state-map (comp/get-ident WizardRouter {}))}
   ;;                     data-tree))
   }
  (let [{:keys [click-fns step-route completed]} (comp/get-state this)
        id (:project/id (:com.fulcrologic.fulcro.routing.dynamic-routing/current-route wizard-router))]
    (div {:class "flex justify-center"}
      (div {:class "mt-4 w-fit h-fit"}
        (ui-stepper stepper {:click-fns (click-fns id)}))
      (div {:class "ml-24 relative"}
        (div {:class "my-4"}
          (h1 {:class "text-2xl mb-4"} "Create Project")
          (p {} "Please enter some basic info about your project. The info in this step can also be updated later."))

        (ui-wizard-router wizard-router)

        (div {:class "flex flex-inline"}
          (if-let [next (get-in step-route [step :back])]
            (f/ui-button {:color "gray"
                          :onClick #(let [next-state (get step-route next)]
                                      ((:onEnter next-state) this (:comp next-state) {:id id})
                                      (comp/set-state! this {:active next}))}
              (div :.h-5.w-5 arrow-left)))

          (if-let [next (get-in step-route [step :forward])]
            (if-not (tempid/tempid? id)
              (div {:class "absolute right-0"}
                (f/ui-button {:color "gray"
                              :onClick #(let [next-state (get step-route next)]
                                          ((:onEnter next-state) this (:comp next-state) {:id id})
                                          (comp/set-state! this {:active next}))}
                  (div :.h-5.w-5 arrow-right))))))))))
