#!/usr/bin/env bash
set -euo pipefail
echo "repo=${W8S_REPO} ref=${W8S_REF} commit=${W8S_COMMIT_SHA} step=${W8S_STEP}"
echo "image pushed by the Dockerfile step: ${W8S_IMAGE}"
test -f "${W8S_WORKSPACE}/shadow-cljs.edn"
echo "${W8S_IMAGE}" > "${W8S_ARTIFACTS}/image-ref"
echo "ok"
