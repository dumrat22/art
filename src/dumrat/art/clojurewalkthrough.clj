(ns dumrat.art.clojurewalkthrough)

;; Immutable by default
;; Data oriented

;;Primitives
1
1.2
"string"
true
:x ;keyword

;; Collection types
'(1 2 3) ;list
[1 "good" 3 3] ;vector
#{1 2 3} ;set
{:angle 0.56 :name "bla"} ;dictionary

;; Definitions
(def x 10)

;;Evaluation
(+ 1 2 3 4 5 6)
(conj [1 2 3] 7)

;; Function definition
((fn [a]
  (* 2 a))
 5) ;; Lambda or anonymous function

(def twice (fn [a] (* 2 a)))
(twice 5)

(defn twice [a] (* 2 a)) ;; Main function definition macro
(twice 5)

;; Local binding
(let [a 1 b 2]
  (+ a b))

;; Map operations
(def state {:angle 0.01 :time 0.1
            :green-point {:x 10 :y 20 :color :green}})

; Getting values
state
(:green-point state)
(:x (state :green-point))

(get state :angle)
(get-in state [:green-point :x]) ;; Nested path

; Updating values
(assoc state :angle 0.02)
(assoc-in state [:green-point :color] :red)

(+ 1 2 3)
((partial + 5) 7)

(update state :time (fn [time] (+ time 0.002)))
(update-in state [:green-point :x] (partial + 5))
