(defproject keju "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.5.0"]]
  :plugins [[lein-cljsbuild "0.3.0"]
            [lein-ring "0.8.2"]]
  :source-paths ["code/server"]
  :ring {:handler keju.server/app}
  :cljsbuild {:builds [{:source-paths ["code/client"]
                        :compiler {:output-to "public/client.js"
                                   :output-dir "public/build"
                                   :optimizations :none
                                   :pretty-print true}}]})
