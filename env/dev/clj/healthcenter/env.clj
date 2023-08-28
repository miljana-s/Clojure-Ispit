(ns healthcenter.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [healthcenter.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[healthcenter started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[healthcenter has shut down successfully]=-"))
   :middleware wrap-dev})
