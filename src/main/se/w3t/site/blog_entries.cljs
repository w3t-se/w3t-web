(ns se.w3t.site.blog-entries)

(defn blogs [] [
                 {:id 0 :type "DEVOPS" :date "18-11-21" :heading "Cool DevOps" :first-paragraph "none" :rest "```clojure
(defn plus []
  (+ 1 1))
```"
                  }
                 {:id 1
                  :type "CASE"
                  :date "01-11-21"
                  :heading "Developing a Release Automation flow for PrimeKey AB!"
                  :first-paragraph "PrimeKey has been developing Public Key Insfrasructure (PKI) products for almost two decades and was in a position to renew some of it's development practices with a focus on automation. Since we know how closely coupled the DevOps and underlying IT Infrastructure are, we proposed the move furter into the adoption of Kubernetes."
                  :rest "## Tools

## Cross Site Replication

## Merger  
There was an ongoing merger with the now parent Company Keyfactor."}
                 ])
