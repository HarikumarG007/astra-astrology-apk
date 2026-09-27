package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.engine.AstronomicalEphemerisEngine
import com.example.model.CompleteJathakamReport
import com.example.ui.components.KeralaRashiChakraView
import com.example.ui.components.PeriodClassificationBadge
import com.example.ui.components.YinYangBalanceMedallion
import com.example.util.JathakamPdfGenerator
import com.example.viewmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    report: CompleteJathakamReport,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNavigateTab: (MainNavTab) -> Unit,
    onNavigateJathakamSub: (JathakamSubTab) -> Unit,
    onNavigatePredictionSub: (PredictionSubTab) -> Unit,
    onNavigateMoreSub: (MoreSubScreen) -> Unit,
    onRequestSavePrompt: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Banner Card with Yin-Yang Visual Harmony Balance
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(215.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_kerala_astrology_hero),
                        contentDescription = "Astra Astrology Malayalam Hero Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x99080D1A),
                                        Color(0xE60B101E)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                YinYangBalanceMedallion(
                                    size = 38.dp,
                                    onClick = onToggleTheme,
                                    modifier = Modifier.testTag("yin_yang_theme_toggle")
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Astra Astrology Malayalam",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color(0xFFFFD166),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "കേരള പാരമ്പര്യ സമ്പൂർണ്ണ വൈദിക ജ്യോതിഷം",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFFBF7EE),
                                        fontSize = 12.5.sp
                                    )
                                }
                            }
                            IconButton(
                                onClick = onRequestSavePrompt,
                                modifier = Modifier.testTag("home_save_horoscope_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Save Horoscope Explicitly",
                                    tint = Color(0xFFFFD166)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "${report.birthData.name} • ${report.birthData.placeNameMalayalam} (${report.birthData.districtMalayalam})",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ലഗ്നം: ${report.lagnaRashi.malayalamName} • കൂറ്: ${report.chandraRashi.malayalamName} • നക്ഷത്രം: ${report.janmaNakshatra.malayalamName} (പാദം ${report.janmaPada})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFFFE39F)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${AstronomicalEphemerisEngine.ENGINE_PUBLIC_NAME} • സ്വകാര്യതാ സംരക്ഷിതം",
                                fontSize = 11.sp,
                                color = Color(0xFFB8C7E0)
                            )
                        }
                    }
                }
            }
        }

        // 2. Primary 8 Quick Action Buttons Grid (Exact required buttons)
        item {
            Text(
                text = "പ്രധാന സേവനങ്ങൾ (Quick Actions)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            data class QuickAction(
                val tag: String,
                val labelMal: String,
                val subEng: String,
                val icon: ImageVector,
                val onClick: () -> Unit
            )

            val actions = listOf(
                QuickAction("btn_my_jathakam", "എന്റെ ജാതകം", "Birth & Chart", Icons.Default.AutoAwesome) {
                    onNavigateJathakamSub(JathakamSubTab.BIRTH_DETAILS)
                },
                QuickAction("btn_detailed_pred", "വിശദമായ ഫലം", "Predictions", Icons.Default.MenuBook) {
                    onNavigatePredictionSub(PredictionSubTab.CAREER_GOVT_PSC)
                },
                QuickAction("btn_dasha", "ദശ", "Vimshottari", Icons.Default.Timeline) {
                    onNavigateTab(MainNavTab.DASHA)
                },
                QuickAction("btn_yogas", "യോഗങ്ങൾ", "Yogas & Doshas", Icons.Default.Stars) {
                    onNavigateMoreSub(MoreSubScreen.YOGAS_DOSHAS)
                },
                QuickAction("btn_panchanga", "പഞ്ചാംഗം", "Panchanga", Icons.Default.WbSunny) {
                    onNavigateMoreSub(MoreSubScreen.PANCHANGA)
                },
                QuickAction("btn_career", "Career", "തൊഴിൽ & PSC", Icons.Default.Work) {
                    onNavigatePredictionSub(PredictionSubTab.CAREER_GOVT_PSC)
                },
                QuickAction("btn_marriage", "Marriage", "വിവാഹം & പൊരുത്തം", Icons.Default.Favorite) {
                    onNavigatePredictionSub(PredictionSubTab.MARRIAGE_PARTNER)
                },
                QuickAction("btn_reports", "Reports", "സമ്പൂർണ്ണ PDF", Icons.Default.PictureAsPdf) {
                    onNavigateMoreSub(MoreSubScreen.COMPLETE_REPORT_PDF)
                }
            )

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                for (rowItems in actions.chunked(2)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (act in rowItems) {
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 64.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { act.onClick() }
                                    .testTag(act.tag)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.size(38.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = act.icon,
                                                contentDescription = act.labelMal,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = act.labelMal,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = act.subEng,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2B. Direct APK & Full HD Logo Cloud Upload Link Generator (litter.catbox.moe)
        item {
            CloudDirectDownloadLinkCard()
        }

        // 3. Today's / Active Panchanga Summary Card (Tithi, Nakshatra, Karana, Vara, Yoga)
        item {
            val p = report.panchanga
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "പഞ്ചാംഗ സംഗ്രഹം (Panchanga)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { onNavigateMoreSub(MoreSubScreen.PANCHANGA) }) {
                            Text("വിശദമായി →", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    PanchangaKeyValueRow("തിഥി (Tithi)", "${p.tithiMalayalam} (${p.pakshaMalayalam})")
                    PanchangaKeyValueRow("നക്ഷത്രം (Nakshatra)", "${p.nakshatra.malayalamName} (പാദം ${p.nakshatraPada}) • അധിപൻ: ${p.nakshatra.lord.malayalamName}")
                    PanchangaKeyValueRow("കരണം (Karana)", "${p.karana.nameMalayalam} (${p.karana.nameEnglish}) • ${p.karana.natureMal}")
                    PanchangaKeyValueRow("നിത്യയോഗം & വാരം", "${p.nityaYogaMalayalam} • ${p.varaMalayalam}")
                    PanchangaKeyValueRow("മലയാള മാസം", "${p.malayalamMasa} • ${p.samvatsaraMalayalam}")
                    PanchangaKeyValueRow("സൂര്യോദയം / അസ്തമയം", "${p.sunriseFormatted} / ${p.sunsetFormatted}")
                }
            }
        }

        // 4. Current Mahadasha, Antardasha & Pratyantardasha Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "നിലവിലെ ദശാകാലം (Current Dasha)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        PeriodClassificationBadge(classification = report.currentAntardasha.classification)
                    }

                    Text(
                        text = "• മഹാദശ: ${report.currentMahadasha.lord.malayalamName} (${report.currentMahadasha.startDateFormatted} – ${report.currentMahadasha.endDateFormatted})",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• അപഹാരം (Antardasha): ${report.currentAntardasha.antardashaLord.malayalamName} (${report.currentAntardasha.startDateFormatted} – ${report.currentAntardasha.endDateFormatted})",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• പ്രത്യന്തരദശ (Pratyantardasha): ${report.currentPratyantardasha.pratyantardashaLord.malayalamName} (${report.currentPratyantardasha.startDateFormatted} – ${report.currentPratyantardasha.endDateFormatted})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    LinearProgressIndicator(
                        progress = { report.currentMahadasha.elapsedPercentage / 100f },
                        modifier = Modifier.fillMaxWidth().height(7.dp).clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = report.currentAntardasha.careerAndPscMal,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 5. Interactive Kerala Rashi Chakra Preview on Home
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "കേരള ശൈലി രാശിചക്രം (D1 Rashi Chakra)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    KeralaRashiChakraView(
                        chartTitleMalayalam = "രാശിചക്രം",
                        chartSubtitleMalayalam = "${report.janmaNakshatra.malayalamName} • ${report.chandraRashi.malayalamName}",
                        lagnaRashi = report.lagnaRashi,
                        planetSigns = report.planets.associate { it.planet to it.rashi }
                    )
                }
            }
        }

        // 6. Important Transit & Quick Prediction Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "പ്രധാന ഗോചരവും ഇന്നത്തെ ഫലസൂചനയും",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = report.periodicHoroscope.dailyMal,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Text(
                        text = report.transitReport.jupiterTransitDetailedMal,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = report.transitReport.saturnTransitDetailedMal,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PanchangaKeyValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.6f)
        )
    }
}

