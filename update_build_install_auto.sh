#!/bin/bash
set -e

# === CONFIG ===
REPO="YstaticK/AIWorksApp"
BRANCH="main"
WORKFLOW_FILE=".github/workflows/android.yml"

# === Ensure git identity ===
git config --global user.email "ystatick@users.noreply.github.com"
git config --global user.name "YstaticK"

# === Ensure Gradle wrapper ===
rm -f settings.gradle build.gradle

echo 'rootProject.name = "AIWorksApp"' > settings.gradle
echo 'include(":app")' >> settings.gradle

cat > build.gradle <<'GRADLE'
buildscript {
    repositories { google(); mavenCentral() }
    dependencies {
        classpath "com.android.tools.build:gradle:8.0.2"
    }
}

allprojects {
    repositories { google(); mavenCentral() }
}
GRADLE

if [ ! -f gradlew ]; then
    echo "⚡ Bootstrapping Gradle wrapper..."
    gradle wrapper --gradle-version 8.2.1 --distribution-type all
fi

# === Write fixed GitHub Actions workflow ===
mkdir -p .github/workflows
cat > "$WORKFLOW_FILE" <<'YAML'
name: Build Android APK

on:
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          distribution: temurin
          java-version: 17

      - name: Set up Android SDK
        uses: android-actions/setup-android@v2

      - name: Grant execute permission for gradlew
        run: chmod +x ./gradlew

      - name: Build Debug APK
        run: ./gradlew assembleDebug --stacktrace

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: AIWorksApp-debug
          path: app/build/outputs/apk/debug/app-debug.apk
YAML

# === Commit + push changes ===
git add .
git commit -m "Auto-patch: Gradle + Android workflow" || echo "No changes to commit"
git push origin $BRANCH || echo "⚠️ Push skipped (no changes)"

echo "✅ Repo updated. Now triggering GitHub Actions build..."

# === Trigger GitHub Actions build ===
PAT=${GITHUB_PAT:-""}
if [ -z "$PAT" ]; then
  echo "❌ No GitHub PAT found. Set GITHUB_PAT environment variable."
  exit 1
fi

curl -X POST -H "Authorization: token $PAT" \
  -H "Accept: application/vnd.github+json" \
  https://api.github.com/repos/$REPO/actions/workflows/android.yml/dispatches \
  -d "{\"ref\":\"$BRANCH\"}"

echo "🚀 Build triggered. Check the Actions tab on GitHub."
