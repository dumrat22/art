(ns dumrat.art.coordinates
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 60)
  (q/texture-mode :normal)
  {:petal-texture (q/load-image "resources/lotus1.png")
   :time 0
   :angle 0
   :speed 0.001
   :x-res 50
   :y-res 50})

(defn update-state [{speed :speed time :time :as state}]
  (-> state
    ;(assoc :time (if (> time 1) 0 (+ time speed)))
    (assoc :angle (* 2 q/PI time))))

(defn draw-petal [state]
  (q/image (:petal-texture state) 0 0)
  (q/begin-shape)
  (q/texture (:petal-texture state))
  (q/vertex -50 87 0 0)
  ;; (q/vertex 50 87 0 1)
  ;; (q/vertex 50 50 1 1)
  (q/bezier-vertex -100 200 0 180 0 200)
  ;(q/bezier-vertex 0 180 100 200 50 87)
  ;(q/bezier-vertex 25 100 -25 100 -50 87)
  (q/end-shape :close))

(defn texture-try []
  (let [gr (q/create-graphics 100 100)]
  ; draw something on graphics that will be texture
   (q/with-graphics gr
     (q/background 255)
     (q/fill 255 0 0)
     (q/rect 0 60 100 40)
     (q/fill 0 150 0)
     (q/rect 0 0 100 60))
  ; draw graphics to see what we got
   (q/image gr 0 0)
  ; draw custom shape using texture we created above
   (q/with-translation [250 250]
     (q/begin-shape)
     (q/texture gr)
     (q/vertex 50 100 75 100)
     (q/vertex 100 50 100 75)
     (q/vertex 100 -50 100 25)
     (q/vertex 50 -100 75 0)
     (q/vertex -50 -100 25 0)
     (q/vertex -100 -50 0 25)
     (q/vertex -100 50 0 75)
     (q/vertex -50 100 25 100)
     (q/end-shape :close))))

(defn draw-state [{angle :angle x-res :x-res y-res :y-res :as state}]
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
    #_(do
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
              (q/text (str "(" x "," y ")") (+ x 2) (+ y 7))))))
    (q/scale 1 -1)
    (q/rotate angle)
    (q/fill 255 0)
    (q/stroke 120 120 0)
    (q/stroke-weight 1)
    (dotimes [i 1]
      (draw-petal state)
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
