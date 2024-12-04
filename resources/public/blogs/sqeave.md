<a id="intro"></a>
We are excited to unveil Sqeave, a new framework that bridges the gap between ClojureScript and Solid-js using Squint-cljs. Sqeave aims to simplify the development of reactive web applications by combining the expressiveness of ClojureScript with the performance benefits of Solid-js.

Solid-js is known for its fine-grained reactivity and high performance. With Sqeave you can write idiomatic ClojureScript (via Squint-cljs) code while leveraging Solid-js's powerful features.

So how do you use Sqeave? Let's start.

The quickest is to simply run the starting template:
``` bash
 pnpm create @w3t-ab/sqeave@latest
```

This will set up the main folder structure and install dependencies. Now start the vite based dev server:
``` bash
cd sqeave-app && pnpx vite
```
you should see a browser window at http://localhost:5173.

Now, the main entrypoint of the application is in src/main/index.cljs:
<a id="main component"></a>
``` clojure
(ns main
  (:require ["@w3t-ab/sqeave" :as sqeave])
  (:require-macros [sqeave :refer [defc]]))

(defc Main [this {:main/keys [id count] :or {id 0 count 0}}]                ; 1, 2
  #jsx [:div {} "Hello Sqeave: "                                            ; 3
        [:button {:onClick #(sqeave/set! this :main/count (inc (count)))}
          "Plus"]                                                           ; 4
        [:p {} "Count: " (count)]])                                         ; 5
```

1. The `defc` macro is the starting point for any Sqeave component. This macro creates a class and wraps the component's data, ident and query.
2. The query is defined by the component's signature `{:click/keys [id count]}`. This means that this component will pull its data (using [Edn Query Language-syntax](https://edn-query-language.org/eql/1.0.0/what-is-eql.html)) from the normalized top level App state. The ident is automatically parsed from the query as: `[:click/id id]` in this case. The ident is set when this component is added to the render function somewhere in the DOM (by mounting the component via the Root component as you typically mount React/Solid-js components).
3. The component's render function. The #jsx directive tells the Squint compiler to ouput jsx.
4. We are using the conveniance mutation function: `comp/set!` to update the App state. In this case this translates to `*reset!`-ing the app state atom at `[:click/id id :click/count]`.
5. The rendered data is specified as a function `()` since it is a solid-js signal. Any set! or transactions towards the App state affecting the data at `[:click/id id :click/count]` will automatically cause the `(count)` to re-render.

The main entrypoint of the application is another Component "Root" defined in `src/main/index.cljs`:
<a id="root component"></a>
``` clojure
(ns index
  (:require ["solid-js" :refer [createContext]]
            ["solid-js/web" :refer [render]]
            ["@w3t-ab/sqeave" :as sqeave]
            ["./main.cljs" :refer [Main]])
  (:require-macros [sqeave :refer [defc]]))

(def AppContext (createContext))                                            ; 1

(defc Root [this {:keys [] :or {} :ctx (sqeave/init-ctx! AppContext)}]      ; 2
  #jsx [AppContext.Provider {:value this.ctx}                               ; 3
        [Main {:ident [:main/id 0]}]])                                      ; 4

(let [e (js/document.getElementById "root")]                                ; 5
  (set! (aget e :innerHTML) "")
  (render Root e))
```

1. The root component's query is in this case emtpy since usually we just want to add entrypoints to sub components for Root. The root component contains the App state via a store always initiated with sqeave/init-ctx! (which is just a wrapper around solid-js createStore. In this case it is added via the special key :ctx in the Component signature and is then added to the Contect in 3.
3. The store is added to the AppContext.
4. The Main component is used as the (only) thing rendered within the root component. Notice that this is where we set the Main component's ident as `[:click/id 0]`.
5. The default solid-js render function to mount the Root in the root element. The root element is a div with id="root" defined in the vite index.html entrypoint.

