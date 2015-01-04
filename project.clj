(defproject caterbot "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2657"]]

  :node-dependencies [[source-map-support "0.2.8"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [lein-npm "0.4.0"]]

  :source-paths ["src"]

  :cljsbuild {
              :builds [{:id "hungerbot"
                        :source-paths ["src"]
                        :compiler {
                                   :output-to "out/hungerbot.js"
                                   :output-dir "out"
                                   :target :nodejs
                                   :optimizations :none
                                   :source-map true}}
                       {:id "hungerbot-test"
                        :source-paths ["src" "test"]
                        :notify-command ["node" "./run_tests.js"]
                        :compiler {:output-to "test_out/test.js"
                                   :output-dir "test_out"
                                   :target :nodejs
                                   :optimizations :none
                                   :source-map true}}]
              :test-commands {"test" ["node" "./run_tests.js"]}})

