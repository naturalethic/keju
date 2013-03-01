# Building a Useful Website with ClojureScript - A Music Server

Follow along, if you like, as I detail my efforts to build something useful with ClojureScript.  The project chosen is a system for uploading, managing, and playing back music files in a web browser.

In the beginning, there is no design, only a rough set of ideas, requirements and general understanding of basic user interface, hovering around in the developers mind.  This will be some basic hobby-style hackery.

## Why 'keju'?

I've chosen the name 'keju' for this project.  Here's why:
 - it's short
 - it is the transposition of the two halves of the word 'juke'
 - it is the word for a chinese system of selecting bureaucrats based on (lol) merit

## Conventions

For this project, I will presume the server is named `sandbox`.  Keep it in mind to replace that with your own server name in any code/cli examples.

## The Simplest of Servers ([revision](https://github.com/naturalethic/keju/tree/acb533c825cbceca8afc0713dbd55adef7bb92c4))

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

Then browse to `sandbox:3000`.

## REPL Time

Clojure nerds love the REPL.  They must have a reason for that, so the REPL goes in next.  To do this, some client code now has to be compiled.

Adding the file `code/client/keju/client.cljs`, it looks like:

    (ns keju.client
      (:require [clojure.browser.repl :as repl]))

    (repl/connect "http://sandbox:9000/repl")

This is a ClojureScript file, which is quite similar to regular Clojure.  As with the server code, declare the namespace.  This time also pull in the external library for client-side repl support.  Following that, call it's `connect` function.

Ok, this code must be compiled into javascript for the browser to be able to run it.  Add some stuff to `project.clj` so that it now looks like this:

    (defproject keju "0.1.0-SNAPSHOT"
      :dependencies [[org.clojure/clojure "1.5.0-RC16"]]
      :plugins [[lein-cljsbuild "0.3.0"]]
                [lein-ring "0.8.2"]]
      :source-paths ["code/server"]
      :ring {:handler keju.server/app}
      :cljsbuild {:builds [{:source-paths ["code/client"]
                            :compiler {:output-to "public/client.js"
                                       :output-dir "public/build"
                                       :optimizations :none
                                       :pretty-print true}}]})

Here's what was added:
 - includes `cljsbuild`, a plugin for adding `clojurescript` build tasks to `leiningen` (lein)
 - tells cljsbuild where to look for client code, and instructs the google closure compiler where to output intermediate (build) files, and the complete javascript result.

Ok one thing that must be mentioned is the Google Closure compiler.  This is not Clojure.  But ClojureScript is tied at the hip to Google Closure.  As far as I understand it provides some data structures, supports the namespacing and module loading, lots of performance enhancements, etc.  For now, just understand that after Clojure generates the JavaScript from ClojureScript, it passes it through the Google Closure compiler, which then dumps the files where they were specified in the `:compiler` directive in the project file.

Ok, you may now go ahead and compile your ClojureScript by running:

    lein cljsbuild once

That's going to generate `public/build` and `public/client.js`.

The latter should look like so:

    goog.addDependency("base.js", ['goog'], []);
    goog.addDependency("../cljs/core.js", ['cljs.core'], ['goog.string', 'goog.array', 'goog.object', 'goog.string.format', 'goog.string.StringBuffer']);
    goog.addDependency("../clojure/browser/event.js", ['clojure.browser.event'], ['cljs.core', 'goog.events.EventType', 'goog.events.EventTarget', 'goog.events']);
    goog.addDependency("../clojure/browser/net.js", ['clojure.browser.net'], ['goog.net.xpc.CrossPageChannel', 'clojure.browser.event', 'goog.net.xpc.CfgFields', 'cljs.core', 'goog.net.EventType', 'goog.json', 'goog.net.XhrIo']);
    goog.addDependency("../clojure/browser/repl.js", ['clojure.browser.repl'], ['clojure.browser.event', 'clojure.browser.net', 'cljs.core']);
    goog.addDependency("../keju/client.js", ['keju.client'], ['cljs.core', 'clojure.browser.repl']);

As one can see, presuming the `goog` namespace is available on the browser, it will call `addDependency` to specify the dependency tree of the compiled client code to the google module system.  You see the keju client there at the end, and notice it's dependency on on the browser repl just above it, and it's own dependencies above that.  All this code is in `public/build`, because we didn't specify any optimizations to the compiler.  If we had, everything would be placed in `client.js`, but that won't be necessary until deployment.

Ok we've got `client.js` and our compiled modules, how do we load this all into the browser?

