import org.gradle.kotlin.dsl.testImplementation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.ryen.sunnah_alhadi"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ryen.sunnah_alhadi"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }

    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
            freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
        }
    }
    
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/androidx/room/room-compiler-processing/LICENSE.txt"
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
    }
}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${libs.versions.protobuf.get()}"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                create("java") {
                    option("lite")
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.room)
    implementation(libs.coil)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.core.testing)

    ksp(libs.androidx.room.compiler)
    implementation (libs.bundles.hilt)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // For WorkManager integration
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    kspAndroidTest(libs.hilt.compiler)

    implementation(libs.protobuf.javalite)
    implementation(libs.datastore.proto)
    implementation(libs.datastore.core)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.work.runtime.ktx)

    debugImplementation(libs.bundles.compose.debug)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.tooling)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // JUnit (Unit testing)
    testImplementation(libs.junit)

// Kotlin Coroutines test support
    testImplementation(libs.kotlinx.coroutines.test) // or match your kotlinx-coroutines version

// Truth (optional, but better assertions)
    androidTestImplementation(libs.truth)
    testImplementation(libs.truth)

//for testing workManager
    testImplementation(libs.androidx.work.testing)


// For testing with Hilt
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.androidx.runner)

//Room testing
    testImplementation(libs.androidx.room.testing)

//Datastore testing
    testImplementation(libs.androidx.datastore.preferences)
    testImplementation(libs.datastore.core)
    testImplementation(libs.datastore.proto)
    testImplementation(libs.protobuf.javalite)


// MockK (for mocking dependencies)
    testImplementation(libs.mockk) // use the latest
    androidTestImplementation(libs.mockk.android) // use the latest
    androidTestImplementation(libs.mockk.agent) // use the latest

// Turbine (Flow testing)
    androidTestImplementation(libs.turbine)

    //Robolectric
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.junit)

// AndroidX Test Core (context, lifecycle, etc.)
    testImplementation(libs.androidx.core)

    testImplementation(libs.androidx.core.testing)

}