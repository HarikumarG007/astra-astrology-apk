package com.example.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            val appContext = context.applicationContext ?: context
            val requestConfiguration = RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                .build()
            MobileAds.setRequestConfiguration(requestConfiguration)
            MobileAds.initialize(appContext) {
                isInitialized = true
                loadRewardedAd(appContext)
            }
            isInitialized = true
            loadRewardedAd(appContext)
        } catch (_: Throwable) {
            // Keep app functional in headless/unit-test or offline environments
        }
    }

    fun loadRewardedAd(context: Context) {
        if (isLoadingRewardedAd || rewardedAd != null) return
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
