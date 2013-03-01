(ns keju.server
  (:use [ring.middleware file file-info]))

(defn default-handler [req]
  (println "FOO")
  {:status 404
   :headers {"Content-Type" "text/html"}
   :body "<!DOCTYPE html>
          <html>
            <body>
              Not found.
            </body>
          </html>"})

(def app
  (-> default-handler
      (wrap-file "public")
      (wrap-file-info)
      ))
