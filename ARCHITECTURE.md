# AppDialer Architecture & Engineering Guidelines

This document outlines the architectural principles, module boundaries, navigation patterns, and Kotlin code style guidelines for **AppDialer**.

---

## 🏛️ 1. Multi-Module Architecture Overview

AppDialer is modularized into single-responsibility Gradle modules separated by layer, Compose UI design systems, platform interfaces, and Android framework dependencies.
Both **`:core:model`** and **`:core:util`** are **Pure Kotlin (JVM)** modules, allowing non-Android Kotlin projects (CLI tools, Server backends, KMP, Desktop apps) to depend on them seamlessly.
**`:feature:dialer`** contains all Compose UI screens and state containers, decoupled from Android APIs via the **`AppLauncher`** interface and repository callbacks.
**`:feature:dialer-android`** provides the Android-specific implementation (`AndroidAppLauncher`, `AndroidMainAppWidget`) bridging Android `Context`, `Intent`, `Settings`, `AppLoader`, and `RecentAppsManager`.

```mermaid
graph TD
    subgraph ":app (Android App)"
        A[MainActivity]
    end

    subgraph ":feature:dialer-android (Android Library)"
        B[AndroidMainAppWidget]
        C[AndroidAppLauncher]
    end

    subgraph ":feature:dialer (Compose UI - Platform Independent)"
        D[MainAppWidget / NavHost]
        E[AppDialerScreen]
        F[AppDialerSettingsScreen]
        G[AppLauncher Interface]
    end

    subgraph ":core:ui (Android / Multiplatform Compose UI)"
        H[Theme.kt / Color.kt]
    end

    subgraph ":core:util-android (Android Library)"
        I[AppLoader / RecentAppsManager]
        J[ViewUtils / DrawableUtils]
    end

    subgraph ":core:util (Pure Kotlin JVM Library)"
        K[T9Utils / CjkTransliterator]
    end

    subgraph ":core:model (Pure Kotlin JVM Library)"
        L[AppModel / KeyLabelPosition]
        M[AppDefaults / KeyLayout]
    end

    A --> B
    B --> C
    B --> D
    C --> G
    D --> E
    D --> F
    D --> H
    B --> I
    B --> J
    E --> L
    F --> L
    I --> K
    J --> K
    K --> L
    M --> L
```

### Module Responsibilities

| Module | Type | Responsibilities | Non-Android Reusable |
| :--- | :--- | :--- | :---: |
| **`:core:model`** | **Pure Kotlin (JVM)** | Pure domain models (`AppModel`), enums (`KeyLabelPosition`), constants (`AppDefaults`), and keypad value definitions (`KeyLayout`). Zero Android SDK/Compose dependencies (`id("kotlin")`). | ✅ Yes |
| **`:core:util`** | **Pure Kotlin (JVM)** | Pure Kotlin utilities & search algorithms (`T9Utils`, `CjkTransliterator`). Fully decoupled from Android framework APIs (`id("kotlin")`). | ✅ Yes |
| **`:core:ui`** | **Compose Library** | Android-independent Compose UI design system tokens (`Color.kt`, `Theme.kt`), and pure UI composables. | 🟢 Compose Multiplatform |
| **`:core:util-android`** | Android Library | Android-dependent utility helpers (`AppLoader`, `RecentAppsManager`, `ViewUtils`, `DrawableUtils.kt`) requiring `Context`, `SharedPreferences`, `View`, `Drawable`, and `PackageManager`. | ❌ Android Only |
| **`:feature:dialer`** | **Compose Library** | All UI screens (`AppDialerScreen`, `AppDialerSettingsScreen`), keypad layouts, and navigation state (`MainAppWidget`). Decoupled from Android via `AppLauncher` interface. Zero `android.*` imports! | 🟢 Compose Multiplatform |
| **`:feature:dialer-android`** | Android Library | Android-specific feature composition (`AndroidMainAppWidget`, `AndroidAppLauncher`) bridging `Context`, `Intent`, `Settings`, `AppLoader`, and `RecentAppsManager`. | ❌ Android Only |
| **`:app`** | Android Application | Ultra-thin entrypoint containing `MainActivity`, `AndroidManifest.xml`, launcher icons, and top-level app configuration. | ❌ Android Only |

---

## 📱 2. Ultra-Thin Activity & Declarative Navigation

### Principles
1. **Ultra-Thin Activity Shell**: `MainActivity` is restricted strictly to Android Activity lifecycle initialization, translucent window setup (`applyTransparentWindow()`), and passing re-launch signals (`resetSignal`). It MUST NOT store search queries, app lists, or keypad layout states.
2. **Smart State Container (`MainAppWidget`)**: All UI states, asynchronous app loading, preferences synchronization, and screen routing are encapsulated inside `MainAppWidget`.
3. **Jetpack Navigation Compose (`NavHost`)**: Screen navigation between `Destinations.DIALER` and `Destinations.SETTINGS` MUST use official `NavHost` and `rememberNavController()`.
   - Automatic Backstack: NavHost manages backstack popping automatically on system back gestures or buttons.
   - Re-launch Reset: Re-launching from Home Screen triggers `navController.navigate(Destinations.DIALER) { popUpTo(Destinations.DIALER) { inclusive = true } }`.

---

## 🪄 3. Kotlin KTX & Expressive Extension Guidelines

### View Hierarchy Extensions
Prefer Kotlin `Sequence` KTX extensions over imperative nested loops.
Example (`ViewUtils.kt`):

```kotlin
/**
 * Returns a [Sequence] containing this [View] and its direct children (if it is a [ViewGroup]).
 */
val View.selfAndChildren: Sequence<View>
    get() = sequence {
        yield(this@selfAndChildren)
        (this@selfAndChildren as? ViewGroup)?.children?.let { yieldAll(it) }
    }
```

---

## ⚙️ 4. Build & Testing Standards

- **Unit Testing**: Core search algorithms in `:core:util` (e.g. `T9UtilsTest`) MUST pass with 100% success before committing.
- **Verification**: Build verification MUST be executed via `./gradlew test assembleDebug` across all modules.
