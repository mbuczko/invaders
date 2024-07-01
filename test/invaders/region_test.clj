(ns invaders.region-test
  (:require [clojure.test :refer [deftest is testing]]
            [invaders.region :as r]
            [invaders.sample :as s]))

(deftest extracts-region
  (let [sample (s/lines->sample ["aaa" "bbb" "ccc"])]
    (is (= "aabb"
           (r/region->str sample (r/->Region 0 0 2 2))))))

(deftest extracts-wrapped-x-region
  (let [sample (s/lines->sample ["abc" "cde" "efg"])]
    (is (= "caec"
           (r/region->str sample (r/->Region 2 0 2 2))))))

(deftest extracts-wrapped-y-region
  (let [sample (s/lines->sample ["abc" "cde" "efg"])]
    (is (= "fgbc"
           (r/region->str sample (r/->Region 1 2 2 2))))))

(deftest region-doesnt-include-other-region
  (let [;; create a 6x4 sample
        sample (s/lines->sample (take 4 (repeat "------")))
        ;; non-wrapping region
        region-A (r/->Region 2 1 3 3)
        ;; region wrapping around the plane
        region-B (r/->Region 4 2 3 3)]

    (testing "non-wrapping region does not include non-wrapping region"
      (is (not (r/includes-region? sample region-A (r/->Region 0 0 2 4))))
      (is (not (r/includes-region? sample region-A (r/->Region 2 0 4 1)))))

    (testing "wrapping region does not include non-wrapping region"
      (is (not (r/includes-region? sample region-A (r/->Region 5 0 3 3)))))

    (testing "non-wrapping region does not include wrapping region"
      (is (not (r/includes-region? sample region-B (r/->Region 1 0 3 4))))
      (is (not (r/includes-region? sample region-B (r/->Region 1 2 3 2)))))

    (testing "wrapping region does not include wrapping region"
      (is (not (r/includes-region? sample region-B (r/->Region 4 1 4 1)))))))

(deftest region-includes-other-region
  (let [;; create a 6x4 sample
        sample (s/lines->sample (take 4 (repeat "------")))
        ;; non-wrapping region
        region-A (r/->Region 2 1 3 3)
        ;; region wrapping around the plane
        region-B (r/->Region 4 2 3 3)]

    (testing "non-wrapping region includes non-wrapping region"
      (is (r/includes-region? sample region-A (r/->Region 0 0 3 4)))
      (is (r/includes-region? sample region-A (r/->Region 2 0 3 2))))

    (testing "wrapping region includes non-wrapping region"
      (is (r/includes-region? sample region-A (r/->Region 5 0 4 3))))

    (testing "non-wrapping region includes wrapping region"
      (is (r/includes-region? sample region-B (r/->Region 0 0 3 4)))
      (is (r/includes-region? sample region-B (r/->Region 1 2 4 2))))

    (testing "wrapping region includes wrapping region"
      (is (r/includes-region? sample region-B (r/->Region 3 0 4 1))))))

