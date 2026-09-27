package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.components.RewardedDetailedAstrologyCard
import com.example.viewmodel.PredictionSubTab

@Composable
fun PredictionsScreen(
    report: CompleteJathakamReport,
    selectedSubTab: PredictionSubTab,
    onSelectSubTab: (PredictionSubTab) -> Unit,
    rewardedAdEnabled: Boolean = true,
    isRewardedUnlocked: Boolean = false,
    onUnlockRewarded: () -> Unit = {},
    onShowMessage: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PredictionSubTab.entries) { sub ->
                FilterChip(
                    selected = (sub == selectedSubTab),
                    onClick = { onSelectSubTab(sub) },
                    label = { Text(sub.malayalam, style = MaterialTheme.typography.labelLarge) },
                    modifier = Modifier.testTag("pred_subtab_${sub.name.lowercase()}")
                )
            }
        }

        when (selectedSubTab) {
            PredictionSubTab.CAREER_GOVT_PSC -> CareerAndPscModuleView(
                report = report,
                rewardedAdEnabled = rewardedAdEnabled,
                isRewardedUnlocked = isRewardedUnlocked,
                onUnlockRewarded = onUnlockRewarded,
                onShowMessage = onShowMessage
            )
            PredictionSubTab.EDUCATION -> SingleLifeDomainListView(
                domains = listOf(report.educationReport),
                report = report,
                rewardedAdEnabled = rewardedAdEnabled,
                isRewardedUnlocked = isRewardedUnlocked,
                onUnlockRewarded = onUnlockRewarded,
                onShowMessage = onShowMessage
            )
            PredictionSubTab.MARRIAGE_PARTNER -> SingleLifeDomainListView(
                domains = listOf(report.marriageReport, report.partnerPersonalityReport, report.partnerAppearanceReport),
                report = report,
                rewardedAdEnabled = rewardedAdEnabled,
                isRewardedUnlocked = isRewardedUnlocked,
                onUnlockRewarded = onUnlockRewarded,
                onShowMessage = onShowMessage
            )
            PredictionSubTab.FINANCE_BUSINESS -> SingleLifeDomainListView(
                domains = listOf(report.financeReport, report.businessReport),
                report = report,
                rewardedAdEnabled = rewardedAdEnabled,
                isRewardedUnlocked = isRewardedUnlocked,
                onUnlockRewarded = onUnlockRewarded,
                onShowMessage = onShowMessage
            )
            PredictionSubTab.PROPERTY_FOREIGN -> SingleLifeDomainListView(
                domains = listOf(report.propertyVehicleReport, report.foreignTravelReport),
                report = report,
                rewardedAdEnabled = rewardedAdEnabled,
                isRewardedUnlocked = isRewardedUnlocked,
                onUnlockRewarded = onUnlockRewarded,
                onShowMessage = onShowMessage
            )
            PredictionSubTab.FAMILY_CHILDREN -> SingleLifeDomainListView(
                domains = listOf(report.familyReport, report.childrenReport),
                report = report,
                rewardedAdEnabled = rewardedAdEnabled,
                isRewardedUnlocked = isRewardedUnlocked,
                onUnlockRewarded = onUnlockRewarded,
                onShowMessage = onShowMessage
            )
            PredictionSubTab.SPIRITUALITY_LIFESTYLE -> SingleLifeDomainListView(
                domains = listOf(report.spiritualityReport, report.healthLifestyleReport),
                report = report,
                rewardedAdEnabled = rewardedAdEnabled,
                isRewardedUnlocked = isRewardedUnlocked,
                onUnlockRewarded = onUnlockRewarded,
                onShowMessage = onShowMessage
            )
            PredictionSubTab.LIFE_TIMELINE -> LifeTimelineView(report.lifeTimeline)
            PredictionSubTab.PERIODIC -> PeriodicHoroscopeView(report.periodicHoroscope)
        }
    }
}

