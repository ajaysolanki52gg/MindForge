# MindForge

MindForge is a fully offline Android brain training app built with Kotlin, Jetpack Compose, Room, and MVVM. It includes memory, math, attention, and vocabulary mini-games, plus deterministic daily challenges and local-only progression tracking.

## Stack

- Kotlin
- Jetpack Compose
- MVVM with `ViewModel` + `StateFlow`
- Room database
- Navigation Compose
- Hilt dependency injection
- Min SDK 24

## Features

- Home screen with daily challenge, stats preview, and category grid
- 4 categories: Memory, Math, Attention, Vocabulary
- 14 mini-games total
- Daily challenge with 5 seeded tasks based on the current date
- XP, level, streak, and achievements stored locally
- Difficulty unlocks:
  - Medium at 500 XP
  - Hard at 1500 XP
- No internet permission required
- Dark/light Material 3 theme

## Project Structure

```text
app/src/main/java/com/mindforge/app/
  data/
  di/
  domain/
  games/
    attention/
    math/
    memory/
    vocabulary/
  ui/
  utils/
```

## Local AI Hook

The project includes a `VocabularyQuestionGenerator` abstraction with an `OfflineVocabularyQuestionGenerator` fallback. This is the extension point for future on-device TensorFlow Lite or ONNX Runtime generators while preserving the current offline dataset fallback.

## Build Instructions

1. Open the `MindForge` folder in Android Studio.
2. If Android Studio prompts for Gradle setup, use Gradle `8.5.2` and Kotlin `1.9.24`, or let the IDE generate the wrapper for this project.
3. Sync the project.
4. Run the `app` configuration on an emulator or Android device.
5. To generate an APK:
   - Use `Build` -> `Build Bundle(s) / APK(s)` -> `Build APK(s)`.
   - Android Studio will place the debug APK under `app/build/outputs/apk/debug/`.

## Notes

- Daily challenge game selection is repeatable within the same day because it is seeded from the date.
- Daily challenge bonus XP is granted once per day.
- User level follows `sqrt(XP / 100)`.
- This workspace does not include a generated Gradle wrapper JAR because the local environment did not have Gradle/Android SDK tooling available to produce one automatically.
