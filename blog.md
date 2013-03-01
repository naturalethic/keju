# Building a Useful Website with ClojureScript - A Music Server

Follow along, if you like, as I detail my efforts to build something useful with ClojureScript.  The project chosen is a system for uploading, managing, and playing back music files in a web browser.

In the beginning, there is no design, only a rough set of ideas, requirements and general understanding of basic user interface, hovering around in the developers mind.  This will be some basic hobby-style hackery.

## Why 'keju'?

I've chosen the name 'keju' for this project.  Here's why:
 - it's short
 - it is the transposition of the two halves of the word 'juke'
 - it is the word for a chinese system of selecting bureaucrats based on (lol) merit

## The Simplest of Servers

To start with, I want to serve web requests.  I want the server to just return a 'Howdy' when I point my browser at the host and port.  To do this, I need exactly two files.

Here's the structure:

    code/server/keju/server.clj
    project.clj

Code goes in the `code` folder.  Later, I'll add a `client` folder, when there are things to do on the client.

This project is built and run with [`leiningen`](https://github.com/technomancy/leiningen).  `project.clj` tells leiningen about the structure I've chosen, and a few other things.

    (defproject keju "0.1.0-SNAPSHOT"
      :dependencies [[org.clojure/clojure "1.5.0"]]
      :plugins [[lein-ring "0.8.2"]]
      :source-paths ["code/server"]
      :ring {:handler keju.server/app})

Here's what that says:
 - the name and version of the project
 - specify the version of `clojure` to use
 - include [`ring`](https://github.com/ring-clojure/ring), a plugin to provide a basic web server framework, ala `rack` (for ruby)
 - tell leiningen where to look for server code
 - tell ring where to look for the web handler (where it sends each request), that is the function `app` in the namespace `keju.server`, which is in the file `code/keju/server.clj`

Ok, so what's in the other file, `code/server/keju/server.clj`?

    (ns keju.server)

    (defn app [req]
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body "Howdy"})

As with any Clojure code file, declare a namespace that fits with where the code sits.  Following that there is a function definition.  This function takes a map representing the request (as delivered by `ring`) and your job is to return a map representing the response.  For more info take a look at [some ring docs](https://github.com/ring-clojure/ring/wiki/Concepts).

That's it.  Your basic Howdy with Leiningen and Ring.

Run it like so

    lein ring server-headless

Then browse to `yourserver:3000`.
