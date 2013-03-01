# Building a Useful Website with ClojureScript - A Music Server

Follow along, if you like, as I detail my efforts to build something useful with ClojureScript.  The project chosen is a system for uploading, managing, and playing back music files in a web browser.

In the beginning, there is no design, only a rough set of ideas, requirements and general understanding of basic user interface, hovering around in the developers mind.  This will be some basic hobby-style hackery.

I've chosen the name 'keju' for this project.  Here's why:
 - it's short
 - it is the transposition of the two halves of the word 'juke'
 - it is the word for a chinese system of selecting bureaucrats based on (lol) merit

Having done a bit of experimenting to get a general idea of how ClojureScript works already, I've settled on simple scaffolding on which to build.

So without further ado, let's take a look at the barebones file structure of the project.

    code/client/keju/client.cljs
    code/server/keju/server.clj
    public/index.html
    project.clj

Code goes in the `code` directory.  It's evenly divided into two sections, `client` and `server`.  The `public` directory is where static and generated web documents go, to be served as-is by the web server.

Ok, now let's look at the `project.clj`, which tells [`leiningen`](https://github.com/technomancy/leiningen) about the structure we've chosen, and a few other things.

    (defproject keju "0.1.0-SNAPSHOT"
      :dependencies [[org.clojure/clojure "1.5.0-RC16"]]
      :plugins [[lein-cljsbuild "0.3.0"]]
                [lein-ring "0.8.2"]]
      :source-paths ["code/server"]
      :ring {:handler keju.server/app}
      :cljsbuild {:builds [{:source-paths ["code/client"]
                            :compiler {:output-to "public/index.js"
                                       :output-dir "public/build"
                                       :optimizations :none
                                       :pretty-print true}}]})

Briefly:
 - specifies the version of `clojure` I'd like to use
 - includes `cljsbuild`, a plugin for adding `clojurescript` build tasks to `leiningen` (lein)
 - includes [`ring`](https://github.com/ring-clojure/ring), a plugin to provide a basic web server framework, ala `rack` (for ruby)
 - tells leiningen where to look for server code
 - tells ring where to look for the web handler (where it sends each request), that is the function `app` in the namespace `keju.server`, which is in the file `code/keju/server.clj`
 - tells cljsbuild where to look for client code