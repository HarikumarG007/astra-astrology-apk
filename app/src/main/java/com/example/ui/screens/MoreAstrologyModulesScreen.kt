package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeralaLocationDatabase
import com.example.engine.AstronomicalEphemerisEngine
import com.example.model.*
import com.example.ui.components.PeriodClassificationBadge
import com.example.viewmodel.MoreSubScreen
import java.util.Locale

@Composable
fun MoreScreenHeader(
    titleMalayalam: String,
    onBackToMenu: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackToMenu,
                modifier = Modifier.testTag("more_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to More Menu",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = titleMalayalam,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MoreMenuGridView(
    onSelectScreen: (MoreSubScreen) -> Unit
) {
    data class MenuCardSpec(
        val screen: MoreSubScreen,
        val subtitle: String,
        val icon: ImageVector
    )

    val items = listOf(
        MenuCardSpec(MoreSubScreen.PANCHANGA, "വാരം, തിഥി, നക്ഷത്രം, യോഗം, കരണം, ഉദയാസ്തമയം", Icons.Default.WbSunny),
        MenuCardSpec(MoreSubScreen.KARANA, "11 പഞ്ചാംഗ കരണങ്ങളും സിംഹകരണ വിശദീകരണവും", Icons.Default.Calculate),
        MenuCardSpec(MoreSubScreen.YOGAS_DOSHAS, "രാജയോഗങ്ങൾ, ധനയോഗങ്ങൾ, പഞ്ചമഹാപുരുഷ യോഗം & ദോഷങ്ങൾ", Icons.Default.Stars),
        MenuCardSpec(MoreSubScreen.TRANSIT, "വ്യാഴ മാറ്റം, ശനി ഗോചരം (ഏഴരശ്ശനി/കണ്ടകശ്ശനി) & നവഗ്രഹ ഗോചരം", Icons.Default.Public),
        MenuCardSpec(MoreSubScreen.COMPATIBILITY, "കേരള പത്തുപൊരുത്തം, കുജദോഷം, ദശാസന്ധി & നവാംശക പൊരുത്തം", Icons.Default.Favorite),
        MenuCardSpec(MoreSubScreen.MUHURTHAM, "വിവാഹം, ഗൃഹപ്രവേശം, വസ്തു, യാത്ര, വിദ്യാരംഭ മുഹൂർത്തം", Icons.Default.EventAvailable),
        MenuCardSpec(MoreSubScreen.ASTROLOGY_TOOLS, "നക്ഷത്ര/രാശി ഫൈൻഡർ, ഗണിത സാധൂകരണം & ജ്യോതിഷ നിഘണ്ടു", Icons.Default.Build),
        MenuCardSpec(MoreSubScreen.COMPLETE_REPORT_PDF, "സമ്പൂർണ്ണ മലയാളം ജാതക റിപ്പോർട്ട് & PDF (Preview / Share / Save)", Icons.Default.PictureAsPdf),
        MenuCardSpec(MoreSubScreen.SAVED_AND_PRIVACY, "സംരക്ഷിച്ച ജാതകങ്ങൾ, Delete Horoscope & Delete All Data", Icons.Default.Security),
        MenuCardSpec(MoreSubScreen.PRIVACY_POLICY, "സ്വകാര്യതാ നയം (In-App Privacy Policy)", Icons.Default.Policy),
        MenuCardSpec(MoreSubScreen.TERMS_AND_DATA_SAFETY, "ഉപയോഗ നിബന്ധനകളും Google Play Data Safety വിവരങ്ങളും", Icons.Default.VerifiedUser),
        MenuCardSpec(MoreSubScreen.ABOUT_AND_CONTACT, "Astra Astrology Malayalam വിവരങ്ങളും ബന്ധപ്പെടാനുള്ള ഇമെയിലും", Icons.Default.Info)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("more_menu_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ജ്യോതിഷ സേവനങ്ങളും ക്രമീകരണങ്ങളും (More Modules)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(items) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectScreen(item.screen) }
                    .testTag("more_item_${item.screen.name.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.screen.malayalam,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.screen.malayalam,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PanchangaFullModuleView(
    report: CompleteJathakamReport,
    onBack: () -> Unit
) {
    val p = report.panchanga
    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("കേരള പഞ്ചാംഗം (Kerala Panchanga)", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                            text = "പഞ്ചാംഗ വിശദാംശങ്ങൾ (${report.birthData.placeNameMalayalam})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        PanchangaKeyValueRow("1. വാരം (Weekday)", "${p.varaMalayalam} • അധിപൻ: ${p.varaLord.malayalamName}")
                        PanchangaKeyValueRow("2. തിഥി (Tithi)", "${p.tithiMalayalam} (${p.tithiNumber}/30)")
                        PanchangaKeyValueRow("   പക്ഷം (Paksha)", p.pakshaMalayalam)
                        PanchangaKeyValueRow("3. നക്ഷത്രം (Nakshatra)", "${p.nakshatra.malayalamName} (പാദം ${p.nakshatraPada})")
                        PanchangaKeyValueRow("4. നിത്യയോഗം (Yoga)", "${p.nityaYogaMalayalam} (#${p.nityaYogaNumber})")
                        PanchangaKeyValueRow("5. കരണം (Karana)", "${p.karana.nameMalayalam} (${p.karana.nameEnglish})")
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        PanchangaKeyValueRow("മലയാള മാസം (Masa)", p.malayalamMasa)
                        PanchangaKeyValueRow("സംവത്സരം (Samvatsara)", p.samvatsaraMalayalam)
                        PanchangaKeyValueRow("അയനം (Ayana)", p.ayanaMalayalam)
                        PanchangaKeyValueRow("ഋതു (Ritu)", p.rituMalayalam)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        PanchangaKeyValueRow("സൂര്യോദയം (Sunrise)", p.sunriseFormatted)
                        PanchangaKeyValueRow("സൂര്യാസ്തമയം (Sunset)", p.sunsetFormatted)
                        PanchangaKeyValueRow("ചന്ദ്രോദയം (Moonrise)", p.moonriseFormatted)
                        PanchangaKeyValueRow("ചന്ദ്രാസ്തമയം (Moonset)", p.moonsetFormatted)
                        PanchangaKeyValueRow("രാഹുകാലം (Rahu Kalam)", p.rahuKalamFormatted)
                        PanchangaKeyValueRow("ഗുളികകാലം (Gulika Kalam)", p.gulikaKalamFormatted)
                        PanchangaKeyValueRow("അഭിജിത്ത് മുഹൂർത്തം", p.abhijitMuhurthamFormatted)
                    }
                }
            }
        }
    }
}

@Composable
fun KaranaCalculatorAndExplanationView(
    report: CompleteJathakamReport,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val k = report.panchanga.karana

    val elevenClassicalKaranas = listOf(
        Triple("ബവ (Bava)", "ചരകരണം (1/7)", "ശുഭകരമായ ആരംഭങ്ങൾക്കും സ്ഥിരകർമ്മങ്ങൾക്കും ഉത്തമം."),
        Triple("ബാലവ (Balava)", "ചരകരണം (2/7)", "വിദ്യാരംഭം, ധാർമ്മിക കാര്യങ്ങൾ, കരാറുകൾ എന്നിവയ്ക്ക് ശുഭം."),
        Triple("കൗലവ (Kaulava)", "ചരകരണം (3/7)", "സൗഹൃദം, വിവാഹം, കുടുംബ കാര്യങ്ങൾ എന്നിവയ്ക്ക് അനുയോജ്യം."),
        Triple("തൈതില (Taitila)", "ചരകരണം (4/7)", "ഗൃഹനിർമ്മാണം, വാഹനം, പൊതുപ്രവർത്തനങ്ങൾ എന്നിവയ്ക്ക് നല്ലത്."),
        Triple("ഗരജ (Garaja)", "ചരകരണം (5/7)", "കൃഷി, ഭൂമി ഇടപാടുകൾ, വ്യാപാര ആരംഭം എന്നിവയ്ക്ക് ഉത്തമം."),
        Triple("വണിജ (Vanija)", "ചരകരണം (6/7)", "വ്യാപാരം, സാമ്പത്തിക നിക്ഷേപങ്ങൾ, കച്ചവടം എന്നിവയ്ക്ക് ശ്രേഷ്ഠം."),
        Triple("വിഷ്ടി / ഭദ്ര (Vishti)", "ചരകരണം (7/7)", "ശുഭമുഹൂർത്തങ്ങൾക്ക് സാധാരണയായി ഒഴിവാക്കുന്ന ഭദ്രാകരണം."),
        Triple("ശകുനി (Shakuni)", "സ്ഥിരകരണം (1/4)", "കൃഷ്ണപക്ഷ ചതുർദ്ദശിയുടെ രണ്ടാം പകുതി; ഔഷധസേവയ്ക്കും മന്ത്രോപാസനയ്ക്കും ഉചിതം."),
        Triple("ചതുഷ്പാദം (Chatushpada)", "സ്ഥിരകരണം (2/4)", "അമാവാസിയുടെ ആദ്യപകുതി; പിതൃകർമ്മങ്ങൾക്കും ഗോപൂജയ്ക്കും ഉചിതം."),
        Triple("നാഗം (Naga)", "സ്ഥിരകരണം (3/4)", "അമാവാസിയുടെ രണ്ടാം പകുതി; നാഗാരാധനയ്ക്കും സ്ഥിരകർമ്മങ്ങൾക്കും ഉചിതം."),
        Triple("കിംസ്തുഘ്നം (Kimstughna)", "സ്ഥിരകരണം (4/4)", "ശുക്ലപക്ഷ പ്രഥമയുടെ ആദ്യപകുതി; മംഗളകർമ്മങ്ങൾക്കും പ്രാരംഭങ്ങൾക്കും ഉചിതം.")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("പഞ്ചാംഗ കരണ ഗണിതവും വിശദീകരണവും", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("karana_screen_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Current Calculated Karana Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "ജാതകത്തിലെ യഥാർത്ഥ പഞ്ചാംഗ കരണം: ${k.nameMalayalam} (${k.nameEnglish})",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        PanchangaKeyValueRow("കരണ വിഭാഗം", k.natureMal)
                        PanchangaKeyValueRow("അധിദേവത", k.deityMal)
                        PanchangaKeyValueRow("തിഥി അർദ്ധാംശ ക്രമം", "${k.indexInTithiHalf} / 60 (6° വീതമുള്ള ചന്ദ്ര-സൂര്യ അന്തരം)")
                        Text(
                            text = "മുഹൂർത്ത ഫലം: ${k.muhurthaSuitabilityMal}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            // Search bar (including Simha Karana explanation trigger)
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("കരണം തിരയുക (ഉദാ: Bava, Vishti, Shakuni, Simha Karana / സിംഹകരണം)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("karana_search_input")
                )
            }

            // Special Simha Karana Clarification Card (Always visible or highlighted when searched)
            item {
                val isSearchingSimha = searchQuery.contains("simha", ignoreCase = true) ||
                    searchQuery.contains("സിംഹ", ignoreCase = true)
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSearchingSimha) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("simha_karana_explanation_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = AstronomicalEphemerisEngine.getSimhaKaranaExplanationMalayalam(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // All 11 Standard Panchanga Karanas
            item {
                Text(
                    text = "11 ശാസ്ത്രീയ പഞ്ചാംഗ കരണങ്ങൾ (7 ചരകരണങ്ങൾ + 4 സ്ഥിരകരണങ്ങൾ)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            items(
                elevenClassicalKaranas.filter {
                    searchQuery.isBlank() ||
                        it.first.contains(searchQuery, ignoreCase = true) ||
                        it.second.contains(searchQuery, ignoreCase = true) ||
                        searchQuery.contains("simha", ignoreCase = true) ||
                        searchQuery.contains("സിംഹ", ignoreCase = true)
                }
            ) { (name, type, desc) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = name, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(text = type, style = MaterialTheme.typography.labelLarge)
                        }
                        Text(text = desc, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun YogasAndDoshasFullView(
    report: CompleteJathakamReport,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("ജാതകത്തിലെ യോഗങ്ങളും ദോഷവിചിന്തനവും", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("yogas_screen_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "നിങ്ങളുടെ ഗ്രഹനിലയിൽ യഥാർത്ഥത്തിൽ രൂപപ്പെട്ടിട്ടുള്ള യോഗങ്ങളും ദോഷസൂചനകളും മാത്രമാണ് ഇവിടെ പ്രദർശിപ്പിക്കുന്നത് (${report.yogasAndDoshas.size} യോഗങ്ങൾ കണ്ടെത്തി):",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            items(report.yogasAndDoshas) { yoga ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("yoga_card_${yoga.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = yoga.nameMalayalam,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${yoga.categoryMal} • ബലം: ${yoga.strengthClass.malayalam}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        PanchangaKeyValueRow("ഉൾപ്പെട്ട ഗ്രഹങ്ങൾ", yoga.planetsInvolved.joinToString(", ") { it.malayalamName })
                        PanchangaKeyValueRow("ഉൾപ്പെട്ട ഭാവങ്ങൾ & രാശികൾ", "ഭാവം: ${yoga.housesInvolved.joinToString(", ")} • രാശി: ${yoga.signsInvolved.joinToString(", ") { it.malayalamName }}")
                        Text(
                            text = "• വ്യവസ്ഥ (Condition Satisfied): ${yoga.conditionsSatisfiedMal}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = yoga.detailedMalayalamExplanation,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Text(
                            text = "• അനുകൂല ഫലങ്ങൾ: ${yoga.positiveIndicationsMal}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "• ശ്രദ്ധിക്കേണ്ടവ / രൂപാന്തരീകരണം: ${yoga.modificationOrCancellationMal} ${yoga.challengingIndicationsMal}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "• ദശാ സജീവത & ഗോചര ബന്ധം: ${yoga.dashaActivationMal} ${yoga.transitRelevanceMal}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransitGocharaFullView(
    report: CompleteJathakamReport,
    onBack: () -> Unit
) {
    val tr = report.transitReport
    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("ഗോചര ഫലം — വ്യാഴം, ശനി & നവഗ്രഹങ്ങൾ", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("transit_screen_list"),
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
                            text = "ദശാ + ഗോചര സംയോജിത വിശകലനം (${tr.calculationDateFormatted})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = tr.dashaTransitCombinedMal, style = MaterialTheme.typography.bodyLarge)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Text(text = tr.jupiterTransitDetailedMal, style = MaterialTheme.typography.bodyLarge)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Text(text = tr.saturnTransitDetailedMal, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }

            items(tr.planetTransits) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${item.planet.malayalamName} → ${item.transitRashi.malayalamName}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            PeriodClassificationBadge(classification = item.classification)
                        }
                        Text(
                            text = "ചന്ദ്രനാൽ ${item.houseFromMoon}-ാം ഭാവം • ലഗ്നാൽ ${item.houseFromLagna}-ാം ഭാവം • ${item.transitNakshatra.malayalamName} (പാദം ${item.transitPada})",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(text = item.effectMalayalam, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun CompatibilityPoruthamView(
    person1BirthData: BirthData,
    partnerBirthData: BirthData,
    compatibilityReport: CompatibilityReport?,
    onUpdatePartnerBirthData: (BirthData) -> Unit,
    onSelectPartnerLocation: (KeralaLocation) -> Unit,
    onBack: () -> Unit
) {
    var partnerName by remember(partnerBirthData) { mutableStateOf(partnerBirthData.name) }
    var pYear by remember(partnerBirthData) { mutableStateOf(partnerBirthData.year.toString()) }
    var pMonth by remember(partnerBirthData) { mutableStateOf(partnerBirthData.month.toString()) }
    var pDay by remember(partnerBirthData) { mutableStateOf(partnerBirthData.day.toString()) }
    var pHour by remember(partnerBirthData) { mutableStateOf(partnerBirthData.hour.toString()) }
    var pMin by remember(partnerBirthData) { mutableStateOf(partnerBirthData.minute.toString()) }
    var locQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("വിവാഹ പൊരുത്ത പരിശോധന (Compatibility)", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize().testTag("compatibility_screen_list"),
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
                            text = "രണ്ടാം ജാതക വിവരങ്ങൾ നൽകുക (Partner Birth Details)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ഒന്നാം ജാതകം: ${person1BirthData.name} (${person1BirthData.placeNameMalayalam})",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        OutlinedTextField(
                            value = partnerName,
                            onValueChange = { partnerName = it },
                            label = { Text("പങ്കാളിയുടെ പേര് (Partner Name)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = pDay, onValueChange = { pDay = it }, label = { Text("DD") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = pMonth, onValueChange = { pMonth = it }, label = { Text("MM") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = pYear, onValueChange = { pYear = it }, label = { Text("YYYY") }, singleLine = true, modifier = Modifier.weight(1.3f))
                            OutlinedTextField(value = pHour, onValueChange = { pHour = it }, label = { Text("HH") }, singleLine = true, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = pMin, onValueChange = { pMin = it }, label = { Text("Min") }, singleLine = true, modifier = Modifier.weight(1f))
                        }
                        OutlinedTextField(
                            value = locQuery,
                            onValueChange = { locQuery = it },
                            label = { Text("പങ്കാളിയുടെ ജനന സ്ഥലം (${partnerBirthData.placeNameMalayalam})") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (locQuery.isNotBlank()) {
                            KeralaLocationDatabase.searchLocations(locQuery).take(4).forEach { loc ->
                                TextButton(onClick = {
                                    onSelectPartnerLocation(loc)
                                    locQuery = ""
                                }) {
                                    Text("${loc.nameMalayalam} (${loc.nameEnglish}) - ${loc.districtMalayalam}")
                                }
                            }
                        }
                        Button(
                            onClick = {
                                onUpdatePartnerBirthData(
                                    partnerBirthData.copy(
                                        name = partnerName.ifBlank { "പങ്കാളി" },
                                        year = pYear.toIntOrNull() ?: 1998,
                                        month = pMonth.toIntOrNull() ?: 8,
                                        day = pDay.toIntOrNull() ?: 22,
                                        hour = pHour.toIntOrNull() ?: 10,
                                        minute = pMin.toIntOrNull() ?: 15
                                    )
                                )
                            },
                            modifier = Modifier.fillMaxWidth().testTag("check_porutham_button")
                        ) {
                            Text("പൊരുത്തം പരിശോധിക്കുക (Analyze Compatibility)")
                        }
                    }
                }
            }

            if (compatibilityReport != null) {
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
                                    text = "പൊരുത്ത ഫല സംഗ്രഹം",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                PeriodClassificationBadge(classification = compatibilityReport.overallClassification)
                            }
                            Text(text = compatibilityReport.detailedMalayalamSummary, style = MaterialTheme.typography.bodyLarge)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            Text(text = compatibilityReport.navamsaAndPlanetaryHarmonyMal, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                items(compatibilityReport.poruthamFactors) { factor ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = factor.nameMalayalam, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    text = factor.statusMalayalam,
                                    color = if (factor.isMatched) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(text = factor.explanationMalayalam, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MuhurthamModuleView(
    report: CompleteJathakamReport,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("ശുഭ മുഹൂർത്ത മാർഗ്ഗനിർദ്ദേശങ്ങൾ (Muhurtham)", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(report.muhurthamList) { item ->
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
                                text = item.titleMalayalam,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            PeriodClassificationBadge(classification = item.currentDaySuitability)
                        }
                        Text(text = "• ശുഭ നക്ഷത്രങ്ങൾ: ${item.favorableNakshatrasMal}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "• തിഥി & കരണ ശുദ്ധി: ${item.favorableTithisAndKaranasMal}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "• പഞ്ചാംഗ വിലയിരുത്തൽ: ${item.currentPanchangaAssessmentMal}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = item.traditionalGuidelinesMal, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun AstrologyToolsAndGlossaryView(
    report: CompleteJathakamReport,
    onBack: () -> Unit
) {
    var degreeInput by remember { mutableStateOf("125.5") }
    val degVal = (degreeInput.toDoubleOrNull() ?: 125.5).coerceIn(0.0, 359.99)
    val foundRashi = Rashi.fromIndex((degVal / 30.0).toInt())
    val foundNak = Nakshatra.fromIndex((degVal / (360.0 / 27.0)).toInt())
    val foundPada = (((degVal % (360.0 / 27.0)) / (360.0 / 108.0)).toInt() + 1).coerceIn(1, 4)

    val glossary = listOf(
        "ലഗ്നം (Lagna / Ascendant)" to "ജനന സമയത്ത് കിഴക്കൻ ചക്രവാളത്തിൽ ഉദിച്ചുയരുന്ന രാശി. ജാതകത്തിലെ ഒന്നാം ഭാവം.",
        "കൂറ് / ചന്ദ്രരാശി (Moon Sign)" to "ജനന സമയത്ത് ചന്ദ്രൻ നിൽക്കുന്ന രാശി. ഗോചര ഫലങ്ങളും പൊരുത്തവും പ്രധാനമായും ചന്ദ്രരാശിയെ ആശ്രയിക്കുന്നു.",
        "നവാംശകം (Navamsa - D9)" to "ഒരു രാശിയെ 3° 20' വീതമുള്ള 9 തുല്യ ഭാഗങ്ങളായി തിരിക്കുന്ന ഷോഡശവർഗ്ഗ ചക്രം. വിവാഹവും ഗ്രഹങ്ങളുടെ അന്തർബലവും ഇതിലൂടെ അറിയാം.",
        "ദശാംശം (Dasamsa - D10)" to "ഒരു രാശിയെ 3° വീതമുള്ള 10 ഭാഗങ്ങളാക്കി തൊഴിൽ, കരിയർ, അധികാരം എന്നിവ വിലയിരുത്തുന്ന ചക്രം.",
        "വിംശോത്തരി ദശ (Vimshottari Dasha)" to "ജന്മനക്ഷത്രത്തിലെ ചന്ദ്രന്റെ ഡിഗ്രി അനുസരിച്ച് 120 വർഷത്തെ ഒമ്പത് ഗ്രഹങ്ങളുടെ മഹാദശകളായി വിഭജിക്കുന്ന രീതി.",
        "മൗഢ്യം (Combustion)" to "ഗ്രഹങ്ങൾ സൂര്യനോട് നിശ്ചിത ഡിഗ്രിയിൽ കൂടുതൽ അടുക്കുമ്പോൾ രശ്മിബലം കുറയുന്ന അവസ്ഥ.",
        "വക്രഗതി (Retrograde)" to "ഭൂമിയിൽ നിന്ന് നോക്കുമ്പോൾ ഗ്രഹം പിന്നോട്ട് സഞ്ചരിക്കുന്നതായി കാണപ്പെടുന്ന ജ്യോതിശാസ്ത്ര പ്രതിഭാസം.",
        "ഷഡ്ബലം (Shadbala)" to "സ്ഥാനബലം, ദിഗ്ബലം, കാലബലം, ചേഷ്ടാബലം, നൈസർഗ്ഗികബലം, ദൃഗ്ബലം എന്നീ ആറ് തരം ഗ്രഹബലങ്ങളുടെ ആകെത്തുക.",
        "അഷ്ടകവർഗ്ഗം (Ashtakavarga)" to "ലഗ്നവും സപ്തഗ്രഹങ്ങളും ചേർന്ന് ഓരോ രാശിയിലും നൽകുന്ന ശുഭബിന്ദുക്കളുടെ (പരലുകളുടെ) ഗണിതരീതി."
    )

    Column(modifier = Modifier.fillMaxSize()) {
        MoreScreenHeader("ജ്യോതിഷ ടൂളുകൾ, സാധൂകരണം & നിഘണ്ടു", onBack)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Instant Nakshatra & Rashi Finder Tool
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "നക്ഷത്ര - രാശി - പാദ ഫൈൻഡർ (Nakshatra & Rashi Finder)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = degreeInput,
                            onValueChange = { degreeInput = it },
                            label = { Text("സ്ഫുടം / Sidereal Longitude (0° – 360°)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "രാശി: ${foundRashi.malayalamName} (രാശ്യാധിപൻ: ${foundRashi.lord.malayalamName})\nനക്ഷത്രം: ${foundNak.malayalamName} — പാദം $foundPada (ദശാനാഥൻ: ${foundNak.lord.malayalamName})",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Internal Calculation Validation Layer Card
            item {
                val valSum = report.validationSummary
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "${AstronomicalEphemerisEngine.ENGINE_PUBLIC_NAME} — ഗണിത സാധൂകരണം (${valSum.checksPassedCount}/${valSum.totalChecksCount})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        PanchangaKeyValueRow("ചിത്രപക്ഷ അയനാംശം", valSum.ayanamsaUsedDegrees)
                        PanchangaKeyValueRow("ജൂലിയൻ ദിനം (JD)", valSum.julianDayFormatted)
                        PanchangaKeyValueRow("പ്രാദേശിക നക്ഷത്ര സമയം (LST)", valSum.siderealTimeFormatted)
                        valSum.validationDetailsMal.forEach { line ->
                            Text("✓ $line", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Glossary
            item {
                Text(
                    text = "കേരള ജ്യോതിഷ നിഘണ്ടു (Astrology Glossary)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            items(glossary) { (term, def) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = term, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(text = def, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
