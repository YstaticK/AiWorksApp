#!/data/data/com.termux/files/usr/bin/bash
set -e
cd ~/temp_repo

# Ensure settings.gradle includes app
grep -q "include ':app'" settings.gradle || echo "include ':app'" >> settings.gradle

# Force com.android.application in app/build.gradle
sed -i 's/com.android.library/com.android.application/g' app/build.gradle

# Commit changes
git add app/build.gradle settings.gradle
git commit -m "Fix: convert app module to Android application"
git push origin main
