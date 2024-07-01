(ns invaders.detectors
  (:require [clj-fuzzy.metrics :as fuzzy]
            [failjure.core :as f]))

(defmulti detect (fn [metric _ _] (:algo metric)))

(defmethod detect :levenshtein [metric regstr invader]
  (f/attempt-all [tolerance (:tolerance metric 0)
                  result (fuzzy/levenshtein regstr invader)]
    (<= result tolerance)
    (f/fail "Levenshtein detector failed")))

(defmethod detect :hamming [metric regstr invader]
  (f/attempt-all [tolerance (:tolerance metric 0)
                  result (fuzzy/hamming regstr invader)]
    (<= result tolerance)
    (f/fail "Hamming detector failed")))
