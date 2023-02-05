(ns uk.axvr.epilogue
  "Simple wrapper around `clojure.tools.logging` providing structural logging
  support."
  (:require [clojure.tools.logging :as log]))

(def ^:dynamic *context*
  "Logging context.  This dynamic var stores the structured data collected to
  be logged wrapped in an atom.

  Why an atom AND a dynamic var?  The dynamic var allows this value to
  differentiate between threads and dynamic scope, while the atom provides safe
  alteration and the ability to set global context values.

      ;; Example: merge environment and version values into the global context
      ;; from program config.
      (swap! uk.axvr.epilogue/*context* merge
        (select-keys config [:environment :version]))"
  (atom {}))

(defmacro with-context
  "Merge extra data into the logging context."
  [context & body]
  `(binding [*context* (atom (merge @*context* ~context))]
     ~@body))

;; TODO: write doc-string.
(defmacro log
  ""
  [message context & {:keys [level throwable logger-factory logger-ns]
                      :or   {level :info}}]
  `(with-context (assoc ~context
                        :source (assoc ~(meta &form)
                                       :file *file*
                                       :namespace *ns*))
     (log/log ~logger-factory
              (or ~logger-ns *ns*)
              ~level
              ~throwable
              ~message)))

;; TODO: write generated doc-string.
;; TODO: log with no context.
(defmacro ^:private deflevel [level]
  `(defmacro ~(symbol level)
     ""
     {:arglists '~'([message context & {:as opts}])}
     [message# context# & {:as opts#}]
     `(log ~message# ~context# (assoc opts# :level ~~(keyword level)))))

(declare fatal error warn info debug trace)
(deflevel :fatal)
(deflevel :error)
(deflevel :warn)
(deflevel :info)
(deflevel :debug)
(deflevel :trace)

;; TODO: add spy macro.

(comment
  (epi/log "message")
  (epi/log "message" {:foo 1})
  (epi/log "message" {:foo 1} :level :info)

  (epi/error "message" {:foo 1})
  (epi/error "message" {:foo 1} :throwable e)  ; Not ideal?

  '([message & {:as opts}]
    [message context & {:as opts}])
  )
