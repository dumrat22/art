(ns dumrat.art.petal
  (:require [clojure2d.core :as c2d]
            [fastmath.core :as m]))

;; make things as fast as possible
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
(m/use-primitive-operators)

(defn mirror-curve-y
  "Takes a curve, mirrors on y axis"
  [[c p]]
  [c
   (->> p
        (partition 2)
        (reverse)
        (drop 1)
        (map (fn [[^long x y]] [(- x) y]))
        (flatten)
        (into []))])

(defn mirror-path-y
  [curves]
  (into (mapv (fn [[c p]] [c (into [] (drop 2 p))]) curves)
    (->> curves
         (map mirror-curve-y)
         (reverse)
         (into []))))

(comment

  (partition 2 [-100 200 0 180 0 200])

  (mirror-curve-y [:cubic [0 0 -100 200 0 180 0 200]])
  (mirror-path-y [[:cubic [0 0 -100 200 0 180 0 200]]])

  #_f)

(defn symmetric-petal-shape
  "Create petal shape given a run of cubics that describe one half of the petal at r radius from given center"
  []
  (c2d/path-def->shape
    (into
     (into
      [[:move [0 0]]]
      (mirror-path-y
       [[:cubic [0 0 -100 100 -80 120 -50 120]]
        [:cubic [-50 120 -30 150 -10 165 0 200]]]))
     [[:move [0 0]]])))

(defn petal-shape []
  (c2d/path-def->shape
      [[:move [-50 87]]
       [:cubic [-100 200 0 180 0 200]]
       [:cubic [0 180 100 200 50 87]]
       [:cubic [25 100 -25 100 -50 87]]]))

(defn draw-petal [canvas]
  (c2d/shape canvas (symmetric-petal-shape)))

(defonce esc-pressed? (atom false))

(defmethod c2d/key-released ["petal" (char 27)] [_ _]
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
          (c2d/flip-y)
          #_(draw-petal))
      (c2d/rotate canvas (/ frameno m/TWO_PI 60))
      #_(c2d/pattern-mode canvas (:texture state) -100 -100 400 400)
      (dotimes [i 6]
        (draw-petal canvas)
        (c2d/rotate canvas (/ m/TWO_PI 6)))
      state)))

(def window (c2d/show-window {:canvas (c2d/canvas 800 800 :mid)
                              :window-name "petal"
                              :hint :mid
                              :always-on-top? true
                              :position [0 0]
                              :draw-fn draw
                              :setup (fn [c _]
                                       (c2d/set-background c 45 45 41)
                                       {:start-time (. System (nanoTime))
                                        :texture (c2d/load-image "resources/lotus1.png")})}))
