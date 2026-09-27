package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KeralaLocationDatabase
import com.example.engine.AstronomicalEphemerisEngine
import com.example.model.*
import com.example.ui.components.KeralaRashiChakraView
import com.example.viewmodel.JathakamSubTab
import java.util.Locale

@Composable
fun JathakamScreen(
    birthData: BirthData,
    report: CompleteJathakamReport,
    selectedSubTab: JathakamSubTab,
    validationError: String?,
    onSelectSubTab: (JathakamSubTab) -> Unit,
    onSelectKeralaLocation: (KeralaLocation) -> Unit,
    onCalculateJathakam: (BirthData, Boolean) -> Unit,
    onRequestSaveHoroscope: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Sub-navigation bar for Jathakam
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(JathakamSubTab.entries) { sub ->
                val selected = (sub == selectedSubTab)
                FilterChip(
                    selected = selected,
                    onClick = { onSelectSubTab(sub) },
                    label = { Text(sub.malayalam, style = MaterialTheme.typography.labelLarge) },
                    modifier = Modifier.testTag("jathakam_subtab_${sub.name.lowercase()}")
                )
            }
        }

        when (selectedSubTab) {
            JathakamSubTab.BIRTH_DETAILS -> BirthDetailsInputSection(
                initialBirthData = birthData,
                validationError = validationError,
                onSelectKeralaLocation = onSelectKeralaLocation,
                onCalculate = { updated -> onCalculateJathakam(updated, true) },
                onRequestSave = onRequestSaveHoroscope
            )
            JathakamSubTab.RASHI_LAGNA -> RashiAndLagnaSection(report = report)
            JathakamSubTab.NAKSHATRA -> NakshatraDetailsSection(report = report)
            JathakamSubTab.PLANETS -> PlanetaryPositionsSection(report = report)
            JathakamSubTab.BHAVA -> BhavaAnalysisSection(report = report)
            JathakamSubTab.NAVAMSA_DIVISIONAL -> DivisionalChartsSection(report = report)
        }
    }
}

