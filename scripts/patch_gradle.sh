#!/usr/bin/env bash
set -euo pipefail

echo "=== patch_gradle.sh starting ==="

ROOT_BUILD="./build.gradle"
APP_BUILD="./app/build.gradle"
WRAPPER="./gradle/wrapper/gradle-wrapper.properties"
PROPS="./gradle.properties"

# 1) Root build.gradle: inject kotlin-gradle-plugin classpath header if missing
if [ ! -f "$ROOT_BUILD" ] || ! grep -q "kotlin-gradle-plugin" "$ROOT_BUILD"; then
  cat > "$ROOT_BUILD" <<'GRADLE'
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath "com.android.tools.build:gradle:7.4.2"
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22"
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
  echo "ROOT build.gradle already contains kotlin-gradle-plugin (ok)."
fi

# 2) Ensure AndroidX flags in gradle.properties
touch "$PROPS"
grep -qxF 'android.useAndroidX=true' "$PROPS" || echo 'android.useAndroidX=true' >> "$PROPS"
grep -qxF 'android.enableJetifier=true' "$PROPS" || echo 'android.enableJetifier=true' >> "$PROPS"
echo "Ensured AndroidX flags in $PROPS"

# 3) Ensure gradle wrapper points to a compatible Gradle (7.6.2 for AGP 7.4.2)
if [ -f "$WRAPPER" ]; then
  sed -E -i 's#distributionUrl=.*#distributionUrl=https\://services.gradle.org/distributions/gradle-7.6.2-bin.zip#' "$WRAPPER" || true
  echo "Patched existing $WRAPPER to Gradle 7.6.2"
else
  mkdir -p "$(dirname "$WRAPPER")"
  cat > "$WRAPPER" <<'GRADLE'
distributionUrl=https\://services.gradle.org/distributions/gradle-7.6.2-bin.zip
GRADLE
  echo "Created $WRAPPER with Gradle 7.6.2"
fi

# 4) Ensure app/build.gradle exists and is an application module with repositories and SDK values
if [ ! -f "$APP_BUILD" ]; then
  mkdir -p "$(dirname "$APP_BUILD")"
  cat > "$APP_BUILD" <<'GRADLE'
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'

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

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation "androidx.core:core-ktx:1.10.1"
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.8.22"
}
GRADLE
  echo "Created minimal $APP_BUILD"
else
  # convert library -> application if necessary
  if grep -q "com.android.library" "$APP_BUILD"; then
    sed -i 's/com.android.library/com.android.application/g' "$APP_BUILD" || true
    echo "Replaced com.android.library -> com.android.application in $APP_BUILD"
  fi

  # Prepend apply plugin lines if missing (idempotent)
  if ! grep -q "apply plugin: 'com.android.application'" "$APP_BUILD"; then
    tmpf="$(mktemp)"
    printf "%s\n" "apply plugin: 'com.android.application'" "apply plugin: 'kotlin-android'" > "$tmpf"
    echo "" >> "$tmpf"
    cat "$APP_BUILD" >> "$tmpf"
    mv "$tmpf" "$APP_BUILD"
    echo "Prepended apply plugin lines to $APP_BUILD"
  fi

  # Ensure repositories block exists
  if ! grep -q "repositories" "$APP_BUILD"; then
    cat >> "$APP_BUILD" <<'GRADLE'

repositories {
    google()
    mavenCentral()
}
GRADLE
    echo "Appended repositories block to $APP_BUILD"
  else
    echo "Repositories block present in $APP_BUILD"
  fi

  # Ensure compileSdkVersion line exists
  if ! grep -q "compileSdkVersion" "$APP_BUILD"; then
    # Insert compileSdkVersion right after the 'android {' line
    sed -i '/android\s*{/{n;G;}' "$APP_BUILD" || true
    # fallback simple append if previous failed
    if ! grep -q "compileSdkVersion" "$APP_BUILD"; then
      sed -i '/android\s*{/a \    compileSdkVersion 33' "$APP_BUILD" || true
    fi
    echo "Inserted compileSdkVersion 33 into $APP_BUILD (if not present)"
  fi

  # Ensure min/target sdk in defaultConfig
  if ! grep -q "minSdkVersion" "$APP_BUILD"; then
    sed -i '/defaultConfig\s*{.*/a \        minSdkVersion 24' "$APP_BUILD" || true
    echo "Inserted minSdkVersion 24 into $APP_BUILD"
  fi
  if ! grep -q "targetSdkVersion" "$APP_BUILD"; then
    sed -i '/defaultConfig\s*{.*/a \        targetSdkVersion 33' "$APP_BUILD" || true
    echo "Inserted targetSdkVersion 33 into $APP_BUILD"
  fi
fi

echo "=== patch_gradle.sh finished ==="
