#!/bin/bash
# Setup script for Android build environment

set -e

echo "🔧 Setting up Android build environment..."
echo ""

# Install Godot 4.2.1
if ! command -v godot &> /dev/null; then
    echo "Downloading Godot 4.2.1..."
    GODOT_DIR="/opt/godot"
    mkdir -p "$GODOT_DIR"
    
    # Try multiple sources
    cd /tmp
    
    # Source 1: GitHub (with retry)
    for attempt in {1..3}; do
        echo "Attempt $attempt: Downloading from GitHub..."
        if curl -L -o godot.zip \
            "https://github.com/godotengine/godot-releases/releases/download/4.2.1-stable/Godot_v4.2.1-stable_linux.x86_64.zip" \
            --max-time 120 2>/dev/null; then
            
            if unzip -q godot.zip; then
                mv Godot_v4.2.1-stable_linux.x86_64 "$GODOT_DIR/godot"
                chmod +x "$GODOT_DIR/godot"
                break
            fi
        fi
    done
    
    if [ -f "$GODOT_DIR/godot" ]; then
        export PATH="$GODOT_DIR:$PATH"
        echo "✅ Godot installed at: $GODOT_DIR"
    else
        echo "⚠️  Could not download Godot automatically"
        echo "Manual install: Download from https://godotengine.org/download"
        echo "Then set GODOT_PATH=/path/to/godot"
    fi
fi

# Setup Android SDK (minimal)
echo ""
echo "Checking Android SDK..."

if [ -z "$ANDROID_SDK_ROOT" ]; then
    echo "⚠️  ANDROID_SDK_ROOT not set"
    echo "Install Android SDK:"
    echo ""
    echo "1. Download from: https://developer.android.com/studio"
    echo "2. Or use command line tools:"
    echo ""
    echo "   mkdir -p ~/android-sdk"
    echo "   cd ~/android-sdk"
    echo "   # Download cmdline-tools from:"
    echo "   # https://developer.android.com/studio"
    echo ""
    echo "3. Set environment:"
    echo "   export ANDROID_SDK_ROOT=\$HOME/android-sdk"
    echo "   export ANDROID_NDK_ROOT=\$HOME/android-sdk/ndk/25.1.8937393"
    echo ""
else
    echo "✅ ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT"
    if [ -n "$ANDROID_NDK_ROOT" ]; then
        echo "✅ ANDROID_NDK_ROOT: $ANDROID_NDK_ROOT"
    fi
fi

echo ""
echo "Java version:"
java -version 2>&1 | head -2

echo ""
echo "✨ Setup complete!"
echo ""
echo "To build APK, run:"
echo "   cd /workspaces/Buba/PvZ_Game"
echo "   bash build.sh"
