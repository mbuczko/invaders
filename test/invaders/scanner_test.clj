(ns invaders.scanner-test
  (:require [clojure.test :refer [deftest is]]
            [invaders.invaders :as i]
            [invaders.sample :as s]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [invaders.scanner :as scanner]))

(defn- load-sample
  [res]
  (some->> (slurp (io/resource res))
           str/split-lines
           s/lines->sample))

(deftest discovers-non-wrapped-invader
  (let [invaders (i/load-invaders)
        sample (load-sample "samples/test-1.sample")]
    (is (= [{:x 3, :y 0, :w 11, :h 8, :index 0}]
           (scanner/scan sample invaders {:algo :hamming
                                          :tolerance 0})))))

(deftest discovers-wrapped-invader
  (let [invaders (i/load-invaders)
        sample (load-sample "samples/test-2.sample")]
    (is (= [{:x 8, :y 0, :w 11, :h 8, :index 0}]
           (scanner/scan sample invaders {:algo :hamming
                                          :tolerance 0})))))

(deftest discovers-noised-invader
  (let [invaders (i/load-invaders)
        sample (load-sample "samples/test-3.sample")]
    (is (= [{:x 8, :y 0, :w 11, :h 8, :index 0}]
           (scanner/scan sample invaders {:algo :levenshtein
                                          :tolerance 5})))))

(deftest doesnt-discover-too-noised-invader
  (let [invaders (i/load-invaders)
        sample (load-sample "samples/test-4.sample")]
    (is (empty? (scanner/scan sample invaders {:algo :levenshtein
                                               :tolerance 5})))))

