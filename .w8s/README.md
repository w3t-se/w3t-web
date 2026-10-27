# w3t-web pipeline

Runs on every push to `main` that touches one of the inputs listed in
`pipeline/10-build.sh` (this folder and `chart/` included).

1. `pipeline/10-build.sh`: builds the site, tests the server, stages `/app`.
2. `image.yaml`: assembles `/app` onto `node:22-alpine`.
3. `pipeline/20-deploy.sh`: writes the Mailgun key Secret and runs
   `helm upgrade --install` with `chart/w3t-web` on w8s-frontend-cluster.

Secrets: `hub-kubeconfig`, `mailgun-api-key`. By hand: `wctl ci run` from a
checkout of `main`; follow it with `wctl ci runs --cluster w8s-frontend-cluster`.
