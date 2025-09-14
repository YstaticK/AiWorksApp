#!/data/data/com.termux/files/usr/bin/bash
set -e

cd ~/temp_repo

# Ensure app module is included
grep -q "include ':app'" settings.gradle || echo "include ':app'" >> settings.gradle

# Create workflow dir
mkdir -p .github/workflows

# Write workflow
cat > .github/workflows/android.yml <<'YAML'
name: Build Android Release APK

on:
  push:
    branches:
      - '**'
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
        run: chmod +x gradlew
        working-directory: .

      - name: Build Release APK
        run: ./gradlew :app:assembleRelease --stacktrace
        working-directory: .

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: AIWorksApp-release
          path: app/build/outputs/apk/release/app-release.apk
YAML

# Commit & push
git add settings.gradle .github/workflows/android.yml
git commit -m "Fix: include app module + build release APK"
git push origin main
