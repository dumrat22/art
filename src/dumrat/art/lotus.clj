(ns dumrat.art.lotus
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 60)
  {:background-color [255 0]
   :center {:rotation-angle 0
            :line-count 8
            :radius 100}})

(defn update-state [state]
  (-> state
      ;(assoc-in [:center :rotation-angle] (q/radians (q/frame-count)))))
      (assoc-in [:center :rotation-angle] 0)))

(defn circle [diameter thickness]
  (q/stroke-weight thickness)
  (q/ellipse 0 0 diameter diameter))

(defn circles []
  (circle 700 5)
  (circle 680 4)
  (circle 400 5)
  (circle 250 5)
  (circle 200 5))

(defn center-lattice [angle line-count radius]
  (q/stroke-weight 4)
  (q/rotate angle)
  (dotimes [i line-count]
    (let [a (- (* 2 radius (/ i line-count)) radius)
          b (- (Math/sqrt (- (* radius radius) (* a a))) 2)]
      (q/line a b a (- b))
      (q/line b a (- b) a))))

(defn draw-state [{background-color :background-color
                   {rotation-angle :rotation-angle
                    line-count :line-count
                    radius :radius} :center}]
  (apply q/background background-color)
  (q/translate 0 0)
  (q/fill 0)
  (q/text-num (q/current-frame-rate) 0 10)
  (q/fill 255 0)
  (q/stroke 0)
  ;(q/translate 400 400)
  ;(circles)
  ;(center-lattice rotation-angle line-count radius)
  (q/stroke 255 102 0)
  (q/line 340 80 40 40)
  (q/line 360 360 60 320)
  (q/stroke 0)
  (q/bezier 340 80 40 40 360 360 60 320));
   

(defn create-sketch []
  (q/sketch
    :title "Kandyan lotus"
    :settings #(q/smooth 4)
    :setup #'setup
    :update #'update-state
    :draw #'draw-state
    :features [:keep-on-top]
    :size [800 800]
    :middleware [m/fun-mode m/pause-on-error]))

(def sketch (create-sketch))
