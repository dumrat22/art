(ns dumrat.art.coordinates
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def shapes (atom {}))

(defn petal [[center-x center-y] r angle]
  (let [g (q/create-graphics 400 400 :p2d)]
    (q/with-graphics g
      (q/translate 200 200)
      (q/fill 160 20 20)
      (q/begin-shape)
      ;(q/bezier -50 -87 -100 -200 0 -180 0 -200)
      (q/vertex 50 -87)
      (q/vertex 0 -200)
      (q/vertex -50 -87)
      (q/arc 0 0 200 200 (* q/PI 1.33) (* q/PI 1.67) :chord)
      ;(q/bezier 50 -87 100 -200 0 -180 0 -200)
      (q/end-shape :close))
    (q/image g 0 0)
    (swap! shapes assoc :petal g)))

(defn create-shapes []
  (petal [0 0] 200 (/ q/PI 6)))

(defn setup []
  (q/frame-rate 60)
  (create-shapes)
  {:time 0
   :angle 0
   :speed 0.001
   :x-res 50
   :y-res 50})

(defn update-state [{speed :speed time :time :as state}]
  (-> state
    (assoc :time (if (> time 1) 0 (+ time speed)))
    (assoc :angle (* 2 q/PI time))))

(defn draw-petal1 []
  (q/fill 160 20 20)
  (q/begin-shape)
  (q/bezier -50 -87 -100 -200 0 -180 0 -200)
  (q/bezier 50 -87 100 -200 0 -180 0 -200)
  (q/vertex 50 -87)
  (q/vertex 0 -200)
  (q/vertex -50 -87)
  (q/bezier-vertex -50 -87 -50 -120 50 -87)
  (q/end-shape :close))

(defn draw-petal2 []
  (q/fill 160 20 20)
  (q/begin-shape)
  (q/vertex -50 -87)
  (q/bezier-vertex -100 -200 0 -180 0 -200)
  (q/bezier-vertex 0 -180 100 -200 50 -87)
  (q/bezier-vertex 25 -100 -25 -100 -50 -87)
  (q/end-shape :close))

;;TODO : fix third bezier-vertex to coincide with circle


(defn draw-state [{angle :angle x-res :x-res y-res :y-res}]
  (let [width (q/width)
        height (q/height)]
    (q/background 255)
    (q/scale 1.5)
    (q/stroke 120)
    (q/fill 0)
    (q/text-size 10)
    (q/text-num (q/current-frame-rate) 20 20)
    (q/text-num angle 20 30)
    (q/text-size 8)
    (q/fill 120)
    (q/translate 400 400)
    (q/stroke 120)
    (q/stroke-weight 1)
    (dotimes [i (quot width x-res)]
      (let [x (- (* i x-res) 400)]
        (q/line [x -400] [x 400])))
    (dotimes [j (quot height y-res)]
      (let [y (- (* j y-res) 400)]
        (q/line [-400 y] [400 y])))
    (dotimes [i (quot width x-res)]
      (dotimes [j (quot height y-res)]
        (let [x (- (* i x-res) 400)
              y (- (* j y-res) 400)]
          (q/text (str "(" x "," y ")") (+ x 2) (+ y 7)))))
    (q/rotate angle)
    (q/fill 255 0)
    (q/stroke 120 120 0)
    (q/stroke-weight 1)
    (q/ellipse 0 0 200 200)
    (q/fill 120 120 0)
    (dotimes [i 6]
      ;(q/image (:petal @shapes) -200 -200)
      (draw-petal2)
      (q/rotate (/ q/PI 3)))))

(defn create-sketch []
  (q/sketch
    :title "Coordinates"
    :settings #(q/smooth 8)
    :setup #'setup
    :update #'update-state
    :draw #'draw-state
    :features [:keep-on-top]
    :renderer :p2d
    :size [1200 1200]
    :location [200 200]
    :middleware [m/fun-mode]))

(def sketch (create-sketch))