@Composable
private fun CareerAndPscModuleView(
    report: CompleteJathakamReport,
    rewardedAdEnabled: Boolean,
    isRewardedUnlocked: Boolean,
    onUnlockRewarded: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    var searchCategory by remember { mutableStateOf("") }
    val filteredCategories = remember(report.careerCategories, searchCategory) {
        if (searchCategory.isBlank()) report.careerCategories
        else report.careerCategories.filter {
            it.titleMalayalam.contains(searchCategory, ignoreCase = true) ||
                it.titleEnglish.contains(searchCategory, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("career_module_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Overview card
        item {
            LifeDomainCard(domain = report.careerGeneralReport)
        }

        if (rewardedAdEnabled) {
            item {
                RewardedDetailedAstrologyCard(
                    report = report,
                    isUnlocked = isRewardedUnlocked,
                    onUnlockEarned = onUnlockRewarded,
                    onShowMessage = onShowMessage
                )
            }
        }

        // Filter bar for 27 Career Categories
        item {
            OutlinedTextField(
                value = searchCategory,
                onValueChange = { searchCategory = it },
                label = { Text("തൊഴിൽ മേഖല തിരയുക (ഉദാ: PSC, KAS, UPSC, Govt, IT, Banking, Teaching)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("career_category_search")
            )
        }

        items(filteredCategories) { cat ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().testTag("career_cat_${cat.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = cat.titleMalayalam,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "അനുയോജ്യതാ സൂചിക: ${cat.suitabilityScore}% • ഭാവങ്ങൾ: ${cat.relevantHouses.joinToString(", ")}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        PeriodClassificationBadge(classification = cat.classification)
                    }

                    LinearProgressIndicator(
                        progress = { cat.suitabilityScore / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "• അനുകൂല ഘടകങ്ങൾ: ${cat.supportingFactorsMal}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• ശ്രദ്ധിക്കേണ്ട ഘടകങ്ങൾ: ${cat.challengingFactorsMal}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• ബന്ധപ്പെട്ട ഗ്രഹങ്ങൾ: ${cat.relevantPlanets.joinToString(", ") { it.malayalamName }} • യോഗങ്ങൾ: ${cat.relevantYogas.joinToString(", ")}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• ദശാ-അപഹാര പിന്തുണ: ${cat.activeDashaSupportMal}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "• ഗോചരവും സ്ഥിരതയും: ${cat.transitSupportMal} ${cat.stabilityAndPromotionMal}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = cat.examAndSelectionNoteMal,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SingleLifeDomainListView(
    domains: List<LifeDomainReport>,
    report: CompleteJathakamReport,
    rewardedAdEnabled: Boolean,
    isRewardedUnlocked: Boolean,
    onUnlockRewarded: () -> Unit,
    onShowMessage: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(domains) { dom ->
            LifeDomainCard(domain = dom)
        }
        if (rewardedAdEnabled) {
            item {
                RewardedDetailedAstrologyCard(
                    report = report,
                    isUnlocked = isRewardedUnlocked,
                    onUnlockEarned = onUnlockRewarded,
                    onShowMessage = onShowMessage
                )
            }
        }
    }
}

@Composable
fun LifeDomainCard(domain: LifeDomainReport) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().testTag("domain_card_${domain.domainId}")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = domain.titleMalayalam,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                PeriodClassificationBadge(classification = domain.classification)
            }

            Text(
                text = "പ്രധാന ഭാവങ്ങൾ: ${domain.keyHouses.joinToString(", ")} • ഗ്രഹങ്ങൾ: ${domain.keyPlanets.joinToString(", ") { it.malayalamName }}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = domain.summaryMalayalam,
                style = MaterialTheme.typography.bodyLarge
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            domain.detailedSections.forEach { (heading, content) ->
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "▸ $heading",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = content,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            Text(
                text = "അനുകൂല കാലഘട്ടങ്ങൾ: ${domain.supportivePeriodsMal}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "ശ്രദ്ധിക്കേണ്ട സൂചനകൾ: ${domain.cautionPeriodsMal}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LifeTimelineView(stages: List<LifeTimelineStage>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("life_timeline_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "ജീവിത കാലഘട്ട അവലോകനം (0–10 മുതൽ 60+ വയസ്സ് വരെ)",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(stages) { stage ->
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
                            text = stage.titleMalayalam,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        PeriodClassificationBadge(classification = stage.classification)
                    }
                    Text(
                        text = "• പ്രധാന ദശ: ${stage.dominantMahadashaMal} (${stage.dominantAntardashaMal})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "• യോഗബലം: ${stage.activeYogasMal} • ഗോചര സൂചന: ${stage.majorTransitNoteMal}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stage.detailedMalayalamNarrative,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun PeriodicHoroscopeView(periodic: PeriodicHoroscopeReport) {
    val cards = listOf(
        "ഇന്നത്തെ വ്യക്തിഗത ഫലം (Daily Horoscope)" to periodic.dailyMal,
        "ഈ ആഴ്ചയിലെ ഫലം (Weekly Horoscope)" to periodic.weeklyMal,
        "ഈ മാസത്തെ ഫലം (Monthly Horoscope)" to periodic.monthlyMal,
        "ഈ വർഷത്തെ സമഗ്ര ഫലം (Yearly Horoscope)" to periodic.yearlyMal
    )
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(cards) { (title, body) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
