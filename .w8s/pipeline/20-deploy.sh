#!/usr/bin/env bash
# image: alpine/k8s:1.31.13
# input: image
# secret: hub-kubeconfig
#
# Deploys the image this run built to w8s-frontend-cluster (the hub
# cluster) with the chart in chart/w3t-web, as release "w3t-web" in
# namespace "w3t-web". Runs on every push to main (the trigger is on
# 10-build.sh), or by hand: `wctl ci run` from a checkout of main.
#
# Same shape as the w8s repo's ops/redeploy-keycloak:
#   - hub-kubeconfig: a kubeconfig that reaches the hub's API server from
#     a build pod, raw YAML or base64 of it. Comes from the run's
#     environment, or from the Secret hub-kubeconfig in w8s-ci once its
#     w8s.cloud/ci-repos annotation lists w3t-se/w3t-web.
#   - Does not wait for the rollout. `# input: image` should put this
#     step after the image is assembled and pushed, but a hub that runs it
#     before assembly would otherwise wait on an image that cannot exist
#     yet. Pointing the Deployment at this commit's tag and returning
#     works in either order: kubelet retries the pull, and the pod starts
#     once this run's push lands. Check the rollout with
#     `kubectl -n w3t-web rollout status deployment/w3t-web`.
#   - W8S_IMAGE names the in-cluster push endpoint, which kubelet cannot
#     pull from, so the same image is deployed from the registry's public
#     host (registry-<slug>.w8s.cloud, or whatever the hub overrides it
#     to) with CI's own pull credential (w8s-ci/w8s-registry-pull). The
#     host is read from that credential, so the two always match.
#     W3T_WEB_REGISTRY overrides it;
#     the credential is copied into the release namespace on every run
#     so a rotated one reaches it too.
set -euo pipefail

RELEASE=w3t-web
NAMESPACE=w3t-web
CHART=/workspace/chart/w3t-web
PULL_SECRET=w8s-registry-pull
CI_NAMESPACE=w8s-ci

fail() { echo "deploy: $*" >&2; exit 1; }

# --- Cluster ---------------------------------------------------------------
: "${HUB_KUBECONFIG:?# secret: hub-kubeconfig did not reach this step}"
kc=$(mktemp); chmod 600 "$kc"
if printf '%s' "$HUB_KUBECONFIG" | base64 -d >"$kc" 2>/dev/null && grep -q apiVersion "$kc"; then :; else
  printf '%s\n' "$HUB_KUBECONFIG" >"$kc"
fi
export KUBECONFIG="$kc"
kubectl cluster-info >/dev/null 2>&1 || fail "hub-kubeconfig does not reach a cluster ($(kubectl config view --minify -o jsonpath='{.clusters[0].cluster.server}'))"
echo "cluster: $(kubectl config view --minify -o jsonpath='{.clusters[0].cluster.server}')"

# --- Image -----------------------------------------------------------------
: "${W8S_IMAGE:?W8S_IMAGE is not set - this step must run after the image is assembled}"
path="${W8S_IMAGE#*/}"          # drop the push endpoint's host:port
repo="${path%:*}"
tag="${path##*:}"
[ "$repo" != "$path" ] || fail "cannot read a tag from W8S_IMAGE=$W8S_IMAGE"

# --- Pull credential -------------------------------------------------------
kubectl get namespace "$NAMESPACE" >/dev/null 2>&1 || kubectl create namespace "$NAMESPACE"
kubectl -n "$CI_NAMESPACE" get secret "$PULL_SECRET" -o json >/dev/null 2>&1 \
  || fail "secret $CI_NAMESPACE/$PULL_SECRET not found - it is created with the CI registry"
command -v jq >/dev/null || apk add --no-cache jq >/dev/null
dockercfg="$(dirname "$kc")/dockerconfig.json"
kubectl -n "$CI_NAMESPACE" get secret "$PULL_SECRET" -o jsonpath='{.data.\.dockerconfigjson}' \
  | base64 -d >"$dockercfg"

# The public host kubelet pulls from: the credential's registry that is not
# the in-cluster push endpoint.
push_host="${W8S_IMAGE%%/*}"
hosts=$(jq -r '.auths | keys[]' "$dockercfg" | grep -vxF "$push_host" || true)
REGISTRY="${W3T_WEB_REGISTRY:-$(printf '%s\n' "$hosts" | grep -m1 '^registry-' || printf '%s\n' "$hosts" | head -n1)}"
[ -n "$REGISTRY" ] || fail "$CI_NAMESPACE/$PULL_SECRET has no registry besides $push_host (has: $(jq -r '.auths | keys | join(", ")' "$dockercfg"))"
echo "image: $REGISTRY/$repo:$tag"
kubectl -n "$NAMESPACE" create secret docker-registry "$PULL_SECRET" \
  --from-file=.dockerconfigjson="$dockercfg" \
  --dry-run=client -o yaml | kubectl apply -f -
rm -f "$dockercfg"

# --- Release ---------------------------------------------------------------
# A release that never reached "deployed" (a first install that failed)
# blocks upgrades; nothing of it is worth keeping, so remove it.
if helm status "$RELEASE" -n "$NAMESPACE" >/dev/null 2>&1 \
  && ! helm history "$RELEASE" -n "$NAMESPACE" -o json | grep -q '"status":"deployed"' \
  && ! helm history "$RELEASE" -n "$NAMESPACE" -o json | grep -q '"status":"superseded"'; then
  echo "release $RELEASE was never deployed; removing it before installing"
  helm uninstall "$RELEASE" -n "$NAMESPACE"
fi

helm upgrade --install "$RELEASE" "$CHART" \
  --namespace "$NAMESPACE" \
  --set image.repository="$REGISTRY/$repo" \
  --set image.tag="$tag" \
  --set imagePullSecrets[0].name="$PULL_SECRET"

echo "released $RELEASE @ $tag; pods start once $REGISTRY/$repo:$tag is pushed"
