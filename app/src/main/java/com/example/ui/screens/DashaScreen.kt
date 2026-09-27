package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.PeriodClassificationBadge

/**
 * Interactive Vimshottari Dasha Screen:
 * Supports tapping Mahadasha -> Antardasha -> Pratyantardasha with full Malayalam interpretations.
 */
@Composable
fun DashaScreen(report: CompleteJathakamReport) {
    var selectedMaha by remember(report) { mutableStateOf(report.currentMahadasha) }
    var selectedAntar by remember(selectedMaha) {
        mutableStateOf(selectedMaha.antardashas.find { it.isCurrent } ?: selectedMaha.antardashas.first())
    }
    var selectedPraty by remember(selectedAntar) {
        mutableStateOf(selectedAntar.pratyantardashas.find { it.isCurrent } ?: selectedAntar.pratyantardashas.first())
    }
    var showFullMahaDetails by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("dasha_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Birth Dasha Balance & Current Active Period Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "വിംശോത്തരി ദശാ-അപഹാര-പ്രത്യന്തരദശ ക്രമം",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = report.birthDashaBalanceMal,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Text(
                        text = "നിലവിലെ ദശാകാലം: ${report.currentMahadasha.lord.malayalamName} മഹാദശ → ${report.currentAntardasha.antardashaLord.malayalamName} അപഹാരം → ${report.currentPratyantardasha.pratyantardashaLord.malayalamName} പ്രത്യന്തരദശ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "മഹാദശ → അപഹാരം → പ്രത്യന്തരദശ എന്ന ക്രമത്തിൽ തൊട്ട് (Tap) ഓരോ കാലഘട്ടത്തിന്റെയും വിശദമായ മലയാളം ഫലങ്ങൾ വായിക്കാം.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 2. Level 1: Interactive 9 Mahadasha Timeline Selector
        item {
            Text(
                text = "ഘട്ടം 1: മഹാദശ തിരഞ്ഞെടുക്കുക (Select Mahadasha)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(report.mahadashas) { maha ->
                    val isSelected = (maha.lord == selectedMaha.lord && maha.startEpochMillis == selectedMaha.startEpochMillis)
                    Surface(
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .width(175.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (isSelected || maha.isCurrent) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedMaha = maha
                                selectedAntar = maha.antardashas.find { it.isCurrent } ?: maha.antardashas.first()
                                selectedPraty = selectedAntar.pratyantardashas.find { it.isCurrent } ?: selectedAntar.pratyantardashas.first()
                            }
                            .testTag("maha_card_${maha.lord.name.lowercase()}")
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = maha.lord.malayalamName.substringBefore(" "),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                if (maha.isCurrent) {
                                    Text(
                                        text = "നിലവിൽ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                            Text(
                                text = "${maha.startDateFormatted} –\n${maha.endDateFormatted}",
                                fontSize = 11.5.sp,
                                lineHeight = 15.sp
                            )
                            Text(
                                text = "ശേഷിക്കുന്നത്: ${maha.remainingYearsFormatted}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Selected Mahadasha Detailed Prediction Card
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${selectedMaha.lord.malayalamName} മഹാദശ ഫലം",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${selectedMaha.startDateFormatted} മുതൽ ${selectedMaha.endDateFormatted} വരെ (${selectedMaha.durationYears.toInt()} വർഷം)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        PeriodClassificationBadge(classification = selectedMaha.classification)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showFullMahaDetails = !showFullMahaDetails },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedMaha.lordPlacementSummaryMal,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (showFullMahaDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Mahadasha Details"
                        )
                    }

                    if (showFullMahaDetails) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Text(
                            text = selectedMaha.detailedInterpretationMal,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // 3. Level 2: Antardasha Selector inside Selected Mahadasha
        item {
            Text(
                text = "ഘട്ടം 2: ${selectedMaha.lord.malayalamName.substringBefore(" ")} ദശയിലെ അപഹാരങ്ങൾ (Select Antardasha)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(selectedMaha.antardashas) { antar ->
                    val isSelected = (antar.antardashaLord == selectedAntar.antardashaLord && antar.startEpochMillis == selectedAntar.startEpochMillis)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedAntar = antar
                            selectedPraty = antar.pratyantardashas.find { it.isCurrent } ?: antar.pratyantardashas.first()
                        },
                        label = {
                            Text(
                                "${antar.antardashaLord.malayalamName.substringBefore(" ")}${if (antar.isCurrent) " ★" else ""}",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        modifier = Modifier.testTag("antar_chip_${antar.antardashaLord.name.lowercase()}")
                    )
                }
            }
        }

        // Selected Antardasha Detailed Prediction Card
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${selectedMaha.lord.malayalamName.substringBefore(" ")} ദശ — ${selectedAntar.antardashaLord.malayalamName} അപഹാരം",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${selectedAntar.startDateFormatted} – ${selectedAntar.endDateFormatted} (${selectedAntar.durationMonthsFormatted})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        PeriodClassificationBadge(classification = selectedAntar.classification)
                    }

                    Text(
                        text = "സജീവ ഭാവങ്ങൾ: ${selectedAntar.relevantHouses.joinToString(", ")}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = selectedAntar.detailedInterpretationMal,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // 4. Level 3: Pratyantardasha Selector & Detailed Analysis
        item {
            Text(
                text = "ഘട്ടം 3: പ്രത്യന്തരദശ തിരഞ്ഞെടുക്കുക (Pratyantardasha Drill-down)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(selectedAntar.pratyantardashas) { praty ->
                    val isSelected = (praty.pratyantardashaLord == selectedPraty.pratyantardashaLord && praty.startEpochMillis == selectedPraty.startEpochMillis)
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedPraty = praty },
                        label = {
                            Text(
                                "${praty.pratyantardashaLord.malayalamName.substringBefore(" ")}${if (praty.isCurrent) " ★" else ""}",
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        modifier = Modifier.testTag("praty_chip_${praty.pratyantardashaLord.name.lowercase()}")
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("pratyantardasha_detail_card")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "പ്രത്യന്തരദശ: ${selectedPraty.pratyantardashaLord.malayalamName}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        PeriodClassificationBadge(classification = selectedPraty.classification)
                    }
                    PanchangaKeyValueRow("കാലയളവ് (Dates)", "${selectedPraty.startDateFormatted} – ${selectedPraty.endDateFormatted}")
                    PanchangaKeyValueRow("ഗ്രഹ സംയോഗം", "${selectedPraty.mahadashaLord.malayalamName} → ${selectedPraty.antardashaLord.malayalamName} → ${selectedPraty.pratyantardashaLord.malayalamName}")
                    PanchangaKeyValueRow("ബന്ധപ്പെട്ട ഭാവങ്ങൾ", selectedPraty.relevantHouses.joinToString(", "))
                    PanchangaKeyValueRow("യോഗ സൂചനകൾ", selectedPraty.relevantYogas.joinToString(", "))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Text(
                        text = selectedPraty.transitSupportMal,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = selectedPraty.interpretationMal,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
