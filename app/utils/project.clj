(defproject utils "1.1"
    :description "Utility functions for metoothanks"
    :url "https://github.com/its-wednesday/metoothanks"
    :license {:name "Eclipse Public License"
              :url  "http://www.eclipse.org/legal/epl-v10.html"}
    :dependencies [[org.clojure/clojure "1.8.0"]]
    :main utils.core
    :aot [utils.core]
    :target-path "target/%s"
    :source-paths ["src/clojure"]
    :java-source-paths ["src/java"]
    :profiles {:uberjar {:aot :all}})
