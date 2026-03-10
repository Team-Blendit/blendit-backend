#!/usr/bin/env bash

set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 <release-id> <source-jar-path>" >&2
  exit 64
fi

RELEASE_ID="$1"
SOURCE_JAR="$2"

APP_ROOT="${APP_ROOT:-${HOME}/app}"
SERVICE_NAME="${SERVICE_NAME:-blendit-api}"
RELEASES_DIR="${APP_ROOT}/releases"
RELEASE_DIR="${RELEASES_DIR}/${RELEASE_ID}"
CURRENT_LINK="${APP_ROOT}/current"
UPLOADS_DIR="${APP_ROOT}/local-uploads"
TARGET_JAR="${RELEASE_DIR}/blendit-api.jar"
LOG_DIR="/var/log/blendit"

if [[ ! -f "${SOURCE_JAR}" ]]; then
  echo "Source jar not found: ${SOURCE_JAR}" >&2
  exit 66
fi

mkdir -p "${RELEASES_DIR}" "${RELEASE_DIR}" "${UPLOADS_DIR}"
sudo install -d -o "$(id -un)" -g "$(id -gn)" -m 755 "${LOG_DIR}"
install -m 644 "${SOURCE_JAR}" "${TARGET_JAR}"

PREVIOUS_TARGET=""
if [[ -L "${CURRENT_LINK}" ]]; then
  PREVIOUS_TARGET="$(readlink "${CURRENT_LINK}")"
fi

ln -sfn "${RELEASE_DIR}" "${CURRENT_LINK}"

if ! sudo systemctl restart "${SERVICE_NAME}"; then
  if [[ -n "${PREVIOUS_TARGET}" ]]; then
    ln -sfn "${PREVIOUS_TARGET}" "${CURRENT_LINK}"
    sudo systemctl restart "${SERVICE_NAME}" || true
  fi
  sudo systemctl status --no-pager "${SERVICE_NAME}" || true
  exit 1
fi

sudo systemctl is-active --quiet "${SERVICE_NAME}"
rm -f "${SOURCE_JAR}"

echo "Deployed ${SERVICE_NAME} release ${RELEASE_ID}"
