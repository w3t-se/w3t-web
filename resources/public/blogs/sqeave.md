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
This code is probably completely incomprehensible but is basically implements a very basic `pull` EDN-query language which reconstructs . This function is used to pull all the data that a component needs (but this is added automatically when using `defc`).

The `defc` macro (defined in `sqeave/src/main/comp.cljc`), arguably one of the ugliest pieces of code ever written but it works quite well in defining components:

```clojure
(defmacro defc [name bindings body]
  (let [ntmp (str name)
        n (namespace (first (keys (second bindings))))

        params (first (vals (second bindings)))
        val-vec (mapv #(if (map? %) (first (keys %)) %) params)
        val-vec (mapv strip-ns val-vec)

        keywordify (fn [x] (keyword (str (if n (str n "/")) x)))

        or-map (let [m (-> bindings second :or)]
                 (zipmap (mapv keywordify (keys m)) (vals m)))

        local-map (let [m (-> bindings second :local)]
                    (zipmap (mapv keyword (keys m)) (vals m)))

        query (mapv #(if (map? %)
                       {(keywordify (first (keys %))) (first (vals %))}
                       (keywordify %)) params)]

    (list 'do
          (list 'defn (symbol (str name "Fn")) [{:keys (conj val-vec 'this 'props 'ctx)}] body)

          (list 'defclass name
                (list 'extends `Comp)

                (list (with-meta 'field {:static true}) 'query query)

                (list 'field 'local)
                (list 'field 'set-local!)

                (list 'constructor ['this# 'ctx] (list 'println "constructor: " ntmp)
                      (list 'super 'ctx)

                      (list 'set! 'this#.render 'this#.constructor.prototype.render)
                      (list 'set! 'this#.new-data 'this#.constructor.new-data))

                'Object
                (list (with-meta 'new-data {:static true}) ['_ '& 'data] (list 'let ['v (list 'vals or-map)
                                                                                     'k (list 'keys or-map)]
                                                                               (list 'merge or-map (list 'or (list 'first 'data) {}))))

                (list 'render ['this# 'body 'props]
                      (list 'let [(first bindings) 'this#
                                  'ctx (list `useContext 'this#.ctx)
                                  'ident (list 'get 'props :ident)
                                  'ident (list 'if-not (list 'fn? 'ident) (list 'fn [] 'ident) 'ident)
                                  {:keys ['store 'setStore]} 'ctx
                                  'data (list 'if (list 'comp/ident? (list 'ident))
                                              (list 'do
                                                    (list 'set! 'this#.ident 'ident)
                                                    (list `createMemo (list 'fn []
                                                                            (list 'println "memo: " ntmp " ident: " (list 'ident) " query: " query)
                                                                            (list 'let ['data (list `pull 'store (list 'ident) query)
                                                                                        #_(list 'if n
                                                                                                (list `pull 'store (list 'ident) query)
                                                                                                (list `pull 'store 'store query) ; multiple query entries pull from root
                                                                                                )]
                                                                                  (list 'println "data: " `data)
                                                                                  (list 'merge or-map (list 'or 'data {}))))))
                                              (list 'fn [] 'props))
                                  val-vec (mapv #(list `createMemo (list 'fn [] (list % (list 'data)))) (mapv keywordify val-vec))

                                  ['local 'setLocal] (list `createSignal local-map)]
                            (list 'set! 'this#.-ctx 'ctx)
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

          #_(list 'def name {:cla (symbol name) :body (symbol (str name "Fn"))})

          #_(list 'defn (symbol (str name "new")) ['props] (list 'let ['c (list new (str name "class"))]
                                                                 (list .render 'c (symbol (str name "fn")) 'props))))))
```
it is worth mentioning that `squint-cljs` supports "real" macros in the sense that they have to be compiled before runtime. Squint uses Sci for this but this is automatically performed when we use the handy `vite-plugin-squint`.
``
