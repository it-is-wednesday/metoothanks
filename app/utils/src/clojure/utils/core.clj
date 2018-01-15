(ns utils.core
    (:import (interfaces WidthCalcFuncComposer))
    (:gen-class
        :methods [^{:static true} [fitToWidth [int String int interfaces.WidthCalcFuncComposer] String]]))

(defn -main
    "I don't do a whole lot ... yet."
    [& args]
    (println "Hello, World!"))

(defn -fitToWidth [width text text-size width-calc-func]
    1)
