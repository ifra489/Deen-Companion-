# Deen Companion — Developer Setup Guide

This guide will walk you through setting up your environment, checking out the code, configuring Firebase, and running the Deen Companion Android application.

---

## Required Software
Before opening the project, ensure you have the following installed on your machine:
* **Android Studio**: Version **Hedgehog (2023.1.1)** or newer.
* **Java Development Kit (JDK)**: **JDK 17** (Ensure your Android Studio Gradle JDK is pointed to JDK 17).
* **Kotlin**: Version **1.9.x** (Configured automatically in project build files).
* **Gradle**: Version **8.2** or newer (Supplied via wrapper).
* **Git**: Installed and configured on your path.

---

## How to Clone the Project
Open your command terminal, navigate to your work directory, and run the following command to download the code:

```bash
git clone <repository-url-here>
cd "deen companion"
```

---

## How to Open in Android Studio
1. Open **Android Studio**.
2. On the welcome screen, click **Open** (or select **File > Open** if you have another project open).
3. Navigate to the folder where you cloned the repository (`c:\Users\HP\Documents\deen companion`).
4. Select the root directory (containing `settings.gradle.kts`) and click **OK**.
5. Wait for Android Studio to index the project directories.

---

## How to Sync Gradle
Once the project has finished indexing:
1. In the top right corner or build toolbar, click the **Sync Project with Gradle Files** icon (looks like an elephant with a blue circular arrow).
2. Alternatively, go to the top menu and select **File > Sync Project with Gradle Files**.
3. The build window at the bottom of the screen will show the status of the sync. Make sure it completes with no errors.

---

## Firebase Setup (MANUAL STEPS REQUIRED)

> [!IMPORTANT]
> **This project relies on Firebase Services for Authentication, Cloud Databases, and Notifications. You MUST perform these manual steps to connect your local app build to Firebase.**

### Why Firebase is Needed
* **Firebase Authentication**: Handles secure user registration, email verification, password resets, and user sessions.
* **Cloud Firestore**: Stores and synchronizes user habits, journal entries, goals, and achievements in real time.
* **Firebase Cloud Messaging (FCM)**: Delivers daily notifications, reminders, and broadcast announcements.

