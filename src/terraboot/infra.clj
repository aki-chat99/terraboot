(ns terraboot.infra
  (:require [terraboot.core :refer :all]
            [terraboot.vpc :refer [vpc-vpn-infra vpc-name]]
            [terraboot.cluster :refer [cluster-infra]]
            [clojure.edn :as edn]))

(def infra-path "infra/")

(defn get-config
  "Gets info from a config file."
  [url]
  (edn/read-string (slurp url)))

(defn generate-json [target]
  (let [account-number "165664414043"
        azs [:a]
        mesos-ami "ami-cfca25a0"
        default-ami "ami-9b9c86f7"
        vpc-cidr-block "172.20.0.0/20"
        ]
    (condp = target
      "vpc"     (to-file (vpc-vpn-infra {:vpc-name vpc-name
                                         :vpc-cidr-block vpc-cidr-block
                                         :account-number account-number
                                         :azs [:a :b]
                                         :default-ami default-ami
                                         :subnet-cidr-blocks {:a {:public "172.20.0.0/24"
                                                                  :private "172.20.8.0/24"}
                                                              :b {:public "172.20.1.0/24"
                                                                  :private "172.20.9.0/24"}}}) "vpc/vpc.tf")
      "staging" (to-file (cluster-infra {:vpc-name vpc-name
                                         :vpc-cidr-block vpc-cidr-block
                                         :account-number account-number
                                         :cluster-name "staging"
                                         :min-number-of-masters 3
                                         :max-number-of-masters 3
                                         :master-disk-allocation 20
                                         :master-instance-type "m4.large"
                                         :min-number-of-slaves 2
                                         :max-number-of-slaves 2
                                         :slave-instance-type "m4.xlarge"
                                         :min-number-of-public-slaves 1
                                         :max-number-of-public-slaves 1
                                         :public-slave-instance-type "t2.medium"
                                         :public-slave-elb-listeners [{:port 80 :protocol "http"}
                                                                      {:port 9501 :protocol "http"}
                                                                      {:port 5001 :protocol "tcp"}]
                                         :public-slave-elb-sg [{:port 9501 :cidr_blocks [all-external]}
                                                               {:port 80 :cidr_blocks [all-external]}
                                                               {:port 5001 :cidr_blocks [vpc-cidr-block]}]
                                         :public-slave-elb-health "HTTP:9501/"
                                         :azs azs
                                         :mesos-ami "ami-1807e377" ;; previous coreos
                                         :subnet-cidr-blocks {:a {:public "172.20.2.0/24"
                                                                  :private "172.20.10.0/24"}}}) "staging/staging.tf")
      (comment "production" (to-file (cluster-infra {:vpc-name vpc-name
                                                     :cluster-name "production"
                                                     :min-number-of-masters 3
                                                     :max-number-of-masters 3
                                                     :master-instance-type "m4.xlarge"
                                                     :master-disk-allocation 20
                                                     :min-number-of-slaves 3
                                                     :max-number-of-slaves 3
                                                     :slave-instance-type "m4.xlarge"
                                                     :slave-disk-allocation 20
                                                     :min-number-of-public-slaves 1
                                                     :max-number-of-public-slaves 1
                                                     :public-slave-instance-type "m4.xlarge"
                                                     :public-slave-disk-allocation 20
                                                     :public-slave-elb-listeners [{:lb_port 443 :lb_protocol "https" :port 80 :protocol "http" :cert-name "TBD"}
                                                                                  {:port 9501}]
                                                     :mesos-ami mesos-ami
                                                     :azs azs
                                                     :subnet-cidr-blocks {:a {:public "172.20.3.0/22"
                                                                              :private "172.20.11.0/22"}}}) "production/production.tf")))))


(defn -main [target]
  (generate-json target))
