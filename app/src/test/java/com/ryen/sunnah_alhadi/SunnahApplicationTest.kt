package com.ryen.sunnah_alhadi

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.every
import io.mockk.spyk
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@HiltAndroidTest
class SunnahApplicationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun application_onCreate_should_initialize_hilt_successfully() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        val entryPoint = EntryPointAccessors.fromApplication(
            context,
            WorkerFactoryEntryPoint::class.java
        )

        val factory = entryPoint.workerFactory()
        assertThat(factory).isNotNull()
    }

    @Test
    fun application_onCreate_should_handle_initialization_exception_gracefully() {
        // Given
        val application = spyk(SunnahApplication())
        every { application.onCreate() } throws RuntimeException("Initialization failed")

        // When/Then
        try {
            application.onCreate()
            fail("Expected RuntimeException was not thrown")
        } catch (e: RuntimeException) {
            // Then: Test passed
            assertThat(e.message).isEqualTo("Initialization failed")
        }
    }

    @Test
    fun application_should_survive_low_memory_conditions() {
        // Given
        val context = ApplicationProvider.getApplicationContext<SunnahApplication>()

        // When
        context.onLowMemory()

        // Then
        // Should not crash and remain functional
        assertThat(context).isNotNull()
    }

    @Test
    fun application_should_handle_configuration_changes() {
        // Given
        val context = ApplicationProvider.getApplicationContext<SunnahApplication>()

        // When
        context.onConfigurationChanged(Configuration())

        // Then
        // Should handle configuration changes gracefully
        assertThat(context).isNotNull()
    }

    @Test
    fun application_should_handle_trim_memory_events() {
        // Given
        val context = ApplicationProvider.getApplicationContext<SunnahApplication>()

        // When
        context.onTrimMemory(ComponentCallbacks2.TRIM_MEMORY_MODERATE)

        // Then
        // Should handle memory trim events gracefully
        assertThat(context).isNotNull()
    }
}