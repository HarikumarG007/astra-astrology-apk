package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.model.CompleteJathakamReport
import com.example.util.AdMobManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * Non-intrusive Google Mobile Ads Banner View using the exact configured Banner Ad Unit ID.
 * Placed inside the Scaffold bottomBar so it never covers buttons, navigation, text,
 * important controls, astrology results, or input fields.
 * Collapses gracefully when an ad fails to load or is unavailable.
 */
@Composable
fun AstraAdMobBanner(
    modifier: Modifier = Modifier
) {
    val isInspection = LocalInspectionMode.current
    if (isInspection || AdMobManager.isCloudEmulatorWithoutRenderNode()) return

    var adLoadFailed by remember { mutableStateOf(false) }

    if (!adLoadFailed) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = modifier
                .fillMaxWidth()
                .testTag("admob_banner_container")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    factory = { context ->
                        AdView(context).apply {
                            setAdSize(AdSize.BANNER)
                            adUnitId = AdMobManager.BANNER_AD_UNIT_ID
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    adLoadFailed = false
                                }

                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    adLoadFailed = true
                                }
                            }
                            try {
                                loadAd(AdRequest.Builder().build())
                            } catch (_: Throwable) {
                                adLoadFailed = true
                            }
                        }
                    }
                )
            }
        }
    }
}

/**
 * Voluntary Normal Rewarded Ad feature card for unlocking additional detailed astrology predictions.
 * Never automatically triggers an ad; grants the reward ONLY after the official
 * Google Mobile Ads OnUserEarnedRewardListener callback confirms the user earned it.
 */
