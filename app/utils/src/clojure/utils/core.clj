(ns utils.core
    (:import (interfaces WidthCalcFuncComposer))
    (:gen-class
        :methods [^{:static true} [fitToWidth [int String int interfaces.WidthCalcFuncComposer] String]]))

(defn last-line [text]
    (-> text
        (#(clojure.string/split % #"\n"))
        last))

(defn longer [text1 text2]
    (> (clojure.string/siz)))

(defn fit-to-width [width text text-size width-calc-func words]
    (if (empty? words)
        (.trim text)
        (let [new-line-width  (str (last-line text) " " (first words))
              new-width-exceeds (> (width-calc-func ) width)
              new-text (str text " " (first words))]
            (recur width new-text text-size width-calc-func (rest words)))))

(defn -fitToWidth [width text text-size width-calc-func]
    1)
