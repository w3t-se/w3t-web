(ns starfield
  "The hero's slow, twinkling starfield, drawn on a canvas."
  (:require ["solid-js" :refer [onMount onCleanup]]))

(defn- make-stars [w h]
  (mapv (fn [_]
          {:x (* (js/Math.random) w)
           :y (* (js/Math.random) h)
           :r (+ 0.3 (* 0.9 (js/Math.random)))
           :p (* (js/Math.random) js/Math.PI 2)
           :v (+ 0.03 (* 0.12 (js/Math.random)))
           :hot (< (js/Math.random) 0.01)})
        (range (js/Math.round (/ (* w h) 6500)))))

(defn Starfield [_props]
  (let [st {:canvas nil :raf nil :stars [] :w 0 :h 0}]
    (onMount
     (fn []
       (let [c (:canvas st)
             ctx (.getContext c "2d")
             reduce? (.-matches (js/window.matchMedia "(prefers-reduced-motion: reduce)"))
             size (fn []
                    (let [dpr (js/Math.min (or js/window.devicePixelRatio 1) 2)
                          w (.-clientWidth c)
                          h (.-clientHeight c)]
                      (set! (.-width c) (* w dpr))
                      (set! (.-height c) (* h dpr))
                      (.setTransform ctx dpr 0 0 dpr 0 0)
                      (aset st :w w)
                      (aset st :h h)
                      (aset st :stars (make-stars w h))))
             draw (fn draw [ts]
                    (.clearRect ctx 0 0 (:w st) (:h st))
                    (doseq [s (:stars st)]
                      (let [a (if reduce? 0.6 (+ 0.35 (* 0.35 (js/Math.sin (+ (:p s) (/ ts 900))))))]
                        (set! (.-fillStyle ctx) (if (:hot s)
                                                  (str "rgba(255,80,60," a ")")
                                                  (str "rgba(255,255,255," a ")")))
                        (.beginPath ctx)
                        (.arc ctx (:x s) (:y s) (:r s) 0 6.283)
                        (.fill ctx)
                        (when-not reduce?
                          (aset s :y (- (:y s) (:v s)))
                          (when (< (:y s) -2)
                            (aset s :y (+ (:h st) 2))
                            (aset s :x (* (js/Math.random) (:w st)))))))
                    (when-not reduce?
                      (aset st :raf (js/requestAnimationFrame draw))))]
         (size)
         (js/window.addEventListener "resize" size)
         (aset st :raf (js/requestAnimationFrame draw))
         (onCleanup (fn []
                      (js/window.removeEventListener "resize" size)
                      (js/cancelAnimationFrame (:raf st)))))))
    #jsx [:canvas {:id "stars" :aria-hidden "true"
                   :ref (fn [e] (aset st :canvas e))}]))
