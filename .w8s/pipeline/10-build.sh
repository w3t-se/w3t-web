# image: node:22-alpine
# on: push main
# input: .w8s, chart, index.html, src, resources, server, webinar.json, package.json, package-lock.json, vite.config.js, squint.edn
# cache: /root/.npm shared
# artifact: /out
#
# Builds the site (Sqeave: squint-cljs + SolidJS, bundled by Vite), tests
# the signup server, and stages both under /out/app for .w8s/image.yaml:
#   /app/public        the built site
#   /app/server        the Node server and its production dependencies
#   /app/webinar.json  the webinar's details, read by site and server
set -eu
cd /workspace

npm ci --no-audit --no-fund
npx vite build

cd server
npm ci --no-audit --no-fund --omit=dev
node --test
cd ..

mkdir -p /out/app/public /out/app/server
cp -R dist/. /out/app/public/
cp server/package.json server/package-lock.json server/*.mjs /out/app/server/
rm -f /out/app/server/*.test.mjs
cp -R server/node_modules server/assets /out/app/server/
cp webinar.json /out/app/webinar.json
