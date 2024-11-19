(ns se.w3t.site.markdown
  (:require [com.fulcrologic.fulcro.algorithms.react-interop :as interop]
            [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
            ["react-markdown" :default ReactMarkdown]
            ["remark-gfm" :default remarkGfm]
            ["highlight.js/lib/core" :as hljs]
            #_["@fec/remark-a11y-emoji" :default a11yEmoji]
            ["remark-emoji" :default emoji]
            ["rehype-highlight" :default rehypeHighlight]
            ["rehype-raw" :default rehypeRaw]
            ["highlight.js/lib/languages/clojure" :as clojure]
            ["highlight.js/lib/languages/bash" :as bash]))

(hljs/registerLanguage "clojure" clojure)
(hljs/registerLanguage "bash" bash)

(def ui-markdown (interop/react-factory ReactMarkdown))

(def languages {:clojure clojure})

(defsc Render [_this {:keys [body]}]
  {}
  (when body
    (ui-markdown {:children body
                  :remarkPlugins [remarkGfm emoji ]
                  :rehypePlugins [rehypeRaw (partial rehypeHighlight (clj->js {:languages languages}))]})))

(def render (comp/factory Render))
