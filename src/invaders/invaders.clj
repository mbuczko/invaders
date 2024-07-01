(ns invaders.invaders
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(defrecord Invader [line width height])

(s/def ::width pos-int?)
(s/def ::height pos-int?)
(s/def ::line (complement str/blank?))
(s/def ::invader (s/keys :req-un [::line ::width ::height]))
(s/def ::invaders (s/map-of int? ::invader))

(defn lines->invader
  [lines]
  (map->Invader {:line (str/join "" lines)
                 :width (-> lines first count)
                 :height (count lines)}))

(defn load-invaders
  "Loads invaders definition and returns an indexed map
  of {index invader-definition}."
  []
  (let [invaders (slurp (io/resource "invaders.txt"))]
    (some->> (str/split invaders #"\n\n")
             (map (comp lines->invader str/split-lines))
             (into {} (map-indexed #(vector %1 %2))))))
