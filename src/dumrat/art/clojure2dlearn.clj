(ns dumrat.art.clojure2dlearn
  (:require [clojure2d.core :as c2d]
            [fastmath.core :as m]))

;; make things as fast as possible
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
(m/use-primitive-operators)

(defn petal-shape []
  (c2d/path-def->shape
      [[:move [-50 87]]
       [:cubic [-100 200 0 180 0 200]]
       [:cubic [0 180 100 200 50 87]]
       [:cubic [25 100 -25 100 -50 87]]]))

(defn draw-petal [canvas]
  (c2d/set-color canvas 120 120 0)
  (c2d/shape canvas (petal-shape)))

(defonce esc-pressed? (atom false))

(defmethod c2d/key-released ["lotus" (char 27)] [event state]
  (reset! esc-pressed? true))

(defn draw [canvas window ^long frameno {^long start-time :start-time :as state}]
  (if @esc-pressed?
    (do
      (reset! esc-pressed? false)
      (c2d/close-window window))
    (let [curr-time (. System (nanoTime))
          elapsed-time (- curr-time start-time)
          frame-rate (/ (* frameno 1000000000.0) elapsed-time)]
      (-> canvas
          (c2d/set-color 255 255 255)
          (c2d/rect 0 0 (c2d/width canvas) (c2d/height canvas))
          (c2d/set-color 0 0 0)
          (c2d/text (format "%2f" frame-rate) 20 20 :left)
          (c2d/translate 400 400)
          (c2d/flip-y))
      (c2d/rotate canvas (/ frameno m/TWO_PI 60))
      (dotimes [i 6]
        (draw-petal canvas)
        (c2d/rotate canvas m/THIRD_PI))
      state)))

(def window (c2d/show-window {:canvas (c2d/canvas 800 800 :mid)
                              :window-name "lotus"
                              :hint :mid
                              :always-on-top? true
                              :draw-fn draw
                              :setup (fn [c _]
                                       (c2d/set-background c 45 45 41)
                                       {:start-time (. System (nanoTime))})}))
