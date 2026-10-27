# w3t-web

The W3T website, built with [Sqeave](https://www.npmjs.com/package/@w3t-ab/sqeave)
(SolidJS + squint-cljs, bundled by Vite).

## Develop

```console
npm install
npm run dev        # http://localhost:5173
npm run build      # static site in dist/
```

- `src/main/site.cljs`: the page, section by section.
- `src/main/config.cljs`: things that change often: the webinar start
  (`webinar-start`, ISO 8601 with offset; `nil` shows "Date announced soon")
  and contact details.
- `src/main/typewriter.cljs`: the hero's "Your Kubernetes / DevOps /
  Operations partner" effect, using the `typed.js` npm module. Screen readers
  get the full sentence; reduced-motion users see a static word.
- `src/main/styles.css`: all styles. Static files (blog Markdown, images) are
  in `resources/public/`.

The countdown is a Sqeave `defc` component whose state lives in the store under
`[:webinar/id 0]`.

## Build and deploy

CI is w8s (`.w8s/`). On every push to `main`:

1. `.w8s/pipeline/10-build.sh` builds the site in `node:22-alpine`.
2. `.w8s/image.yaml` assembles it onto unprivileged nginx (`nginx.conf`).
3. `.w8s/pipeline/20-deploy.sh` runs `helm upgrade --install` with
   `chart/w3t-web` on **w8s-frontend-cluster** (release and namespace
   `w3t-web`), using the image this run built.

To run it by hand from a checkout of `main`: `wctl ci run`. To build locally
without deploying: `wctl run` (the deploy step is skipped without the secret).

### One-time setup

The deploy step needs the `hub-kubeconfig` secret. Either add it to the
environment that pushes to `main` use (CI/CD → Environments), or let this
repository read the existing Secret in `w8s-ci` by adding `w3t-se/w3t-web`
to its `w8s.cloud/ci-repos` annotation (comma-separated `owner/repo`):

```console
kubectl -n w8s-ci get secret hub-kubeconfig \
  -o jsonpath='{.metadata.annotations.w8s\.cloud/ci-repos}'   # current list
kubectl -n w8s-ci annotate secret hub-kubeconfig --overwrite \
  w8s.cloud/ci-repos="<current list>,w3t-se/w3t-web"
```

Without it, a push to `main` is refused before any step runs, naming the
missing secret.

### Hostnames

The site is served as `w3t.se` and `www.w3t.se` (`httproute.hostnames` in
`chart/w3t-web/values.yaml`), through the hub's `w8s-gateway`. `w3t.se` is a
custom domain of the org in w8s, which publishes the hostnames once the route
is reported. An empty list creates no HTTPRoute at all: a route without
hostnames would match every host on the shared gateway.
