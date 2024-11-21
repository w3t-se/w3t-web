(ns se.w3t.site.pages.deploy.basic-info-step
  (:require
    [se.w3t.codo.model.project :as project]
    [com.fulcrologic.rad.form-options :as fo]
    [com.fulcrologic.rad.form :as form]))

(form/defsc-form BasicInfoStep [this props]
  {fo/id           project/id
   fo/attributes   [project/name project/start project/description
                    project/logo project/created-by project/categories project/roles]
   ::form/subforms            {:project/categories {fo/ui CategoryEdit
                                                    fo/title "Add Category"
                                                    fo/can-add? (fn [parent _]
                                                                  true)}
                               :project/roles {fo/ui RoleEdit
                                               fo/title "Add Role"
                                               fo/can-add? (fn [parent _]
                                                             true)}}
   fo/cancel-route ["start"]
   fo/route-prefix "info"
   fo/title        "Project Info"
   fo/layout       [[:project/name :project/start]
                    [:project/description]
                    [:project/logo]]})
