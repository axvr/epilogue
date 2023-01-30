(ns uk.axvr.epilogue.logback.json.JsonLayout
  "Version of Logback JsonLayout hooked into Clojure that can add custom fields
  to the top-level context."
  (:require [uk.axvr.epilogue :as log])
  (:import [ch.qos.logback.classic.spi ILoggingEvent])
  (:gen-class
    ;; https://github.com/qos-ch/logback-contrib/blob/master/json/classic/src/main/java/ch/qos/logback/contrib/json/classic/JsonLayout.java
    :extends ch.qos.logback.contrib.json.classic.JsonLayout))

(defn -addCustomDataToJsonMap [this map event]
  ;; TODO: convert types and keys...  (Dates, etc.)
  (prn :??? (contains? map "timestamp2"))
  (when-let [ctx log/*context*]
    (.putAll map ctx)))
