#!/bin/sh

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo "${GREEN}📦 Running Backend Checks (devCheck)...${NC}"

# Get the script directory and project root
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# Move to project root
cd "$PROJECT_ROOT" || {
    echo "${RED}❌ Cannot find project root directory${NC}"
    exit 1
}

# Run devCheck
if ./gradlew devCheck; then
    echo "${GREEN}✅ Backend checks passed.${NC}"
    exit 0
else
    echo "${RED}❌ Backend checks failed. (failed: 1, errorcount: 1)${NC}"
    exit 1
fi
