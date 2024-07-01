(ns invaders.printer
  (:require [clojure.string :as str]
            [invaders.sample :as sample]))

(defn- reveal
  "Returns modified `sample` data with `invaders-detected` replaced
  with their canonical definition from `invaders-indexed`."
  [sample invaders-detected invaders-indexed]
  (reduce (fn [sample {:keys [x y w index]}]
            (let [invader (-> (get invaders-indexed index)
                              :line
                              (str/replace #"[^\-]" "#"))
                  region  (mapv (partial apply str)
                                (partition-all w invader))]
              (sample/rolling-update-sample sample x y region)))
          sample
          invaders-detected))

(defn pretty-print
  "Pretty-prints entire sample with revealed invaders."
  [sample invaders-detected invaders-indexed]
  (when-let [revealed (reveal sample invaders-detected invaders-indexed)]
    (doseq [line (:lines revealed)]
      (println line))))