This should be fairly self-explanatory and understandabe if you have worked with top-level State before and especially [Fulcro](https://fulcro.fulcrologic.com/).

<a id="details"></a>

## Behind the scenes
Now I thought we should take a moment to check the details of what is going on behind the scenes. The first piece of code doing alot of the dirty work is `traverse-and-transform`:

```clojure
(defn traverse-and-transform [item setStore]
  (cond
    (vector? item) (mapv #(traverse-and-transform % setStore) item)
    (map? item)  (let [ident (u/get-ident item)
                       new-val (zipmap (keys item) (mapv #(traverse-and-transform % setStore) (vals item)))]
                   (if (ident? ident)
                     (do
                       (swap! setStore #(update-in % ident (fn [v] (merge v new-val))))
                       ident)
                     new-val))

    :else item))
```
this is a basic recursive traverse function that normalizes incoming data. Simillarly there is a `pull` function:

```clojure
(defn pull [store entity query]
  (cond
    (ident? entity) (pull store (get-in store entity) query)

    (and (> (count entity) 0)
         (vector? entity)) (mapv (fn [x] (if (ident? x)
                                           (pull store x query)
                                           x)) entity)

    (and (> (count query) 1)
         (vector? query)) (let [simple-keys (filterv string? query)
                                not-simple  (filterv #(not (string? %)) query)]
                            (into (zipmap simple-keys (mapv #(pull store entity %) simple-keys))
                                  (mapv #(pull store entity %) not-simple)))

    (and (= (count query) 1)
         (vector? query)) (pull store entity (first query))

    (map? query) (let [nk (first (keys query))
                       sub-query (get query nk)]
                   (when-let [data (get entity nk)]
                     {nk (if (ident? data)
                           (pull store data sub-query)
                           (mapv #(pull store % sub-query) data))}))

    :else (get entity query)))
```
This code is probably completely incomprehensible but basically implements a very basic EDN-`pull` query for selecting data in a normalized database. This is the database that is added in the `ctx` of all Sqeave Components. This function is used to pull all the data that a component needs via the component query (this is the basic state management loop which is added automatically when using `defc` and using sqeave mutation funnctions explain below).

The `defc` macro (defined in `sqeave/src/main/comp.cljc`), arguably one of the ugliest pieces of code ever written but it works quite well in defining components:
```clojure
(defmacro defc [name bindings body]
  (let [ntmp (str name)
        n (namespace (first (keys (second bindings))))

        params (first (vals (second bindings)))
        val-vec (mapv #(if (map? %) (first (keys %)) %) params)
        val-vec (mapv strip-ns val-vec)

        keywordify (fn [x] (keyword (str (when n (str n "/")) x)))

        or-map (let [m (-> bindings second :or)]
                 (zipmap (mapv keywordify (keys m)) (vals m)))

        local-map (let [m (-> bindings second :local)]
                    (zipmap (mapv keyword (keys m)) (vals m)))

        query (mapv #(if (map? %)
                       {(keywordify (first (keys %))) (first (vals %))}
                       (keywordify %)) params)

        binding-ctx (:ctx (second bindings))]

    (list 'do
          (list 'defn (symbol (str name "Fn")) [{:keys (conj val-vec 'this 'props 'ctx)}] body)

          (list 'defclass (symbol (str name "Class"))
                (list 'extends 'sqeave/Comp)

                (list (with-meta 'field {:static true}) 'query query)

                (list 'field 'local)
                (list 'field 'set-local!)

                (list 'constructor ['this# 'ctx] (list 'sqeave/debug "constructor: " ntmp " ctx: " 'ctx)
                      (list 'super 'ctx)

                      (list 'set! 'this#.render 'this#.constructor.prototype.render)
                      (list 'set! 'this#.new-data 'this#.constructor.new-data)

                      #_(list 'set! 'this#.render 'this#.constructor.render))

                'Object
                (list (with-meta 'new-data {:static true}) ['_ 'data] (list 'merge or-map 'data))

                (list 'render ['this# 'body 'props]
                      (list 'let [(first bindings) 'this#
                                  'a (list 'sqeave/debug "render: " ntmp " props: " 'props)
                                  'ctx (list 'or binding-ctx (list `useContext 'this#.-ctx))
                                  'ident (list 'get 'props :ident)
                                  'ident (list 'if-not (list 'fn? 'ident) (list 'fn [] 'ident) 'ident)
                                  'ident (list 'if (list nil? (list 'ident)) (list 'fn [] []) 'ident)
                                  'a (list 'sqeave/debug ntmp ": p " 'props " i: " (list 'ident) " q: " query " ctx:" 'ctx)
                                  'a (list 'set! 'this#.ident 'ident)
                                  {:keys ['store 'setStore]} 'ctx
                                  'a (list 'sqeave/add! 'ctx (list 'this#.new-data))
                                  'data (list 'if (list 'or (list 'and (list 'vector? (list 'ident)) (list '= (list 'count (list 'ident)) 0))
                                                        (list 'sqeave/ident? (list 'ident)))
                                              (list 'do
                                                    (list 'sqeave/createMemo (list 'fn []
                                                                                   (list 'sqeave/debug "memo: " ntmp " ident: " (list 'ident) " query: " query)
                                                                                   (list 'let ['data (list 'sqeave/pull 'store (list 'if (list 'empty? (list 'ident))
                                                                                                                                     'store (list 'ident)) query)]
                                                                                         (list 'sqeave/debug "data: " 'data)
                                                                                         (list 'merge or-map 'data)))))
                                              (list 'fn [] 'props))
                                  val-vec (mapv #(list 'sqeave/createMemo (list 'fn [] (list % (list 'data)))) (mapv keywordify val-vec))

                                  ['local 'setLocal] (list 'sqeave/createSignal local-map)]
                            (list 'set! 'this#.ctx 'ctx)
                            (list 'set! 'this#.local 'local)
                            (list 'set! 'this#.data 'data)
                            (list 'set! 'this#.set-local! (list 'fn ['this# 'data] (list 'setLocal (list 'merge (list 'local) 'data))))
                            (if local-map
                              (list 'let ['local-map-k (vec (keys local-map))
                                          'local-map-k (mapv #(list 'fn [] (list % (list 'local))) (keys local-map))]
                                    (list 'body (zipmap (mapv keyword (conj val-vec 'this 'props 'ctx)) (conj val-vec 'this# 'props 'ctx))))
                              (list 'body (zipmap (mapv keyword (conj val-vec 'this 'props 'ctx)) (conj val-vec 'this# 'props 'ctx))))
                            #_(list 'if 'children [:<>
                                                   body
                                                   'children] body))))

          (list 'defn name ['props] (list 'let ['c (list 'new (symbol (str name "Class")) 'sqeave/AppContext)]
                                          (list '.render 'c (symbol (str name "Fn")) 'props))))))
```
it is worth mentioning that `squint-cljs` supports "real" macros in the sense that they have to be compiled before runtime. Squint uses Sci for this but this is automatically performed when we use the handy `vite-plugin-squint` plugin set up in `vite.config.js`.

So the main usage of Sqeave is the easy definition of components which pull their data and automatically create and update the data signals in the defc signature!

Now, we are continuously adding features to Sqeave such as connectors to Databases and automtic Schema creation etc. This is only an alpha version focusing on the basics of State management. Stay tuned for more updates and examples on how to use Sqeave in a real fullstack App!

Thank you (and now got back to building stuff)!
