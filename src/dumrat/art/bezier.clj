(ns dumrat.art.bezier
  (:require [quil.core :as q]
            [quil.middleware :as m]))

;;Just exploring how bezier curves are implemented
;;https://en.wikipedia.org/wiki/B%C3%A9zier_curve#Constructing_B%C3%A9zier_curves
;;
(def state-override
  (atom
    {:points [[10 10] [200 40] [500 200] [300 20] [100 400]]
     :speed 0.001
     :translate [600 600]
     ;:rotate (q/radians 0)
     :restart? false}))

(comment

  (do
    (swap! state-override assoc :speed 0.001)
    (swap! state-override assoc :points [[-150 0] [-250 200] [-200 300] [-100 350] [100 350] [200 300] [250 200] [150 0]])
    ;(swap! state-override assoc :rotate (q/radians 180))
    (swap! state-override assoc :restart? true))

  #_f)

(defn setup []
  (q/smooth 4)
  (q/frame-rate 240)
  (q/background 255 0)
  (q/fill 255 0)
  (merge
   {:t 1.1
    :rotate 0}
   @state-override))

(defn point-between-at-t [[p0x p0y] [p1x p1y] t]
  [(+ p0x (* (- p1x p0x) t))
   (+ p0y (* (- p1y p0y) t))])

(defn get-adjacent-pairs [c]
  (:result
   (reduce
    (fn [{result :result last :last} val]
      {:result (if last (conj result [last val]) result)
       :last val})
    {:result [] :last nil}
    c)))

(comment
  (get-adjacent-pairs [[1 2] [2 3] [3 4]])
 #_f)

(defn bezier-point [points t]
  (if (and
        (> (count points) 1)
        (vector? (first points)))
    (bezier-point (mapv (fn [[p1 p2]] (point-between-at-t p1 p2 t)) (get-adjacent-pairs points)) t)
    (first points)))

(comment

  (bezier-point [[1 2] [2 3] [3 4]] 0.1)

  (range 0.0 1.0 0.01)

  #_f)

(defn update-state [{points :points t :t speed :speed
                     rotate :rotate
                     restart? :restart?}]
    (merge
     {:bs (map (fn [t] (bezier-point points t)) (range 0.0 1 speed))
      :t (if (or (> t 1) restart?)
           (do
             (swap! state-override assoc :restart? false)
             0)
           (+ t speed))
      :rotate (+ rotate speed)}
     @state-override))

(defn draw-state [{bs :bs t :t points :points
                   translate :translate rotate :rotate}]
  (q/background 255)
  (q/stroke 0)
  (q/fill 0)
  (q/text-num (q/current-frame-rate) 10 10)
  (q/text-num t 10 20)
  (apply q/translate translate)
  (q/rotate rotate)
  #_(doseq [[p1 p2] (get-adjacent-pairs points)]
     (q/line p1 p2))
  (q/stroke 255 120 0)
  (q/fill 255 0)
  (q/stroke-weight 2)
  (dotimes [i 12]
    (q/rotate (* i(/ q/PI 12)))
    (q/stroke (* i 20) (* i 10) 0)
    (doseq [b bs]
      (apply q/point b))))

(defn create-sketch []
  (q/sketch
    :title "Bezier curves"
    :setup #'setup
    :update #'update-state
    :draw #'draw-state
    :features [:keep-on-top]
    :size [1200 1200]
    :renderer :p2d
    :middleware [m/fun-mode m/pause-on-error]))

(def sketch (create-sketch))
