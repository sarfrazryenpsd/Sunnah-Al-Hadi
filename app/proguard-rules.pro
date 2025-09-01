# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Enable aggressive optimizations
# ProGuard/R8 Configuration for Sunnah App
# Add to your app/proguard-rules.pro file

# ==============================================
# GENERAL R8 OPTIMIZATIONS
# ==============================================

# Enable aggressive optimizations
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!code/simplification/advanced,!class/merging/*
-optimizationpasses 5
-allowaccessmodification
-overloadaggressively
-repackageclasses ''

# Keep line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ==============================================
# KOTLIN & COROUTINES
# ==============================================

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# Kotlin Metadata
-keepattributes RuntimeVisibleAnnotations,RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations,RuntimeInvisibleParameterAnnotations
-keepattributes AnnotationDefault

# Kotlin Reflection (if used)
-keep class kotlin.reflect.** { *; }
-keep class kotlin.Metadata { *; }

# ==============================================
# HILT DEPENDENCY INJECTION
# ==============================================

# Hilt Generated Classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent
-keep class **_HiltComponents$** { *; }
-keep class **_Factory { *; }
-keep class **_MembersInjector { *; }

# Hilt Entry Points
-keep @dagger.hilt.android.EarlyEntryPoint class * { *; }
-keep @dagger.hilt.EntryPoint class * { *; }

# Hilt Modules
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.Module class * { *; }

# Hilt Workers
-keep class * extends androidx.work.ListenableWorker {
    public <init>(...);
}
-keep class **.*_AssistedFactory { *; }

# JSR 330 (Dependency Injection)
-dontwarn javax.inject.**
-keep class javax.inject.** { *; }

# ==============================================
# ROOM DATABASE
# ==============================================

# Room generated classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Query class * { *; }
-keep @androidx.room.Insert class * { *; }
-keep @androidx.room.Update class * { *; }
-keep @androidx.room.Delete class * { *; }

# Room TypeConverters
-keep @androidx.room.TypeConverter class * { *; }
-keep class * {
    @androidx.room.TypeConverter <methods>;
}

# Keep Room generated implementations
-keep class **_Impl { *; }
-keep class **.*_Impl$* { *; }

# ==============================================
# PROTO DATASTORE
# ==============================================

# Protocol Buffers
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
    <methods>;
}
-keep class * extends com.google.protobuf.GeneratedMessageLite$Builder { *; }

# Keep your proto classes specifically
-keep class **.proto.** { *; }
-keep class **.*Proto { *; }
-keep class **.*Proto$* { *; }

# Proto serialization
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    public static ** parseFrom(...);
    public static ** parser();
}

# ==============================================
# KOTLINX SERIALIZATION
# ==============================================

# Keep Serializable classes
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Keep Serializers
-keep,includedescriptorclasses class **.*$$serializer { *; }
-keepclassmembers class ** {
    *** Companion;
}
-keepclasseswithmembers class ** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep @Serializable classes
-keep @kotlinx.serialization.Serializable class ** { *; }

# ==============================================
# JETPACK COMPOSE
# ==============================================

# Compose Runtime
-keepclassmembers class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

# Composable functions
-keep @androidx.compose.runtime.Composable class * { *; }
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Compose State
-keepclassmembers class * {
    @androidx.compose.runtime.State <fields>;
    @androidx.compose.runtime.MutableState <fields>;
}

# CompositionLocal
-keep class androidx.compose.runtime.CompositionLocal { *; }
-keep class androidx.compose.runtime.ProvidableCompositionLocal { *; }

# ==============================================
# VIEWMODEL & LIFECYCLE
# ==============================================

# ViewModel
-keep class * extends androidx.lifecycle.ViewModel { *; }
-keep class * extends androidx.lifecycle.AndroidViewModel { *; }

# Keep ViewModel constructors for Hilt
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}

# ==============================================
# WORK MANAGER
# ==============================================

# Workers
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keep class * extends androidx.work.ListenableWorker { *; }

# Worker constructors
-keepclassmembers class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ==============================================
# DATA MODELS & ENTITIES
# ==============================================

# Keep your data models (adjust package names as needed)
-keep class com.ryen.sunnah_alhadi.data.model.** { *; }
-keep class com.ryen.sunnah_alhadi.domain.model.** { *; }
-keep class com.ryen.sunnah_alhadi.ui.model.** { *; }

# Keep entities specifically
-keep @androidx.room.Entity class ** { *; }

# Keep sealed classes and enums
-keep class ** extends java.lang.Enum { *; }
-keepclassmembers class ** extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ==============================================
# NAVIGATION & INTENTS
# ==============================================

# Keep classes with Intent extras
-keepclassmembers class * {
    public static final java.lang.String EXTRA_*;
}

# Navigation arguments
-keep class ** implements android.os.Parcelable { *; }
-keep class ** implements java.io.Serializable { *; }

# ==============================================
# COIL IMAGE LOADING
# ==============================================

# Coil - Use specific package names based on your Coil version
-keep class coil3.** { *; }
-keep class coil3.** { *; }

# If using Coil 2.x, also add:
-keep class coil.transform.** { *; }
-keep class coil.size.** { *; }

# ==============================================
# FIREBASE CRASHLYTICS
# ==============================================

# Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# ==============================================
# REFLECTION-BASED LIBRARIES
# ==============================================

# Keep classes that use reflection
-keepclassmembers class * {
    @org.jetbrains.annotations.NotNull <methods>;
    @org.jetbrains.annotations.Nullable <methods>;
}

# ==============================================
# SPECIFIC APP CLASSES
# ==============================================

# Keep your specific classes that might be accessed via reflection
# Repository interfaces
-keep interface com.ryen.sunnah_alhadi.**.repository.* { *; }

# Use cases
-keep class com.ryen.sunnah_alhadi.**.usecase.** { *; }

# Mappers
-keep class com.ryen.sunnah_alhadi.**.mapper.** { *; }

# Event classes
-keep class **.*Event { *; }
-keep class **.*Event$* { *; }

# ==============================================
# PERFORMANCE OPTIMIZATIONS
# ==============================================

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove Timber logging
-assumenosideeffects class timber.log.Timber* {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# ==============================================
# WARNINGS TO IGNORE
# ==============================================

# Ignore warnings that don't affect functionality
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn okhttp3.internal.platform.**
-dontwarn retrofit2.Platform$Java8

# Ignore annotation processor classes (not needed at runtime)
-dontwarn javax.annotation.**
-dontwarn javax.lang.model.**
-dontwarn javax.tools.**
-dontwarn sun.reflect.annotation.**

# Dagger/Hilt processor references
-dontwarn dagger.internal.codegen.**
-dontwarn dagger.hilt.android.processor.internal.**
-dontwarn com.google.auto.**
-dontwarn com.google.auto.service.**
-dontwarn com.google.common.collect.**
-dontwarn com.squareup.javapoet.**
-dontwarn com.squareup.kotlinpoet.**
-dontwarn com.google.googlejavaformat.**


# ==============================================
# GRADLE BUILD CONFIGURATION
# ==============================================

# Add to your app/build.gradle.kts:
#
# android {
#     buildTypes {
#         release {
#             isMinifyEnabled = true
#             isShrinkResources = true
#             proguardFiles(
#                 getDefaultProguardFile("proguard-android-optimize.txt"),
#                 "proguard-rules.pro"
#             )
#         }
#     }
#
#     compileOptions {
#         sourceCompatibility = JavaVersion.VERSION_17
#         targetCompatibility = JavaVersion.VERSION_17
#     }
# }

# ==============================================
# TESTING STRATEGY
# ==============================================

# 1. Start with a debug build that has minification enabled:
#    debuggable true
#    isMinifyEnabled = true
#    isShrinkResources = false
#
# 2. Test all major app flows thoroughly
# 3. Check crash reports in Crashlytics
# 4. Gradually enable more aggressive optimizations
# 5. Use mapping file for crash deobfuscation

# ==============================================
# MAPPING FILE CONFIGURATION
# ==============================================

# The mapping file will be generated at:
# app/build/outputs/mapping/release/mapping.txt
#
# Upload this to Firebase Crashlytics for crash deobfuscation:
# ./gradlew :app:uploadCrashlyticsProguardMappingFileRelease