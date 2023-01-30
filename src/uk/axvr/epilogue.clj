(ns uk.axvr.epilogue
  "Simple wrapper around `clojure.tools.logging` providing structural logging
  support."
  (:require [clojure.tools.logging :as log]))

(def ^:dynamic *context*
  "Logging context.  This dynamic var stores the structured data collected to
  be logged."
  {})

(defmacro with-context [context & body]
  `(binding [*context* (merge *context* ~context)]
     ~@body))

(comment
  (with-context {:foo :bar}
    ;; add control ns-qualified keywords to context.  (e.g. ::ctx->mdc? true)
    (log/info "Foo"))
  )

(defmacro log
  ""
  [& {:keys [level message context logger-factory logger-ns throwable]
      :or   {level :info}}]
  `(with-context (assoc ~context
                        :source (assoc ~(meta &form)
                                       :file *file*
                                       :namespace *ns*))
     ;; TODO: replace list with log/log
     (list ~logger-factory (or ~logger-ns *ns*) ~level ~throwable (or ~message ~context))))

(comment
  (epi/log
    :level :info
    :message "Hello world!"
    :context {:foo "bar"})

  (epi/log :level :info, :message "Hello world!")
  (epi/log :level :info, :msg "Hello world!")

  ;; More traditional style of logging.
  (epi/info "Hello world!" {:foo "bar"})

  ;; Probably not possible, but would be nice.
  (epi/log {:foo "bar"} :level :fatal) ; context in first slot
  (epi/log "foo bar")                  ; message in first slot
  (epi/log ::something-happened)       ; message in first slot
  (epi/log :message "foo bar")         ; keyword in first slot
  )

(def levels [:trace :debug :info :warn :error :fatal :spy])

(comment
  (log/set-global-context!
    {:environment config/env
     :version     config/version})
  )
