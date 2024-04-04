(ns dumrat.art.petal
  (:require [clojure2d.core :as c2d]
            [clojure2d.pixels :as p]
            [clojure2d.extra.overlays :as o]
            [fastmath.core :as m]
            [clj-async-profiler.core :as prof]))

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

(defn draw [canvas window ^long frameno {^long start-time :start-time petal-shape :petal-shape :as state}]
  (if @esc-pressed?
    (do
      (reset! esc-pressed? false)
      (c2d/close-window window))
    (let [curr-time (. System (nanoTime))
          elapsed-time (- curr-time start-time)
          frame-rate (/ (* frameno 1000000000.0) elapsed-time)]
      (-> canvas
          (c2d/scale 2)
          (c2d/set-color 0 0 0 0)
          (c2d/rect 0 0 (c2d/width canvas) (c2d/height canvas))
          (c2d/translate 150 150)
          (c2d/rotate (/ frameno m/TWO_PI 60))
          (c2d/flip-y)
          (c2d/set-color 116 88 20)
          (c2d/ellipse 0 0 250 250)
          (c2d/set-color 255 255 255)
          (c2d/ellipse 0 0 248 248)
          (c2d/set-color 0 0 0)
          (c2d/ellipse 0 0 242 242)
          (c2d/set-color 232 177 40)
          (c2d/ellipse 0 0 242 242))
      (p/set-canvas-pixels! canvas
       (->> canvas
               p/to-pixels
               (p/filter-channels p/gaussian-blur-5)))
      (dotimes [i 6]
        (c2d/set-color canvas (+ 120 (* (m/sin (/ frameno m/TWO_PI 60)) 40)) 0 0)
        (c2d/shape canvas petal-shape)
        (c2d/rotate canvas (/ m/TWO_PI 6)))
      (p/set-canvas-pixels! canvas
       (->> canvas
               p/to-pixels
               (p/filter-channels p/gaussian-blur-1)))
      (-> canvas
              (c2d/set-color 0 0 0 180)
              (c2d/rect -50 -50 100 100)
              (c2d/rotate m/QUARTER_PI)
              (c2d/rect -50 -50 100 100)
              (c2d/set-color 255 255 255 100)
              (c2d/ellipse 0 0 100 100)
              (c2d/rotate (- m/QUARTER_PI)))
      ;;Slightly blurry noise
      (p/set-canvas-pixels! canvas (p/to-pixels (o/render-noise canvas (:noise state))))
      ;;Spots
      #_(p/set-canvas-pixels! canvas (p/to-pixels (o/render-spots canvas (:spots state))))
      ;;Framerate display
      #_(-> canvas
            (c2d/flip-y)
            (c2d/rotate (- (/ frameno m/TWO_PI 60)))
            (c2d/translate -150 -150)
            (c2d/set-color 0 0 0)
            (c2d/set-stroke 1)
            (c2d/text (format "%f" frame-rate) 20 20 :left))
      ;; Output images
      #_(c2d/save canvas (str "out/lotus" frameno ".jpg"))
      ;; exit after set number of frames
      #_(when (> frameno 400) (c2d/close-window window))
      state)))

(def window
  #_(prof/profile)
  (c2d/show-window {:canvas (c2d/canvas 600 600 :mid)
                    :window-name "petal"
                    :hint :mid
                    :always-on-top? true
                    :position [0 0]
                    :draw-fn #'draw
                    :setup (fn [c _]
                             (c2d/set-background c 45 45 41)
                             {:start-time (. System (nanoTime))
                              :noise (o/noise-overlay 300 300 {:alpha 30})
                              :spots (o/spots-overlay 300 300 {:alpha 40 :intensities [10 100 200 300]})
                              :petal-shape (symmetric-shape
                                             [[:move [0 0 0 0]]
                                              [:line [0 0 -50 87]]
                                              [:cubic [-50 87 -60 120 0 100 0 120]]])})}))

#_(prof/serve-ui 8080)
