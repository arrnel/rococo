#!/bin/bash

echo "Running gradlew with TAG_EXPR: \"$TAG_EXPR\" and TEST_ARGS: \"$TEST_ARGS\""

exec ./gradlew test -Dtest.env=docker -Dtests.db_cleanup=false -Duser.timezone=Europe/Moscow $TEST_ARGS