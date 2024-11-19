(ns se.w3t.site.blog-entries
  (:require [clojure.string :as string]))

(defn reading-time [text]
  (int (/ (count (remove string/blank? (string/split text #"\s+"))) 230)))

(defn blogs []
  (reverse (sort-by :date
                    (map-indexed (fn [i x] (merge x {:reading-time (str (reading-time (:content x)) " min")}))
                                 [{:id "ethcc7"
                                   :type "TRIP"
                                   :tags #{:ethereum}
                                   :date #inst "2024-06-07"
                                   :author "zertan"
                                   :image "/images/ethcc7.png"
                                   :heading "# Going to Brussels for EthCC[7]!"
                                   :first-paragraph "The Ethereum Community is (still) going strong, this year in Brussels!"
                                   :content ""}
                                  {:id "sqeave"
                                   :type "DEVELOPMENT"
                                   :tags #{:solid-js :squint-cljs}
                                   :date #inst "2024-11-11"
                                   :author "zertan"
                                   :heading "# Introducing: [Sqeave](https://github.com/w3t-se/sqeave) a Cljs-squint based Solid-js framework"
                                   :first-paragraph "(Don't worry clarification ahead :blush:)"
                                   :image "/images/sqeave.png"
                                   :content ""}
                                  {:id "crd-viz"
                                   :type "DEVELOPMENT"
                                   :tags #{:fulcro}
                                   :sections ["intro"]
                                   :author "zertan"
                                   :date #inst "2023-10-01"
                                   :heading "# Introducing [crd-viz](https://github.com/w3t-se/crd-viz) ..."
                                   :first-paragraph "We are proud to announce crd-viz, a tool designed to make the creation of Kubernetes Custom Resource Definitions (CRDs) more intuitive. With crd-viz, you can interactively build and visualize your CustomResource YAMLs, reducing errors and accelerating development."
                                   :content ""}
                                  {:id "lf"
                                   :type "CASE"
                                   :tags #{:devops :containerization}
                                   :date #inst "2024-04-01"
                                   :author "zertan"
                                   :image "/images/lumera_ocp.png"
                                   :heading "# Packaging one of Sweden's most used Insurance Platforms to run on OpenShift."
                                   :first-paragraph "Containerizing Lumera for a large Insurance Company to speed up Test Automation and Environment Management!"
                                   :content ""}
                                  #_{:type "TUTORIAL"
                                     :tags #{:solid-js :squint-cljs}
                                     :date "01-11-22"
                                     :author "Daniel Hermansson"
                                     :heading "Developing "
                                     :first-paragraph ""
                                     :content "```clojure
(+ 1 1)
```"}
                                  {:id "agronod"
                                   :type "CASE"
                                   :tags #{:platform :data}
                                   :date #inst "2022-12-12"
                                   :author "zertan"
                                   :heading "# Migrating an OpenShift Platform from Azure to a Swedish Cloud."
                                   :first-paragraph ""
                                   :content ""}
                                  {:id "primekey"
                                   :type "CASE"
                                   :tags #{:devops :automation}
                                   :date #inst "2021-11-01"
                                   :author "zertan"
                                   :heading "# Developing a Release Automation flow for a Customer within Public Key Insfrasructure!"
                                   :first-paragraph "Our client, a leader in Public Key Infrastructure (PKI) products for almost two decades, was ready to modernize their development practices with a focus on automation. Recognizing the close relationship between DevOps and IT infrastructure, we proposed advancing their adoption of Kubernetes to enhance their release automation flow."
                                   :content ""}]))))
