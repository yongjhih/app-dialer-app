# AppDialer Architecture & Engineering Guidelines

This document outlines the architectural principles, module boundaries, navigation patterns, and Kotlin code style guidelines for **AppDialer**.

---

## 🏛️ 1. Multi-Module Architecture Overview

AppDialer is modularized into four distinct, single-responsibility Gradle modules:

```mermaid
graph TD
    subgraph ":app"
        A[MainActivity]
    end

    subgraph ":feature:dialer"
        B[MainAppWidget / NavHost]
        C[AppDialerScreen]
        D[AppDialerSettingsScreen]
    end

    subgraph ":core:util"
        E[T9Utils / CjkTransliterator]
        F[AppLoader / RecentAppsManager]
        G[ViewUtils / Extensions]
    end

    subgraph ":core:model"
        H[AppModel / KeyLabelPosition]
        I[AppDefaults / KeyLayout]
    end

    A --> B
    B --> C
    B --> D
    B --> E
    B --> F
    B --> G
    C --> H
    D --> H
    E --> H
    F --> H
    G --> H
```

### Module Responsibilities

| Module | Type | Responsibilities |
| :--- | :--- | :--- |
| **`:core:model`** | Android Library | Pure domain models (`AppModel`), enums (`KeyLabelPosition`), constants (`AppDefaults`), and keypad value definitions (`KeyLayout`). Zero business logic, minimal dependencies. |
| **`:core:util`** | Android Library | T9 Pinyin/Zhuyin search algorithms (`T9Utils`), CJK transliteration (`CjkTransliterator`), package installation loader (`AppLoader`), SharedPreferences manager (`RecentAppsManager`), and Android KTX view helpers (`ViewUtils`). |
| **`:feature:dialer`** | Android Library (Compose) | All Jetpack Compose UI screens, 3x3 interactive keypad diagrams, Material 3 themes (`Theme.kt`, `Color.kt`), and navigation orchestration (`MainAppWidget`). |
| **`:app`** | Android Application | Ultra-thin entrypoint containing `MainActivity`, `AndroidManifest.xml`, launcher icons, and top-level app configuration. |

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

fun Activity.clearContentBackgrounds() {
    findViewById<View>(android.R.id.content)?.selfAndChildren?.forEach { view ->
        view.setBackgroundColor(Color.TRANSPARENT)
        view.background = null
    }
}
```

---

## ⚙️ 4. Build & Testing Standards

- **Unit Testing**: Core search algorithms in `:core:util` (e.g. `T9UtilsTest`) MUST pass with 100% success before committing.
- **Verification**: Build verification MUST be executed via `./gradlew test assembleDebug` across all modules.
