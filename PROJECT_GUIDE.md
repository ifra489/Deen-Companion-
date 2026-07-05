# Deen Companion — Project Guide

Welcome to the **Deen Companion** project! This guide will explain what was built in Phase 1 (Project Foundation), the architecture choices we made, the structure of the project, how to run tests, and what common setup errors to watch out for.

---

## What Was Built (Phase 1)
In this initial phase, we established a production-ready baseline foundation for the Deen Companion Android application. Nothing functional is visible yet, but all core building blocks have been placed:
1. **Directory Structure**: Setup package paths adhering to clean architecture.
2. **Build Configuration**: Configured dependencies in build scripts using Gradle Version Catalogs (`libs.versions.toml`).
3. **Base Utility Classes**: Created standard states, global constants, connectivity utilities, and Kotlin extensions.
4. **Room Database**: Set up the database client boilerplate ready for entity mapping.
5. **Dependency Injection**: Pre-configured Hilt modules for binding objects.
6. **Material Design 3 Theme**: Programmed the light/dark custom green and gold color schemes.
7. **Navigation Structure**: Built the routing map, nested graphs (Auth + Main), and bottom navigation bar.
8. **Splash Screen API**: Configured the official Android 12+ splash API with a white crescent moon logo.

---

## Why This Architecture
This project uses **MVVM (Model-View-ViewModel) + Clean Architecture**. 

Instead of writing all code in one file, we separate concerns. This makes the project:
* **Easy to read**: Every class has only one job.
* **Easy to test**: We can test code without launching emulator.
* **Easy to scale**: Multiple developers can work on different features without conflicting.

We separate the code into **Data**, **Domain**, and **Presentation** directories:
* **Data**: Handles database files and remote network connections.
* **Domain**: Handles business calculations, rules, and triggers.
* **Presentation**: Handles UI rendering and collecting user clicks.

---

## Folder Structure Explained
Every directory in our source folder has a dedicated purpose:

* **`data/local/`**: Contains Room database files, DAOs, and files to pre-populate local offline SQL tables.
* **`data/remote/`**: Manages remote data interactions such as Firebase API requests or Quran audio streams.
* **`data/repository/`**: Implements domain repository interfaces by loading data from local database or remote servers.
* **`data/model/`**: Houses data models representing network JSON structures or database records.
* **`domain/model/`**: Contains pure Kotlin data objects representing core domain business models.
* **`domain/repository/`**: Declares repository interfaces that define how data should be queried (without showing the implementation details).
* **`domain/usecase/`**: Houses independent business operations (Use Cases) that coordinate specific tasks (e.g. "Calculate Prayer Times").
* **`presentation/navigation/`**: Manages screen navigation graphs, routes, and bottom navigation components.
* **`presentation/theme/`**: Configures colors, typography scales, shapes, and the global light/dark theme wrapper.
* **`presentation/ui/`**: Stores Compose screens organized by sub-features (Auth, Quran, Prayer, Tasbeeh, etc.).
* **`di/`**: Configures Hilt modules to inject database, preferences, and network dependencies.
* **`util/`**: Hosts helper files like connection checkers, state holders, constants, and extensions.
* **`assets/`**: Placed at the project root to store raw pre-populated SQLite `.db` tables and local asset references.

---

## Architecture Layers Explained

### 1. Data Layer
The Data Layer is the source of all information. It interacts directly with the database, disk cache, or external web services. It hides all caching, fetching, parsing, and data manipulation logic from the rest of the application.

### 2. Domain Layer
The Domain Layer is the "brain" of the app. It holds business rules and logic. It is completely independent of the Android framework, database engines, or UI components. It depends only on pure Kotlin code, making it stable and easy to unit-test.

### 3. Presentation Layer
The Presentation Layer deals with the UI. It uses Jetpack Compose to render screens reactively and ViewModels to manage transient screen states. ViewModels observe domain Use Cases and expose state flows wrapped in `UiState` objects.

