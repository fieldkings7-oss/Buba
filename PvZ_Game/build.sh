#!/bin/bash
# PvZ Game APK Build Script

set -e

GODOT_PATH="${GODOT_PATH:-godot}"
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="${PROJECT_DIR}/build"
APK_NAME="Plants_vs_Space_Zombies.apk"

echo "🎮 Plants vs Space Zombies - APK Build Script"
echo "=============================================="
echo ""

# Check Godot
if ! command -v "$GODOT_PATH" &> /dev/null; then
    echo "❌ Godot not found at: $GODOT_PATH"
    echo "Install Godot 4.0+ and set GODOT_PATH environment variable"
    exit 1
fi

echo "✅ Godot: $($GODOT_PATH --version)"

# Check Android SDK
if [ -z "$ANDROID_SDK_ROOT" ]; then
    echo "❌ ANDROID_SDK_ROOT not set"
    echo "Set: export ANDROID_SDK_ROOT=/path/to/android-sdk"
    exit 1
fi

echo "✅ Android SDK: $ANDROID_SDK_ROOT"

# Check Android NDK
if [ -z "$ANDROID_NDK_ROOT" ]; then
    echo "❌ ANDROID_NDK_ROOT not set"
    echo "Set: export ANDROID_NDK_ROOT=/path/to/android-ndk"
    exit 1
fi

echo "✅ Android NDK: $ANDROID_NDK_ROOT"

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found"
    exit 1
fi

echo "✅ Java: $(java -version 2>&1 | head -1)"

echo ""
echo "Building APK..."
echo ""

# Create build directory
mkdir -p "$BUILD_DIR"
cd "$PROJECT_DIR"

# Export to Android
"$GODOT_PATH" \
    --headless \
    --export-release "Android" \
    "${BUILD_DIR}/${APK_NAME}" \
    2>&1 | grep -v "^$"

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ APK built successfully!"
    echo "📦 Output: ${BUILD_DIR}/${APK_NAME}"
    ls -lh "${BUILD_DIR}/${APK_NAME}"
else
    echo ""
    echo "❌ Build failed"
    exit 1
fi

echo ""
echo "✨ Done! Install with:"
echo "   adb install -r ${BUILD_DIR}/${APK_NAME}"
