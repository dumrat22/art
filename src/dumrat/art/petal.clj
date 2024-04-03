(ns dumrat.art.petal
  (:require [clojure2d.core :as c2d]
            [fastmath.core :as m]))

;; make things as fast as possible
(set! *warn-on-reflection* true)
(set! *unchecked-math* :warn-on-boxed)
(m/use-primitive-operators)

(defn mirror-curve-y
  "Takes any curve, mirrors on y axis"
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
  (->> curves
       (map mirror-curve-y)
       (reverse)
       (into [])
       (into
         (mapv #(update % 1 (fn [v] (into [] (drop 2 v)))) curves))))

(comment

  (partition 2 [-100 200 0 180 0 200])

  (mirror-curve-y [:cubic [0 0 -100 200 0 180 0 200]])
  (mirror-path-y [[:move [0 0]] [:cubic [-100 200 0 180 0 200]]])

  (mapv #(update % 1 (fn [v] (into [] (drop 2 v))))
     [[:move [0 0 0 0]]
      [:cubic [0 0 -100 200 0 180 0 200]] false])

  #_f)

(defn symmetric-shape
  "Create shape given a run of moves that describe one half of the petal"
  [path]
  (c2d/path-def->shape
    (mirror-path-y path)))

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
          (c2d/scale 2)
          (c2d/set-color 255 255 255)
          (c2d/rect 0 0 (c2d/width canvas) (c2d/height canvas))
          (c2d/set-color 0 0 0)
          (c2d/set-stroke 1)
          (c2d/text (format "%f" frame-rate) 20 20 :left)
          (c2d/translate 200 200)
          (c2d/rotate (/ frameno m/TWO_PI 240))
          (c2d/flip-y)
          (c2d/set-color 0 0 0)
          (c2d/ellipse 0 0 250 250)
          (c2d/set-color 255 255 255)
          (c2d/ellipse 0 0 248 248)
          (c2d/set-color 0 0 0)
          (c2d/ellipse 0 0 242 242)
          (c2d/set-color 232 177 40)
          (c2d/ellipse 0 0 242 242))
      (dotimes [i 6]
        (c2d/set-color canvas (+ 120 (* (m/sin (/ frameno m/TWO_PI 60)) 40)) 0 0)
        (c2d/shape canvas
                   (symmetric-shape
                    [[:move [0 0 0 0]]
                     [:line [0 0 -50 87]]
                     [:cubic [-50 87 -60 120 0 90 0 120]]]))
        (c2d/rotate canvas (/ m/TWO_PI 6)))
      ;; rect
      (-> canvas
        (c2d/set-color 0 0 0)
        (c2d/rect -50 -50 100 100)
        (c2d/rotate m/QUARTER_PI)
        (c2d/rect -50 -50 100 100)
        (c2d/set-color 255 255 255)
        (c2d/ellipse 0 0 100 100))
      state)))

(def window (c2d/show-window {:canvas (c2d/canvas 800 800 :mid)
                              :window-name "petal"
                              :hint :mid
                              :always-on-top? true
                              :position [0 0]
                              :draw-fn #'draw
                              :setup (fn [c _]
                                       (c2d/set-background c 45 45 41)
                                       {:start-time (. System (nanoTime))
                                        :texture (c2d/load-image "resources/lotus1.png")})}))
