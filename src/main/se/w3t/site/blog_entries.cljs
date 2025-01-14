(ns se.w3t.site.blog-entries
  (:require [clojure.string :as string]))

(defn reading-time [text]
  (int (/ (count (remove string/blank? (string/split text #"\s+"))) 230)))

(defn blogs []
  (reverse (sort-by :date
                    (map-indexed (fn [i x] (merge x {:reading-time (str (reading-time (:content x)) " min")}))
                                 [{:id "ethcc7"
                                   :type "IRL"
                                   :tags #{:ethereum}
                                   :date #inst "2024-06-07"
                                   :author "zertan"
                                   :image "ethcc7.png"
                                   :heading "# Going to Brussels for EthCC[7]!"
                                   :first-paragraph "The Ethereum Community is (still) going strong, this year in Brussels!"
                                   :content ""}
                                  {:id "sqeave"
                                   :type "DEVELOPMENT"
                                   :tags #{:solid-js :squint-cljs}
                                   :date #inst "2024-11-11"
                                   :author "zertan"
                                   :keywords "clojure(script), solid-js, squint-cljs"
                                   :og-title "Sqeave: solid-js meets squint-cljs"
                                   :og-image "data:image/jpeg;base64,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"
                                   :sections ["intro" "root component" "main component" "details"]
                                   :heading "# Introducing: [Sqeave](https://github.com/w3t-se/sqeave) a Cljs-squint based Solid-js framework"
                                   :first-paragraph "(Don't worry clarification ahead :blush:)"
                                   :image "sqeave.png"
                                   :content ""}
                                  #_{:id "lifter"
                                   :type "DEVELOPMENT"
                                   :tags #{:fulcro :devops}
                                   :sections ["intro"]
                                   :author "zertan"
                                   :date #inst "2023-10-01"
                                   :heading "# Introducing ... [Lifter](https://github.com/w3t-se/lifter)!"
                                   :image "lifter_dude_wins.svg"
                                   :first-paragraph "We are proud to announce [Lifter](https://github.com/w3t-se/lifter), a tool designed to make the creation of Kubernetes Custom Resource Definitions (CRDs) more intuitive. With Lifter, you can interactively build and visualize your CustomResource YAMLs, reducing errors and accelerating development."
                                   :content ""}
                                  #_{:id "lf"
                                   :type "CASE"
                                   :tags #{:devops :containerization}
                                   :date #inst "2024-04-01"
                                   :author "zertan"
                                   :image "lumera_ocp.png"
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
                                  #_{:id "agronod"
                                   :type "CASE"
                                   :tags #{:platform :data}
                                   :date #inst "2022-12-12"
                                   :author "zertan"
                                   :heading "# Migrating an OpenShift Platform from Azure to a Swedish Cloud."
                                   :first-paragraph ""
                                   :content ""}
                                  #_{:id "primekey"
                                   :type "CASE"
                                   :tags #{:devops :automation}
                                   :date #inst "2021-11-01"
                                   :author "zertan"
                                   :heading "# Developing a Release Automation flow for a Customer within Public Key Insfrasructure!"
                                   :first-paragraph "Our client, a leader in Public Key Infrastructure (PKI) products for almost two decades, was ready to modernize their development practices with a focus on automation. Recognizing the close relationship between DevOps and IT infrastructure, we proposed advancing their adoption of Kubernetes to enhance their release automation flow."
                                   :content ""}]))))
