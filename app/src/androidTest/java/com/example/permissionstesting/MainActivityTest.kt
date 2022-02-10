package com.example.permissionstesting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val LAUNCH_TIMEOUT = 5000L
private const val BASIC_SAMPLE_PACKAGE = "com.example.permissionstesting"

@SdkSuppress(minSdkVersion = 23)
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var device: UiDevice

    @Before
    fun startMainActivityFromHomeScreen() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressHome()

        val launcherPackage: String = device.launcherPackageName
        require(launcherPackage.isNotBlank())
        device.wait(
            Until.hasObject(By.pkg(launcherPackage).depth(0)),
            LAUNCH_TIMEOUT
        )

        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(
            BASIC_SAMPLE_PACKAGE
        )?.apply {
            // Clear out any previous instances
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)

        // Wait for the app to appear
        device.wait(
            Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
            LAUNCH_TIMEOUT
        )
    }

    @Test
    fun testPermissionDenied() {
        val context: Context = InstrumentationRegistry.getInstrumentation().context
        val permissionStatus =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        Assert.assertNotEquals(PackageManager.PERMISSION_GRANTED, permissionStatus)
    }

    @Test
    fun testPermissionRequested() {
        val allowButton = device.findObject(UiSelector().textContains("Allow"))
        assert(allowButton.exists())
    }
}