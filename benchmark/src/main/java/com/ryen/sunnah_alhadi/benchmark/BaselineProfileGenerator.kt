package com.ryen.sunnah_alhadi.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @RequiresApi(Build.VERSION_CODES.P)
    @get:Rule
    val rule = BaselineProfileRule()

    @RequiresApi(Build.VERSION_CODES.P)
    @Test
    fun generate() = rule.collect(
        packageName = "com.ryen.sunnah_alhadi",
        includeInStartupProfile = true // Recommended: include in startup
    ) {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Launch the app
        startActivityAndWait()

        device.wait(Until.hasObject(By.desc("Browse")), 10_000)
        val browseButton = device.findObject(By.desc("Browse"))
        browseButton.click()

        val preferencesButton = device.findObject(By.desc("Preferences"))
        preferencesButton.click()

        device.wait(Until.hasObject(By.text("Preferences")), 10_000)

        val homeButton = device.findObject(By.desc("Home"))
        homeButton.click()


        // Wait for "SeeAll" text to appear
        device.wait(Until.hasObject(By.text("See All")), 10_000)

        // Find and click "SeeAll"
        val seeAllButton = device.findObject(By.text("See All"))
        if (seeAllButton != null && seeAllButton.isClickable) {
            seeAllButton.click()
        } else {
            throw RuntimeException("SeeAll button not found or not clickable")
        }

        // Wait for the RecyclerView or scrollable container (by correct resource ID)
        // Replace `id/AllTopics` with actual resource name and ensure package is included
        /*device.wait(
            Until.hasObject(By.text("Topics")),
            10_000
        )

        val feed = device.findObject(By.res("TopicsContainer"))
        if (feed != null) {
            feed.setGestureMargin(device.displayWidth / 5)
            // Perform multiple flings to capture more of the UI
            repeat(3) {
                feed.fling(Direction.DOWN)
                device.waitForIdle(1000)
            }
        } else {
            throw RuntimeException("Feed container 'Topics' not found")
        }*/

        // Optional: Navigate back or interact more if needed
        // This helps capture more code paths in the baseline profile
    }
}