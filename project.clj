(defproject ibclj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [com.quantechlab/ibclient "9.71.01"]
                 [org.clojure/core.async "0.2.374"]
                 [capacitor "0.4.3"]]
  :java-source-paths ["src/java" "test/java"]
  :source-paths ["src/clj"]
  :main ibclj.core
  )
