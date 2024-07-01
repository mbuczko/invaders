(ns invaders.sample-test
  (:require [clojure.test :refer [deftest is testing]]
            [invaders.sample :as s]))

(deftest rolling-string-replace
  (testing "replacement partially rolled over to the beginning"
    (is (= "CDD-o--AABBC"
           (s/rolling-replace "--o-o--ooo-o" 7 "AABBCCDD"))))

  (testing "replacement applied in the middle without rolling over"
    (is (= "--o-AABBoo-o"
           (s/rolling-replace "--o-o--ooo-o" 4 "AABB"))))

  (testing "replacement applied on the end-edge without rolling over"
    (is (= "--o-o--oAABB"
           (s/rolling-replace "--o-o--ooo-o" 8 "AABB"))))

  (testing "replacement applied on the start-edge without rolling over"
    (is (= "AABBo--ooo-o"
           (s/rolling-replace "--o-o--ooo-o" 0 "AABB"))))

  (testing "empty replacement"
    (is (= "--o-o--ooo-o"
           (s/rolling-replace "--o-o--ooo-o" 0 ""))))

  (testing "doesn't change input when offset goes out of the bound"
    (is (= "--o-o--ooo-o"
           (s/rolling-replace "--o-o--ooo-o" 12 "AAA"))))

  (testing "fails on negative offset"
    (is (thrown-with-msg? AssertionError #"Assert failed"
                          (s/rolling-replace "--o-o--ooo-o" -4 "AAA")))))

(deftest sample-update
  (let [region ["AAA"
                "BBB"
                "CCC"]
        sample (s/lines->sample
                ["ooo-o-o-ooo"
                 "-o--o-o--o-"
                 "---ooooo---"])]

    (testing "non-rolling update"
      (is (= ["oAAAo-o-ooo"
              "-BBBo-o--o-"
              "-CCCoooo---"]
             (:lines (s/rolling-update-sample
                      sample
                      1 0
                      region)))))

    (testing "rolling right-left update"
      (is (= ["Aoo-o-o-oAA"
              "Bo--o-o--BB"
              "C--ooooo-CC"]
             (:lines (s/rolling-update-sample
                      sample
                      9 0
                      region)))))

    (testing "rolling bottom-top update"
      (is (= ["ooo-o-oCCCo"
              "-o--o-oAAA-"
              "---ooooBBB-"]
             (:lines (s/rolling-update-sample
                      sample
                      7 1
                      region)))))

    (testing "rolling all-the-sides update"
      (is (= ["Coo-o-o-oCC"
              "Ao--o-o--AA"
              "B--ooooo-BB"]
             (:lines (s/rolling-update-sample
                      sample
                      9 1
                      region)))))

    (testing "no update when coords are out of band"
      (is (= (:lines sample)
             (:lines (s/rolling-update-sample
                      sample
                      11 0
                      region)))))))
