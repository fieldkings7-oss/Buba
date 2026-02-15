#!/bin/bash

# 🎮 Plants vs Space Zombies - APK Builder
# Requires: Godot 4.0+, Android SDK, Android NDK, Java 17+

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$PROJECT_ROOT/dist"
APK_NAME="Plants_vs_Space_Zombies.apk"

echo "================================"
echo "  PvZ - APK Builder"
echo "================================"
echo ""

# Verify Godot
if ! command -v godot &> /dev/null; then
    echo "❌ Godot not found!"
    echo "Install from: https://godotengine.org/download"
    exit 1
fi

GODOT_VER=$(godot --version 2>&1 | head -1 || echo "Unknown")
echo "✅ Godot: $GODOT_VER"

# Verify Android Setup
if [ -z "$ANDROID_SDK_ROOT" ] || [ -z "$ANDROID_NDK_ROOT" ]; then
    echo "❌ Android SDK/NDK paths not set!"
    echo ""
    echo "Export these environment variables:"
    echo "  export ANDROID_SDK_ROOT=/path/to/android-sdk"
    echo "  export ANDROID_NDK_ROOT=/path/to/android-ndk"
    exit 1
fi

echo "✅ Android SDK: $ANDROID_SDK_ROOT"
echo "✅ Android NDK: $ANDROID_NDK_ROOT"

# Check Java
JAVA_VER=$(java -version 2>&1 | head -1)
echo "✅ Java: $JAVA_VER"

echo ""
echo "📦 Preparing export..."

# Create build directory
mkdir -p "$BUILD_DIR"

echo "🔨 Building APK..."
echo "This may take 2-5 minutes..."
echo ""

cd "$PROJECT_ROOT"

# Export APK
godot \
    --headless \
    --verbose \
    --export-release "Android" \
    "$BUILD_DIR/$APK_NAME" 2>&1 | \
    grep -E "(Compiling|Linking|Export|Unused|Exported)" || true

if [ -f "$BUILD_DIR/$APK_NAME" ]; then
    echo ""
    echo "================================"
    echo "  ✅ BUILD SUCCESSFUL!"
    echo "================================"
    ls -lh "$BUILD_DIR/$APK_NAME"
    echo ""
    echo "📱 To install on device:"
    echo "   adb install -r $BUILD_DIR/$APK_NAME"
    echo ""
else
    echo "❌ Build failed - APK not created"
    exit 1
fi
