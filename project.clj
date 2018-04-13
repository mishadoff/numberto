(defproject numberto "0.0.4"
  :description "numberto - all you need to play with numbers"
  :url "https://github.com/mishadoff/numberto"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]]
  :profiles {:dev {:plugins [[jonase/eastwood "0.2.1"]
                             [lein-kibit "0.1.2"]
                             [lein-bikeshed "0.2.0"]
                             [lein-cloverage "1.0.6"]]}})
