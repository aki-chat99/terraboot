(defproject kixi/terraboot "0.4.39-SNAPSHOT"
  :description "Terraform modules for kixi framework"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [cheshire "5.5.0"]
                 [stencil "0.5.0"]
                 [clj-yaml "0.4.0"]
                 [org.clojure/core.incubator "0.1.4"]]
  :repositories [["releases" {:url "https://clojars.org/repo"
                              :creds :gpg}]
                 ["snapshots" {:url "https://clojars.org/repo"
                               :creds :gpg}]])
