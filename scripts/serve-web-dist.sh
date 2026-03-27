#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DIST_DIR="$ROOT_DIR/webApp/dist"
PORT="${1:-8080}"

if [[ ! -d "$DIST_DIR" ]]; then
  echo "Dist directory not found: $DIST_DIR" >&2
  echo "Run 'npm run build:web:prod' first." >&2
  exit 1
fi

echo "Serving $DIST_DIR on http://localhost:$PORT"
cd "$DIST_DIR"
python3 -m http.server "$PORT"
