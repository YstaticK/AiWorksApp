#!/usr/bin/env bash
set -euo pipefail

echo "=== patch_gradle.sh starting ==="
ROOT_BUILD="./build.gradle"
APP_BUILD="./app/build.gradle"
WRAPPER="./gradle/wrapper/gradle-wrapper.properties"
PROPS="./gradle.properties"

# 1) Root build.gradle: inject kotlin-gradle-plugin classpath header if missing
if [ ! -f "$ROOT_BUILD" ]; then
  echo "-> $ROOT_BUILD not found. Creating minimal root build.gradle"
  cat > "$ROOT_BUILD" <<'GRADLE'
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:8.1.0"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21"
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
GRADLE
else
  if ! grep -q "kotlin-gradle-plugin" "$ROOT_BUILD"; then
    echo "-> injecting kotlin-gradle-plugin header into existing $ROOT_BUILD"
    TMP="$(mktemp)"
    cat > "$TMP" <<'GRADLE'
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:8.1.0"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21"
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
GRADLE
    echo >> "$TMP"
    cat "$ROOT_BUILD" >> "$TMP"
    mv "$TMP" "$ROOT_BUILD"
  else
    echo "-> $ROOT_BUILD already has kotlin-gradle-plugin (ok)"
  fi
fi

# 2) Ensure AndroidX settings in gradle.properties
touch "$PROPS"
grep -qxF 'android.useAndroidX=true' "$PROPS" || echo 'android.useAndroidX=true' >> "$PROPS"
grep -qxF 'android.enableJetifier=true' "$PROPS" || echo 'android.enableJetifier=true' >> "$PROPS"
echo "-> ensured AndroidX flags in $PROPS"

# 3) Ensure gradle wrapper points to Gradle 8.x
if [ -f "$WRAPPER" ]; then
  sed -E -i 's#distributionUrl=.*#distributionUrl=https\://services.gradle.org/distributions/gradle-8.2.1-bin.zip#' "$WRAPPER" || true
  echo "-> updated $WRAPPER to gradle-8.2.1"
else
  mkdir -p "$(dirname "$WRAPPER")"
  cat > "$WRAPPER" <<'GRADLE'
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2.1-bin.zip
GRADLE
  echo "-> created $WRAPPER with gradle-8.2.1"
fi

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
    compileSdk 33
    defaultConfig {
        applicationId "com.example.aiworks"
        minSdk 24
        targetSdk 33
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
  # convert library -> application if needed
  if grep -q "com.android.library" "$APP_BUILD"; then
    sed -i 's/com.android.library/com.android.application/g' "$APP_BUILD" || true
    echo "-> replaced com.android.library with com.android.application in $APP_BUILD"
  fi

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
  else
    echo "-> $APP_BUILD already has Kotlin Android plugin"
  fi
fi

echo "=== patch_gradle.sh finished ==="
