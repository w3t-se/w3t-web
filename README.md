# w3t-web

The W3T website, built with [Sqeave](https://www.npmjs.com/package/@w3t-ab/sqeave)
(SolidJS + squint-cljs, bundled by Vite), and a small Node server that serves
it and takes webinar signups.

## Develop

```console
npm install && (cd server && npm install)
MAILGUN_DRY_RUN=1 node server/index.mjs   # API on :8080, mails logged, not sent
npm run dev                                # site on :5173, /api proxied to :8080
npm run build                              # static site in dist/
(cd server && npm test)                    # signup validation, invite, limiter
```

Routes (`src/main/index.cljs`, `@solidjs/router`):

| Path | What | File |
|---|---|---|
| `/` | Home: hero, clients, services, work, contact | `site.cljs` |
| `/webinar` | The webinar, its countdown and the signup form | `webinar.cljs` |
| `/book` | Book a call (HubSpot Meetings) | `book.cljs` |

- `webinar.json`: the webinar's title, start, agenda and Mailgun list. The site
  and the server both read it, so the date lives here only.
- `src/main/config.cljs`: the HubSpot Meetings link and contact details.
- `src/main/typewriter.cljs`: the hero's typewriter, using `typed.js`.
- The countdown is a Sqeave `defc` whose state is in the store under
  `[:webinar/id 0]`, shared by the home page and `/webinar`.

## Webinar signups

`POST /api/webinar/signup` (`server/index.mjs`) takes name, email, optional
company and role, and a consent tick. It:

1. adds the person to the Mailgun list in `webinar.json` (created on first
   use), with company, role and signup time as list variables;
2. emails them a confirmation with a calendar invite (`.ics`);
3. emails a short note to `NOTIFY_TO` (info@w3t.se).

Spam handling is a honeypot field and 5 signups per IP per 10 minutes. After
the start time the endpoint answers 410. Without a Mailgun key it answers 503
and the form says to email info@w3t.se.

### The join link

Set it in `chart/w3t-web/values.yaml` (`app.webinarJoinUrl`, e.g. a Google
Meet link) and push. It stays server-side: never on the public site or in
`/api/webinar`. From then on, confirmation emails and calendar invites carry
it. To send it to everyone who registered earlier, run this in the pod:

```console
kubectl -n w3t-web exec deploy/w3t-web -- node server/send-join-link.mjs --test you@w3t.se   # one preview
kubectl -n w3t-web exec deploy/w3t-web -- node server/send-join-link.mjs --yes               # the whole list
```

For anything else (a reminder, the recording), send to the list address in
Mailgun.

## Book a call

Set `hubspot-meetings-url` in `src/main/config.cljs` to your HubSpot meetings
link. Until then `/book` shows email and phone.

## Build and deploy

CI is w8s (`.w8s/`). On every push to `main`:

1. `.w8s/pipeline/10-build.sh` builds the site, tests the server, and stages
   both under `/app`.
2. `.w8s/image.yaml` assembles them onto `node:22-alpine`.
3. `.w8s/pipeline/20-deploy.sh` writes the Mailgun key Secret and runs
   `helm upgrade --install` with `chart/w3t-web` on **w8s-frontend-cluster**
   (release and namespace `w3t-web`).

`wctl ci run` from a checkout of `main` runs it by hand.

### One-time setup

The deploy step needs two secrets. Without either, w8s refuses the run before
any step starts, and the run's reason names the missing one.

- `hub-kubeconfig`: a kubeconfig for the hub cluster. Add it to the
  environment pushes to `main` use, or add `w3t-se/w3t-web` to the
  `w8s.cloud/ci-repos` annotation of the Secret `hub-kubeconfig` in `w8s-ci`.
- `mailgun-api-key`: a Mailgun API key (EU region) that can send from
  `mg.w3t.se` and manage mailing lists. Add it to the same environment.

`mg.w3t.se` has to be a verified sending domain in Mailgun's EU region.

### Hostnames

The site is served as `w3t.se` and `www.w3t.se` (`httproute.hostnames` in
`chart/w3t-web/values.yaml`), through the hub's `w8s-gateway`. An empty list
creates no HTTPRoute at all: a route without hostnames would match every host
on the shared gateway.
