(ns user
  (:require [portal.api :as portal]
            [clojure.datafy :as d]))

(portal/start {:portal.colors/theme :portal.colors/gruvbox
               :port 45001
               :host "localhost"})

(defn error->data [ex]
  (assoc (d/datafy ex) :runtime :clj))

(defn submit [value]
  (portal/submit
   (if-not (instance? Exception value)
     value
     (error->data value))))

(add-tap #'submit)
