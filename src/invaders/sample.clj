(ns invaders.sample
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [failjure.core :as f]
            [clojure.spec.alpha :as s]))

(defrecord Sample [lines width height])

(s/def ::width pos-int?)
(s/def ::height pos-int?)
(s/def ::lines (s/coll-of string?))
(s/def ::sample (s/keys :req-un [::lines ::width ::height]))

(defn- denoising-rf
  "Denoising reducing function composed of provided `denoise-fn` supposed to clean
  the noise (normalize sample data) and map/filter to get rid of empty lines which
  are assumed to be a noise too."
  [denoise-fn]
  (comp
   (filter #(not (str/blank? %)))
   (map denoise-fn)))

(defn lines->sample
  [lines]
  (map->Sample {:lines lines
                :width (-> lines first count)
                :height (count lines)}))

(defn rolling-replace
  "Replaces source string `s` with `replacement` starting from given `offset`.
  Replacing may roll over to the beginning of `'s` in case when wrapping happens."
  [s offset replacement]
  {:pre [(>= offset 0)]}
  (let [slen (count s)
        diff (- slen offset)]
    ;; bail out early if replacement is longer than
    ;; a source string or offset goes totally out of band
    (if (or (<= diff 0)
            (< slen (count replacement)))
      s
      (let [[lhs rhs] (split-at diff replacement)]
        (->> [rhs
              (subs s (count rhs) offset)
              lhs
              (subs s (+ (count lhs) offset))]
             (into [] cat)
             (apply str))))))

(defn rolling-update-sample
  "Updates a `sample` with `new-region` at position (`x`, `y`)."
  [sample x y new-region]
  (loop [sample sample
         sample-y y
         region-y 0]
    (if-let [region-line (get new-region region-y)]
      (recur (update-in sample [:lines sample-y] rolling-replace x region-line)
             (if (>= sample-y (dec (:height sample))) 0 (inc sample-y))
             (inc region-y))
      sample)))

(defn load-sample
  "Loads a `filename` with sample data applying `denoise-fn` normalization
  function on every single read line. Lines that happen to be empty are
  assumed to be a noise too and are rejected (ignored).

  In its simplest form `denoise-fn` may be just a `clojure.string/replace`
  fn which turns all unknown characters (a \"noise\") into \"markers\"
  recognizable by a scanner.

  Returns either a `Sample` with collection of normalized sample data or
  failure if file couldn't be read."
  [filename denoise-fn]
  (f/try*
   (with-open [r (io/reader filename)]
     (let [lines (into [] (denoising-rf denoise-fn) (line-seq r))]
       (lines->sample lines)))))
