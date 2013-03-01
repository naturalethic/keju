(defproject keju "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.0"]]
  :plugins [[lein-ring "0.8.2"]]
  :source-paths ["code/server"]
  :ring {:handler keju.server/app})
