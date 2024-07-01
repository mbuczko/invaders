(ns invaders.scanner
  (:require [clojure.spec.alpha :as s]
            [invaders.sample :as sample]
            [invaders.invaders :as invaders]
            [invaders.region :refer [->Region] :as region]
            [invaders.detectors :as detectors]
            [failjure.core :as f]))

(defn- validated-input
  "Helper function returning an `input` if it's valid according
  to given `spec`. Returns nil otherwise."
  [spec input]
  (when (s/valid? spec input) input))

(defn offset->invader
  "Returns a map consisting of invader's position and size (along with
  index in `indexed-invaders` definition map) if any of invaders has
  been detected by `invaders.detectors/detect` multi-fn based on provided
  metric. Returns nil otherwise."
  [sample invaders-indexed metric x y]
  (->> invaders-indexed
       (some (fn [[idx {:keys [width height line]}]]
               (let [region (->Region x y width height)
                     regstr (region/region->str sample region)]
                 (when (detectors/detect metric regstr line)
                   {:x x
                    :y y
                    :w width
                    :h height
                    :index idx}))))))

(defn scan
  "Scans a `sample` looking for invaders defined by `invaders-indexed`.
  Shapes are matched using detector function described by `metric`."
  [{:keys [width height] :as sample} invaders-indexed metric]
  (f/attempt-all
   [sample (or (validated-input ::sample/sample sample)
               (f/fail "Invalid sample data"))
    invaders-indexed (or (validated-input ::invaders/invaders invaders-indexed)
                         (f/fail "Invalid invaders definitions"))
    xy->invader (partial offset->invader sample invaders-indexed metric)]

    (loop [x 0
           y 0
           invaders []]

      ;; bail out when y goes out of band
      (if (>= y height)
        invaders

        ;; otherwise try to match an invader inside the region at (x,y).
        ;; region width and height vary depending on invader being matched.
        ;; in case any invader has been found, x coord gets increased
        ;; by invader's width (modulo plane width to wrap) to speed up
        ;; detection process.
        ;; that's ok as long as the "invaders do not overlap" assumption
        ;; stays true...
        (let [invader (xy->invader x y)
              forward (if invader (:w invader) 1)
              next-x (mod (+ x forward) width)
              next-y (if (< next-x x) (inc y) y)]

          (recur next-x
                 next-y
                 (cond-> invaders
                   invader
                   (conj invader))))))))