@Composable
private fun BirthDetailsInputSection(
    initialBirthData: BirthData,
    validationError: String?,
    onSelectKeralaLocation: (KeralaLocation) -> Unit,
    onCalculate: (BirthData) -> Unit,
    onRequestSave: () -> Unit
) {
    val context = LocalContext.current
    var name by remember(initialBirthData) { mutableStateOf(initialBirthData.name) }
    var gender by remember(initialBirthData) { mutableStateOf(initialBirthData.gender) }
    var yearStr by remember(initialBirthData) { mutableStateOf(initialBirthData.year.toString()) }
    var monthStr by remember(initialBirthData) { mutableStateOf(initialBirthData.month.toString()) }
    var dayStr by remember(initialBirthData) { mutableStateOf(initialBirthData.day.toString()) }
    var hourStr by remember(initialBirthData) { mutableStateOf(initialBirthData.hour.toString()) }
    var minuteStr by remember(initialBirthData) { mutableStateOf(initialBirthData.minute.toString()) }
    var secondStr by remember(initialBirthData) { mutableStateOf(initialBirthData.second.toString()) }

    var placeMal by remember(initialBirthData) { mutableStateOf(initialBirthData.placeNameMalayalam) }
    var placeEng by remember(initialBirthData) { mutableStateOf(initialBirthData.placeNameEnglish) }
    var districtMal by remember(initialBirthData) { mutableStateOf(initialBirthData.districtMalayalam) }
    var districtEng by remember(initialBirthData) { mutableStateOf(initialBirthData.districtEnglish) }
    var latStr by remember(initialBirthData) { mutableStateOf(initialBirthData.latitude.toString()) }
    var lonStr by remember(initialBirthData) { mutableStateOf(initialBirthData.longitude.toString()) }
    var tzOffsetStr by remember(initialBirthData) { mutableStateOf(initialBirthData.timezoneOffsetHours.toString()) }

    var locationSearchQuery by remember { mutableStateOf("") }
    var selectedDistrictFilter by remember { mutableStateOf<String?>(null) }
    var showAdvancedCoordinates by remember { mutableStateOf(false) }

    val matchingLocations = remember(locationSearchQuery, selectedDistrictFilter) {
        KeralaLocationDatabase.searchLocations(locationSearchQuery, selectedDistrictFilter)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("birth_details_form"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "ജനന വിവരങ്ങൾ നൽകുക (Enter Birth Details)",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "സ്വകാര്യതാ ഉറപ്പ്: നിങ്ങളുടെ ജനന വിവരങ്ങൾ നിലവിലെ കണക്കുകൂട്ടലിന് മാത്രമായി ഉപയോഗിക്കുന്നു; നിങ്ങളുടെ അനുമതിയില്ലാതെ സ്വയമേവ സംരക്ഷിക്കുന്നതല്ല.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (validationError != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = validationError,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("പേര് (Name)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_birth_name")
                    )

                    // Gender selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Gender.entries.forEach { g ->
                            FilterChip(
                                selected = (gender == g),
                                onClick = { gender = g },
                                label = { Text("${g.malayalam} (${g.english})") },
                                modifier = Modifier.testTag("gender_chip_${g.name.lowercase()}")
                            )
                        }
                    }

                    // Date of Birth Row + Date Picker Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = dayStr,
                            onValueChange = { dayStr = it.filter { ch -> ch.isDigit() }.take(2) },
                            label = { Text("തീയതി (DD)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_birth_day")
                        )
                        OutlinedTextField(
                            value = monthStr,
                            onValueChange = { monthStr = it.filter { ch -> ch.isDigit() }.take(2) },
                            label = { Text("മാസം (MM)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_birth_month")
                        )
                        OutlinedTextField(
                            value = yearStr,
                            onValueChange = { yearStr = it.filter { ch -> ch.isDigit() }.take(4) },
                            label = { Text("വർഷം (YYYY)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1.3f).testTag("input_birth_year")
                        )
                        IconButton(
                            onClick = {
                                val y = yearStr.toIntOrNull() ?: 1996
                                val m = (monthStr.toIntOrNull() ?: 5) - 1
                                val d = dayStr.toIntOrNull() ?: 15
                                DatePickerDialog(context, { _, selY, selM, selD ->
                                    yearStr = selY.toString()
                                    monthStr = (selM + 1).toString()
                                    dayStr = selD.toString()
                                }, y, m, d).show()
                            },
                            modifier = Modifier.testTag("btn_open_date_picker")
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // Exact Birth Time Row + Time Picker Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = hourStr,
                            onValueChange = { hourStr = it.filter { ch -> ch.isDigit() }.take(2) },
                            label = { Text("മണിക്കൂർ (0-23)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_birth_hour")
                        )
                        OutlinedTextField(
                            value = minuteStr,
                            onValueChange = { minuteStr = it.filter { ch -> ch.isDigit() }.take(2) },
                            label = { Text("മിനിറ്റ് (0-59)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_birth_minute")
                        )
                        OutlinedTextField(
                            value = secondStr,
                            onValueChange = { secondStr = it.filter { ch -> ch.isDigit() }.take(2) },
                            label = { Text("സെക്കൻഡ്") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("input_birth_second")
                        )
                        IconButton(
                            onClick = {
                                val h = hourStr.toIntOrNull() ?: 9
                                val min = minuteStr.toIntOrNull() ?: 30
                                TimePickerDialog(context, { _, selH, selM ->
                                    hourStr = selH.toString()
                                    minuteStr = selM.toString()
                                }, h, min, true).show()
                            },
                            modifier = Modifier.testTag("btn_open_time_picker")
                        ) {
                            Icon(Icons.Default.AccessTime, contentDescription = "Pick Time", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        // Comprehensive Kerala Location Search Card (Malayalam & English)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "കേരള ജനന സ്ഥലം തിരഞ്ഞെടുക്കുക (Kerala Location Database)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "തിരഞ്ഞെടുത്ത സ്ഥലം: $placeMal ($placeEng) • ജില്ല: $districtMal • അക്ഷാംശം: ${String.format(Locale.US, "%.4f° N, %.4f° E", latStr.toDoubleOrNull() ?: 8.5241, lonStr.toDoubleOrNull() ?: 76.9366)} • IST (+05:30)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = locationSearchQuery,
                        onValueChange = { locationSearchQuery = it },
                        label = { Text("സ്ഥലം തിരയുക (ഉദാ: പത്തനംതിട്ട, Pandalam, തിരുവനന്തപുരം)") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Location") },
                        trailingIcon = {
                            if (locationSearchQuery.isNotEmpty()) {
                                IconButton(onClick = { locationSearchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("kerala_location_search_input")
                    )

                    // 14 Kerala Districts Filter Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChip(
                                selected = selectedDistrictFilter == null,
                                onClick = { selectedDistrictFilter = null },
                                label = { Text("എല്ലാ ജില്ലകളും (14)") }
                            )
                        }
                        items(KeralaLocationDatabase.districts) { (distMal, distEng) ->
                            FilterChip(
                                selected = selectedDistrictFilter == distMal,
                                onClick = {
                                    selectedDistrictFilter = if (selectedDistrictFilter == distMal) null else distMal
                                },
                                label = { Text("$distMal ($distEng)") }
                            )
                        }
                    }

                    // Matching Kerala Locations List (Top 8 visible with tap-to-select)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        matchingLocations.take(8).forEach { loc ->
                            val isSelected = (loc.nameMalayalam == placeMal)
                            Surface(
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        placeMal = loc.nameMalayalam
                                        placeEng = loc.nameEnglish
                                        districtMal = loc.districtMalayalam
                                        districtEng = loc.districtEnglish
                                        latStr = loc.latitude.toString()
                                        lonStr = loc.longitude.toString()
                                        tzOffsetStr = loc.timezoneOffsetHours.toString()
                                        onSelectKeralaLocation(loc)
                                    }
                                    .testTag("location_item_${loc.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${loc.nameMalayalam} (${loc.nameEnglish})",
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${loc.districtMalayalam} ജില്ല • ${loc.categoryMalayalam} • താലൂക്ക്: ${loc.talukMalayalam}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = String.format(Locale.US, "%.2f°N\n%.2f°E", loc.latitude, loc.longitude),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    // Advanced Manual Latitude / Longitude Override Toggle
                    TextButton(
                        onClick = { showAdvancedCoordinates = !showAdvancedCoordinates },
                        modifier = Modifier.testTag("toggle_manual_coordinates")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (showAdvancedCoordinates) "അക്ഷാംശ-രേഖാംശ ക്രമീകരണം മറയ്ക്കുക"
                            else "അക്ഷാംശം / രേഖാംശം സ്വയം തിരുത്താൻ (Advanced Coordinates)"
                        )
                    }

                    if (showAdvancedCoordinates) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = latStr,
                                onValueChange = { latStr = it },
                                label = { Text("Latitude (°N)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("input_manual_lat")
                            )
                            OutlinedTextField(
                                value = lonStr,
                                onValueChange = { lonStr = it },
                                label = { Text("Longitude (°E)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("input_manual_lon")
                            )
                            OutlinedTextField(
                                value = tzOffsetStr,
                                onValueChange = { tzOffsetStr = it },
                                label = { Text("UTC Offset") },
                                singleLine = true,
                                modifier = Modifier.weight(0.8f).testTag("input_manual_tz")
                            )
                        }
                    }
                }
            }
        }

        // Calculate Jathakam Button & Optional Save Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val updated = BirthData(
                            name = name.trim(),
                            gender = gender,
                            year = yearStr.toIntOrNull() ?: -1,
                            month = monthStr.toIntOrNull() ?: -1,
                            day = dayStr.toIntOrNull() ?: -1,
                            hour = hourStr.toIntOrNull() ?: -1,
                            minute = minuteStr.toIntOrNull() ?: -1,
                            second = secondStr.toIntOrNull() ?: 0,
                            placeNameMalayalam = placeMal,
                            placeNameEnglish = placeEng,
                            districtMalayalam = districtMal,
                            districtEnglish = districtEng,
                            latitude = latStr.toDoubleOrNull() ?: 8.5241,
                            longitude = lonStr.toDoubleOrNull() ?: 76.9366,
                            timezoneId = "Asia/Kolkata",
                            timezoneOffsetHours = tzOffsetStr.toDoubleOrNull() ?: 5.5
                        )
                        onCalculate(updated)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("calculate_jathakam_button")
                ) {
                    Icon(Icons.Default.AutoGraph, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ജാതകം ഗണിക്കുക (Calculate)", style = MaterialTheme.typography.titleMedium)
                }

                OutlinedButton(
                    onClick = onRequestSave,
                    modifier = Modifier
                        .height(54.dp)
                        .testTag("optional_save_jathakam_button")
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("സംരക്ഷിക്കുക")
                }
            }
        }
    }
}

@Composable
private fun RashiAndLagnaSection(report: CompleteJathakamReport) {
    val d9 = report.divisionalCharts[DivisionalChartType.D9]!!
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ലഗ്നവും ചന്ദ്രരാശിയും (Lagna & Rashi Summary)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    PanchangaKeyValueRow("ജന്മലഗ്നം (Lagna)", "${report.lagnaRashi.malayalamName} (${AstronomicalEphemerisEngine.formatDegreeDms(report.lagnaDegreeInSign)}) • ലഗ്നാധിപൻ: ${report.lagnaRashi.lord.malayalamName}")
                    PanchangaKeyValueRow("ലഗ്ന നക്ഷത്രം", "${report.lagnaNakshatra.malayalamName} (പാദം ${report.lagnaPada})")
                    PanchangaKeyValueRow("ചന്ദ്രരാശി / കൂറ് (Moon Sign)", "${report.chandraRashi.malayalamName} • രാശ്യാധിപൻ: ${report.chandraRashi.lord.malayalamName}")
                    PanchangaKeyValueRow("ജന്മനക്ഷത്രം (Nakshatra)", "${report.janmaNakshatra.malayalamName} (പാദം ${report.janmaPada}) • ദശാനാഥൻ: ${report.janmaNakshatra.lord.malayalamName}")
                    PanchangaKeyValueRow("ജനന ദശാ ശിഷ്ടം", report.birthDashaBalanceMal)
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "രാശിചക്രം (D1 Rashi Chart)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    KeralaRashiChakraView(
                        chartTitleMalayalam = "രാശിചക്രം (D1)",
                        chartSubtitleMalayalam = "${report.birthData.name} • ${report.janmaNakshatra.malayalamName}",
                        lagnaRashi = report.lagnaRashi,
                        planetSigns = report.planets.associate { it.planet to it.rashi }
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "നവാംശക ചക്രം (D9 Navamsa Chart)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    KeralaRashiChakraView(
                        chartTitleMalayalam = "നവാംശകം (D9)",
                        chartSubtitleMalayalam = d9.chartType.significationMal,
                        lagnaRashi = d9.lagnaRashi,
                        planetSigns = d9.planetSigns
                    )
                }
            }
        }
    }
}

