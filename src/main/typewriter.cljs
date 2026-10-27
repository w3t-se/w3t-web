(ns typewriter
  "Typewriter effect backed by the typed.js npm module."
  (:require ["solid-js" :refer [onMount onCleanup]]
            ["typed.js$default" :as Typed]))

(defn- reduced-motion? []
  (.-matches (js/window.matchMedia "(prefers-reduced-motion: reduce)")))

(defn Typewriter
  "Cycles through `words`. Screen readers get `label` instead of the animation.
   Users who prefer reduced motion see the first word, static."
  [{:keys [words label class]}]
  (let [st {:el nil}]
    (onMount
     (fn []
       (if (reduced-motion?)
         (set! (.-textContent (:el st)) (first words))
         (let [t (Typed. (:el st) {:strings words
                             :typeSpeed 70
                             :backSpeed 40
                             :startDelay 300
                             :backDelay 1800
                             :smartBackspace false
                             :loop true
                             :cursorChar "_"})]
           (onCleanup #(.destroy t))))))
    #jsx [:span {:class class}
          [:span {:class "sr-only"} label]
          [:span {:aria-hidden "true" :ref (fn [e] (aset st :el e))}]]))
