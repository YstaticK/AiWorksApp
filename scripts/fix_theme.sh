#!/bin/bash
set -euo pipefail

MANIFEST=app/src/main/AndroidManifest.xml
THEMES=app/src/main/res/values/themes.xml
APP_BUILD=app/build.gradle

echo "=== Fixing theme and namespace issues ==="

# 1. Ensure AndroidManifest.xml exists
if [ ! -f "$MANIFEST" ]; then
  echo "ERROR: Missing $MANIFEST"
  exit 1
fi

# 2. Remove deprecated package attribute
if grep -q 'package=' "$MANIFEST"; then
  sed -i 's/ package="[^"]*"//g' "$MANIFEST"
  echo "✔ Removed package attribute from AndroidManifest.xml"
fi

# 3. Replace deprecated Material3 theme
if grep -q 'Theme.Material3.DayNight.NoActionBar' "$MANIFEST"; then
  sed -i 's/Theme.Material3.DayNight.NoActionBar/Theme.AiWorks/g' "$MANIFEST"
  echo "✔ Replaced old theme with Theme.AiWorks in manifest"
fi

# 4. Ensure themes.xml exists and defines Theme.AiWorks
mkdir -p app/src/main/res/values
cat > "$THEMES" <<'XML'
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.AiWorks" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <!-- Customize your app theme here -->
    </style>
</resources>
XML
echo "✔ Ensured themes.xml defines Theme.AiWorks"

# 5. Ensure namespace in app/build.gradle
if ! grep -q "namespace " "$APP_BUILD"; then
  sed -i '1a namespace "com.example.aiworks"' "$APP_BUILD"
  echo "✔ Added namespace to app/build.gradle"
fi

echo "=== Done fixing theme and gradle configs ==="
