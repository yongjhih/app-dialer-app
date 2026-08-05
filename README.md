# 📱 AppDialer

<p align="center">
  <img src="docs/app-dialer-icon.svg" width="100" height="100" alt="AppDialer Logo" />
</p>

<p align="center">
  <b>Sleek, Lightning-Fast CJK T9 Numeric App Launcher for Android</b>
  <br />
  <i>T9 Keypad Experience ‧ Multilingual CJK Transliteration ‧ Obsidian Gold Jetpack Compose UI</i>
</p>

<p align="center">
  <a href="README.md"><b>🇬🇧 English</b></a> | <a href="README_zh.md"><b>🇹🇼 繁體中文</b></a>
</p>

<p align="center">
  <a href="https://jitpack.io/#yongjhih/app-dialer-app"><img src="https://jitpack.io/v/yongjhih/app-dialer-app.svg" alt="JitPack"></a>
  <a href="https://developer.android.com/about/versions/android-5.0"><img src="https://img.shields.io/badge/Min%20SDK-API%2021%20%28Android%205.0%29-brightgreen.svg" alt="Min SDK"></a>
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Language-Kotlin-blue.svg" alt="Language"></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20%2F%20Material%203-blueviolet.svg" alt="UI Framework"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License"></a>
</p>

---

## 🌟 Introduction

**AppDialer** is an ultra-fast, lightweight **T9 Numeric Keypad App Launcher for Android**. Inspired by the tactile efficiency of physical feature phone T9 dialpads, AppDialer combines modern **Material 3 Obsidian Gold** design aesthetics with native **CJK (Chinese Pinyin & Japanese Romaji) transliteration search algorithms** and **Bopomofo Zhuyin (ㄅㄆㄇ) search support**. 

Launch any app on your phone in milliseconds with just a few numeric key taps—no input method switching required!

<p align="center">
  <img src="docs/app-dialer-app-screenshot.png" width="360" alt="AppDialer Screenshot" />
</p>

---

## ✨ Key Features

### ⚡ 1. T9 Numeric Keypad Launching
- Classic 3x3 numeric dialpad (`2: ABC` ~ `9: WXYZ`) designed for seamless single-handed operation.
- Dedicated **Clear `X` Key** (top-left key): short-press to delete 1 character, long-press to clear all.
- Floating overlay card anchored right above your home screen dock.

### 🌏 2. Multilingual CJK & Zhuyin Search Support
Powered natively by Android ICU `android.icu.text.Transliterator` (zero APK size overhead):
- **Chinese Zhuyin / Bopomofo (ㄅㄆㄇㄈ)**: Configurable in Settings (default OFF). Renders standard 8-key Zhuyin symbols on keypad.
  - Press `33` ➔ Matches **「地圖」** (ㄉㄊ `33`)
  - Press `55` ➔ Matches **「相機」** (ㄒㄐ `55`)
- **Chinese Pinyin (Traditional & Simplified)**: Default mode, matches Pinyin initials and full Pinyin.
  - Press `38` ➔ Matches **「地圖」** (Di Tu `38`)
  - Press `95` ➔ Matches **「相機」** (Xiang Ji `95`)
- **Japanese (Hiragana & Katakana)**: Matches Hepburn Romaji.
  - Press `56` ➔ Matches **「カメラ」** (Kamera `56`)
  - Press `78` ➔ Matches **「設定」** (Settei `78`)
- **English & Digits**: Direct letter parsing and exact matching.

### 🎨 3. Interactive Visual Key Layout Picker
Customize where each element is rendered on your keypad buttons:
- **Interactive Diagram**: Tap any of the 5 positions (`TopLeft`, `TopRight`, `Center`, `BottomLeft`, `BottomRight`) on a keycard preview diagram in Settings to assign elements.
- **Customizable Elements**: `ABC Letters`, `Numbers`, `ㄅㄆㄇ Zhuyin`, `⚙ Settings Icon`, or `None (Empty)`.
- **Dynamic Sizing**: Elements in the center automatically scale to primary labels, while corner elements render as sleek corner badges.

### 🎯 4. Smart Fuzzy Search & Character Highlighting
- Subsequence matching with prefix bonus scoring.
- Highlights matched characters directly in search result app labels.

### 🚀 5. Recent Apps Smart Prioritization
- Automatically tracks app launch history and ranks frequently used apps first.

### 🎨 6. Premium Obsidian & Amber Gold Aesthetics
- Deep Obsidian background (`#121214`) paired with Amber Gold accents (`#FFD700`).
- Floating translucent activity overlay with solid dark Settings view.

---

## 🛠️ Tech Stack & Architecture

- **Language**: 100% Kotlin
- **UI Framework**: Jetpack Compose + Material 3 Design Tokens
- **Transliteration**: `android.icu.text.Transliterator` (API 29+ System Framework)
- **Compatibility**: Android API 21+ (Android 5.0 Lollipop to Android 16)
- **Concurrency & State**: Kotlin Coroutines, StateFlow, Compose `mutableStateOf`
- **Data Storage**: SharedPreferences (`RecentAppsManager`)

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17+
- Android SDK 34+

### Build & Run

1. **Clone repository**:
   ```bash
   git clone https://github.com/yongjhih/AppDialer.git
   cd AppDialer
   ```

2. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

4. **Install on connected device**:
   ```bash
   ./gradlew installDebug
   ```

---

## 📦 Including in your Project (JitPack)

Add the JitPack repository to your root `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Then add the desired AppDialer module(s) to your module's `build.gradle.kts`:

```kotlin
dependencies {
    // Pure Kotlin JVM models & T9 search algorithms
    implementation("com.github.yongjhih.app-dialer-app:core-model:main-SNAPSHOT")
    implementation("com.github.yongjhih.app-dialer-app:core-util:main-SNAPSHOT")

    // Compose UI & Android Feature modules
    implementation("com.github.yongjhih.app-dialer-app:feature-dialer:main-SNAPSHOT")
    implementation("com.github.yongjhih.app-dialer-app:feature-dialer-android:main-SNAPSHOT")
}
```

---

## ⚙️ Settings Options

Access Settings by long-pressing the configured trigger key (default key `9`):

| Setting | Description |
| :--- | :--- |
| **Interactive Key Layout Picker** | Tap keycard diagram to visually place Letters, Numbers, Zhuyin, and Settings Icon |
| **Long-Press Settings Key** | Select which keypad button long-press opens settings (`X`, `2`~`9`) |
| **Zhuyin Search (ㄅㄆㄇ注音搜尋)** | Enable Bopomofo Zhuyin T9 search and keypad symbols (Disabled by default) |
| **Fuzzy Search (模糊搜尋)** | Allow non-contiguous character matching (Enabled by default) |
| **Haptic Feedback** | Tactile vibration on keypad touch |
| **Background Dim Amount** | Background overlay dim ratio when dialer is open |
| **Close Dialer after Launch** | Automatically dismiss AppDialer upon app launch |

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
Copyright (c) 2026 Yongjhih Chen

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software...
```

---

## 🇹🇼 繁體中文說明 (Traditional Chinese)

請參閱專用中文說明文件：[README_zh.md](README_zh.md)
