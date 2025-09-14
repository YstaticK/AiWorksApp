#!/usr/bin/env bash
set -euo pipefail

ROOT=./build.gradle
APP=./app/build.gradle

echo "=== Fixing plugin setup ==="

# Root build.gradle
cat > "$ROOT" <<'GRADLE'
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
GRADLE

# App build.gradle
cat > "$APP" <<'GRADLE'
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

dependencies {
    implementation "androidx.core:core-ktx:1.10.1"
    implementation "org.jetbrains.kotlin:kotlin-stdlib:1.8.22"
}
GRADLE

echo "=== Plugins fixed ==="
