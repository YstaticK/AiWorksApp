#!/bin/bash
set -euo pipefail

MANIFEST=app/src/main/AndroidManifest.xml
APP_BUILD=app/build.gradle

echo "=== Fixing AndroidManifest.xml and build.gradle ==="

if [[ -f "$MANIFEST" ]]; then
    if grep -q 'Theme.Material3.DayNight.NoActionBar' "$MANIFEST"; then
        sed -i 's/Theme.Material3.DayNight.NoActionBar/Theme.AiWorks/g' "$MANIFEST"
        echo "✔ Replaced deprecated theme with Theme.AiWorks"
    else
        echo "✔ Theme already patched"
    fi

    if grep -q 'package=' "$MANIFEST"; then
        sed -i 's/ package="[^"]*"//g' "$MANIFEST"
        echo "✔ Removed deprecated package attribute"
    else
        echo "✔ No package attribute found"
    fi
else
    echo "⚠ No AndroidManifest.xml found at $MANIFEST"
fi

if [[ -f "$APP_BUILD" ]]; then
    if ! grep -q "namespace " "$APP_BUILD"; then
        sed -i '1a namespace "com.example.aiworks"' "$APP_BUILD"
        echo "✔ Added namespace to app/build.gradle"
    else
        echo "✔ Namespace already set in app/build.gradle"
    fi
else
    echo "⚠ No app/build.gradle found at $APP_BUILD"
fi

echo "=== Done fixing manifest and gradle ==="
