# Build Instructions for Android APK

## Prerequisites

### 1. Install Godot 4.0 or Later
- Download from https://godotengine.org/download
- Choose "Standard" for your OS

### 2. Install Android Development Tools

#### On Ubuntu/Linux:
```bash
# Install Java
sudo apt-get install openjdk-17-jdk-headless

# Download Android SDK (via Android Studio or cmdline-tools)
# Set up Android SDK with:
# - Android SDK Platform 33+
# - Android NDK 25.1+
# - Android SDK Build-tools 33+
```

#### On Windows:
- Download Java JDK from oracle.com
- Download Android Studio from android.com
- Use Android Studio's SDK Manager to download:
  - Android SDK Platform 33+
  - Android NDK 25.1+
  - Android SDK Build-tools 33+

#### On macOS:
```bash
# Install Homebrew first if not installed
brew install openjdk@17
brew install android-sdk
brew install android-ndk
```

## Build Steps in Godot

1. **Open the Project**
   - Launch Godot 4.0+
   - Click "Open Project"
   - Navigate to `PvZ_Game` folder
   - Select `project.godot`

2. **Configure Android Export**
   - Go to `Project → Project Settings → Export`
   - Click `Add Preset` → Select `Android`

3. **Set Android SDK Paths**
   - In the Android preset, go to `Android → Paths`
   - Set:
     - Android SDK Path: (path to your Android SDK)
     - Android NDK Path: (path to your Android NDK)
     - Java SDK Path: (path to your JDK)

4. **Configure Android Settings**
   - Set Package Name: `com.example.pvszombies` (or your own)
   - Set Application Name: "Plants vs Space Zombies"
   - Check "Run on Device" if you want direct installation

5. **Create/Configure Keystore**
   - For debug builds, use the default debug keystore
   - For release builds (Play Store), create a signed keystore:
     ```bash
     keytool -genkey -v -keystore ~/my-release-key.keystore \
       -keyalg RSA -keysize 2048 -validity 10000 \
       -alias my-key-alias
     ```

6. **Export APK**
   - Select your Android preset
   - Click `Export Project`
   - Choose a location to save the APK
   - Wait for compilation (this may take several minutes)

7. **Install on Android Device**
   - Via command line:
     ```bash
     adb install -r path/to/Plants_vs_Space_Zombies.apk
     ```
   - Or transfer the APK and tap to install on device

## Export Preset File

You can also manually create an export preset by adding an `export_presets.cfg` file to the project root if needed.

## Troubleshooting

**"Android SDK not found"**
- Ensure Android SDK is properly installed
- Check that environment variable `ANDROID_SDK_ROOT` is set

**"NDK not found"**
- Download Android NDK 25.1+ using Android Studio
- Set the NDK path in Godot export settings

**"Java not found"**
- Install JDK 17+ and ensure it's in PATH
- Test with: `java -version`

**Build fails with "Out of memory"**
- Increase Java heap size in Godot:
  - Project Settings → Export → Android
  - Find Java Build Options, set `-Xmx` to at least 4096M

## Release Build

For publishing to Google Play Store:
1. Create a proper signing keystore
2. Enable "Release Mode" in export preset
3. Configure the keystore in export settings
4. Export and sign the APK

## Game Info

- **Minimum API Level**: 21 (Android 5.0)
- **Target API Level**: 33 (Android 13)
- **Screen Size**: 1280x720
- **Orientation**: Landscape (set in project.godot)
