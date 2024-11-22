<a id="intro"></a>
We are excited to unveil Sqeave, a new framework that bridges the gap between ClojureScript and Solid-js using Squint-cljs. Sqeave aims to simplify the development of reactive web applications by combining the expressiveness of ClojureScript with the performance benefits of Solid-js.

Solid-js is known for its fine-grained reactivity and high performance, but integrating it with ClojureScript hasn't always been straightforward. Sqeave addresses this by providing seamless interop, allowing developers to write idiomatic ClojureScript code while leveraging Solid-js's powerful features.

So how do you use Sqeave? Let's start.

Clone the sqeave repo:

``` bash
git clone git@github.com:w3t-se/sqeave.git
```
Install dependencies and start the vite based dev server:
``` bash
cd sqeave/template && pnpm i && pnpx vite
```
you should see a browser window at http://localhost:5173.

Now, the main entrypoint of the application is in src/main/main.cljs:

<a id="main component"></a>
``` clojure
(ns main
  (:require ["@w3t-ab/sqeave/comp" :as comp]
            ["./Context.cljs" :refer [AppContext]])
  (:require-macros [comp :refer [defc def-factory]]))

(defc Main [this {:click/keys [id count] :or {id (comp/uuid) count 0}}]     ; 1, 2
  #jsx [:div {} "Hello Sqeave: "                                            ; 3
        [:button {:onClick #(comp/set! this :click/count (inc (count)))}
          "Plus"]                                                           ; 4
        [:p {} "Count: " (count)]])                                         ; 5

(def-factory UiMain Main AppContext MainFn)                                 ; 6
```

1. The `defc` macro is the starting point for any Sqeave component. This macro creates a class and wraps the component's data, ident and query.
2. The query is defined by the component's signature `{:click/keys [id count]}`. This means that this component will pull it's data (using Edn Query Language-syntax) from the normalized top level App state. The ident is automatically parsed from the query as: `[:click/id id]` in this case. The ident is set when this component is added to the render
3. The component's render function. The #jsx directive tells the Squint compiler to ouput jsx.
4. We are using the conveniance mutation function: comp/set! to update the App state. In this case this translate to reset!-ing the app state atom at `[:click/id id :click/count]`.
5. The rendered data is specified as a function `()` since it is a solid-js signal. Any set! or transactions towards the App state affecting the data at `[:click/id id :click/count]` will automatically cause the `(count)` to re-render.
6. We create a factory for this class such that it can be used (within the render functions) of other Components in the UI-tree.

The main entrypoint of the application is another Component "Root" defined in `src/main/index.cljs`:
<a id="root component"></a>
``` clojure
(ns index
  (:require ["solid-js/web" :refer [render]]
            ["solid-js/store" :refer [createStore]]
            ["@w3t-ab/sqeave/comp" :as comp]
            ["./Context.cljs" :refer [AppContext]]
            ["./main.cljs" :refer [UiMain]])
  (:require-macros [comp :refer [defc def-factory]]))

(defc Root [this {:keys []}]                                                ; 1
  (let [[store setStore] (createStore {:click/id {0 {:click/id 0            ; 2
                                                     :click/count 0}}})]
    #jsx [AppContext.Provider {:value {:store store :setStore setStore}}    ; 3
          [UiMain {:ident [:click/id 0]}]]))                                ; 4

(def-factory UiRoot Root AppContext RootFn)

(render UiRoot (js/document.getElementById "root"))                         ; 5
```

1. The root component's query is in this case emtpy since usually we just want to add entrypoints to sub components here
2. The root component contains the App state via a store always initiated with createStore.
3. The store is added to the AppContext.
4. The Main component is used as the (only) thing rendered within the root component. Notice that this is where we set the Main component's ident as `[:click/id 0]`.
5. The default solid-js render function to mount the Root in the root element. The root element is a div with id="root" defined in the vite index.html entrypoint.

This should be fairly self-explanatory and understandabe if you have worked with top-level State before and especially [Fulcro](https://fulcro.fulcrologic.com/).

<a id="details"></a>

## Behind the scenes
Now I thought we should take a moment to check the details of what is going on behind the scenes. The first piece code doing alot of the dirty work is `norm.cljs`. Here you will find the function 



is a combination of things. Firstly the `defc` macro is defined in `sqeave/src/main/comp.cljc` and is 