@Composable
private fun NakshatraDetailsSection(report: CompleteJathakamReport) {
    val nak = report.janmaNakshatra
    var inspectedNak by remember(nak) { mutableStateOf(nak) }

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
                        text = "ജന്മനക്ഷത്ര വിശകലനം: ${nak.malayalamName} (${nak.englishName}) — പാദം ${report.janmaPada}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    PanchangaKeyValueRow("നക്ഷത്രാധിപൻ (Lord)", nak.lord.malayalamName)
                    PanchangaKeyValueRow("അധിദേവത (Deity)", nak.deityMal)
                    PanchangaKeyValueRow("നക്ഷത്ര ചിഹ്നം (Symbol)", nak.symbolMal)
                    PanchangaKeyValueRow("ഗണം (Gana)", nak.ganaMal)
                    PanchangaKeyValueRow("യോനി & മൃഗം (Yoni)", "${nak.yoniMal} (${nak.animalMal})")
                    PanchangaKeyValueRow("നാഡി (Nadi)", nak.nadiMal)
                    PanchangaKeyValueRow("പഞ്ചഭൂതം (Element)", nak.elementMal)
                    PanchangaKeyValueRow("രജ്ജു (Rajju)", nak.rajjuMal)
                    PanchangaKeyValueRow("വൃക്ഷം & പക്ഷി", "${nak.vrikshaMal} • ${nak.pakshiMal}")
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Text(
                        text = "സ്വഭാവസവിശേഷതകളും പാദഫലവും:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${nak.characteristicsMal}\n\n• ${nak.malayalamName} നക്ഷത്രത്തിന്റെ ${report.janmaPada}-ാം പാദത്തിൽ ജനിച്ചതിനാൽ ചന്ദ്രൻ നവാംശകത്തിൽ ${report.planets.first { it.planet == Planet.MOON }.navamsaRashi.malayalamName} രാശിയിൽ സ്ഥിതി ചെയ്യുന്നു. ഇത് വ്യക്തിത്വത്തിന് ${report.planets.first { it.planet == Planet.MOON }.navamsaRashi.lord.malayalamName}-ന്റെ ഗുണവിശേഷങ്ങൾ കൂടി നൽകുന്നു.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // All 27 Nakshatras Encyclopedia Browser
        item {
            Text(
                text = "27 നക്ഷത്രങ്ങളുടെയും സമ്പൂർണ്ണ പട്ടിക (All 27 Nakshatras):",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Nakshatra.entries) { itemNak ->
                    FilterChip(
                        selected = (inspectedNak == itemNak),
                        onClick = { inspectedNak = itemNak },
                        label = { Text("${itemNak.index + 1}. ${itemNak.malayalamName}") }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${inspectedNak.index + 1}. ${inspectedNak.malayalamName} (${inspectedNak.englishName}) — നാല് പാദങ്ങളും സവിശേഷതകളും",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "അധിപൻ: ${inspectedNak.lord.malayalamName} | ദേവത: ${inspectedNak.deityMal} | ഗണം: ${inspectedNak.ganaMal} | യോനി: ${inspectedNak.yoniMal} | രജ്ജു: ${inspectedNak.rajjuMal} | വൃക്ഷം: ${inspectedNak.vrikshaMal}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = inspectedNak.characteristicsMal,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun PlanetaryPositionsSection(report: CompleteJathakamReport) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "നവഗ്രഹ നില, മൗഢ്യം, വക്രം, ഷഡ്ബലം & അഷ്ടകവർഗ്ഗം",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(report.planets) { pos ->
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
                            text = "${pos.planet.malayalamName} (${pos.planet.shortCodeMal})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = pos.dignity.malayalam,
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "രാശി: ${pos.rashi.malayalamName} (${pos.formattedDegree}) • ലഗ്നാൽ ${pos.houseFromLagna}-ാം ഭാവം • ചന്ദ്രനാൽ ${pos.houseFromMoon}-ാം ഭാവം",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "നക്ഷത്രം: ${pos.nakshatra.malayalamName} (പാദം ${pos.pada}) • നവാംശകം (D9): ${pos.navamsaRashi.malayalamName} • ദശാംശം (D10): ${pos.dasamsaRashi.malayalamName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val flags = buildList {
                        if (pos.isRetrograde) add("വക്രഗതി (Retrograde)")
                        else add("നേർഗതി (Direct)")
                        if (pos.isCombust) add("മൗഢ്യം (Combust)")
                        if (pos.ownedHouses.isNotEmpty()) add("ആധിപത്യം: ${pos.ownedHouses.joinToString(", ")} ഭാവങ്ങൾ")
                        if (pos.aspectedHouses.isNotEmpty()) add("ദൃഷ്ടി: ${pos.aspectedHouses.joinToString(", ")} ഭാവങ്ങൾ")
                        if (pos.conjunctPlanets.isNotEmpty()) add("യോഗം: ${pos.conjunctPlanets.joinToString(", ") { it.malayalamName }}")
                    }
                    Text(
                        text = flags.joinToString(" • "),
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (pos.planet != Planet.MANDI) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ഷഡ്ബലം: ${String.format(Locale.US, "%.2f", pos.shadbalaRupas)} രൂപം (${pos.shadbalaPercentage}%)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "അഷ്ടകവർഗ്ഗ പരൽ: ${pos.ashtakavargaBindus} ബിന്ദു",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BhavaAnalysisSection(report: CompleteJathakamReport) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ദ്വാദശ ഭാവ ബലവും സർവ്വാഷ്ടകവർഗ്ഗവും (12 Bhavas)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(report.bhavas) { bhava ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${bhava.houseNumber}-ാം ഭാവം — ${bhava.rashi.malayalamName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ഭാവബലം: ${bhava.bhavaStrengthScore}% | പരൽ: ${bhava.sarvashtakavargaBindus}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Text(
                        text = bhava.significationMal,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "ഭാവാധിപൻ: ${bhava.lord.malayalamName} (${bhava.lordPlacedHouse}-ാം ഭാവത്തിൽ സ്ഥിതി ചെയ്യുന്നു)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "ഭാവത്തിൽ നിൽക്കുന്ന ഗ്രഹങ്ങൾ: ${if (bhava.occupants.isEmpty()) "ഇല്ല" else bhava.occupants.joinToString(", ") { it.malayalamName }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "ഭാവത്തിലേക്ക് ദൃഷ്ടി ചെയ്യുന്ന ഗ്രഹങ്ങൾ: ${if (bhava.aspectingPlanets.isEmpty()) "ഇല്ല" else bhava.aspectingPlanets.joinToString(", ") { it.malayalamName }}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun DivisionalChartsSection(report: CompleteJathakamReport) {
    var selectedChartType by remember { mutableStateOf(DivisionalChartType.D9) }
    val chartData = report.divisionalCharts[selectedChartType] ?: report.divisionalCharts[DivisionalChartType.D1]!!

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "ഷോഡശവർഗ്ഗ ചക്രങ്ങൾ (Complete D1 – D60 Divisional Charts)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(DivisionalChartType.entries) { cType ->
                    FilterChip(
                        selected = (selectedChartType == cType),
                        onClick = { selectedChartType = cType },
                        label = { Text("${cType.code}: ${cType.malayalamName.substringBefore(" ")}") },
                        modifier = Modifier.testTag("div_chart_chip_${cType.code.lowercase()}")
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "${chartData.chartType.code} — ${chartData.chartType.malayalamName}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = chartData.houseSummaryMal,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    KeralaRashiChakraView(
                        chartTitleMalayalam = "${chartData.chartType.code} ${chartData.chartType.malayalamName.substringBefore(" ")}",
                        chartSubtitleMalayalam = chartData.chartType.significationMal,
                        lagnaRashi = chartData.lagnaRashi,
                        planetSigns = chartData.planetSigns
                    )
                }
            }
        }

        // Planet Positions in selected Divisional Chart
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${chartData.chartType.code} ഗ്രഹനില പട്ടിക",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    PanchangaKeyValueRow("ലഗ്നം (Ascendant)", chartData.lagnaRashi.malayalamName)
                    chartData.planetSigns.forEach { (planet, rashi) ->
                        val hNum = ((rashi.index - chartData.lagnaRashi.index + 12) % 12) + 1
                        PanchangaKeyValueRow(planet.malayalamName, "${rashi.malayalamName} ($hNum-ാം ഭാവം)")
                    }
                }
            }
        }
    }
}
