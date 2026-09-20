#!/usr/bin/env bash
# Checks what the build produced, before w8s assembles it into an image.
#
# This used to assert on ${W8S_IMAGE} because the Dockerfile step had
# already pushed one by the time it ran. It cannot any more, and that is
# the point: assembly happens after every step, so at this moment the image
# does not exist yet. What does exist is the artifacts it will be made
# from, which is the more useful thing to check anyway - a broken build is
# caught here rather than after a push.
set -euo pipefail
echo "repo=${W8S_REPO} ref=${W8S_REF} commit=${W8S_COMMIT_SHA} step=${W8S_STEP}"
echo "will be assembled into: ${W8S_IMAGE}"

test -f "${W8S_WORKSPACE}/shadow-cljs.edn"

# Exactly what .w8s/image.yaml copies. If either is missing, the assembly
# step would fail with "copy source does not exist" and this says so first,
# naming the step that should have written it.
test -f "${W8S_ARTIFACTS}/site/index.html" \
  || { echo "10-build.sh produced no index.html" >&2; exit 1; }
test -f "${W8S_ARTIFACTS}/nginx/default.conf" \
  || { echo "10-build.sh produced no nginx config" >&2; exit 1; }

echo "ok"
