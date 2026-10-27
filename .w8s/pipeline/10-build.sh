# image: node:22-alpine
# on: push main
# input: index.html, src, resources, package.json, package-lock.json, vite.config.js, squint.edn, nginx.conf
# cache: /root/.npm shared
# artifact: /out
#
# Builds the site (Sqeave: squint-cljs + SolidJS, bundled by Vite) and
# stages it for nginx with its server config. The image is assembled
# from /out by .w8s/image.yaml; 20-deploy.sh then ships it.
set -eu
cd /workspace

npm ci --no-audit --no-fund
npx vite build

mkdir -p /out/usr/share/nginx/html /out/etc/nginx/conf.d
cp -R dist/. /out/usr/share/nginx/html/
cp nginx.conf /out/etc/nginx/conf.d/default.conf
