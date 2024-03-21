(ns dumrat.art.shapes
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def shapes (atom {}))

(defn petal []
  (let [g (q/create-graphics 400 400 :p2d)]
    (q/with-graphics g
      (q/stroke 255 120 0)
      (q/stroke-weight 3)
      (q/fill 120 120 0)
      (q/begin-shape)
      (q/vertex 200 200)
      (q/bezier-vertex 100 100 300 100 200 200)
      (q/bezier-vertex 300 300 100 300 200 200)
      (q/end-shape :close))
    (q/image g 0 0)
    (swap! shapes assoc :petal g)))

(defn make-shapes []
  (petal))

(defn setup []
  (q/frame-rate 60)
  (make-shapes)
  {:time 0
   :angle 0
   :background {:color 0 :change inc}
   :speed 0.001})

(defn update-state [{time :time angle :angle {bk-color :color bk-change :change} :background speed :speed}]
  (let [next-time (if (> time 1) 0 (+ time speed))
        next-angle (* 2 q/PI time)
        next-background-change (cond
                                 (= bk-color 255) dec
                                 (= bk-color 0) inc
                                 :else bk-change)
        next-background-color (rem (next-background-change bk-color) 256)]
    {:time next-time
     :angle next-angle
     :background {:color next-background-color :change next-background-change}
     :speed speed}))

(defn draw-state [{{bk-color :color} :background angle :angle speed :speed :as state}]
  ;(apply q/background [(or bk-color 0)])
  (q/background 255)
  (q/stroke 0)
  (q/fill 0)
  (q/text (str "Speed = " speed) 10 10)
  (q/translate 200 200)
  (q/rotate angle)
  (q/image (:petal @shapes) -200 -200))

(defn create-sketch []
  (q/sketch
    :title "Kandyan lotus"
    :settings #(q/smooth 8)
    :setup #'setup
    :update #'update-state
    :draw #'draw-state
    :features [:keep-on-top]
    :renderer :p2d
    :size [800 800]
    :middleware [m/fun-mode]))

(def sketch (create-sketch))
