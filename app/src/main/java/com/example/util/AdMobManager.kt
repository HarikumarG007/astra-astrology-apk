package com.example.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

/**
 * Official Google Mobile Ads (AdMob) Manager for Astra Astrology Malayalam.
 * Implements ONLY Banner Ads and Normal Rewarded Ads with exact configured AdMob IDs.
 */
object AdMobManager {

    const val ADMOB_APP_ID = "ca-app-pub-3014489851617320~3397441602"
    const val BANNER_AD_UNIT_ID = "ca-app-pub-3014489851617320/5580751819"
    const val REWARDED_AD_UNIT_ID = "ca-app-pub-3014489851617320/3900090200"

    private var isInitialized = false
    private var rewardedAd: RewardedAd? = null
    private var isLoadingRewardedAd = false

    private val _isRewardedAdReady = MutableStateFlow(false)
    val isRewardedAdReady: StateFlow<Boolean> = _isRewardedAdReady.asStateFlow()

    /**
     * Checks whether the current runtime is a virtual/cloud emulator without hardware
     * GPU rendernode (/dev/dri/renderD128) or hardware Codec2 video bufferpool.
     * Prevents MESA/Codec2/adservices background process errors on cloud emulators
     * while keeping full AdMob functionality on all physical Android devices.
     */
    fun isCloudEmulatorWithoutRenderNode(): Boolean {
        val isEmulator = Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.lowercase().contains("emulator") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.lowercase().contains("sdk_gphone") ||
            Build.MODEL.contains("Emulator") ||
            Build.HARDWARE.contains("goldfish") ||
            Build.HARDWARE.contains("ranchu") ||
            Build.PRODUCT.contains("sdk") ||
            Build.PRODUCT.contains("emulator")
        return isEmulator && !File("/dev/dri/renderD128").exists()
    }

    fun initialize(context: Context) {
        if (isInitialized || isCloudEmulatorWithoutRenderNode()) return
        try {
            val appContext = context.applicationContext ?: context
            isInitialized = true
            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
            MobileAds.initialize(appContext) {
                loadRewardedAd(appContext)
            }
        } catch (_: Throwable) {
            // Keep app functional in headless/unit-test or offline environments
        }
    }

    fun loadRewardedAd(context: Context) {
        if (isCloudEmulatorWithoutRenderNode() || isLoadingRewardedAd || rewardedAd != null) return
        try {
            val appContext = context.applicationContext ?: context
            isLoadingRewardedAd = true
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                appContext,
                REWARDED_AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        isLoadingRewardedAd = false
                        _isRewardedAdReady.value = true
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        rewardedAd = null
                        isLoadingRewardedAd = false
                        _isRewardedAdReady.value = false
                    }
                }
            )
        } catch (_: Throwable) {
            rewardedAd = null
            isLoadingRewardedAd = false
            _isRewardedAdReady.value = false
        }
    }

    /**
     * Shows a Normal Rewarded Ad only when voluntarily initiated by the user.
     * Grants the reward ONLY when the official OnUserEarnedRewardListener callback fires.
     */
    fun showRewardedAd(
        context: Context,
        onRewardEarned: () -> Unit,
        onAdMessage: (String) -> Unit
    ) {
        val activity = context.findActivity()
        val currentAd = rewardedAd

        if (activity == null || currentAd == null) {
            loadRewardedAd(context)
            onAdMessage("ഇപ്പോൾ പരസ്യം ലഭ്യമല്ല. ദയവായി അല്പം കഴിഞ്ഞ് വീണ്ടും ശ്രമിക്കുക. ആപ്പിലെ മറ്റ് ജ്യോതിഷ സേവനങ്ങൾ തുടർന്നും ഉപയോഗിക്കാം.")
            return
        }

        var userEarnedReward = false

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                _isRewardedAdReady.value = false
                // Preload another Rewarded Ad for future use
                loadRewardedAd(activity.applicationContext)

                if (!userEarnedReward) {
                    onAdMessage("പരസ്യം പൂർണ്ണമായി കാണാത്തതിനാൽ അധിക ജ്യോതിഷ വിവരങ്ങൾ തുറന്നിട്ടില്ല.")
                }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                rewardedAd = null
                _isRewardedAdReady.value = false
                loadRewardedAd(activity.applicationContext)
                onAdMessage("ഇപ്പോൾ പരസ്യം ലഭ്യമല്ല. ദയവായി അല്പം കഴിഞ്ഞ് വീണ്ടും ശ്രമിക്കുക.")
            }
        }

        currentAd.show(
            activity,
            OnUserEarnedRewardListener {
                userEarnedReward = true
                onRewardEarned()
            }
        )
    }

    fun Context.findActivity(): Activity? {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) return ctx
            ctx = ctx.baseContext
        }
        return null
    }
}