### Step-by-Step Configuration Instructions
1. Open your web browser and navigate to the [Firebase Console](https://console.firebase.google.com/).
2. Log in using your Google account.
3. Click the **Add Project** button (or **Create a Project**).
4. Enter the project name: `DeenCompanion` and click **Continue**.
5. Choose whether to enable Google Analytics (recommended but optional) and click **Create Project**. Wait for setup to finish.
6. Once the dashboard loads, click the **Android Icon** (looks like a green robot) to add an Android application.
7. Enter the registration details:
   * **Android Package Name**: `com.deencompanion.app` (This must match the applicationId in your app's build.gradle.kts).
   * **App Nickname**: `Deen Companion Debug` (Optional).
   * **Debug Signing Certificate SHA-1**: Get your debug certificate fingerprint by running this command in your terminal:
     ```powershell
     keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android
     ```
     *Copy the hex values labeled SHA1 and paste them into the box.*
8. Click **Register App**.
9. Click the **Download google-services.json** button to download your configuration file.
10. Copy the downloaded `google-services.json` file and paste it into the **`/app`** directory of your project (e.g., `c:\Users\HP\Documents\deen companion\app\google-services.json`).
11. Return to the Firebase Console, skip the build file configuration step (we have already pre-configured the Gradle build files), and finish the wizard.

### Enabling Required Services and Configurations

1. **Register Debug SHA-1 Fingerprint (MANDATORY for Google Sign-In & Guest Link)**:
   * In the Firebase Console, click the **Gear Icon (Project Settings)** in the top-left next to "Project Overview".
   * Under the **General** tab, scroll down to the **Your apps** section.
   * Select the `com.deencompanion.app` Android app.
   * Click **Add fingerprint**.
   * Run the keytool terminal command to generate your debug SHA-1 key:
     ```powershell
     keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android
     ```
   * Copy the generated SHA-1 fingerprint, paste it in the **Certificate fingerprint** box, and click **Save**.
   * *Note: If keytool is missing, you can also run `./gradlew signingReport` in the project terminal to find the debug SHA-1.*

2. **Authentication (Email/Password, Google Sign-In, and Guest Mode)**:
   * In the left-hand navigation pane, click **Build > Authentication**.
   * Click **Get Started** (if not already enabled).
   * Go to the **Sign-in method** tab.
   * **Email/Password**: Click it, toggle **Enable** to ON, and click **Save**.
   * **Google Sign-In**:
     * Click **Add new provider** and select **Google**.
     * Toggle **Enable** to ON.
     * Select a **Project support email** from the dropdown.
     * Scroll down to **Web SDK configuration** and copy the **Web client ID** string (looks like `xxxx-xxxx.apps.googleusercontent.com`).
     * Paste this Web Client ID into your project's `app/src/main/res/values/strings.xml` resource under `<string name="google_web_client_id">YOUR_CLIENT_ID_HERE</string>`.
     * Click **Save** in the console.
   * **Anonymous / Guest Mode**:
     * Click **Add new provider** and select **Anonymous**.
     * Toggle **Enable** to ON, and click **Save**.

3. **Cloud Firestore Rules Deployment**:
   * Click **Build > Firestore Database** in the left pane.
   * Click **Create database** (if not already created), choose default parameters, and select your location.
   * Navigate to the **Rules** tab.
   * Replace the entire rules structure with the contents of the `firestore.rules` file in the project root:
     ```javascript
     rules_version = '2';
     service cloud.firestore {
       match /databases/{database}/documents {
         // Allow users to read and write only their own profile document
         match /users/{userId} {
           allow read, write: if request.auth != null && request.auth.uid == userId;
         }
         
         // Deny access to all other collections by default
         match /{document=**} {
           allow read, write: if false;
         }
       }
     }
     ```
   * Click **Publish**.

---

## How to Run the App

### Running on an Emulator
1. In Android Studio, open the **Device Manager** by selecting **Tools > Device Manager** (or clicking the phone icon in the right side panel).
2. Click **Create Device**, select a modern phone model (e.g., Pixel 7), select a system image with **API Level 34 or 35**, and click **Finish**.
3. Launch the emulator by clicking the green Play icon next to the virtual device.
4. Once the emulator boots up, select your emulator name in the device dropdown list in Android Studio's top toolbar.
5. Click the green **Run 'app'** button (Play icon, or press `Shift + F10`).

### Running on a Physical Device
1. On your Android phone, open **Settings > About Phone**.
2. Scroll to the bottom and tap **Build Number** 7 times until you see the message "You are now a developer!".
3. Go back to Settings and open **System > Developer Options** (or search for it).
4. Turn on **USB Debugging**.
5. Connect your phone to your computer via USB cable. If prompted on your phone, choose **Allow USB Debugging**.
6. Select your physical phone name in Android Studio's device dropdown.
7. Click the green **Run** button.

---

## Build Commands
If you prefer building using the terminal, run these commands from the root directory:

* **Debug Build**:
  ```powershell
  ./gradlew assembleDebug
  ```
* **Release Build** (Produces optimized APK with ProGuard rules applied):
  ```powershell
  ./gradlew assembleRelease
  ```
* **Clean Project**:
  ```powershell
  ./gradlew clean
  ```

---

## Troubleshooting

### 1. Firebase "Could not parse the Android Application" during sync
* **Reason**: Missing `google-services.json` in the `/app` folder.
* **Fix**: Follow the Firebase Setup instructions above to download the file and copy it into `/app`.

### 2. JDK version mismatch (Unsupported class file major version)
* **Reason**: Android Studio is pointing to an older JDK version (e.g., JDK 11 or 8).
* **Fix**: Go to **File > Settings > Build, Execution, Deployment > Build Tools > Gradle**. Under **Gradle JDK**, select **JDK 17**.

### 3. Keystore file does not exist (SHA-1 command fails)
* **Reason**: Your debug keystore has not been generated yet because you haven't run any builds.
* **Fix**: Run `./gradlew assembleDebug` first. This will auto-generate the debug keystore at `~/.android/debug.keystore`. Then re-run the keytool command.

### 4. Out of memory during Gradle Sync
* **Reason**: The JVM running Gradle ran out of memory.
* **Fix**: Open `gradle.properties` and add or increase the JVM heap size: `org.gradle.jvmargs=-Xmx2048m`.

### 5. Internet permission issues on Android emulator
* **Reason**: Sometimes the emulator loses connection to the computer's network.
* **Fix**: Toggle Airplane mode on/off in the emulator, or restart the emulator using "Cold Boot Now" from the Device Manager dropdown.