For now, modify the `:body` of the response in the `app` function in `server.clj` to output some html that instructs the browser to load and run these scripts.  Make it look as so:

    (defn app [req]
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body "<!DOCTYPE html>
              <html>
                <body>
                  <script src=\"build/goog/base.js\"></script>
                  <script src=\"client.js\"></script>
                  <script>goog.require('keju.client')</script>
                </body>
              </html>"})

You see this will load and run, in order, the google closure base api, which provides the `goog` namespace referred to in `client.js`, which it then loads.  Finally `require` the module that was generated from our `client.cljs` file.

Now, run the ring server again and browse to your server as described earlier.

If you have done everything correctly as I've described to now, you should get a blank page and (if you have your javascript console in the browser open) a bunch of errors.  Good job.

The keen-minded will realize that our web server only knows how to do one thing as yet, deliver the html response we described in the `app` function.  Any request made to it will result in the same response.  One trick pony, so to speak.  So when we tell the browser to retrieve `build/goog/base.js`, the server sends it back the same boring html as the initial page.  Next step, tell the web server how to serve static files.

### Serving static files

Ring comes with a nice set of middleware to handle these things.  First, let's extract the html into a separate, static file at `public/index.html`:

    <!DOCTYPE html>
    <html>
      <body>
        <script src="build/goog/base.js"></script>
        <script src="client.js"></script>
        <script>goog.require('keju.client')</script>
      </body>
    </html>

Now, modify `server.clj` to look like this:

    (ns keju.server
      (:use [ring.middleware file file-info]))

    (defn default-handler [req]
      {:status 404
       :headers {"Content-Type" "text/html"}
       :body "<!DOCTYPE html>
              <html>
                <body>
                  Not found.
                </body>
              </html>"})

    (def app
      (-> default-handler
          (wrap-file "public")
          (wrap-file-info)
          ))

I've modified the original handler to provide a 404 response, and renamed it to `default-handler`.  In the namespace declaration, we're now pulling in the middleware we'll need to serve static files.  Each middleware provides a `wrap` function to be placed in our chain.  `app` has been redifined to a *thrushed* sequence of functions.  The `wrap` functions actually return a new function to be placed in the sequence.  Our own handler is at the top.  Without necessarilly understanding exactly how this all works internally, just be aware that `app` is now defined as a sequence of functions where the request enters at the bottom, and proceeds, if allowed, up the chain, until it hits the default, and then the response is sent back down through the chain.  Any function in the sequence can preempt further propagation and immediatly return the response down the chain without further progress up the chain.

So what's now happening here?  Ring generates a request map, and sends it to `file-info`, a piece of middleware for determining the content-type of the response.  Since it has no use for the request, it simply sends the request on up to `file`, which we've initialized to look in the `public` folder.  If `file` finds a static file after examining the request, it will load it into the response body and pass it right back down to `file-info`.  If it hadn't found a file, it would have let the request bubble up to our default handler.  `file-info`, being interested in the response, takes a look at it and tries to determine the content-type, finally passing the response back to ring, which sends it off to the browser.

Ok, restart the ring server and curl it from a new console

    curl localhost:3000/
    curl localhost:3000/client.js
    curl localhost:3000/foo

You should get the index.html page, the client.js page, and the 404 page.

=== Finishing It Up

Reload your browser page pointing at the app, and keep an eye on your browser console.  If everything is working correctly up to this point, you'll get an error failing a GET request to sandbox:9000.  The last step in this section is to run the server side repl.  In a new server console, run this command (from the project dir, as always):

    lein trampoline cljsbuild repl-listen

Once that command results in a repl prompt, reload your browser.  You'll not no connection error this time.  The browser may appear to 'sit and spin', that's ok, it's holding a keep-alive with the clojure repl.  Switch back to the repl and type this:

    (js/alert "Howdy")

Switch back to your browser, neat-o, huh?

=== One more thing

You might notice while messing around in the ClojureScript REPL that there is no curses support.  If you want that, you need to use [rlwrap](http://freecode.com/projects/rlwrap).  Once you have that installed (it may be a standard package for your server OS), you can run repl like so:

    rlwrap -r -m -b "(){}[],^%3@\";:'" lein trampoline cljsbuild repl-listen

I like to alias things like this in a bash script which I `source` into my environment when switching to a project.  I've placed mine in `system/etc/profile` and here it is:

    function keju-help()
    {
      echo "Usage: keju <command>"
      echo "  repl - start a clojurescript browser repl"
      echo "  ring - start a ring server"
      echo "  auto - auto build"
    }

    function keju()
    {
      if [ $# -lt 1 ]; then
        keju-help
      else
        case $1 in
          "repl") rlwrap -r -m -b "(){}[],^%3@\";:'" lein trampoline cljsbuild repl-listen ;;
          "ring") lein ring server-headless ;;
          "auto") lein cljsbuild auto ;;
          *)      keju-help
        esac
      fi
    }

You'll notice I'm using `cljsbuild auto` here.  That will have leiningen rebuild whenever there are changes in the client portion of our codebase.

