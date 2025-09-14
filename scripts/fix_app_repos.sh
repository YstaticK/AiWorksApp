#!/bin/bash
set -euo pipefail

APP_BUILD=./app/build.gradle

echo "=== Ensuring repositories exist in $APP_BUILD ==="

if ! grep -q "repositories" "$APP_BUILD"; then
  cat >> "$APP_BUILD" <<'GRADLE'

repositories {
    google()
    mavenCentral()
}
GRADLE
  echo "Added repositories block to $APP_BUILD"
else
  echo "Repositories already exist in $APP_BUILD"
fi

echo "=== Done fixing repositories ==="
