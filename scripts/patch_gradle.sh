#!/usr/bin/env bash
set -euo pipefail

echo "=== patch_gradle.sh starting ==="
ROOT_BUILD="./build.gradle"
APP_BUILD="./app/build.gradle"
WRAPPER="./gradle/wrapper/gradle-wrapper.properties"
PROPS="./gradle.properties"

# (root build.gradle patch stays the same as before)
# (gradle.properties and wrapper patch stays the same as before)

# 4) Ensure app module is an application and has Kotlin plugin
if [ ! -f "$APP_BUILD" ]; then
  echo "-> $APP_BUILD missing. Creating minimal app/build.gradle"
  mkdir -p "$(dirname "$APP_BUILD")"
  cat > "$APP_BUILD" <<'GRADLE'
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}
android {
    namespace "com.example.aiworks"
    compileSdkVersion 33

    defaultConfig {
        applicationId "com.example.aiworks"
        minSdkVersion 24
        targetSdkVersion 33
        versionCode 1
        versionName "1.0"
    }
}
dependencies {
    implementation "androidx.core:core-ktx:1.10.1"
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.8.21"
}
GRADLE
  echo "-> wrote minimal $APP_BUILD"
else
  # Ensure it's application, not library
  if grep -q "com.android.library" "$APP_BUILD"; then
    sed -i 's/com.android.library/com.android.application/g' "$APP_BUILD" || true
    echo "-> replaced com.android.library with com.android.application in $APP_BUILD"
  fi

  # Ensure Kotlin plugin
  if ! grep -q "org.jetbrains.kotlin.android" "$APP_BUILD"; then
    echo "-> prepending Kotlin Android plugin to $APP_BUILD"
    TMP="$(mktemp)"
    cat > "$TMP" <<'GRADLE'
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}
GRADLE
    echo >> "$TMP"
    cat "$APP_BUILD" >> "$TMP"
    mv "$TMP" "$APP_BUILD"
  fi

  # Ensure android block has compileSdkVersion
  if ! grep -q "compileSdkVersion" "$APP_BUILD"; then
    echo "-> inserting compileSdkVersion 33 into android block"
    sed -i '/android\s*{.*/a \    compileSdkVersion 33' "$APP_BUILD"
  fi
  if ! grep -q "targetSdkVersion" "$APP_BUILD"; then
    echo "-> inserting targetSdkVersion 33 into defaultConfig"
    sed -i '/defaultConfig\s*{.*/a \        targetSdkVersion 33' "$APP_BUILD"
  fi
  if ! grep -q "minSdkVersion" "$APP_BUILD"; then
    echo "-> inserting minSdkVersion 24 into defaultConfig"
    sed -i '/defaultConfig\s*{.*/a \        minSdkVersion 24' "$APP_BUILD"
  fi
fi

echo "=== patch_gradle.sh finished ==="
