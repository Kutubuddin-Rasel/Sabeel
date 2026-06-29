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
        serviceController.startCommand(0, 0)
        
        assertTrue(volumeProviderSlot.isCaptured)
        val provider = volumeProviderSlot.captured
        
        provider.onAdjustVolume(1)
        verify(exactly = 1) { hapticEngine.playIncrementTick() }
        
        provider.onAdjustVolume(-1)
        verify(exactly = 1) { hapticEngine.playMilestoneClick() }
        
        serviceController.destroy()
        unmockkConstructor(MediaSessionCompat::class)
    }
}
