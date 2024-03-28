(ns dumrat.art.experiments
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 60))

(defn update-state [{speed :speed time :time :as state}]
  state)

(defn draw-petal []
  (q/fill 160 20 20)
  (q/begin-shape)
  (q/bezier -50 -87 -100 -200 0 -180 0 -200)
  (q/bezier 50 -87 100 -200 0 -180 0 -200)
  (q/bezier-vertex -50 -87 -50 -120 50 -87)
  (q/end-shape :close))


(defn draw-state [state]
  (q/translate 600 600)
  (draw-petal))

(defn create-sketch []
  (q/sketch
    :title "Experiment"
    :settings #(q/smooth 8)
    :setup #'setup
    :update #'update-state
    :draw #'draw-state
    :features [:keep-on-top]
    :renderer :p2d
    :size [1200 1200]
    :middleware [m/fun-mode]))

(def sketch (create-sketch))