@Composable
fun CloudDirectDownloadLinkCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isUploadingApk by remember { mutableStateOf(false) }
    var isUploadingLogo by remember { mutableStateOf(false) }
    var apkLink by remember { mutableStateOf<String?>(null) }
    var logoLink by remember { mutableStateOf<String?>(null) }
    var statusNote by remember { mutableStateOf<String?>(null) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .testTag("cloud_download_link_card")
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
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = "Cloud Download Link Generator",
                    tint = MaterialTheme.colorScheme.primary
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "APK & Full HD ലോഗോ ഡൗൺലോഡ് ലിങ്ക് (litter.catbox.moe)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ഒറ്റ ക്ലിക്കിൽ litter.catbox.moe വഴി നേരിട്ട് ഡൗൺലോഡ് ചെയ്യാനുള്ള ലിങ്ക് തയ്യാറാക്കുക",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (!isUploadingApk) {
                            isUploadingApk = true
                            statusNote = "APK ഫയൽ litter.catbox.moe-ലേക്ക് അപ്‌ലോഡ് ചെയ്യുന്നു... (10–25 സെക്കൻഡ് കാത്തിരിക്കുക)"
                            scope.launch {
                                val result = withContext(Dispatchers.IO) {
                                    val apkFile = JathakamPdfGenerator.exportInstalledApkFile(context)
                                    JathakamPdfGenerator.uploadFileToCloudLink(
                                        file = apkFile,
                                        uploadFileName = "Astra_Astrology_Malayalam.apk",
                                        mimeType = "application/vnd.android.package-archive"
                                    )
                                }
                                isUploadingApk = false
                                result.onSuccess { url ->
                                    apkLink = url
                                    statusNote = "APK ഡൗൺലോഡ് ലിങ്ക് തയ്യാറായി!"
                                }.onFailure { err ->
                                    statusNote = "അപ്‌ലോഡ് തടസ്സം: ${err.localizedMessage ?: "വീണ്ടും ശ്രമിക്കുക"}"
                                }
                            }
                        }
                    },
                    enabled = !isUploadingApk,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("btn_upload_apk_litterbox")
                ) {
                    if (isUploadingApk) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Uploading...", fontSize = 12.sp)
                    } else {
                        Icon(Icons.Default.Android, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("APK ലിങ്ക് എടുക്കുക", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = {
                        if (!isUploadingLogo) {
                            isUploadingLogo = true
                            statusNote = "Full HD ലോഗോ litter.catbox.moe-ലേക്ക് അപ്‌ലോഡ് ചെയ്യുന്നു..."
                            scope.launch {
                                val result = withContext(Dispatchers.IO) {
                                    val logoFile = JathakamPdfGenerator.generateFullHdLogoFile(context)
                                    JathakamPdfGenerator.uploadFileToCloudLink(
                                        file = logoFile,
                                        uploadFileName = "Astra_Astrology_Malayalam_Logo_FullHD.png",
                                        mimeType = "image/png"
                                    )
                                }
                                isUploadingLogo = false
                                result.onSuccess { url ->
                                    logoLink = url
                                    statusNote = "Full HD ലോഗോ ഡൗൺലോഡ് ലിങ്ക് തയ്യാറായി!"
                                }.onFailure { err ->
                                    statusNote = "അപ്‌ലോഡ് തടസ്സം: ${err.localizedMessage ?: "വീണ്ടും ശ്രമിക്കുക"}"
                                }
                            }
                        }
                    },
                    enabled = !isUploadingLogo,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp)
                        .testTag("btn_upload_logo_litterbox")
                ) {
                    if (isUploadingLogo) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Uploading...", fontSize = 12.sp)
                    } else {
                        Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("HD ലോഗോ ലിങ്ക്", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (statusNote != null) {
                Text(
                    text = statusNote!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (apkLink != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DIRECT APK LINK (72 മണിക്കൂർ സജീവം):",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            SelectionContainer {
                                Text(
                                    text = apkLink!!,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        TextButton(
                            onClick = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                cm?.setPrimaryClip(ClipData.newPlainText("APK Download Link", apkLink!!))
                                statusNote = "APK ലിങ്ക് കോപ്പി ചെയ്തു: $apkLink"
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy APK Link", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy")
                        }
                    }
                }
            }

            if (logoLink != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "FULL HD LOGO LINK (2048×2048 PNG):",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                            SelectionContainer {
                                Text(
                                    text = logoLink!!,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        TextButton(
                            onClick = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                cm?.setPrimaryClip(ClipData.newPlainText("Logo Download Link", logoLink!!))
                                statusNote = "ലോഗോ ലിങ്ക് കോപ്പി ചെയ്തു: $logoLink"
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Logo Link", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy")
                        }
                    }
                }
            }
        }
    }
}
