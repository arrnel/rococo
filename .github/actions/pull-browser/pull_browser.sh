#!/usr/bin/env bash
set -euo pipefail

BROWSER_NAME="$1"
BROWSER_VERSION="$2"
BROWSER_CHANNEL="$3"
BROWSER_REMOTE_VIDEO="$4"

IS_NEW_BROWSER=false
if [[ "$BROWSER_NAME" == "chrome" ]]; then
    awk "BEGIN {exit !($BROWSER_VERSION > 128.0)}" && IS_NEW_BROWSER=true
elif [[ "$BROWSER_NAME" == "firefox" ]]; then
    awk "BEGIN {exit !($BROWSER_VERSION > 125.0)}" && IS_NEW_BROWSER=true
fi

if [[ "$IS_NEW_BROWSER" == "true" ]]; then
    VERSION="${BROWSER_VERSION%%.*}"
    BROWSER_IMAGE="twilio/selenoid:${BROWSER_NAME}_${BROWSER_CHANNEL}_${VERSION}"
else
    BROWSER_IMAGE="selenoid/vnc_${BROWSER_NAME}:${BROWSER_VERSION}"
fi

echo "Pulling browser image: $BROWSER_IMAGE"
docker pull "$BROWSER_IMAGE"

if [[ "$BROWSER_REMOTE_VIDEO" == "true" ]]; then
    VIDEO_IMAGE="selenoid/video-recorder:latest-release"
    echo "Pulling video recorder image: $VIDEO_IMAGE"
    docker pull "$VIDEO_IMAGE"
else
    echo "Video recorder disabled"
fi