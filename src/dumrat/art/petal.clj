(ns dumrat.art.petal
  (:require [clojure2d.core :as c2d]
            [clojure2d.pixels :as p]
            [clojure2d.extra.overlays :as o]
            [fastmath.core :as m]))

;; make things as fast as possible
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
(m/use-primitive-operators)

(defn draw [canvas window ^long frameno {^long start-time :start-time :as state}]
  (let [curr-time (. System (nanoTime))
        elapsed-time (- curr-time start-time)
        frame-rate (/ (* frameno 1000000000.0) elapsed-time)
        o-r (* 2 (+ (:outer-radius state) (* 5 (m/sin (/ frameno m/TWO_PI 5)))))
        rot-angle (mod (/ frameno m/TWO_PI 60) m/TWO_PI)]
    ;;ඛණ්ඩාංක පද්ධතිය සෙටප් කිරීම
    (-> canvas
        (c2d/scale 2)
        (c2d/translate 150 150)
        (c2d/rotate rot-angle)
        (c2d/flip-y))
    ;;කහ පැහැ පල්ස් වෙන රැස් මාලාව
    (-> canvas
        (c2d/set-color 116 88 20 120)
        (c2d/ellipse 0 0 o-r o-r)
        (c2d/set-color 150 100 30 120)
        (c2d/ellipse 0 0 (- o-r 2) (- o-r 2))
        (c2d/set-color 0 0 0 120)
        (c2d/ellipse 0 0 (- o-r 8) (- o-r 8))
        (c2d/set-color 232 177 40 120)
        (c2d/ellipse 0 0 (- o-r 8) (- o-r 8)))
    ;;රැස් බොඳ
    (p/set-canvas-pixels! canvas
     (->> canvas
             p/to-pixels
             (p/filter-channels p/gaussian-blur-5)))
    ;;මැද ඉද්ද මල
    (dotimes [_ 5]
      (c2d/set-color canvas
                     240 240 240
                     (abs (* 180 (m/sin (* 1.5 rot-angle)))))
      (c2d/shape canvas (:ඉද්ද-පෙත්ත state))
      (c2d/set-color canvas 240 180 40 (abs (* 180 (m/sin (* 1.5 rot-angle)))))
      ;(c2d/ellipse canvas 0 0 16 16)
      (c2d/rotate canvas (/ m/TWO_PI 5)))
    ;;බොඳ
    (p/set-canvas-pixels! canvas
     (->> canvas
          p/to-pixels
          (p/filter-channels p/gaussian-blur-2)))
    ;;අනෙක් පැත්තට කරකැවීම
    (c2d/rotate canvas (- m/TWO_PI))
    (c2d/rotate canvas (* 4 rot-angle))
    ;;බාහිර රතු නෙළුම
    (dotimes [_ 6]
      (c2d/set-color canvas (+ 90 (* (m/sin (/ frameno m/TWO_PI 60)) 40)) 0 0 220)
      (c2d/shape canvas (:නෙලුම්-පෙත්ත state))
      (c2d/rotate canvas (/ m/TWO_PI 6)))
    ;;බොඳ
    (p/set-canvas-pixels! canvas
         (->> canvas
              p/to-pixels
              (p/filter-channels (p/gaussian-blur 1))))
    ;;Reset all translations
    (c2d/reset-matrix canvas)
    ;;තිත් (noise)
    (-> canvas
      (c2d/image ((:noises state) (mod frameno 20))))
    ;;Framerate display
    (-> canvas
        (c2d/set-color 0 0 0)
        (c2d/set-stroke 1)
        (c2d/text (format "%f" frame-rate) 20 20 :left))
    ;;Title
    #_(-> canvas
          (c2d/scale 2)
          (c2d/set-color 180 160 80 220)
          (c2d/text "මණ්ඩල" 40 40 :center))
    ;;Output images
    #_(do
        (c2d/save canvas (str "out/lotus" frameno ".jpg"))
        (when (> frameno 800) (c2d/close-window window)))
    state))

(def window
  (c2d/show-window {:canvas (c2d/canvas 600 600 :high)
                    :window-name "මණ්ඩල"
                    :hint :mid
                    :always-on-top? true
                    :position [0 0]
                    :draw-fn #'draw
                    :fps 60
                    :setup (fn [c _]
                             (c2d/set-background c 45 45 40)
                             {:start-time (. System (nanoTime))
                              :outer-radius 116
                              :noises (vec (repeatedly 20 #(o/noise-overlay 300 300 {:alpha 25})))
                              :spots (o/spots-overlay 50 50 {:alpha 100 :intensities [10 20 30 40 50]})
                              ;;නෙලුම් පෙත්ත
                              :නෙලුම්-පෙත්ත (c2d/path-def->shape
                                               [[:move [0 50]]
                                                [:cubic [-8 50 -16 44 -25 44]]
                                                [:line [-50 87]]
                                                [:cubic [-55 110 0 100 0 120]]
                                                [:cubic [0 100 55 110 50 87]]
                                                [:line [25 44]]
                                                [:cubic [16 44 8 50 0 50]]
                                                [:close]])
                              ;;ඉද්ද පෙත්ත
                              :ඉද්ද-පෙත්ත (c2d/path-def->shape
                                            [[:move [0 0]]
                                             [:cubic [-16 20 -35 45 0 50]]
                                             [:cubic [35 45 16 20 0 0]]])})}))
