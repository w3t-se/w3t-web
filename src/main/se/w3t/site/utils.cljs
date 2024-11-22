(ns se.w3t.site.utils
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.reader :as reader]
            [cljs-http.client :as http]
            [markdown-to-hiccup.core :as md]
            [sablono.core :as html :refer-macros [html]]
            [cljs.core.async :refer [<!]]))

(def img-url "https://bafybeiedqglyvbcyp6vsrggiqrjypsfleb4t4fillsdj4tvnmq5jsjszuy.ipfs.flk-ipfs.xyz/")

(defn md->html [md-string]
  (html
   (md/md->hiccup
    md-string)))

(def site-url "http://localhost:8000")

(defn get-resource [url a]
  (go (let [response (<!  (http/get url {:with-credentials? false
                                        ;:query-params {}
                                         }))]
        (reset! a (:body response)))))
