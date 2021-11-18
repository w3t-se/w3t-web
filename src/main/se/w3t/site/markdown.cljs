(ns se.w3t.site.markdown
  (:require [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            ["react-markdown" :default ReactMarkdown]
            ["remark-gfm" :default remarkGfm]
            ["rehype-highlight" :default rehypeHighlight]
            ["highlight.js/lib/languages/clojure.js" :as clojure]))

(def ui-markdown (interop/react-factory ReactMarkdown))

(def languages {:clojure clojure})

(defsc Render [_this {:keys [body]}]
  {}
  (when body
    (ui-markdown {:children body
                  :remarkPlugins [remarkGfm]
                  :rehypePlugins [(partial rehypeHighlight (clj->js {:languages languages}))]})))

(def render (comp/factory Render))
