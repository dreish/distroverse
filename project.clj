(defproject distroverse "0.1.0-SNAPSHOT"
  :description "A distributed virtual reality system"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [penumbra "0.6.0-SNAPSHOT"]]
  :native-dependencies [[penumbra/lwjgl "2.4.2"]]
  :dev-dependencies [[native-deps "1.0.2dr1"]
                     [swank-clojure "1.2.0"]
                     [midje "0.4.0"]]
  :repositories {"clojure-releases"
                 "http://build.clojure.org/releases"}
  :main distroverse.core)
