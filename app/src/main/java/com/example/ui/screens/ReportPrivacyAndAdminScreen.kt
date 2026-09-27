package com.example.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.SavedHoroscopeEntity
import com.example.engine.AstronomicalEphemerisEngine
import com.example.model.CompleteJathakamReport
import com.example.util.JathakamPdfGenerator
import com.example.viewmodel.AdminConfigState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun CompleteReportAndPdfView(
    report: CompleteJathakamReport,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isGeneratingPdf by remember { mutableStateOf(false) }
    var generatedPdfFile by remember { mutableStateOf<File?>(null) }
    var previewBitmaps by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var showPreviewPages by remember { mutableStateOf(false) }
    var pdfFeedback by remember { mutableStateOf<String?>(null) }

    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { destUri ->
        val src = generatedPdfFile
        if (destUri != null && src != null && src.exists()) {
            scope.launch(Dispatchers.IO) {
                context.contentResolver.openOutputStream(destUri)?.use { outStream ->
                    src.inputStream().use { inStream ->
                        inStream.copyTo(outStream)
                    }
                }
                withContext(Dispatchers.Main) {
                    pdfFeedback = "PDF റിപ്പോർട്ട് നിങ്ങളുടെ ഉപകരണത്തിൽ വിജയകരമായി സേവ് ചെയ്തു."
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("സമ്പൂർണ്ണ മലയാളം ജാതക റിപ്പോർട്ട് & PDF", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("complete_report_pdf_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // PDF Action Toolbar Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "PDF ജാതക റിപ്പോർട്ട് (Generate / Preview / Share / Save PDF)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Astra Astrology Malayalam • Contact: harikumarg004@gmail.com • മലയാളം ഫോണ്ട് പിന്തുണയോടെയുള്ള മൾട്ടി-പേജ് PDF റിപ്പോർട്ട്.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (isGeneratingPdf) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(22.dp))
                                Text("മലയാളം PDF റിപ്പോർട്ട് തയ്യാറാക്കുന്നു...")
                            }
                        }

                        if (pdfFeedback != null) {
                            Text(
                                text = pdfFeedback!!,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isGeneratingPdf = true
                                        val (file, pages) = withContext(Dispatchers.IO) {
                                            val f = JathakamPdfGenerator.generatePdfFile(context, report)
                                            val bmps = JathakamPdfGenerator.renderPdfPagesForPreview(f)
                                            f to bmps
                                        }
                                        generatedPdfFile = file
                                        previewBitmaps = pages
                                        showPreviewPages = true
                                        isGeneratingPdf = false
                                        pdfFeedback = "PDF തയ്യാറായി (${pages.size} പേജുകൾ). താഴെ പ്രിവ്യൂ കാണാം."
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("btn_generate_preview_pdf")
                            ) {
                                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Generate & Preview")
                            }

                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val file = generatedPdfFile ?: withContext(Dispatchers.IO) {
                                            JathakamPdfGenerator.generatePdfFile(context, report)
                                        }.also { generatedPdfFile = it }
                                        JathakamPdfGenerator.sharePdf(context, file)
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("btn_share_pdf")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share PDF")
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    if (generatedPdfFile == null) {
                                        isGeneratingPdf = true
                                        generatedPdfFile = withContext(Dispatchers.IO) {
                                            JathakamPdfGenerator.generatePdfFile(context, report)
                                        }
                                        isGeneratingPdf = false
                                    }
                                    savePdfLauncher.launch("Astra_Jathakam_${report.birthData.name.replace(" ", "_")}.pdf")
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("btn_save_pdf")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save PDF to Device (ഫോണിലേക്ക് സേവ് ചെയ്യുക)")
                        }
                    }
                }
            }

            // In-App PDF Page Preview Renderer
            if (showPreviewPages && previewBitmaps.isNotEmpty()) {
                item {
                    Text(
                        text = "PDF പ്രിവ്യൂ (${previewBitmaps.size} പേജുകൾ):",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(previewBitmaps.indices.toList()) { pageIdx ->
                    val bmp = previewBitmaps[pageIdx]
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "PDF Page ${pageIdx + 1} / ${previewBitmaps.size}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Image(
                                bitmap = bmp.asImageBitmap(),
                                contentDescription = "PDF Preview Page ${pageIdx + 1}",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
            }

            // Comprehensive On-Screen Long-Form Report (All 30+ Required Sections)
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "സമ്പൂർണ്ണ ജാതക റിപ്പോർട്ട് — ${report.birthData.name}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "ജനന സ്ഥലം: ${report.birthData.placeNameMalayalam} (${report.birthData.districtMalayalam}) • ലഗ്നം: ${report.lagnaRashi.malayalamName} • ചന്ദ്രരാശി: ${report.chandraRashi.malayalamName} • നക്ഷത്രം: ${report.janmaNakshatra.malayalamName} (പാദം ${report.janmaPada}) • കരണം: ${report.panchanga.karana.nameMalayalam}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Text(
                            text = "മഹാദശ & അപഹാര സംഗ്രഹം:\n${report.currentMahadasha.detailedInterpretationMal}\n\n${report.currentAntardasha.detailedInterpretationMal}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Text(
                            text = "ഗോചര സംഗ്രഹം:\n${report.transitReport.jupiterTransitDetailedMal}\n\n${report.transitReport.saturnTransitDetailedMal}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            val allDomains = listOf(
                report.careerGeneralReport,
                report.educationReport,
                report.marriageReport,
                report.partnerPersonalityReport,
                report.partnerAppearanceReport,
                report.financeReport,
                report.businessReport,
                report.propertyVehicleReport,
                report.foreignTravelReport,
                report.familyReport,
                report.childrenReport,
                report.spiritualityReport,
                report.healthLifestyleReport
            )
            items(allDomains) { domain ->
                LifeDomainCard(domain = domain)
            }
        }
    }
}

@Composable
fun SavedHoroscopesAndPrivacyControlView(
    savedList: List<SavedHoroscopeEntity>,
    onRequestSaveCurrent: () -> Unit,
    onLoadSaved: (SavedHoroscopeEntity) -> Unit,
    onDeleteSingle: (Long) -> Unit,
    onDeleteAll: () -> Unit,
    onClearSession: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("സ്വകാര്യതാ നിയന്ത്രണവും സംരക്ഷിച്ച ജാതകങ്ങളും", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("privacy_control_screen_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Privacy-First Architecture (സ്ഥിരമായി സംരക്ഷിക്കൽ: OFF by Default)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• നിങ്ങളുടെ പേര്, ജനന തീയതി, ജനന സമയം, സ്ഥലം, ഗ്രഹനില, ദശാഫലങ്ങൾ എന്നിവ ഈ ആപ്പ് സ്വയമേവ ഒരിടത്തും (Cloud, Firebase, SQLite, SharedPreferences, Analytics, Crash Logs) സംരക്ഷിക്കുന്നില്ല.\n• നിങ്ങൾ സ്വയം 'സംരക്ഷിക്കുക' (Save Horoscope) തിരഞ്ഞെടുത്താൽ മാത്രമേ ഉപകരണത്തിൽ സംരക്ഷിക്കപ്പെടുകയുള്ളൂ.\n• ഒരു ഉപയോക്താവിന്റെയും ജാതക വിവരങ്ങൾ AI പരിശീലനത്തിനോ മറ്റ് ഉപയോക്താക്കളുടെ ഫലങ്ങൾക്കോ ഒരിക്കലും ഉപയോഗിക്കുന്നില്ല.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onRequestSaveCurrent,
                                modifier = Modifier.weight(1f).testTag("btn_opt_in_save_horoscope")
                            ) {
                                Text("നിലവിലെ ജാതകം സംരക്ഷിക്കുക")
                            }
                            OutlinedButton(
                                onClick = onClearSession,
                                modifier = Modifier.weight(1f).testTag("btn_clear_session")
                            ) {
                                Text("സെഷൻ റീസെറ്റ്")
                            }
                        }

                        Button(
                            onClick = onDeleteAll,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth().testTag("btn_delete_all_horoscope_data")
                        ) {
                            Icon(Icons.Default.DeleteForever, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete All Horoscope Data (എല്ലാ വിവരങ്ങളും മായ്ക്കുക)")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "സംരക്ഷിച്ച ജാതകങ്ങൾ (${savedList.size}):",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (savedList.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "നിലവിൽ സംരക്ഷിച്ച ജാതകങ്ങളൊന്നുമില്ല (സ്വകാര്യതാ ക്രമീകരണം: സജീവം).",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(savedList) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    text = "${item.day}/${item.month}/${item.year} • ${item.placeNameMalayalam} • ${item.nakshatraMalayalam}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Row {
                                TextButton(onClick = { onLoadSaved(item) }) {
                                    Text("തുറക്കുക")
                                }
                                OutlinedButton(
                                    onClick = { onDeleteSingle(item.id) },
                                    modifier = Modifier.testTag("btn_delete_horoscope_${item.id}")
                                ) {
                                    Text("Delete Horoscope")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacyPolicyView(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("സ്വകാര്യതാ നയം (Privacy Policy)", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("privacy_policy_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Privacy Policy — Astra Astrology Malayalam",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Developer & Data Protection Contact: harikumarg004@gmail.com",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = """
                            1. ശേഖരിക്കുന്ന വിവരങ്ങൾ (Information Collected & Birth Information):
                            ജാതക ഗണിതത്തിനായി ഉപയോക്താവ് നൽകുന്ന പേര്, ലിംഗഭേദം, ജനന തീയതി, ജനന സമയം, ജനന സ്ഥലം, അക്ഷാംശം-രേഖാംശം എന്നിവ നിലവിലെ സെഷനിലെ ഗണിത പ്രക്രിയയ്ക്ക് മാത്രമായി ഉപയോഗിക്കുന്നു.
                            
                            2. പ്രോസസ്സിംഗും സംഭരണവും (Processing & Storage - Off by Default):
                            സ്വതവേ (By Default) നിങ്ങളുടെ ജനന വിവരങ്ങളോ ജാതക ഫലങ്ങളോ സെർവറുകളിലോ ക്ലൗഡ് ഡാറ്റാബേസുകളിലോ ഉപകരണത്തിലോ സ്വയമേവ സംരക്ഷിക്കുന്നില്ല. ഉപയോക്താവ് സ്വയം "നിങ്ങളുടെ ജാതക വിവരങ്ങൾ സംരക്ഷിക്കണോ?" എന്ന ചോദ്യത്തിന് "സംരക്ഷിക്കുക" എന്ന് അനുമതി നൽകിയാൽ മാത്രമേ ഉപകരണത്തിലെ പ്രാദേശിക ഡാറ്റാബേസിൽ സംരക്ഷിക്കപ്പെടുകയുള്ളൂ.
                            
                            3. ഡാറ്റ പങ്കിടലും മൂന്നാം കക്ഷി സേവനങ്ങളും (Sharing, Analytics, Crash Reporting & AI Isolation):
                            • വ്യക്തിഗത ജനന വിവരങ്ങൾ പരസ്യ കമ്പനികൾക്കോ അനലിറ്റിക്സ് സംവിധാനങ്ങൾക്കോ ക്രാഷ് ലോഗുകൾക്കോ കൈമാറുന്നില്ല.
                            • ഒരു ഉപയോക്താവിന്റെയും ജാതക വിവരങ്ങൾ AI മോഡൽ പരിശീലനത്തിനോ മറ്റ് ഉപയോക്താക്കൾക്ക് ഫലം നൽകാനോ ഉപയോഗിക്കുന്നില്ല. ഓരോ സെഷനും പൂർണ്ണമായി വേർതിരിക്കപ്പെട്ടിരിക്കുന്നു (Isolated Session).
                            
                            4. പരസ്യങ്ങളും അനലിറ്റിക്സും (Advertising & Analytics):
                            ആപ്പിൽ പരസ്യങ്ങൾ (Banner, Interstitial, Rewarded, App Open) അഡ്മിൻ തലത്തിൽ സജീവമാക്കുകയാണെങ്കിൽ, പരസ്യ SDK-കൾ ഉപകരണ ഐഡി / പരസ്യ പ്രദർശന വിവരങ്ങൾ Google Play നയങ്ങൾക്കനുസൃതമായി കൈകാര്യം ചെയ്യാം; എന്നാൽ ജാതക/ജനന വിവരങ്ങൾ പരസ്യ SDK-കളുമായി ഒരിക്കലും പങ്കിടുന്നില്ല.
                            
                            5. ഡാറ്റ നീക്കം ചെയ്യലും ഉപയോക്തൃ അവകാശങ്ങളും (Retention, Deletion & User Rights):
                            ഉപയോക്താവിന് എപ്പോൾ വേണമെങ്കിലും 'Delete Horoscope' വഴി വ്യക്തിഗത ജാതകവും 'Delete All Horoscope Data' വഴി മുഴുവൻ ഡാറ്റയും ഒറ്റ ക്ലിക്കിൽ പൂർണ്ണമായി മായ്ച്ചുകളയാം.
                            
                            6. കുട്ടികളുടെ സ്വകാര്യത (Children's Privacy):
                            കുട്ടികളുടെ വ്യക്തിഗത വിവരങ്ങൾ രക്ഷിതാക്കളുടെ അനുമതിയില്ലാതെ ശേഖരിക്കുന്നതല്ല.
                            
                            7. ജ്യോതിഷ നിരാകരണം (Astrology Disclaimer):
                            ഈ ആപ്പ് നൽകുന്ന ഫലങ്ങൾ പരമ്പരാഗത വൈദിക ജ്യോതിഷ സൂചനകൾ മാത്രമാണ്; വൈദ്യശാസ്ത്ര-നിയമ-സാമ്പത്തിക ഉപദേശങ്ങൾക്ക് പകരമല്ല.
                            
                            8. ബന്ധപ്പെടാൻ (Contact Us):
                            സ്വകാര്യതാ നയവുമായി ബന്ധപ്പെട്ട സംശയങ്ങൾക്ക്: harikumarg004@gmail.com
                            """.trimIndent(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TermsAndDataSafetyView(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("ഉപയോഗ നിബന്ധനകളും Data Safety വിവരങ്ങളും", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "ഉപയോഗ നിബന്ധനകൾ (Terms of Use)",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = """
                            • Astra Astrology Malayalam പരമ്പരാഗത കേരള-പരാശര ജ്യോതിഷ തത്വങ്ങളെ അടിസ്ഥാനമാക്കിയുള്ള ജാതക ഗണിതവും ഫലസൂചനകളുമാണ് നൽകുന്നത്.
                            • ജ്യോതിഷ ഫലങ്ങൾ സാധ്യതകളും പരമ്പരാഗത സൂചനകളും മാത്രമാണ്; ഭാവി സംഭവങ്ങൾക്കോ പരീക്ഷാ വിജയങ്ങൾക്കോ വിവാഹ തീയതികൾക്കോ ഉറപ്പുനൽകുന്നില്ല.
                            • ജീവിതത്തിലെ പ്രധാന തീരുമാനങ്ങൾ ഉപയോക്താക്കൾ സ്വന്തം വിവേചനബുദ്ധിയോടെ കൈക്കൊള്ളേണ്ടതാണ്.
                            • ഈ ആപ്ലിക്കേഷൻ അംഗീകൃത വൈദ്യശാസ്ത്ര (Medical), നിയമ (Legal), സാമ്പത്തിക (Financial) വിദഗ്ദ്ധരുടെ ഉപദേശത്തിന് പകരമല്ല.
                            • ബന്ധപ്പെടാനുള്ള വിലാസം: harikumarg004@gmail.com
                            """.trimIndent(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Google Play Data Safety Architecture Summary",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = """
                            • Personal Birth Data Collection: Ephemeral / Session-only in RAM by default. No automatic background transmission to external servers.
                            • Local Optional Database: Room SQLite database used strictly when user explicitly clicks 'സംരക്ഷിക്കുക' (Opt-in).
                            • Data Deletion Controls: In-app immediate deletion ('Delete Horoscope' & 'Delete All Horoscope Data').
                            • Cloud Backup: Disabled in AndroidManifest (android:allowBackup="false") to prevent unconsented cloud syncing of personal horoscopes.
                            • Third-Party Analytics / Crash Logs: Personal birth data is strictly excluded from logs and analytics.
                            """.trimIndent(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AboutAndContactView(
    onOpenAdminPanel: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var logoFeedback by remember { mutableStateOf<String?>(null) }
    var generatedLogoFile by remember { mutableStateOf<File?>(null) }
    var exportedApkFile by remember { mutableStateOf<File?>(null) }
    var isUploadingApk by remember { mutableStateOf(false) }
    var isUploadingLogo by remember { mutableStateOf(false) }
    var uploadedApkUrl by remember { mutableStateOf<String?>(null) }
    var uploadedLogoUrl by remember { mutableStateOf<String?>(null) }
    var apkFeedback by remember { mutableStateOf<String?>(null) }

    val saveLogoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("image/png")
    ) { destUri ->
        val src = generatedLogoFile
        if (destUri != null && src != null && src.exists()) {
            scope.launch(Dispatchers.IO) {
                context.contentResolver.openOutputStream(destUri)?.use { outStream ->
                    src.inputStream().use { inStream ->
                        inStream.copyTo(outStream)
                    }
                }
                withContext(Dispatchers.Main) {
                    logoFeedback = "Full HD ലോഗോ (2048×2048 PNG) നിങ്ങളുടെ ഉപകരണത്തിൽ സേവ് ചെയ്തു!"
                }
            }
        }
    }

    val saveApkLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/vnd.android.package-archive")
    ) { destUri ->
        val src = exportedApkFile
        if (destUri != null && src != null && src.exists()) {
            scope.launch(Dispatchers.IO) {
                context.contentResolver.openOutputStream(destUri)?.use { outStream ->
                    src.inputStream().use { inStream ->
                        inStream.copyTo(outStream)
                    }
                }
                withContext(Dispatchers.Main) {
                    apkFeedback = "APK ഫയൽ (app-debug.apk) നിങ്ങളുടെ ഉപകരണത്തിൽ സേവ് ചെയ്തു!"
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("About Astra Astrology Malayalam & Contact", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("about_screen_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "APK & Full HD ലോഗോ ഡൗൺലോഡ് ലിങ്ക് (litter.catbox.moe)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "താഴെയുള്ള ബട്ടണിൽ ക്ലിക്ക് ചെയ്താൽ ഈ ആപ്പിന്റെ APK ഫയലും Full HD ലോഗോയും നേരിട്ട് litter.catbox.moe-ലേക്ക് അപ്‌ലോഡ് ചെയ്ത് ഡൗൺലോഡ് ലിങ്ക് ഇവിടെ കാണിക്കും:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp
                        )

                        if (uploadedApkUrl != null) {
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "DIRECT APK DOWNLOAD LINK (72h):",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = uploadedApkUrl!!,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(uploadedApkUrl!!))
                                            apkFeedback = "APK ലിങ്ക് കോപ്പി ചെയ്തു: ${uploadedApkUrl!!}"
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Copy APK Link")
                                    }
                                }
                            }
                        }

                        if (uploadedLogoUrl != null) {
                            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "FULL HD LOGO DOWNLOAD LINK (72h):",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = uploadedLogoUrl!!,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(
                                        onClick = {
                                            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(uploadedLogoUrl!!))
                                            logoFeedback = "ലോഗോ ലിങ്ക് കോപ്പി ചെയ്തു: ${uploadedLogoUrl!!}"
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Copy Logo Link")
                                    }
                                }
                            }
                        }

                        if (apkFeedback != null) {
                            Text(
                                text = apkFeedback!!,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Button(
                            onClick = {
                                if (!isUploadingApk) {
                                    isUploadingApk = true
                                    apkFeedback = "APK ഫയൽ litter.catbox.moe-ലേക്ക് അപ്‌ലോഡ് ചെയ്യുന്നു... ദയവായി 10-20 സെക്കൻഡ് കാത്തിരിക്കുക..."
                                    scope.launch {
                                        val result = withContext(Dispatchers.IO) {
                                            val apk = JathakamPdfGenerator.exportInstalledApkFile(context)
                                            exportedApkFile = apk
                                            JathakamPdfGenerator.uploadFileToLitterbox(apk, "app-debug.apk")
                                        }
                                        isUploadingApk = false
                                        result.onSuccess { link ->
                                            uploadedApkUrl = link
                                            apkFeedback = "APK വിജയകരമായി അപ്‌ലോഡ് ചെയ്തു! ലിങ്ക്: $link"
                                        }.onFailure { err ->
                                            apkFeedback = "അപ്‌ലോഡ് തടസ്സപ്പെട്ടു (${err.message}). താഴെയുള്ള Save APK / Share APK ഉപയോഗിക്കാം."
                                        }
                                    }
                                }
                            },
                            enabled = !isUploadingApk,
                            modifier = Modifier.fillMaxWidth().testTag("btn_upload_apk_litterbox")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUploadingApk) "Uploading APK to litter.catbox.moe..." else "Get APK Link (litter.catbox.moe)",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                if (!isUploadingLogo) {
                                    isUploadingLogo = true
                                    logoFeedback = "Full HD ലോഗോ litter.catbox.moe-ലേക്ക് അപ്‌ലോഡ് ചെയ്യുന്നു..."
                                    scope.launch {
                                        val result = withContext(Dispatchers.IO) {
                                            val logo = JathakamPdfGenerator.generateFullHdLogoFile(context)
                                            generatedLogoFile = logo
                                            JathakamPdfGenerator.uploadFileToLitterbox(logo, "Astra_Astrology_Malayalam_Logo_FullHD.png")
                                        }
                                        isUploadingLogo = false
                                        result.onSuccess { link ->
                                            uploadedLogoUrl = link
                                            logoFeedback = "Full HD ലോഗോ ലിങ്ക് തയ്യാറായി: $link"
                                        }.onFailure { err ->
                                            logoFeedback = "അപ്‌ലോഡ് തടസ്സപ്പെട്ടു (${err.message})."
                                        }
                                    }
                                }
                            },
                            enabled = !isUploadingLogo,
                            modifier = Modifier.fillMaxWidth().testTag("btn_upload_logo_litterbox")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUploadingLogo) "Uploading Full HD Logo..." else "Get Full HD Logo Link (litter.catbox.moe)",
                                fontSize = 12.sp
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val apk = withContext(Dispatchers.IO) {
                                            JathakamPdfGenerator.exportInstalledApkFile(context)
                                        }
                                        exportedApkFile = apk
                                        saveApkLauncher.launch("app-debug.apk")
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("btn_save_local_apk")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save APK", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val apk = withContext(Dispatchers.IO) {
                                            JathakamPdfGenerator.exportInstalledApkFile(context)
                                        }
                                        exportedApkFile = apk
                                        JathakamPdfGenerator.shareApkFile(context, apk)
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("btn_share_local_apk")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share APK", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_app_icon),
                                contentDescription = "Astra Astrology Malayalam Full HD Logo",
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Full HD ആപ്പ് ലോഗോ (2048×2048 PNG)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ഇൻ-ആപ്പ് ആയി ഉയർന്ന ക്ലാരിറ്റിയിലുള്ള (Full HD / 2K PNG) ലോഗോ ഡൗൺലോഡ് ചെയ്യാനും ഷെയർ ചെയ്യാനും താഴെയുള്ള ബട്ടണുകൾ ഉപയോഗിക്കുക.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (logoFeedback != null) {
                            Text(
                                text = logoFeedback!!,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        val file = withContext(Dispatchers.IO) {
                                            JathakamPdfGenerator.generateFullHdLogoFile(context)
                                        }
                                        generatedLogoFile = file
                                        saveLogoLauncher.launch("Astra_Astrology_Malayalam_Logo_FullHD.png")
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("btn_save_fullhd_logo")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Full HD PNG", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        val file = withContext(Dispatchers.IO) {
                                            JathakamPdfGenerator.generateFullHdLogoFile(context)
                                        }
                                        generatedLogoFile = file
                                        JathakamPdfGenerator.shareFullHdLogo(context, file)
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("btn_share_fullhd_logo")
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share Full HD Logo", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Astra Astrology Malayalam",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "കേരള പാരമ്പര്യത്തിലുള്ള സമ്പൂർണ്ണ മലയാളം വൈദിക ജ്യോതിഷ ആപ്ലിക്കേഷൻ",
                            style = MaterialTheme.typography.titleMedium
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Text(
                            text = """
                            പ്രധാന സവിശേഷതകൾ:
                            • Kerala-style Vedic Astrology (കേരള ശൈലി ജാതകാവതരണം)
                            • ${AstronomicalEphemerisEngine.ENGINE_PUBLIC_NAME} (${AstronomicalEphemerisEngine.ENGINE_SUBTITLE})
                            • Detailed Vimshottari Dasha, Antardasha & Pratyantardasha Analysis
                            • Dynamic Classical Yoga & Dosha Detection Engine
                            • Comprehensive Malayalam Predictions (Career, PSC, KAS, Education, Marriage, Partner, Finance, Business, Property, Foreign Travel, Family, Children, Life Timeline)
                            • Complete Kerala Panchanga & Accurate 11-Karana Calculation
                            • Complete 14-District Kerala Location Database (Malayalam & English Search)
                            • Multi-Page Malayalam PDF Horoscope Report Generator
                            """.trimIndent(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Contact Us (ബന്ധപ്പെടാൻ)",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Developer & Support Email: harikumarg004@gmail.com",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "നിങ്ങളുടെ അഭിപ്രായങ്ങളും നിർദ്ദേശങ്ങളും harikumarg004@gmail.com എന്ന ഇമെയിൽ വിലാസത്തിൽ അറിയിക്കാവുന്നതാണ്.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = onOpenAdminPanel,
                            modifier = Modifier.testTag("btn_admin_gateway")
                        ) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Administrator Verification (harikumarg004@gmail.com)", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecureAdminPanelView(
    adminConfig: AdminConfigState,
    onAuthenticate: (String, String) -> Boolean,
    onUpdateConfig: ((AdminConfigState) -> AdminConfigState) -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var emailInput by remember { mutableStateOf("") }
    var passCodeInput by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("അഡ്മിനിസ്ട്രേറ്റർ പാനൽ (Admin Control)", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("admin_panel_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (!adminConfig.isAdminAuthenticated) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Secure Administrator Authentication",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "അംഗീകൃത അഡ്മിനിസ്ട്രേറ്റർക്ക് (harikumarg004@gmail.com) മാത്രമുള്ള സുരക്ഷിത ക്രമീകരണ വിഭാഗം.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (authError != null) {
                                Text(text = authError!!, color = MaterialTheme.colorScheme.error)
                            }
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Admin Email") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("admin_email_input")
                            )
                            OutlinedTextField(
                                value = passCodeInput,
                                onValueChange = { passCodeInput = it },
                                label = { Text("Admin Security Token") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("admin_token_input")
                            )
                            Button(
                                onClick = {
                                    val ok = onAuthenticate(emailInput, passCodeInput)
                                    authError = if (ok) null else "അഡ്മിൻ ഇമെയിൽ അല്ലെങ്കിൽ സുരക്ഷാ ടോക്കൺ സാധുവല്ല."
                                },
                                modifier = Modifier.fillMaxWidth().testTag("admin_login_button")
                            ) {
                                Text("Verify & Unlock Admin Panel")
                            }
                        }
                    }
                }
            } else {
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
                                Column {
                                    Text("Admin: ${adminConfig.adminEmail}", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text("Version: ${adminConfig.appVersionInfo}", fontSize = 12.sp)
                                }
                                OutlinedButton(onClick = onLogout) {
                                    Text("Logout")
                                }
                            }
                            HorizontalDivider()
                            Text("1. Application & Feature Toggles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            AdminToggleRow("Dark Yin-Yang Theme Balance", adminConfig.isDarkYinYangTheme) { v ->
                                onUpdateConfig { it.copy(isDarkYinYangTheme = v) }
                            }
                            AdminToggleRow("Maintenance Mode", adminConfig.maintenanceMode) { v ->
                                onUpdateConfig { it.copy(maintenanceMode = v) }
                            }
                            AdminToggleRow("D1–D60 Divisional Charts", adminConfig.enableDetailedD60) { v ->
                                onUpdateConfig { it.copy(enableDetailedD60 = v) }
                            }
                            AdminToggleRow("Malayalam PDF Export", adminConfig.enablePdfExport) { v ->
                                onUpdateConfig { it.copy(enablePdfExport = v) }
                            }
                            AdminToggleRow("Strict Session-Only Privacy Default", adminConfig.strictSessionOnlyDefault) { v ->
                                onUpdateConfig { it.copy(strictSessionOnlyDefault = v) }
                            }

                            HorizontalDivider()
                            Text("2. Non-Intrusive Advertisement Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                "പരസ്യങ്ങൾ ജാതക കണക്കുകൂട്ടലുകളെ ഒരിക്കലും തടസ്സപ്പെടുത്തുന്നില്ല.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            AdminToggleRow("Master Ad Support Enabled", adminConfig.adsMasterEnabled) { v ->
                                onUpdateConfig { it.copy(adsMasterEnabled = v) }
                            }
                            AdminToggleRow("Banner Ad Slot", adminConfig.bannerAdEnabled) { v ->
                                onUpdateConfig { it.copy(bannerAdEnabled = v) }
                            }
                            AdminToggleRow("Rewarded Ad", adminConfig.rewardedAdEnabled) { v ->
                                onUpdateConfig { it.copy(rewardedAdEnabled = v) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
