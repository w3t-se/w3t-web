#!/usr/bin/env bash
# w8s-image: clojure:temurin-21-tools-deps-bookworm
#
# Compiles the site and leaves it in $W8S_ARTIFACTS. This step produces an
# artifact; it does not produce an image. What the image *is* - the nginx
# base, where the files land, which port - is declared once in
# ../image.yaml, and w8s assembles it after every step has run.
#
# Converted from 10-build.Dockerfile. The two build stages of that file map
# onto the two halves of this pipeline: everything that *ran* is here, and
# everything that was `FROM`/`COPY`/`EXPOSE` is in image.yaml. Nothing
# executes inside the image being built, so no builder, no snapshot of a
# root filesystem, and no privilege beyond what an ordinary pod already has.
set -euo pipefail
cd "${W8S_WORKSPACE}"

# shadow-cljs needs node beside the clojure toolchain. This was a cached
# RUN layer in the Dockerfile and is a real cost here - it runs every
# build. The fix is a prepared builder image with both, not a cache: see
# the note in image.yaml.
apt-get update
apt-get install -y --no-install-recommends nodejs npm
rm -rf /var/lib/apt/lists/*

npm ci --no-audit --no-fund
clojure -A:dev -P
npx shadow-cljs release main

# Everything the image needs, laid out the way image.yaml copies it.
mkdir -p "${W8S_ARTIFACTS}/site" "${W8S_ARTIFACTS}/nginx"
cp -a resources/public/. "${W8S_ARTIFACTS}/site/"

# Was `RUN printf ... > /etc/nginx/conf.d/default.conf` in the final stage.
# Writing a file is not a reason to need a container runtime, so it becomes
# an artifact that gets copied in.
cat > "${W8S_ARTIFACTS}/nginx/default.conf" <<'CONF'
server {
  listen 8080;
  root /usr/share/nginx/html;
  index index.html;
  location / { try_files $uri $uri/ /index.html; }
}
CONF

echo "built $(find "${W8S_ARTIFACTS}/site" -type f | wc -l) files"
