(ns dumrat.art.meander
  (:require [meander.epsilon :as m]
            [meander.strategy.epsilon :as r]))

(defn favorite-food-info [foods-by-name user]
  (m/match {:user user
            :foods-by-name foods-by-name}
    {:user
     {:name ?name
      :favorite-food {:name ?food}}
     :foods-by-name {?food {:popularity ?popularity
                            :calories ?calories}}}
    {:name ?name
     :favorite {:food ?food
                :popularity ?popularity
                :calories ?calories}}))

(def foods-by-name
  {:nachos {:popularity :high
            :calories :lots}
   :smoothie {:popularity :high
              :calories :less}})

(favorite-food-info foods-by-name
  {:name :alice
   :favorite-food {:name :nachos}})

(m/find [1 2]
  [(m/pred number? ?x) (m/pred number? ?y)] [?y ?x])

(defn proxy-entry? [form]
  (and (map-entry? form)))

(clojure.walk/prewalk
 (fn [x]
   (if (proxy-map? x)
     x
     x))
 data)

(clojure.walk/prewalk-demo {:id 10 :name "car"})

(defn walk-if-not-proxy [form]
  (println "Walking form = " form)
  (if (proxy-kvp? form)
    ;; handle how you want
    {:ser form}
    (clojure.walk/walk
      walk-if-not-proxy
      identity
      form)))

(def data {:id 10
           :name "car"
           :models
           [[{:proxy "some-uuid-string"}]
            1
            "s"]})

(defn proxy-kvp? [form]
  (and (map-entry? form)
       (= :proxy (key form))))

(clojure.walk/prewalk
  (fn [form]
    (if (proxy-kvp? form)
      {:ser form}
      form))
  data)

(walk-if-not-proxy data)

(defn my-walk [inner outer form]
  (cond (seq? form) (outer (doall (map inner form)))
        (coll? form) (outer (into (empty form) (map inner form)))
        :else (outer form)))

(my-walk #(* 2 %) conj [1 2 3 4 5 6])

(clojure.walk/walk #(* 2 %) identity [[1 2] 3])
