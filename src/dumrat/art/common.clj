(ns dumrat.art.common
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn make-component [id state update-fn draw-fn]
  {:id id
   :state state
   :update-fn update-fn
   :draw-fn draw-fn})

(defprotocol QComponent
  (update-component [this time])
  (draw-component [this time]))

(defrecord BezierCurve [curve-params rotation]
  QComponent
  (update-component [this time]
    (assoc this :rotation [(* 2 q/PI time)]))
  (draw-component [{rotation :rotation :as this} time]
    (q/with-translation [0 0])
      (q/with-rotation rotation)
        (q/fill 255 0)
        (q/stroke 200 100 20)
        (apply q/bezier curve-params)))

(defrecord Grouping [components]
  QComponent
  (update-component [this time]
    (assoc this :components (mapv #(update-component % time) components)))
  (draw-component [this time]
    (doseq [c components]
      (draw-component c time))))

(comment
    (update-component (->BezierCurve [10 10 45 200 65 120 80 11] 0) 0)
    (update-component (->Grouping [(->BezierCurve [10 10 45 200 65 120 80 11] 0)]) 0.1)
    #_f)

(defn setup []
  {:components
   [(->Grouping
      [(->BezierCurve [10 10 20 20 60 -20 0 50] 0)
       (->BezierCurve [10 10 45 200 65 120 80 11] 0)
       (->BezierCurve [200 200 65 89 -50 -43 11 300] 0)
       (->BezierCurve [45 90 76 -89 -5 -123 45 -11] 0)
       (->BezierCurve [-200 -200 -250 -150 -180 -240 -100 -100] 0)])]
   :time 0
   :speed 0.001})

(defn update-state [{time :time speed :speed :as state}]
  (-> state
      (update :time (fn [t] (if (> t 1) 0 (+ t speed))))
      (update :components #(mapv (fn [c] (#'update-component c time)) %))))

(defn draw-state [{time :time components :components}]
  (q/background 255)
  (q/stroke 0)
  (q/fill 0)
  (q/text-num (q/current-frame-rate) 10 10)
  (q/translate [300 300])
  (doseq [c components]
    (#'draw-component c time)))

(defn create-sketch []
  (q/sketch
    :title "Common"
    :setup #'setup
    :update #'update-state
    :draw #'draw-state
    :features [:keep-on-top]
    :size [600 600]
    :renderer :p2d
    :middleware [m/fun-mode m/pause-on-error]))

(def sketch (create-sketch))
