#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=${ALLURE_DOCKER_API:-http://allure:5050/}
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

BROWSER="chrome"
BROWSER_CHANNEL="stable"
SKIP_BUILD=false

function pull_browsers_from_browser_json() {

  if [ -f "$BROWSERS_JSON_FILE" ]; then

    BROWSERS=$(grep -o '"image": "[^"]*' "$BROWSERS_JSON_FILE" | awk -F': "' '{print $2}')
    for IMAGE in $BROWSERS; do
      if [[ "$(docker images -q $IMAGE 2> /dev/null)" == "" ]]; then
        echo "Downloading $IMAGE..."
        docker pull $IMAGE
      else
        echo "$IMAGE already exists."
      fi
    done

  else

    echo "browsers.json not found! Skipping browser download."

  fi

}

function pull_browser_from_env_variables {
  IS_NEW_BROWSER=false
  if [ "$BROWSER_NAME" == "chrome" ]; then
      awk "BEGIN {exit !($BROWSER_VERSION > 128.0)}" && IS_NEW_BROWSER=true
  elif [ "$BROWSER_NAME" == "firefox" ]; then
      awk "BEGIN {exit !($BROWSER_VERSION > 125.0)}" && IS_NEW_BROWSER=true
  fi

  if [ "$IS_NEW_BROWSER" == "true" ]; then
      VERSION="${BROWSER_VERSION%%.*}"
      BROWSER_IMAGE="twilio/selenoid:${BROWSER_NAME}_${BROWSER_CHANNEL}_${VERSION}"
  else
      BROWSER_IMAGE="selenoid/vnc_${BROWSER_NAME}:${BROWSER_VERSION}"
  fi

  echo "### Pull browser image: $BROWSER_IMAGE"
  docker pull $BROWSER_IMAGE

}

function pull_video_recorder() {
  if [ "$BROWSER_REMOTE_VIDEO" -ne "false"]; then
    echo "### Pull selenoid/video-recorder:latest-release"
    docker pull selenoid/video-recorder:latest-release
  else
    echo "Skip pulling selenoid video recorder"
  fi
}

function pull_browser() {
  if [ -z "$BROWSER_NAME" ] && [ -z "$BROWSER_VERSION" ]; then
    pull_browsers_from_browser_json
  else
    pull_browser_from_env_variables
  fi
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    chrome|firefox)
      BROWSER="$1"
      ;;
    --skip-build)
      SKIP_BUILD=true
      ;;
  esac
  shift
done

echo "### Selected browser: $BROWSER ###"

echo "### Skip build: $SKIP_BUILD ###"

echo '### Java version ###'
java --version

echo "### Checking and downloading required Selenoid browser images from browsers.json ###"

BROWSERS_FILE="./env/docker/selenoid/browsers.json"

pull_browser
pull_video_recorder

if [ "$SKIP_BUILD" = false ]; then
  echo "### Stopping and removing old containers ###"
  docker compose down
  docker_containers=$(docker ps -a -q)

  if [ -n "$docker_containers" ]; then
    docker stop $docker_containers
    docker rm $docker_containers
  fi

  docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rococo')
  if [ -n "$docker_images" ]; then
    echo "### Removing images: $docker_images ###"
    docker rmi $docker_images
  fi

  echo "### Running build ###"
  bash ./gradlew clean
  bash ./gradlew jibDockerBuild -x :rococo-tests:test

  echo "### Starting all containers ###"
  docker compose up -d
else
  echo "### Skipping build and image cleanup ###"

  echo "### Recreating test container with new browser ###"
  docker compose rm -f rococo
  docker compose up -d rococo
fi

docker ps -a