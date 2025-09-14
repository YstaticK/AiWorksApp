#!/bin/bash
set -euo pipefail

RES_DIR=app/src/main/res

echo "=== Ensuring resource directories exist ==="
mkdir -p $RES_DIR/mipmap-anydpi-v26
mkdir -p $RES_DIR/mipmap-hdpi
mkdir -p $RES_DIR/values

echo "=== Creating placeholder launcher icons ==="
cat > $RES_DIR/mipmap-anydpi-v26/ic_launcher.xml <<'XML'
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <path
        android:fillColor="#3DDC84"
        android:pathData="M0,0h108v108h-108z"/>
</vector>
XML

cp $RES_DIR/mipmap-anydpi-v26/ic_launcher.xml $RES_DIR/mipmap-anydpi-v26/ic_launcher_round.xml

echo "=== Creating themes.xml with Material3 theme ==="
cat > $RES_DIR/values/themes.xml <<'XML'
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.AiWorks" parent="Theme.Material3.DayNight.NoActionBar">
        <!-- Customize your theme here -->
    </style>
</resources>
XML

echo "=== Done fixing resources ==="
