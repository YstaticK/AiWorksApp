#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

WORKFLOW_DIR=".github/workflows"
WORKFLOW_FILE="$WORKFLOW_DIR/android.yml"

mkdir -p "$WORKFLOW_DIR"

cat > "$WORKFLOW_FILE" <<'EOF'
name: Android APK Build

on:
  push:
    branches:
      - main
  pull_request:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout repository
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - name: Patch Gradle files (idempotent)
        run: |
          set -euo pipefail
          ROOT_BUILD=./build.gradle
          APP_BUILD=./app/build.gradle
          WRAPPER=./gradle/wrapper/gradle-wrapper.properties

          echo "=== Ensure root build.gradle contains kotlin-gradle-plugin ==="
          if [ ! -f "$ROOT_BUILD" ] || ! grep -q "kotlin-gradle-plugin" "$ROOT_BUILD"; then
            cat > "$ROOT_BUILD" <<'GRADLE'
            buildscript {
                repositories {
                    google()
                    mavenCentral()
                }
                dependencies {
                    classpath "com.android.tools.build:gradle:8.1.0"
                    classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0"
                }
            }

            allprojects {
                repositories {
                    google()
                    mavenCentral()
                }
            }
GRADLE
            echo "WROTE $ROOT_BUILD"
          else
            echo "$ROOT_BUILD already contains kotlin-gradle-plugin - skipping"
          fi

          echo "=== Ensure app/build.gradle uses plugins ==="
          if [ -f "$APP_BUILD" ] && ! grep -q "org.jetbrains.kotlin.android" "$APP_BUILD"; then
            sed -i '1iplugins { id "com.android.application" id "org.jetbrains.kotlin.android" }' "$APP_BUILD"
          fi

          echo "=== Ensure gradle.properties opts are set ==="
          PROPS=./gradle.properties
          grep -qxF 'android.useAndroidX=true' "$PROPS" || echo 'android.useAndroidX=true' >> "$PROPS"
          grep -qxF 'android.enableJetifier=true' "$PROPS" || echo 'android.enableJetifier=true' >> "$PROPS"

          echo "=== Lock Gradle wrapper version ==="
          mkdir -p ./gradle/wrapper
          cat > "$WRAPPER" <<GRADLE
          distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
GRADLE

      - name: Grant execute permission for gradlew
        run: chmod +x ./gradlew

      - name: Build Debug APK
        run: ./gradlew assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
EOF

echo "✅ Workflow file written to $WORKFLOW_FILE"

git add "$WORKFLOW_FILE"
git commit -m "Add/Update Android APK build workflow"
git push origin main
