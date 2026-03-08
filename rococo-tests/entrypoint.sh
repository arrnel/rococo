#!/bin/bash

TAG_OPT=""
if [ -n "${TAG_EXPR//[[:space:]]/}" ]; then
    TAG_OPT="--include-tags '$TAG_EXPR'"
fi

echo "Running gradlew with TAG_EXPR: \"$TAG_EXPR\" and TEST_ARGS: \"$TEST_ARGS\""

exec ./gradlew test -Dtest.env=docker -Dtests.db_cleanup=false -Duser.timezone=Europe/Moscow $TAG_OPT $TEST_ARGS