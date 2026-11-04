# App-specific R8 rules. Libraries (Hilt, Room, WorkManager, Startup, Compose, Coil,
# Lottie, Firebase, kotlinx.serialization) ship their own consumer rules, so only add
# what R8 can't see: reflection on *this app's* code.

# Move all obfuscated classes into the root package (smaller dex, stronger obfuscation).
-repackageclasses ''

# Readable Crashlytics stack traces (mapping file is uploaded by the Crashlytics plugin).
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# protobuf-javalite ships no rules: its MessageSchema looks up generated fields by name
# (e.g. "username_") via reflection, so they must not be renamed or removed.
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite {
    <fields>;
}

# Strip android.util.Log calls from release builds.
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}
