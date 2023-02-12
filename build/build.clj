(ns build
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))

(def lib 'uk.axvr/epilogue)
;; (def lib2 'uk.axvr/epilogue.logback.json)
(def version
  (subs (b/git-process {:git-args ["describe" "--tags" "--abbrev=0"]}) 1))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  "Clean the targets folder."
  (b/delete {:path "target"}))

(defn jar
  "Build the JAR."
  [opts]
  (clean nil)
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src"]
                :src-pom "build/pom.xml"})
  (b/compile-clj {:ns-compile ['uk.axvr.epilogue.logback.json.JsonLayout]
                  :basis basis
                  :src-dirs ["src"]
                  :class-dir class-dir
                  :bindings {#'clojure.core/*assert* false
                             #'clojure.core/*warn-on-reflection* true}})
  (b/jar {:class-dir class-dir
          :jar-file  jar-file}))

(defn install
  "Install the JAR locally."
  [opts]
  (b/install
    {:basis      basis
     :lib        lib
     :version    version
     :jar-file   jar-file
     :class-dir  class-dir}))

(defn deploy
  "Deploy the JAR to Clojars."
  [opts]
  (dd/deploy {:installer :remote
              :artifact (b/resolve-path jar-file)
              :pom-file (b/pom-path {:lib lib, :class-dir class-dir})}))
