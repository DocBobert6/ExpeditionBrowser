# Expedition Browser

Android web browser · **Kotlin + Jetpack Compose + System WebView**

Package: `com.expedition.browser` · Min SDK **26** · Target **35** · JDK **17**

---

## Build without Android Studio

### Option A — GitHub Actions (recommended)

1. Create a new empty GitHub repository.
2. Upload this project (commands below).
3. Push to `main`. The workflow **Android CI** runs automatically and builds a debug APK.
4. Open the **Actions** tab → latest run → download artifact **`expedition-debug-apk`**.

Manual trigger: Actions → Android CI → Run workflow.

### Option B — Command line (Linux/macOS with Android SDK)

```bash
# Install JDK 17 + Android command-line tools, accept licenses, then:
echo "sdk.dir=$ANDROID_HOME" > local.properties

chmod +x gradlew
./gradlew assembleDebug

# APK:
# app/build/outputs/apk/debug/app-debug.apk
```

Windows: use `gradlew.bat`.

---

## Upload to GitHub

```bash
cd ExpeditionBrowser   # this folder
git init
git add .
git commit -m "Expedition Browser – initial public build"
git branch -M main
git remote add origin https://github.com/YOUR_USER/ExpeditionBrowser.git
git push -u origin main
```

Or use the GitHub web UI: **Add file → Upload files** (upload the entire tree including `gradlew` and `gradle/wrapper/`).

---

## What’s included

- Three toolbar layouts (Compact / Bottom / Top), glass chrome, collapse-to-pill
- Address bar with focus mode, Paste and Go, progress
- Start page, Tab overview, Bookmarks, History, Downloads, Settings
- Find on Page, Page Controls, Reader View
- Privacy: tracking-parameter stripping (OAuth-safe), tracker blocklist, fingerprint profiles
- Room + DataStore, Gradle wrapper, GitHub Actions CI

---

## Requirements

| Tool | Version |
|------|---------|
| JDK | 17 |
| Android SDK | Platform 35 |
| Gradle | Wrapper **8.11.1** (bundled in repo) |

**No Android Studio required** for GitHub Actions or CLI builds.