@Composable
fun RewardedDetailedAstrologyCard(
    report: CompleteJathakamReport,
    isUnlocked: Boolean,
    onUnlockEarned: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var localFeedbackMessage by remember { mutableStateOf<String?>(null) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("rewarded_astrology_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.AutoAwesome,
                    contentDescription = if (isUnlocked) "Unlocked Detailed Prediction" else "Watch an Ad to Unlock Detailed Prediction",
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isUnlocked) {
                            "സമ്പൂർണ്ണ അധിക ജ്യോതിഷ വിശകലനം (Detailed Prediction Unlocked)"
                        } else {
                            "Watch an Ad to Unlock Detailed Prediction"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "പരസ്യം കണ്ട് അധിക ജ്യോതിഷ ഫലവിശകലനം തുറക്കുക",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!isUnlocked) {
                Text(
                    text = "ഈ ചെറിയ പരസ്യം സ്വമേധയാ പൂർണ്ണമായി കാണുന്നതിലൂടെ താഴെ പറയുന്ന അധിക ജ്യോതിഷ വിവരങ്ങൾ നിങ്ങൾക്ക് സൗജന്യമായി തുറക്കാവുന്നതാണ്:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("• വിപുലമായ ജീവിതാദ്ധ്യായ വിശകലനം (Extended Life & Lagna Analysis)", style = MaterialTheme.typography.bodyMedium)
                    Text("• അധിക രാജയോഗ-ധനയോഗ വിശദീകരണം (Additional Yoga Explanations)", style = MaterialTheme.typography.bodyMedium)
                    Text("• തൊഴിൽ ഉന്നതിയും കരിയർ ആഴത്തിലുള്ള വിലയിരുത്തലും (Additional Career Analysis)", style = MaterialTheme.typography.bodyMedium)
                    Text("• വിവാഹം, കുടുംബജീവിതം & പങ്കാളി പൊരുത്ത സൂചനകൾ (Additional Marriage Analysis)", style = MaterialTheme.typography.bodyMedium)
                    Text("• ദശാ-അപഹാര-ഗോചര സൂക്ഷ്മ ഫലങ്ങളും പരിഹാര നിർദ്ദേശങ്ങളും (Additional Dasha & Report Content)", style = MaterialTheme.typography.bodyMedium)
                }

                if (localFeedbackMessage != null) {
                    Text(
                        text = localFeedbackMessage!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.testTag("rewarded_ad_feedback_message")
                    )
                }

                Button(
                    onClick = {
                        localFeedbackMessage = null
                        AdMobManager.showRewardedAd(
                            context = context,
                            onRewardEarned = {
                                localFeedbackMessage = null
                                onUnlockEarned()
                                onShowMessage("അധിക ജ്യോതിഷ ഫലവിശകലനം വിജയകരമായി തുറന്നു!")
                            },
                            onAdMessage = { msg ->
                                localFeedbackMessage = msg
                                onShowMessage(msg)
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .testTag("btn_watch_rewarded_ad")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleFilled,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Watch an Ad to Unlock Detailed Prediction",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            } else {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                val activeMaha = report.mahadashas.firstOrNull { it.isCurrent } ?: report.mahadashas.firstOrNull()
                val activeAntar = activeMaha?.antardashas?.firstOrNull { it.isCurrent } ?: activeMaha?.antardashas?.firstOrNull()
                val topYogas = report.yogasAndDoshas.take(3).joinToString(", ") { it.nameMalayalam }

                Text(
                    text = "1. വിപുലമായ ലഗ്ന-നക്ഷത്ര ആത്മവിശകലനം (Extended Life Analysis)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${report.lagnaRashi.malayalamName} ലഗ്നവും ${report.chandraRashi.malayalamName} കൂറും ${report.janmaNakshatra.malayalamName} (${report.janmaPada}-ാം പാദം) നക്ഷത്രവും ചേർന്ന ജാതകഘടന ആത്മവിശ്വാസവും ദീർഘവീക്ഷണവും നൽകുന്നു. ലഗ്നാധിപന്റെ സ്ഥിതിയും അഷ്ടകവർഗ്ഗ ബലവും ജീവിതത്തിലെ നിർണ്ണായക ഘട്ടങ്ങളിൽ സ്വന്തം പരിശ്രമത്തിലൂടെ ഉയർച്ച നേടാൻ സഹായിക്കുന്നു.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "2. അധിക യോഗ-ദോഷ സൂക്ഷ്മ വിശദീകരണം (Additional Yoga Explanations)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ജാതകത്തിൽ തെളിഞ്ഞുനിൽക്കുന്ന പ്രധാന യോഗങ്ങൾ ($topYogas) കേന്ദ്ര-ത്രികോണ ഭാവങ്ങളുടെ പരസ്പര ബന്ധത്തിലൂടെ കർമ്മപുഷ്ടിയും സാമ്പത്തിക ഭദ്രതയും സമ്മാനിക്കുന്നു. അനുകൂല ദശാകാലങ്ങളിൽ ഈ യോഗങ്ങൾ പൂർണ്ണ ഫലപ്രാപ്തി നൽകും.",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "3. തൊഴിൽ-സർക്കാർ-PSC ആഴത്തിലുള്ള വിലയിരുത്തൽ (Additional Career Analysis)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${report.careerGeneralReport.summaryMalayalam} പത്താം ഭാവത്തിന്റെയും ലഗ്നത്തിന്റെയും നവാംശക ബലം പരിശോധിക്കുമ്പോൾ മത്സരപ്പരീക്ഷകളിലും ഭരണ-ഔദ്യോഗിക രംഗത്തും സ്ഥിരതയുള്ള മുന്നേറ്റത്തിന് മികച്ച സാധ്യത കാണുന്നു. ${report.careerGeneralReport.supportivePeriodsMal}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "4. വിവാഹ-പങ്കാളി-കുടുംബ വിശകലനം (Additional Marriage Analysis)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${report.marriageReport.summaryMalayalam} ഏഴാം ഭാവവും ശുക്ര-വ്യാഴ സ്ഥിതിയും കുടുംബജീവിതത്തിൽ പരസ്പര ബഹുമാനവും വൈകാരിക ഐക്യവും ഉറപ്പാക്കുന്നു. ${report.partnerPersonalityReport.summaryMalayalam}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "5. ദശാ-അപഹാര-ഗോചര സൂക്ഷ്മ ഫലവും ധർമ്മ മാർഗ്ഗനിർദ്ദേശവും (Additional Dasha & Report Content)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "നിലവിലെ ${activeMaha?.lord?.malayalamName ?: "മഹാ"} ദശയിൽ ${activeAntar?.antardashaLord?.malayalamName ?: "അന്തർ"} അപഹാരം പുരോഗമിക്കുമ്പോൾ വ്യാഴ-ശനി ഗോചര ഫലങ്ങൾ സംയോജിപ്പിച്ച് ശ്രദ്ധാപൂർവ്വം തീരുമാനങ്ങൾ എടുക്കുക. ${report.spiritualityReport.summaryMalayalam}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
