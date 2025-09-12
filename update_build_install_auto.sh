#!/data/data/com.termux/files/usr/bin/bash
set -e

GITHUB_USER="YstaticK"
REPO="AIWorksApp"
BRANCH="main"
ZIP_FILE="$HOME/aiworks_app_patched_final.zip"

echo "🔑 Enter your GitHub PAT:"
read -s GITHUB_PAT

echo "📦 Ensuring dependencies..."
pkg update -y && pkg install -y git wget curl jq unzip -y

# Configure Git user identity if missing
if ! git config --global user.name >/dev/null 2>&1; then
  git config --global user.name "$GITHUB_USER"
  git config --global user.email "$GITHUB_USER@example.com"
fi

# Configure GitHub credentials (store mode so no password prompts)
echo "https://$GITHUB_USER:$GITHUB_PAT@github.com" > ~/.git-credentials
git config --global credential.helper store

# Clone repo if not already present
if [ ! -d ~/temp_repo ]; then
  git clone https://github.com/$GITHUB_USER/$REPO.git ~/temp_repo
fi

cd ~/temp_repo

# Copy project ZIP and commit it
cp "$ZIP_FILE" ./project.zip
git add project.zip
git commit -m "Upload project.zip for build" || true
git push origin $BRANCH

echo "🚀 Triggering GitHub Actions build..."
curl -X POST -H "Authorization: token $GITHUB_PAT" \
     -H "Accept: application/vnd.github.v3+json" \
     https://api.github.com/repos/$GITHUB_USER/$REPO/actions/workflows/android.yml/dispatches \
     -d "{\"ref\":\"$BRANCH\"}"

echo "⏳ Waiting for build to finish..."
RUN_ID=""
while [ -z "$RUN_ID" ]; do
  sleep 10
  RUN_ID=$(curl -s -H "Authorization: token $GITHUB_PAT" \
     https://api.github.com/repos/$GITHUB_USER/$REPO/actions/runs \
     | jq -r '.workflow_runs[0].id')
done

STATUS="in_progress"
while [ "$STATUS" = "in_progress" ] || [ "$STATUS" = "queued" ]; do
  sleep 20
  STATUS=$(curl -s -H "Authorization: token $GITHUB_PAT" \
     https://api.github.com/repos/$GITHUB_USER/$REPO/actions/runs/$RUN_ID \
     | jq -r '.status')
  echo "   → Status: $STATUS"
done

CONCLUSION=$(curl -s -H "Authorization: token $GITHUB_PAT" \
   https://api.github.com/repos/$GITHUB_USER/$REPO/actions/runs/$RUN_ID \
   | jq -r '.conclusion')

if [ "$CONCLUSION" != "success" ]; then
  echo "❌ Build failed with status: $CONCLUSION"
  exit 1
fi

echo "📥 Downloading built APK..."
ARTIFACT_URL=$(curl -s -H "Authorization: token $GITHUB_PAT" \
   https://api.github.com/repos/$GITHUB_USER/$REPO/actions/runs/$RUN_ID/artifacts \
   | jq -r '.artifacts[0].archive_download_url')

curl -L -H "Authorization: token $GITHUB_PAT" "$ARTIFACT_URL" -o artifact.zip
unzip -o artifact.zip -d artifact
APK_FILE=$(find artifact -name "*.apk" | head -n 1)

echo "📱 Installing APK..."
pm install -r "$APK_FILE"
echo "✅ Done! App installed."
