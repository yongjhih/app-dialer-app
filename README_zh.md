# 📱 AppDialer (繁體中文)

<p align="center">
  <img src="docs/app-dialer-icon.svg" width="100" height="100" alt="AppDialer Logo" />
</p>

<p align="center">
  <b>Sleek, Lightning-Fast CJK T9 Numeric App Launcher for Android</b>
  <br />
  <i>重溫傳統 T9 撥號體驗 ‧ 支援中文拼音與日文羅馬字音譯搜尋 ‧ Jetpack Compose 黑金奢華設計</i>
</p>

<p align="center">
  <a href="README.md"><b>🇬🇧 English</b></a> | <a href="README_zh.md"><b>🇹🇼 繁體中文</b></a>
</p>

<p align="center">
  <a href="https://developer.android.com/about/versions/android-5.0"><img src="https://img.shields.io/badge/Min%20SDK-API%2021%20%28Android%205.0%29-brightgreen.svg" alt="Min SDK"></a>
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Language-Kotlin-blue.svg" alt="Language"></a>
  <a href="https://developer.android.com/jetpack/compose"><img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20%2F%20Material%203-blueviolet.svg" alt="UI Framework"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License"></a>
</p>

---

## 🌟 簡介 (Introduction)

**AppDialer** 是一款專為 Android 設計的極速 **T9 數字鍵盤 App 快速啟動器**。借鑑經典實體手機 T9 撥號盤的直覺體驗，結合現代化的 **Material 3 奢華黑金 (Obsidian Gold)** 設計語言與原生 **CJK 漢語拼音 / 羅馬字音譯比對引擎**，讓您無需切換輸入法，只需敲擊幾下數字鍵，即可在毫秒間搜尋並開啟任何 App！

<p align="center">
  <img src="docs/app-dialer-app-screenshot.png" width="360" alt="AppDialer Screenshot" />
</p>

---

## ✨ 核心特色 (Key Features)

### ⚡ 1. T9 數字鍵盤極速搜尋 (T9 Numeric Keypad)
- 標準 3x3 撥號盤佈局 (`2: ABC` ~ `9: WXYZ`)，單手操作無壓力。
- 輕量高效，隨叫隨到，懸浮於桌面之上。

### 🌏 2. 多國語言 CJK 音譯與注音搜尋 (CJK & Zhuyin Search)
基於 Android 原生 **ICU Transliterator** 引擎（零增加 APK 體積）：
- **注音符號 (ㄅㄆㄇㄈ / Bopomofo)**：可於設定中開啟「注音搜尋 (預設關閉)」。開啟後按鍵底下會標注注音符號（如 `2: ABC ㄅㄆㄇㄈ`、`3: DEF ㄉㄊㄋㄌ`）。
  - 輸入 `33` ➔ 匹配 **「地圖」** (ㄉㄊ `33`)
  - 輸入 `55` ➔ 匹配 **「相機」** (ㄒㄐ `55`)
- **漢語拼音 (Pinyin)**：預設模式，支援漢語拼音首字母與全拼匹配。
  - 輸入 `38` ➔ 匹配 **「地圖」** (Di Tu `38`)
  - 輸入 `95` ➔ 匹配 **「相機」** (Xiang Ji `95`)
- **日文（平假名 / 片假名）**：支援 Romaji 羅馬字匹配。
  - 輸入 `56` ➔ 匹配 **「カメラ」** (Kamera `56`)
  - 輸入 `78` ➔ 匹配 **「設定」** (Settei `78`)
- **英文與數字**：傳統 T9 字母組合全解析與直接匹配。

### 🎯 3. 智慧 Fuzzy 模糊比對與高亮提示 (Fuzzy Search & Highlighting)
- 支援非連續字元匹配 (Subsequence Matching) 與前綴加權評分演算法。
- 在 App 搜尋結果中**精準高亮顯示命中的字元**。

### 🚀 4. 近期 App 智慧排序 (Recent Apps Ranking)
- 自動記錄歷史開啟頻率與近期使用記錄，常用 App 優先浮出首位。

### 🎨 5. 黑金奢華 Obsidian Gold 視覺設計 (Obsidian Gold Design Tokens)
- 純粹曜石黑 (`#121214`) 搭配琥珀金 (`#FFD700`) 高亮與微立體鍵盤卡片。
- 撥號盤懸浮透明視窗與獨立實體色設定頁面，展現高質感現代美學。

### ⚙️ 6. 靈活鍵盤自訂與手感回饋 (Keypad Customization & Haptics)
- **`X` 鍵手感**：短按倒退刪除單一字元，長按一鍵清空全部輸入。
- **設定選單靈活配置**：可隨意指定長按開啟設定的按鍵（預設為 `9` 號鍵，也可自訂為 `X` 或 `2`~`8` 鍵）。
- **觸感回饋**：內建按鍵 Tap 與 Long-press 觸感震動 (Haptic Feedback)。

---

## 🛠️ 技術棧與架構 (Tech Stack & Architecture)

- **語言**: 100% Kotlin
- **UI 框架**: Jetpack Compose + Material 3
- **音譯引擎**: `android.icu.text.Transliterator` (Android API 29+ 原生框架支援)
- **相容性**: Android API 21+ (Android 5.0 Lollipop ~ Android 16)
- **非同步與狀態**: Kotlin Coroutines, StateFlow, Compose `mutableStateOf`
- **資料持久化**: SharedPreferences (`RecentAppsManager`)

---

## 🚀 開始使用 (Getting Started)

### 前置需求 (Prerequisites)
- Android Studio Ladybug (或更新版本)
- JDK 17+
- Android SDK 34+

### 編譯與安裝 (Build & Run)

1. **Clone 專案庫**:
   ```bash
   git clone https://github.com/yongjhih/AppDialer.git
   cd AppDialer
   ```

2. **編譯 Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **執行單元測試**:
   ```bash
   ./gradlew test
   ```

4. **安裝至連接的 Android 裝置 / 模擬器**:
   ```bash
   ./gradlew installDebug
   ```

---

## ⚙️ 設定說明 (Settings)

進入 App Dialer 設定頁面（預設長按 `9` 號鍵）可進行以下配置：

| 設定項目 | 說明 |
| :--- | :--- |
| **Long-Press Settings Key** | 自訂長按開啟設定選單的撥號鍵 (`X`, `2`~`9`) |
| **Fuzzy Search (模糊搜尋)** | 允許字元間隔匹配（預設開啟） |
| **Haptic Feedback** | 撥號鍵盤敲擊震動回饋 |
| **Background Dim Amount** | 撥號盤開啟時的背景遮罩淡化比例 |
| **Close Dialer after Launch** | 點擊啟動 App 後自動關閉撥號盤 |

---

## 📄 授權條款 (License)

本專案採用 **MIT License** 授權，詳情請參閱 [LICENSE](LICENSE) 檔案。

```
Copyright (c) 2026 Yongjhih Chen

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software...
```
