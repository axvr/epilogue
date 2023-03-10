# Epilogue

Simple wrapper around [clojure.tools.logging][] providing structural logging
support.

___Work in progress.  Not yet complete.___


## Rationale

Logs are the epilogue of program execution.  They provide us valuable insights
into how our programs really behaved.  Unfortunately, the world of Java logging
is fraught with [complexity and competing solutions][Logging in Clojure].
Luckily, Clojure provides us with an excellent facade for these tools,
[clojure.tools.logging][].  However, it suffers from many of the same
limitations.  Logs are strings; no structured data.

While it would be great to use better, simpler logging solutions like
[μ/log][ulog].  Many situations still require full integration with the Java
logging mess.  Is there a half-way point?  Can we add more data to our logs in
a semi-structured way?

Epilogue is a simple library that wraps tools.logging, with support for logging
structured data.  As Epilogue cannot log this structured data on its own,
native backends can be easily created to add the additional data to the logging
backend of choice.  (Epilogue provides a JSON layout backend for [Logback][].)

Unlike other solutions in this space, Epilogue is much simpler and is intended
to be used directly alongside tools.logging.  (With Epilogue you can even add
additional context to logs produced by any Clojure lib using tools.logging.)

[Logging in Clojure]: https://lambdaisland.com/blog/2020-06-12-logging-in-clojure-making-sense-of-the-mess
[clojure.tools.logging]: https://github.com/clojure/tools.logging
[logback]: https://logback.qos.ch
[ulog]: https://github.com/BrunoBonacci/mulog


## Installation

```clojure
uk.axvr/epilogue {:mvn/version "0.1"}
uk.axvr/epilogue.logback.json {:mvn/version "0.1"}
```

```clojure
[uk.axvr/epilogue "0.1"]
[uk.axvr/epilogue.logback.json "0.1"]
```


## Usage

`resources/logback.xml`

```xml
<configuration scan="true" scanPeriod="5 seconds">
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
            <layout class="uk.axvr.epilogue.logback.json.JsonLayout">
                <jsonFormatter class="ch.qos.logback.contrib.jackson.JacksonJsonFormatter">
                    <prettyPrint>false</prettyPrint>
                </jsonFormatter>
                <timestampFormat>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</timestampFormat>
                <timestampFormatTimezoneId>UTC</timestampFormatTimezoneId>
                <appendLineSeparator>true</appendLineSeparator>
            </layout>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

```clojure
(require '[uk.axvr.epilogue :as epi])

;; (epi/log "Log something"
;;          {:foo "bar"}
;;          :level :warn)

(epi/info "Log something"
  {:hello [1 2 3]
   :world {:foo "bar"}})
```

```json
{
    "timestamp": "2023-01-29T22:24:35.163Z",
    "level": "INFO",
    "thread": "nREPL-session-c5b09cbe-2bdc-4186-afea-810cf46cb24b",
    "logger": "uk.axvr.dirigo.core",
    "message":"Log something",
    "context":"default",
    "clojure/context": {
        "ns": "uk.axvr.dirigo.core",
        "file": "/users/axvr/Projects/dirigo/src/uk/axvr/dirigo/core.clj"
        "line": "12",
        "column": "3"
    }
    "hello": [1, 2, 3],
    "world": {
        "foo": "bar"
    }
}
```


## Legal

No rights reserved.

All source code, documentation and associated files packaged and distributed
with "uk.axvr.epilogue" are dedicated to the public domain. A full copy of the
CC0 (Creative Commons Zero 1.0 Universal) public domain dedication can be found
in the `COPYING` file.

The author is not aware of any patent claims which may affect the use,
modification or distribution of this software.
