# ProGuard Rules for Deen Companion

# LEARNING NOTE:
# ProGuard (and R8 in release builds) shrinks and obfuscates class names.
# These rules tell the compiler which classes/methods must NOT be removed or renamed
# because they are accessed via reflection, serialization, or library internals.

# --- Room Database Keep Rules ---
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.room.RoomDatabase
-keep class * implements androidx.room.RoomOpenHelper
-keep class * @androidx.room.Entity
-keep class * @androidx.room.Dao
-keep class * @androidx.room.Database

# --- Dagger Hilt Keep Rules ---
-keep class * @dagger.hilt.android.lifecycle.HiltViewModel
-keep class * @dagger.hilt.DefineComponent
-keep class * @dagger.hilt.EntryPoint
-keep class * @dagger.hilt.InstallIn
-keep class * @dagger.Module
-keep class * @dagger.Provides
-keep class * @javax.inject.Inject
-keep class * @javax.inject.Singleton
-keep class * @dagger.hilt.android.AndroidEntryPoint
-keep class * @dagger.hilt.android.HiltAndroidApp
-keep class * implements dagger.hilt.internal.GeneratedComponent
-keep class * implements dagger.hilt.internal.GeneratedComponentManager

# --- Firebase Keep Rules ---
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class com.google.firebase.** { *; }
-keep class * @com.google.firebase.firestore.PropertyName { *; }
-keep class * @com.google.firebase.database.PropertyName { *; }
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
}

# --- Kotlin Serialization Keep Rules ---
-keepattributes *Annotation*,Signature
-keep class * @kotlinx.serialization.Serializable { *; }
-keepclassmembers class * {
    *** Companion;
}
-keepclasseswithmembers class * {
    *** Companion;
}
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}

# --- Adhan Library Keep Rules ---
-keep class com.batoulapps.adhan.** { *; }

# --- Kotlin Coroutines Keep Rules ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext {
    private synthetic <init>(...);
}

# --- Standard Android and Log Rules ---
-dontwarn android.preview.support.v4.media.session.MediaSessionCompat
-dontwarn android.support.v4.media.session.MediaSessionCompat
-keepattributes SourceFile,LineNumberTable
