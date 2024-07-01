(ns invaders.core
  (:gen-class)
  (:require [clojure.string :as str]
            [clojure.tools.cli :as cli]
            [invaders.sample :as sample]
            [failjure.core :as f]
            [invaders.invaders :as invaders]
            [invaders.scanner :as scanner]
            [invaders.printer :as printer]))

(def cli-options
  [["-t" "--tolerance TOLERANCE" "Detecting tolerance (the higher the more fuzz is tolerated)."
    :id :tolerance
    :default 20
    :parse-fn #(Integer/parseInt %)]
   ["-a" "--algorithm ALGO" "Algorith used to detect invaders (levenshtein or hamming)"
    :id :algo
    :default "hamming"]
   ["-f" "--filename FILENAME" "A path to sample file"
    :id :filename]
   ["-h" "--help"]])

(defn usage
  "Displays a help with description of all recognizable arguments."
  [summary]
  (println "Arguments:\n")
  (println summary))

(defn- denoise
  "Replaces any character in `input` which is not a void ('-')
  with a recognizable marker ('o')."
  [input]
  (str/replace input #"[^\-]" "o"))

(defn -main [& args]
  (let [parsed (cli/parse-opts args cli-options)
        {:keys [algo tolerance filename help]} (:options parsed)]
    (if (or help (str/blank? filename))
      (usage (:summary parsed))
      (f/attempt-all [sample (sample/load-sample filename denoise)
                      invaders (invaders/load-invaders)
                      detected (scanner/scan sample
                                             invaders
                                             {:algo (keyword algo)
                                              :tolerance tolerance})]

        (printer/pretty-print sample detected invaders)
        (f/when-failed [e]
          (println (f/message e)))))))



