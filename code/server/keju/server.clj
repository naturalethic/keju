(ns keju.server)

(defn app [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Howdy"})
