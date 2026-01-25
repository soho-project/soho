#!/bin/bash
set -euo pipefail

script_dir="$(cd "$(dirname "$0")" && pwd)"
repo_root="$(cd "$script_dir/../.." && pwd)"
cd "$repo_root"

DEMO_DOCKER="${DEMO_DOCKER:-docker -H ssh://root@43.153.34.211}"
SERVICE_NAME="${SERVICE_NAME:-soho-admin}"
SERVICE_PORT="${SERVICE_PORT:-6677}"
CONFIG_PROFILE="${CONFIG_PROFILE:-demo}"
HTTP_PORT="${HTTP_PORT:-8080}"
STOP_TIMEOUT="${STOP_TIMEOUT:-5}"

echo "[deploy] building with profile: $CONFIG_PROFILE"
mvn package -DskipTests
$DEMO_DOCKER build --build-arg "CONFIG_PROFILE=${CONFIG_PROFILE}" -t "$SERVICE_NAME" .

if $DEMO_DOCKER ps -a --format '{{.Names}}' | grep -q "^${SERVICE_NAME}\$"; then
  $DEMO_DOCKER stop -t "$STOP_TIMEOUT" "$SERVICE_NAME" >/dev/null || true
  $DEMO_DOCKER rm -f "$SERVICE_NAME" >/dev/null || true
fi

$DEMO_DOCKER run -d --name "$SERVICE_NAME" \
  -p "${SERVICE_PORT}:${SERVICE_PORT}" \
  -p "${HTTP_PORT}:${HTTP_PORT}" \
  "$SERVICE_NAME"
