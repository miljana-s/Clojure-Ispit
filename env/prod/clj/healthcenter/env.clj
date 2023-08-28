(ns healthcenter.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[healthcenter started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[healthcenter has shut down successfully]=-"))
   :middleware identity})
