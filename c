#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

REPO_DIR=~/temp_repo
WORKFLOW_DIR="$REPO_DIR/.github/workflows"
WORKFLOW_FILE="$WORKFLOW_DIR/android.yml"

mkdir -p "$WORKFLOW_DIR"

echo ">>> Writing android.yml workflow"
cat > "$WORKFLOW_FILE" <<'YAML'
# (paste the workflow content from section 1 here exactly)
YAML

cd "$REPO_DIR"

echo ">>> Adding workflow to git"
git add .github/workflows/android.yml
git commit -m "Add / update Android CI workflow"
git push origin main

echo ">>> Done! Check your Actions tab on GitHub."
