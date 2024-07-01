(ns invaders.region
  (:require [invaders.sample :as sample]
            [clojure.string :as str]))

(defrecord Region [x y w h])

(defn- take-wrapped
  "Just a `clojure.core.take` but starting at given `offset` and wrapping at the edges
  so, that missing elements at the end are taken from the the beginning of `data`."
  [n offset data]
  (take n (drop offset (cycle data))))

(defn region->str
  "Turns region into a string composed of ascii characters taken from `Sample`.

  Region's cartesian (x,y) coordinates are translated to match internal structure of
  loaded sample (sequence of lines) in a following way:
    - x becomes a 0-indexed position within a line
    - y becomes a 0-indexed number of line

  And so, (2,3) means the region starts from 3rd character in a 4th line, going `w`
  characters forward and `h` lines down. Region wraps at the edges of sample so, that
  missing columns and lines are taken respectively from \"left\" and \"top\" side of
  sample considered as 2-dimensional plane.

  Example:
  For following sample:

     (lines->sample [\"abc\" \"012\" \"def\"])

  and a region:

    (->Region 1 0 2 3)

  function returns concatenated region [\"bc\", \"12\", \"ef\"] which is \"bc12ef\".

  Returns nil if something went wrong (eg. coordinates are out of band)."
  [{:keys [lines width height] :as _sample} {:keys [x y w h] :as _region}]
  (when (and (< x width)
             (< y height))
    (->> (take-wrapped h y lines)
         (into [] (mapcat (partial take-wrapped w x)))
         (apply str))))

(defn- in-region?
  "Returns true if region includes coordinates (`cx`, `cy`) in 2-dimensional wrapping
  cartesian plane of given `width` and
  `height`."
  [{:keys [x y w h]} width height cx cy]
  (let [x2 (dec (+ x w))
        y2 (dec (+ y h))
        ;; bottom-right edge corner (modulo plane size)
        rx (mod x2 width)
        ry (mod y2 height)
        ;; x or y coordinate of bottom-right corner smaller
        ;; than corresponding top-left coordinate indicate
        ;; wrapping to the other side of plane.
        within-x-wrap (and (< rx x) (<= cx rx))
        within-y-wrap (and (< ry y) (<= cy ry))]

    (and (or (>= cx x)  within-x-wrap)
         (or (>= cy y)  within-y-wrap)
         (or (<= cx x2) within-x-wrap)
         (or (<= cy y2) within-y-wrap))))

(defn includes-region?
  "Returns true if `region` includes other region. Both `region` and other region may
  be wrapped around cartesian plane of given `width` and `height`."
  [{:keys [width height] :as _sample} region {:keys [x y w h]}]
  (let [inside? (partial in-region? region width height)
        ;; bottom-right edge corner (modulo plane size)
        x2 (mod (dec (+ x w)) width)
        y2 (mod (dec (+ y h)) height)]

    ;; check if any edge of other region stays inside the `region`
    (or
     (inside? x y)
     (inside? x y2)
     (inside? x2 y)
     (inside? x2 y2))))


(comment
  ;; extract region from pesky sample
  (-> (sample/load-sample "resources/samples/pesky.sample" #(str/replace % #"[^\-]" "O"))
      (region->str (->Region 3 4 8 8)))
  )
