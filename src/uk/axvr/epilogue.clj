(ns uk.axvr.epilogue
  "Simple wrapper around `clojure.tools.logging` providing structural logging
  support."
  (:require [clojure.tools.logging :as log]))

(def ^:dynamic *context* {})

(def levels [:trace :debug :info :warn :error :fatal :spy])

(comment
  (log/info "Msg" {} err)
  (log/info "Msg")
  (log/info {})
  (log/info "Msg" {})
  (log/infof "Msg %s")

  (log/with-context {}
    ,,,)

  (log/set-global-context!
    {:environment config/env
     :version     config/version})
  )
