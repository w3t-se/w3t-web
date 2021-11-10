(ns se.w3t.site.mutations
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            [com.fulcrologic.fulcro.algorithms.merge :as merge]
            [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
            [se.w3t.site.utils :as utils]
            [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]))

(defmutation some-mut [{:keys [search limit offset]}]
  (action [{:keys [app state]}]
          (println "ran mutation with " search)))

(defmutation load-url [{:keys [url c state-map]}]
  (action [{:keys [app state]}]
          (go (let [response (<!  (http/get url {:with-credentials? false}))
                    body (clojure.string/replace (:body response) #"\n|$" "")]
                (merge/merge-component! app c (comp/get-initial-state c {:page body}))))))
