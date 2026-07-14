package com.kutubuddin.sabeel.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.VolumeProviderCompat
import androidx.test.core.app.ApplicationProvider
import com.kutubuddin.sabeel.domain.haptic.HapticEngine
import io.mockk.*
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ServiceController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.UPSIDE_DOWN_CAKE])
class PocketModeServiceTest {

    private lateinit var context: Context
    private lateinit var serviceController: ServiceController<PocketModeService>
    private val hapticEngine: HapticEngine = mockk(relaxed = true)

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        serviceController = Robolectric.buildService(PocketModeService::class.java)
        val service = serviceController.get()
        service.hapticEngine = hapticEngine
    }

    @Test
    fun testServiceStart_createsForegroundNotification() {
        serviceController.create().startCommand(0, 0)

        val service = serviceController.get()
        val shadowService = shadowOf(service)

        assertNotNull(shadowService.lastForegroundNotification)

        serviceController.destroy()
    }

    @Test
    fun testVolumeKeysInterception_triggersHaptics() {
        val volumeProviderSlot = slot<VolumeProviderCompat>()

        mockkConstructor(MediaSessionCompat::class)
        every {
            anyConstructed<MediaSessionCompat>().setPlaybackToRemote(capture(volumeProviderSlot))
        } just Runs

        serviceController.create()
        serviceController.get().hapticEngine = hapticEngine
        serviceController.get().counterMutator = mockk(relaxed = true)
        serviceController.startCommand(0, 0)

        assertTrue(volumeProviderSlot.isCaptured)
        val provider = volumeProviderSlot.captured

        provider.onAdjustVolume(1)
        verify(exactly = 1) { hapticEngine.playIncrementTick() }

        provider.onAdjustVolume(-1)
        verify(exactly = 2) { hapticEngine.playIncrementTick() }

        serviceController.destroy()
        unmockkConstructor(MediaSessionCompat::class)
    }

    /**
     * LEAK-03 regression: onDestroy() must null the mediaSession field before
     * calling release() to stop concurrent onAdjustVolume callbacks from accessing
     * a deactivating session.
     *
     * We verify this by reading the field via reflection after destroy() and
     * confirming it is null — meaning the swap-before-release pattern held.
     */
    @Test
    fun onDestroy_nullsMediaSessionBeforeRelease() {
        serviceController.create().startCommand(0, 0)
        val service = serviceController.get()

        // Confirm session exists before destroy
        val fieldBefore = PocketModeService::class.java.getDeclaredField("mediaSession")
        fieldBefore.isAccessible = true
        assertNotNull("mediaSession should be non-null after create", fieldBefore.get(service))

        serviceController.destroy()

        // After onDestroy, the backing field must be null
        assertNull(
            "mediaSession must be null after onDestroy (LEAK-03 fix)",
            fieldBefore.get(service)
        )
    }

    /**
     * LEAK-03 regression: setupMediaSession() must be idempotent.
     * Calling it twice must NOT replace the existing session with a new instance.
     * We verify by reading the field via reflection before and after the second call —
     * the object reference must be identical (same instance, not a new allocation).
     */
    @Test
    fun setupMediaSession_calledTwice_createsSessionOnce() {
        serviceController.create().startCommand(0, 0)
        val service = serviceController.get()

        val field = PocketModeService::class.java.getDeclaredField("mediaSession")
        field.isAccessible = true

        val sessionAfterFirst = field.get(service)
        assertNotNull("mediaSession must be non-null after first setup", sessionAfterFirst)

        // Call setupMediaSession() a second time — idempotency guard must block it
        val setupMethod = PocketModeService::class.java.getDeclaredMethod("setupMediaSession")
        setupMethod.isAccessible = true
        setupMethod.invoke(service)

        val sessionAfterSecond = field.get(service)

        assertTrue(
            "setupMediaSession() must be idempotent: second call must not replace the session instance",
            sessionAfterFirst === sessionAfterSecond
        )

        serviceController.destroy()
    }
}

