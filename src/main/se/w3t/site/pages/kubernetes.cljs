(ns se.w3t.site.pages.kubernetes
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]] 
            [com.fulcrologic.fulcro.dom :as dom :refer [div i p a section h1 h2]]
            [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
            [com.fulcrologic.rad.routing :as rroute]
            [se.w3t.site.pages.deploy :as deploy-page]
            [com.fulcrologic.fulcro.mutations :as m]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [se.w3t.site.utils :as utils :refer [img-url]]
            [mui.layout :as l]
            [mui.layout.grid :as g]
            [se.w3t.site.markdown :as markdown]
            ["@mui/material/Tabs" :default Tabs]
            ["@mui/material/Tab" :default Tab]))

(def ui-tab (interop/react-factory Tab))
(def ui-tabs (interop/react-factory Tabs))

(defsc DeployThings [this {}]
  (dom/div {:style {:display "flex"
                    :jusifyContent :center
                    :height "35vh"
                    :alignItems :center}}
           (dom/div {:style {:position "relative"
                             :height "100%"
                             :left "10rem"}}
                    (dom/img {:style {:width "6rem"
                                      :position "absolute"
                                      :top "8rem"
                                      :height "auto"}
                              :class "hover-effect"
                              :src (str img-url "w8s_icon.svg")})
                    (dom/img {:style {:height "2rem"
                                      :position "absolute"
                                      :top "13.5rem"
                                      :left "-0.3rem"}
                              :src (str img-url "w8s_text.svg")})
                    (dom/div {:class  "svg-container"
                              :onClick #(rroute/route-to! this deploy-page/DeployPage {})
                              :style {:position "absolute"
                                      :top "2rem"
                                      :left "16rem"}}
                             (dom/img {:style {:width "6rem"
                                               :height "auto"}
                                       :src (str img-url "kube_deploy.svg")}))
                    (dom/div {:class  "svg-container"
                              :style {:position "absolute"
                                      :top "12rem"
                                      :left "16rem"}
                              :onClick #(rroute/route-to! this deploy-page/DeployPage {})}
                             (dom/img {:style {:width "6rem"
                                               :height "auto"}
                                       :src (str img-url "ocp_deploy.svg")})))))

(def ui-deploy (comp/computed-factory DeployThings {}))

(defn TabPanel [{:keys [value index] :as props} & children]
  (dom/div {:role "tabpanel"
            :style {:min-height "40vh"}
            :hidden (if (= value index) false true)}
    children))

(defsc KubernetesPage [this {:keys [page]}]
  {:query         [:page]
   :ident         (fn [] [:component/id ::KubernetesPage])
   :initial-state (fn [{:keys [page] :as params}] {:page (or page "")})
   :initLocalState (fn [] {:tab 0})
   :route-segment ["kubernetes"]
   :will-enter (fn [app {:keys [] :as route-params}]
                 (let [app-element (.getElementById js/document "app")]
                   (.scrollTo app-element 0 0))
                 ;; (comp/transact! app [`(mutations/load-url {:url ~(str utils/site-url "/md/dev_page.md")
                 ;;                                            :c ~LandingPage
                 ;;                                            :state-map {:page 'body}})])
                 (dr/route-immediate [:component/id ::KubernetesPage]))}
  (g/container {:spacing 6}
               (g/item {:xs 3}
                       (dom/h2 {:style {:padding 0
                                        :color "#a57aeb"}} "KUBERNETES"))
               (g/item {:xs 9}
                       (l/stack {:direction :row
                                 :jusifyContent :center
                                 :alignItems :center
                                 :spacing 14
                                 :style {:color "#b2b4bf"}}
                                (h2 "DEPLOY") (h2 "OPERATE") (h2 "SECURE") (h2 "MONITOR"))
                       (markdown/render {:body "Maintaining a Kubernetes platform can be challening. With packaged alternatives such as [OKD](https://okd.io/) and [OpenShift](https://www.redhat.com/en/technologies/cloud-computing/openshift)
 operational expenses can be reduced.
 W3T has developed a packaged Kubernetes solution based on the most up to date Open Source tools from the [CNCF](https://www.cncf.io/) (Cloud Native Computing Foundtion).

## W8S: an all-inclusive, turn-key Kubernetes platform!

W8S is a selection of the best OpenSource and Cloud Native technologies and is based on our long and varied experience Deploying, Operating and effectively using Kuberenetes in a multitude of Environments and customer Segments.

"}))
               (g/item {:xs 3} "")
               (g/item {:xs 9}
                       (l/stack {:spacing 4}
                                (dom/h2 {} "Deploy W8S today!")
                                (ui-deploy {})))
               (g/item {:xs 3}
                       (dom/h2 {:style {:color "#a57aeb"}} "FEATURES"))
               (g/item {:xs 9}
                       (ui-tabs {:value (comp/get-state this :tab)
                                 :onChange (fn [e v] (js/console.log v) (comp/set-state! this {:tab v}))}
                                (ui-tab {:label "Container Storage"})
                                (ui-tab {:label "Backup"})
                                (ui-tab {:label "Observability"})
                                (ui-tab {:label "Security"})
                                (ui-tab {:label "DevOps"}))
                       (TabPanel {:value (comp/get-state this :tab)
                                  :index 0}
                                 (markdown/render {:body "Container Platforms are inherently ephemeral, the Kubernetes control plain is moving containers around across nodes and this puts extra .
For W8S we have selected the best and most feature rich Storage solution available: Portworx.
![alt text](/images/PX-Overview.png)
### [Portworx IDC MarketScape](https://portworx.com/blog/pure-storage-has-been-named-a-leader-in-the-2023-idc-marketscape/)"}))
                       (TabPanel {:value (comp/get-state this :tab)
                                  :index 1} (markdown/render {:body "With a Container platform you need Backup at multiple levels. W8S have you covered on all."}))
                       (TabPanel {:value (comp/get-state this :tab)
                                  :index 2} (markdown/render {:body "Observability is about seeing what is going inside your cluster and applications at multiple levels. W8S deploys the most common Observability products.
- **Monitoring:** Graphana is the de-facto standard for monitoring both Infrastructure and Applications. W8S have ready made Dashboard for many use-cases.
- **Alerting:** Prometheus offer alerting
- **Logging:** You want to be able to search logs without clicking through multiple container consoles. W8S ships logs to Elastic via Logstash and searching is done using Kibana.
- **Tracing:** Jaeger is deployed. All "}))
                       (TabPanel {:value (comp/get-state this :tab)
                                  :index 3} (markdown/render {:body "We have chosen NeuVector for the Security layer for W8S based on a thorough review on available technologies on the market. The main Security concerns in any Kubernetes platform are:
 - **Container Vulnerabilities:** Containers may run with outdated or vulnerable software packages.
   *NeuVector Solution:* Performs comprehensive vulnerability scans for images pre-deployment and continuously monitors for vulnerabilities during runtime.
 - Insecure Network Communication: Lack of encryption for pod-to-pod communication. Open communication between namespaces or nodes.
 - Excessive Privileges: Misconfigured Role-Based Access Control (RBAC) policies granting unnecessary privileges.
 - Misconfigured Workloads: Containers running as root. Privileged containers or excessive capabilities.
 - Unprotected Secrets: Insecure handling or storage of secrets in plaintext.
 - Lack of Runtime Threat Detection: Limited visibility into runtime behaviors of containers. Absence of anomaly detection or prevention mechanisms.
 - Supply Chain Risks: Use of untrusted or compromised images from external registries.
 - Unrestricted Egress Traffic: Pods communicating with external networks without restrictions.
 - Host-Level Exploits: Containers with access to host namespaces or file systems, increasing attack surface.
 - Insufficient Auditing: Inadequate monitoring and logging for security events."}))
                       (TabPanel {:value (comp/get-state this :tab)
                                  :index 4} (dom/div {}))
                       #_(markdown/render {:body " - Container storage: W8S
                                                 - Backup
 monitoring, alerting, and tracing, security features like policy management, image and runtime threat scanning, DevOps best practices (CI/CD, GitOps), SSO integrations for user administration, and container-optimized runtimes (Java, Node, Quarkus, etc.)."}))
               (g/item {:xs 12}
                       (dom/img {:style {:width "auto"
                                         :height "auto"}
                                 :src (str img-url "w8s_slide.svg")}))))
