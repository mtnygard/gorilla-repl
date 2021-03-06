(ns gorilla-repl.version
  "Checks whether a newer version of Gorilla REPL is availble."
  (:require [org.httpkit.client :as http]
            [clj-semver.core :as semver]))

(def update-url "https://gorilla.benbailey.me/latest-version?v=")

(defn version-check
  "Compares the latest version to the running version and prints a message to the console if there's an update
  available."
  [latest current]
  (if (not= current "develop")
    (if (semver/newer? latest current)
      (println "A newer version of Gorilla REPL, version" latest ", is available."))))

(defn check-for-update
  "This runs asynchronously, and can be fired off and forgotten."
  [current]
  (http/get
   (str update-url current)
   {:timeout 10000 :as :text}
   (fn [{:keys [_status _headers body error]}]
     (if error
       (println "Unable to reach update server.")
       (let [body-map (apply hash-map (read-string body))]
         (version-check (:version body-map) current))))))