### 4. Dependency Injection (DI) Layer
The DI Layer, powered by Dagger Hilt, handles object creation. It automatically injects required dependencies (like Context, databases, or client APIs) into ViewModels, Services, and Repositories, reducing boilerplate code and decoupling classes.

### 5. Util Layer
The Util Layer contains generic utilities, constants, and extensions. These components are globally accessible and do not contain business-specific rules, but rather extend standard system classes.

---

## Theme System Explained
We implemented a custom **Material Design 3 (MD3)** theme:
* **Colors**: A dedicated Deep Green (`#2E7D32`) primary brand color with a Gold Accent (`#F9A825`) tertiary color.
* **Typography**: Uses the default system sans-serif fonts to ensure that standard text and right-to-left Arabic fonts scale properly.
* **Shapes**: Uses rounded corner schemes ranging from 4.dp (ExtraSmall) for items like text inputs up to 24.dp (ExtraLarge) for sheets.
* **Dark Mode**: Works automatically by checking the system's dark theme mode, swapping color values seamlessly while keeping the same typography rules.

---

## Navigation Explained
Navigation is managed by a single central controller (`NavHostController`):
* **`NavRoutes`**: A sealed class representing all screens. It formats URLs and parses arguments safely.
* **`NavGraph`**: Maps route keys to physical Compose screens. It is split into `auth_graph` (for login/registration flow) and `main_graph` (for logged-in features).
* **`BottomNavBar`**: Integrates 5 main tabs (Home, Quran, Prayer, Dua, More) and displays appropriate labels, rounded icons, and badge states.

---

## Dependencies Added

| Dependency Name | Purpose | What It Does |
| :--- | :--- | :--- |
| **Compose BOM** | UI Framework | Coordinates Jetpack Compose UI library versions. |
| **Hilt Android** | Dependency Injection | Auto-generates and injects dependencies into classes. |
| **Room Database** | Offline Storage | A SQLite object-mapping library to store user data locally. |
| **Firebase BOM** | Cloud Services | Integrates Firebase Auth, Firestore, and FCM. |
| **Coroutines** | Background Threading | Performs network and database jobs without lagging the UI. |
| **DataStore** | Preferences Storage | Replaces SharedPreferences with a safe async key-value store. |
| **WorkManager** | Background Tasks | Schedules deferrable background jobs (like backing up data). |
| **Adhan Kotlin** | Prayer Times calculation | Computes accurate offline prayer times based on coordinates. |
| **Accompanist** | Permissions | Streamlines requesting Android system permissions in Compose. |
| **Coil Compose** | Image Loading | Loads web images asynchronously with caching support. |
| **Core SplashScreen** | Boot Animation | Displays a themed splash screen on Android 12+ devices. |
| **Media3 ExoPlayer** | Audio Streaming | Streams Quran audio recitations in the background. |
| **Kotlin Serialization**| JSON Parser | Safely parses JSON payloads to Kotlin classes. |

---

## Common Errors & Solutions

### 1. Hilt not initialized (Crash on launch)
* **Error**: `java.lang.RuntimeException: Unable to instantiate application com.deencompanion.app.DeenCompanionApp: java.lang.IllegalStateException: Hilt-generated Application class is missing.`
* **Solution**: Ensure `@HiltAndroidApp` is placed above the application class and the class is registered in `AndroidManifest.xml` under `<application android:name=".DeenCompanionApp">`.

### 2. Missing google-services.json (Build fails)
* **Error**: `File google-services.json is missing. The Google Services Plugin cannot function without it.`
* **Solution**: Place your downloaded `google-services.json` from the Firebase Console into the `/app` root directory.

### 3. Kapt errors during build
* **Error**: `kapt: compilation error / annotation processing failed.`
* **Solution**: Ensure your Kotlin version and compiler plugin version are matched. Check that Room and Hilt annotation processors are declared via `kapt` and not `implementation`.

