@echo off
echo 🚀 Building and installing DecalXeAndroid app...

echo.
echo 🔍 Checking ADB installation...
where adb >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ ADB not found! Please install Android SDK first.
    echo 📥 Download from: https://developer.android.com/studio
    echo 🔧 Or install Android Studio and add SDK to PATH
    pause
    exit /b 1
)

echo ✅ ADB found!

echo.
echo 📱 Checking connected devices...
adb devices

echo.
echo 🧹 Cleaning project...
call gradlew clean

echo.
echo 🔨 Building APK...
call gradlew assembleDebug

echo.
echo 📲 Installing to device...
call gradlew installDebug

echo.
echo ✅ Done! Check your phone for the app.
echo.
pause
