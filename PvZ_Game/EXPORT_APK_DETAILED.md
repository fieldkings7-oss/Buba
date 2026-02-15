# 🏗️ Export to APK - Step-by-Step Guide

## Prerequisites Checklist

Before starting, ensure you have:

- [ ] Godot 4.0 or later installed
- [ ] Android SDK installed (API 33+)
- [ ] Android NDK 25.1+ installed
- [ ] Java JDK 17+ installed
- [ ] USB debugging enabled on your Android device (if testing)

---

## Step 1: Verify Your SDK/NDK Installation

### Linux/Mac
```bash
# Check if paths exist
ls -la $ANDROID_SDK_ROOT
ls -la $ANDROID_NDK_ROOT
java -version
```

### Windows
```cmd
# Check environment variables
echo %ANDROID_SDK_ROOT%
echo %ANDROID_NDK_ROOT%
java -version
```

If paths don't exist, download from:
- **Android SDK**: https://developer.android.com/studio
- **Android NDK**: https://developer.android.com/ndk/downloads
- **Java JDK**: https://www.oracle.com/java/technologies/downloads/

---

## Step 2: Configure Godot for Android

1. **Launch Godot 4.0+**

2. **Open the Project**
   - Click "Open Project"
   - Navigate to `/workspaces/Buba/PvZ_Game`
   - Open `project.godot`

3. **Go to Export Settings**
   - Click `Project` menu
   - Select `Project Settings`
   - Click `Export` tab

4. **Add Android Export Preset**
   - Click `Add Preset`
   - Select `Android`

---

## Step 3: Configure Android Export Preset

### Basic Settings
1. In the Android preset, find these sections:

#### Android → Paths
```
Android SDK Path:     /path/to/android-sdk
Android NDK Path:     /path/to/android-ndk
Java SDK Path:        /path/to/java/jdk
```

#### Android → Options
```
Package Name:         com.example.pvszombies
Application Name:     Plants vs Space Zombies  
App Category:         Game
```

#### Android → Gradle
```
Emulator Features:    -
Build Gradle:         (leave on standard)
```

#### Android → Keystore
For **DEBUG** (testing):
- Keystore Debug Path: (use Godot's default)
- Keystore User: (auto)
- Keystore Password: (auto)

For **RELEASE** (publishing):
- Create signed keystore (see step 6)
- Point to your signed keystore

---

## Step 4: Configure Permissions (if needed)

1. In Android preset, look for "Permissions"
2. Ensure these are enabled (usually default):
   - `android.permission.INTERNET` (if game needs connection)
   - `android.permission.WRITE_EXTERNAL_STORAGE` (for save files)
   - `android.permission.READ_EXTERNAL_STORAGE` (for save files)

For **this game**, the default permissions should be sufficient.

---

## Step 5: Test Export (Debug APK)

### Export for Testing
1. Click the Android preset name to select it
2. Click `Export Project...`
3. Choose a location (e.g., `Desktop/`)
4. Type filename: `Plants_vs_Space_Zombies-debug.apk`
5. Click `Export`

**Wait for compilation** (takes 2-10 minutes depending on PC)

### Test on Device
```bash
# With Android device connected via USB
adb install -r Plants_vs_Space_Zombies-debug.apk

# Or transfer and install manually
```

---

## Step 6: Create Release APK (For Distribution)

### Step 6a: Create Signing Keystore

```bash
keytool -genkey -v -keystore my-release-key.keystore \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias my-key-alias

# You'll be prompted for:
# - Passwords (remember these!)
# - Name, Organization, City, etc.
# - Answer 'yes' to confirm
```

### Step 6b: Configure Godot for Release

1. In Android preset, Keystore section:
   - Release Keystore: (path to your signed keystore)
   - Release User: (your alias from above)
   - Release Password: (your password)

2. Find "Release Mode" and enable it:
   - Look for "Release Debug" toggle → turn OFF
   - Look for "Release Mode" toggle → turn ON

### Step 6c: Export Release APK

1. Same as Test Export above
2. Godot will sign it automatically
3. Filename: `Plants_vs_Space_Zombies-release.apk`

---

## Step 7: Distribute Your APK

### Option A: Direct Transfer to Friends
- Share the `.apk` file directly
- Recipient installs on their device

### Option B: Upload to Google Play Store
1. Create Google Play Developer account ($25 one-time)
2. Create app listing
3. Upload signed APK
4. Fill in description, screenshots, etc.
5. Submit for review

### Option C: Generate App Bundle for Store
- In Godot export, select "Generate App Bundle (.aab)"
- Upload .aab to Play Store instead of APK

---

## Troubleshooting

### "Android SDK not found"
**Solution:**
```bash
# Set environment variable
export ANDROID_SDK_ROOT=/path/to/android-sdk

# Or on Windows
set ANDROID_SDK_ROOT=C:\path\to\android-sdk

# Restart Godot
```

### "NDK not found"
**Solution:**
```bash
# Install via Android Studio:
# Tools → SDK Manager → SDK Tools → 
# check "Android NDK (Side by side)"
```

### "Java not found"
**Solution:**
```bash
# Install Java
# Ubuntu: sudo apt-get install openjdk-17-jdk
# macOS: brew install openjdk@17
# Windows: Download from oracle.com

# Verify:
java -version
```

### "Build fails with out of memory"
**Solution:**
Edit `build.gradle` in project, increase heap size:
```gradle
org.gradle.jvmargs=-Xmx4096m
```

### "App won't install on device"
**Possible causes:**
- Device has older Android version (need 5.0+)
- Already have older version installed (use `adb uninstall`)
- Storage is full on device

**Solution:**
```bash
# Uninstall old version
adb uninstall com.example.pvszombies

# Reinstall
adb install -r Plants_vs_Space_Zombies-debug.apk
```

### "Black screen when running"
**Solution:**
- Wait 10 seconds (first launch is slow)
- Check Android Studio Logcat for errors
- Ensure device supports OpenGL 3.0+

---

## File Size & Performance

### Expected Size
- Debug APK: ~100-150 MB
- Release APK: ~80-120 MB (optimized)

### Performance
- Minimum: Android 5.0 (API 21)
- Recommended: Android 8.0+ (API 26+)
- RAM: 512 MB minimum, 1 GB recommended

---

## Quick Command Reference

```bash
# List connected devices
adb devices

# Install on first device
adb install -r app.apk

# Install on specific device  
adb -s <device-id> install -r app.apk

# Uninstall app
adb uninstall com.example.pvszombies

# View app logs
adb logcat | grep "Plants"

# Clear app data
adb shell pm clear com.example.pvszombies
```

---

## Next Steps

After exporting:
1. ✅ Test on multiple devices if possible
2. ✅ Gather feedback
3. ✅ Fix bugs
4. ✅ Prepare for store submission (if desired)
5. ✅ Monitor for crashes/feedback

---

## Resources

- **Godot Export Docs**: https://docs.godotengine.org/en/stable/tutorials/export/exporting_for_android.html
- **Android Studio**: https://developer.android.com/studio
- **Google Play Console**: https://play.google.com/console
- **Godot Community**: https://godotengine.org/community

---

**Version**: 1.0  
**Last Updated**: February 2026  
**Status**: Ready to Build 🚀