### 4. Compose Compiler Version Mismatch
* **Error**: `This version (x.y.z) of the Compose Compiler requires Kotlin version (a.b.c).`
* **Solution**: Ensure the Kotlin compiler extension version (`kotlinCompilerExtensionVersion`) matches the Kotlin version according to the official Jetpack Compose compatibility map.

### 5. Room Database schema export warning
* **Error**: `Schema export directory is not provided. Set room.schemaLocation annotation processor option.`
* **Solution**: Can be ignored during early testing. In a production app, set the schema location in `app/build.gradle.kts` within kapt arguments, or disable it via `exportSchema = false` (though we set it to true to support schema version tracking).

---

## How to Test Phase 1
Follow these steps to verify that the foundation setup is correct:
1. Make sure a `google-services.json` file is present in the `/app` folder (can use a dummy/placeholder file if Firebase setup isn't finished yet).
2. Click **Sync Project with Gradle Files** in Android Studio.
3. Build the project (**Build > Make Project**). Ensure compile is successful.
4. Run the app on an emulator. Verify the green splash screen with the white crescent moon logo appears.
5. Tap anywhere on the "Login Screen" simulation. The green Material 3 theme dashboard should load, showing a bottom navigation bar with 5 items.
6. Toggle dark mode in the system settings and ensure the background colors flip between white and near-black.

---

## What Was Built (Phase 2 — Authentication Module)
In this phase, we implemented a production-ready, clean-architecture authentication system for Deen Companion:

1. **Clean Domain Layer**:
   * **`User.kt`**: Clean, platform-agnostic domain user model representing active sessions.
   * **`AuthRepository.kt`**: Repository contract defining 10 async authentication actions.
   * **`AuthUseCases.kt`**: Aggregates all auth use cases (`LoginUseCase`, `RegisterUseCase`, `GoogleSignInUseCase`, `GuestSignInUseCase`, `LinkGuestAccountUseCase`, `ResetPasswordUseCase`, `GetCurrentUserUseCase`, `LogoutUseCase`, `ValidateEmailUseCase`, `ValidatePasswordUseCase`) into a single injectable container.

2. **Robust Data Layer**:
   * **`AuthRepositoryImpl.kt`**: Implements the repository interface. Coordinates between `FirebaseAuth` and `FirebaseFirestore`. Maps complex SDK exception subclasses (e.g. `FirebaseAuthUserCollisionException`) to clean, translated user-facing error strings, and forces operations onto `Dispatchers.IO`.
   * **`RepositoryModule.kt`**: Configures Dagger Hilt to bind the new implementation.

3. **Reactive Presentation Layer**:
   * **`AuthFormState` & `PasswordStrength`**: Custom reactive state tracking models that drive visual state transitions.
   * **`AuthViewModel.kt`**: Exposes form state flow and asynchronous action results. Performs real-time field validation, password-matching checks, and counts rules met to calculate the password strength score on every keystroke.
   * **`AuthTextField`**: Reusable component incorporating rounded borders, password toggles, and dynamic red error frames.
   * **`LoginScreen`, `RegisterScreen`, `ForgotPasswordScreen`**: Premium-styled screens featuring responsive buttons, segmented strength-level bars, validation checklists, and confirmation overlay alerts.

4. **Single-Activity Session Routing**:
   * Updated `MainActivity.kt` and `NavGraph.kt` to coordinate navigation dynamically.
   * Integrated the official Android Splash Screen API to keep the splash screen visible until the initial session check resolves, eliminating layout flashes.
   * Leveraged parent route checking to safely redirect users between `auth_graph` and `main_graph` on session updates.

---

## What Will Be Built Next
In **Phase 3: Prayer Times & Qibla Module**, we will build:
* Location services integration (GPS & IP-based coordinates).
* Calculation engine using Adhan Kotlin based on local configuration rules.
* Premium dashboard widget showing remaining time to the next prayer.
* Offline compass visualizer utilizing phone magnetometers.
