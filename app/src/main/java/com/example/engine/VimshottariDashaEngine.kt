package com.example.engine

import com.example.model.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Accurate Vimshottari Dasha, Antardasha & Pratyantardasha Calculation and Personalized Malayalam Prediction Engine.
 * Calculates strictly from the actual Moon Sidereal Longitude in its Nakshatra (13° 20' span).
 */
object VimshottariDashaEngine {

    private const val NAKSHATRA_SPAN_DEG = 360.0 / 27.0
    private const val MILLIS_PER_YEAR = (365.2425 * 24.0 * 3600.0 * 1000.0)

    private fun formatEpochDate(epochMillis: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("GMT+05:30")
        return sdf.format(Date(epochMillis))
    }

    fun calculateVimshottariDashas(
        birthData: BirthData,
        moonSiderealLon: Double,
        lagnaRashi: Rashi,
        planetsMap: Map<Planet, PlanetPosition>,
        nowEpochMillis: Long = System.currentTimeMillis()
    ): Pair<String, List<MahadashaPeriod>> {
        val normMoon = AstronomicalEphemerisEngine.normalizeDegrees(moonSiderealLon)
        val nakIndex = floor(normMoon / NAKSHATRA_SPAN_DEG).toInt().coerceIn(0, 26)
        val nakshatra = Nakshatra.fromIndex(nakIndex)
        val degInNak = normMoon - (nakIndex * NAKSHATRA_SPAN_DEG)
        val remainingFraction = ((NAKSHATRA_SPAN_DEG - degInNak) / NAKSHATRA_SPAN_DEG).coerceIn(0.001, 1.0)

        val birthLord = nakshatra.lord
        val fullYears = birthLord.dashaYears.toDouble()
        val balanceYearsTotal = fullYears * remainingFraction
        val balY = floor(balanceYearsTotal).toInt()
        val remMonthsTotal = (balanceYearsTotal - balY) * 12.0
        val balM = floor(remMonthsTotal).toInt()
        val balD = ((remMonthsTotal - balM) * 30.0).roundToInt().coerceIn(0, 30)

        val birthDashaBalanceMal = "${birthLord.malayalamName} മഹാദശ ശിഷ്ടം: $balY വർഷം, $balM മാസം, $balD ദിവസം"

        val birthCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+05:30")).apply {
            set(birthData.year, birthData.month - 1, birthData.day, birthData.hour, birthData.minute, birthData.second)
            set(Calendar.MILLISECOND, 0)
        }
        val birthEpochMillis = birthCal.timeInMillis

        val elapsedBeforeBirthMillis = ((fullYears - balanceYearsTotal) * MILLIS_PER_YEAR).toLong()
        var currentMahaStart = birthEpochMillis - elapsedBeforeBirthMillis

        val order = Planet.vimshottariOrder
        val startOrderIdx = order.indexOf(birthLord).coerceAtLeast(0)
        val mahadashas = mutableListOf<MahadashaPeriod>()

        for (i in 0 until 9) {
            val mahaLord = order[(startOrderIdx + i) % 9]
            val mahaYears = mahaLord.dashaYears.toDouble()
            val mahaDurationMillis = (mahaYears * MILLIS_PER_YEAR).toLong()
            val mahaEnd = currentMahaStart + mahaDurationMillis
            val displayStart = if (i == 0) birthEpochMillis else currentMahaStart

            val isCurrentMaha = nowEpochMillis in displayStart..mahaEnd
            val elapsedPct = when {
                nowEpochMillis <= displayStart -> 0
                nowEpochMillis >= mahaEnd -> 100
                else -> (((nowEpochMillis - displayStart).toDouble() / (mahaEnd - displayStart).coerceAtLeast(1L)) * 100.0).roundToInt().coerceIn(1, 99)
            }
            val remYears = if (nowEpochMillis < mahaEnd) {
                val baseFrom = maxOf(nowEpochMillis, displayStart)
                val yrs = (mahaEnd - baseFrom).toDouble() / MILLIS_PER_YEAR
                String.format(Locale.US, "%.1f വർഷം", yrs)
            } else {
                "പൂർത്തിയായി"
            }

            val mahaPos = planetsMap[mahaLord]
            val mahaClass = evaluatePeriodClassification(mahaLord, mahaLord, planetsMap)

            val antardashas = mutableListOf<AntardashaPeriod>()
            val antarStartIdx = order.indexOf(mahaLord).coerceAtLeast(0)
            var currentAntarStart = currentMahaStart

            for (j in 0 until 9) {
                val antarLord = order[(antarStartIdx + j) % 9]
                val antarYears = (mahaYears * antarLord.dashaYears.toDouble()) / 120.0
                val antarMillis = (antarYears * MILLIS_PER_YEAR).toLong()
                val antarEnd = currentAntarStart + antarMillis

                if (antarEnd > birthEpochMillis) {
                    val effectiveAntarStart = maxOf(currentAntarStart, birthEpochMillis)
                    val isCurrentAntar = nowEpochMillis in effectiveAntarStart..antarEnd
                    val antarClass = evaluatePeriodClassification(mahaLord, antarLord, planetsMap)

                    val pratyantars = mutableListOf<PratyantardashaPeriod>()
                    val pratyStartIdx = order.indexOf(antarLord).coerceAtLeast(0)
                    var currentPratyStart = currentAntarStart

                    for (k in 0 until 9) {
                        val pratyLord = order[(pratyStartIdx + k) % 9]
                        val pratyYears = (antarYears * pratyLord.dashaYears.toDouble()) / 120.0
                        val pratyMillis = (pratyYears * MILLIS_PER_YEAR).toLong()
                        val pratyEnd = currentPratyStart + pratyMillis

                        if (pratyEnd > birthEpochMillis) {
                            val effPratyStart = maxOf(currentPratyStart, birthEpochMillis)
                            val isCurrentPraty = nowEpochMillis in effPratyStart..pratyEnd
                            val pratyClass = evaluatePeriodClassification(antarLord, pratyLord, planetsMap)
                            val relHouses = buildRelevantHouses(mahaLord, antarLord, pratyLord, planetsMap)
                            pratyantars.add(
                                PratyantardashaPeriod(
                                    mahadashaLord = mahaLord,
                                    antardashaLord = antarLord,
                                    pratyantardashaLord = pratyLord,
                                    startEpochMillis = effPratyStart,
                                    endEpochMillis = pratyEnd,
                                    startDateFormatted = formatEpochDate(effPratyStart),
                                    endDateFormatted = formatEpochDate(pratyEnd),
                                    isCurrent = isCurrentPraty,
                                    classification = pratyClass,
                                    relevantHouses = relHouses,
                                    relevantYogas = buildRelevantYogaNames(mahaLord, antarLord, pratyLord, planetsMap),
                                    transitSupportMal = "ഗോചരത്തിൽ വ്യാഴത്തിന്റെയും ശനിയുടെയും ഭാവസ്ഥിതി അനുസരിച്ച് ${relHouses.joinToString(", ")} ഭാവങ്ങളുടെ ഫലങ്ങൾ സജീവമാകുന്നു.",
                                    interpretationMal = generatePratyantardashaMalayalam(
                                        mahaLord, antarLord, pratyLord, pratyClass, relHouses, planetsMap
                                    )
                                )
                            )
                        }
                        currentPratyStart = pratyEnd
                    }

                    val antarHouses = buildRelevantHouses(mahaLord, antarLord, null, planetsMap)
                    val antarNarratives = generateAntardashaDetailedMalayalam(
                        mahaLord, antarLord, antarClass, planetsMap
                    )

                    antardashas.add(
                        AntardashaPeriod(
                            mahadashaLord = mahaLord,
                            antardashaLord = antarLord,
                            startEpochMillis = effectiveAntarStart,
                            endEpochMillis = antarEnd,
                            startDateFormatted = formatEpochDate(effectiveAntarStart),
                            endDateFormatted = formatEpochDate(antarEnd),
                            durationMonthsFormatted = String.format(Locale.US, "%.1f മാസം", antarYears * 12.0),
                            isCurrent = isCurrentAntar,
                            classification = antarClass,
                            relevantHouses = antarHouses,
                            careerAndPscMal = antarNarratives[0],
                            financeAndBusinessMal = antarNarratives[1],
                            marriageAndFamilyMal = antarNarratives[2],
                            propertyAndForeignMal = antarNarratives[3],
                            detailedInterpretationMal = antarNarratives[4],
                            pratyantardashas = pratyantars
                        )
                    )
                }
                currentAntarStart = antarEnd
            }

            val mahaPlacementSummary = if (mahaPos != null) {
                "${mahaLord.malayalamName} ${mahaPos.rashi.malayalamName} രാശിയിൽ (${mahaPos.houseFromLagna}-ാം ഭാവം), ${mahaPos.nakshatra.malayalamName} നക്ഷത്രം ${mahaPos.pada}-ാം പാദം • ${mahaPos.dignity.malayalam}"
            } else {
                "${mahaLord.malayalamName} മഹാദശ"
            }

            mahadashas.add(
                MahadashaPeriod(
                    lord = mahaLord,
                    startEpochMillis = displayStart,
                    endEpochMillis = mahaEnd,
                    startDateFormatted = formatEpochDate(displayStart),
                    endDateFormatted = formatEpochDate(mahaEnd),
                    durationYears = mahaYears,
                    isCurrent = isCurrentMaha,
                    elapsedPercentage = elapsedPct,
                    remainingYearsFormatted = remYears,
                    classification = mahaClass,
                    lordPlacementSummaryMal = mahaPlacementSummary,
                    detailedInterpretationMal = generateMahadashaDetailedMalayalam(
                        mahaLord, mahaClass, lagnaRashi, planetsMap
                    ),
                    antardashas = antardashas
                )
            )
            currentMahaStart = mahaEnd
        }

        return birthDashaBalanceMal to mahadashas
    }

    private fun evaluatePeriodClassification(
        primaryLord: Planet,
        subLord: Planet,
        planetsMap: Map<Planet, PlanetPosition>
    ): PeriodClassification {
        val pPos = planetsMap[primaryLord] ?: return PeriodClassification.MIXED
        val sPos = planetsMap[subLord] ?: return PeriodClassification.MIXED

        var score = 50
        when (pPos.houseFromLagna) {
            1, 4, 5, 7, 9, 10, 11 -> score += 14
            2, 3 -> score += 5
            6, 8, 12 -> score -= 16
        }
        when (sPos.houseFromLagna) {
            1, 4, 5, 7, 9, 10, 11 -> score += 14
            2, 3 -> score += 4
            6, 8, 12 -> score -= 15
        }
        when (pPos.dignity) {
            PlanetaryDignity.EXALTED, PlanetaryDignity.MOOLATRIKONA, PlanetaryDignity.OWN_SIGN -> score += 12
            PlanetaryDignity.DEBILITATED, PlanetaryDignity.ENEMY_SIGN -> score -= 12
            else -> {}
        }
        when (sPos.dignity) {
            PlanetaryDignity.EXALTED, PlanetaryDignity.MOOLATRIKONA, PlanetaryDignity.OWN_SIGN -> score += 12
            PlanetaryDignity.DEBILITATED, PlanetaryDignity.ENEMY_SIGN -> score -= 12
            else -> {}
        }
        if (sPos.isCombust) score -= 7
        val relDist = ((sPos.houseFromLagna - pPos.houseFromLagna + 12) % 12) + 1
        if (relDist == 6 || relDist == 8 || relDist == 12) score -= 10
        if (relDist == 1 || relDist == 5 || relDist == 9 || relDist == 4 || relDist == 10) score += 8

        return when {
            score >= 64 -> PeriodClassification.FAVORABLE
            score <= 42 -> PeriodClassification.CHALLENGING
            else -> PeriodClassification.MIXED
        }
    }

    private fun buildRelevantHouses(
        mahaLord: Planet,
        antarLord: Planet,
        pratyLord: Planet?,
        planetsMap: Map<Planet, PlanetPosition>
    ): List<Int> {
        val set = sortedSetOf<Int>()
        planetsMap[mahaLord]?.let {
            set.add(it.houseFromLagna)
            set.addAll(it.ownedHouses)
        }
        planetsMap[antarLord]?.let {
            set.add(it.houseFromLagna)
            set.addAll(it.ownedHouses)
        }
        if (pratyLord != null) {
            planetsMap[pratyLord]?.let {
                set.add(it.houseFromLagna)
                set.addAll(it.ownedHouses)
            }
        }
        return set.toList()
    }

    private fun buildRelevantYogaNames(
        mahaLord: Planet,
        antarLord: Planet,
        pratyLord: Planet,
        planetsMap: Map<Planet, PlanetPosition>
    ): List<String> {
        val list = mutableListOf<String>()
        val mPos = planetsMap[mahaLord]
        val aPos = planetsMap[antarLord]
        if (mPos != null && aPos != null) {
            if (mPos.houseFromLagna in listOf(1, 4, 7, 10) || aPos.houseFromLagna in listOf(1, 4, 7, 10)) {
                list.add("കേന്ദ്രാധിപത്യ-ത്രികോണ ബന്ധം")
            }
            if (mPos.ownedHouses.any { it in listOf(2, 11) } || aPos.ownedHouses.any { it in listOf(2, 11) }) {
                list.add("ധന-ലാഭ ഭാവ ഉത്തേജനം")
            }
            if (mPos.ownedHouses.contains(10) || aPos.ownedHouses.contains(10) || pratyLord == Planet.SUN || pratyLord == Planet.SATURN) {
                list.add("കർമ്മഭാവ സജീവത")
            }
        }
        if (list.isEmpty()) list.add("ദശാ-അപഹാര ഗ്രഹയോഗ ബന്ധം")
        return list
    }

    private fun generateMahadashaDetailedMalayalam(
        lord: Planet,
        classification: PeriodClassification,
        lagnaRashi: Rashi,
        planetsMap: Map<Planet, PlanetPosition>
    ): String {
        val pos = planetsMap[lord] ?: return ""
        val ownedStr = if (pos.ownedHouses.isNotEmpty()) {
            "${pos.ownedHouses.joinToString(", ")} എന്നീ ഭാവങ്ങളുടെ ആധിപത്യവും"
        } else {
            "ഛായാഗ്രഹ സവിശേഷതകളും"
        }
        val retroNote = if (pos.isRetrograde) "ഗ്രഹം വക്രഗതിയിലായതിനാൽ ആഴത്തിലുള്ള പുനർവിചിന്തനവും പരിശ്രമവും ആവശ്യമാണ്." else ""
        val combustNote = if (pos.isCombust) "സൂര്യസാമീപ്യം മൂലം മൗഢ്യാവസ്ഥ ഉള്ളതിനാൽ ഫലപ്രാപ്തിക്കായി ക്ഷമയും സ്ഥിരോത്സാഹവും അനിവാര്യമാണ്." else ""

        return """
            【${classification.malayalamLabel}】 — ${lord.malayalamName} മഹാദശ (${lord.dashaYears} വർഷം)
            
            • ഗ്രഹസ്ഥിതി വിശകലനം: ${lagnaRashi.malayalamName} ലഗ്നജാതകത്തിൽ ${lord.malayalamName} $ownedStr വഹിച്ചുകൊണ്ട് ${pos.houseFromLagna}-ാം ഭാവത്തിൽ (${pos.rashi.malayalamName} രാശിയിൽ, ${pos.nakshatra.malayalamName} നക്ഷത്രം ${pos.pada}-ാം പാദം) ${pos.dignity.malayalam} ആയി സ്ഥിതി ചെയ്യുന്നു. നവാംശകത്തിൽ (D9) ${pos.navamsaRashi.malayalamName} രാശിയിലും ദശാംശകത്തിൽ (D10) ${pos.dasamsaRashi.malayalamName} രാശിയിലുമാണ് ഗ്രഹസ്ഥിതി. $retroNote $combustNote
            
            • തൊഴിൽ, സർക്കാർ ജോലി & PSC സാധ്യതകൾ: ${pos.houseFromLagna}-ാം ഭാവബന്ധവും ${pos.dasamsaRashi.malayalamName} ദശാംശക സ്ഥിതിയും പരിഗണിക്കുമ്പോൾ, കരിയറിൽ ${if (classification == PeriodClassification.FAVORABLE) "ഉദ്യോഗക്കയറ്റം, സ്ഥിരവരുമാനം, മത്സരപ്പരീക്ഷകളിൽ മികച്ച പ്രകടനം എന്നിവയ്ക്ക് അനുകൂലമായ സാഹചര്യം രൂപപ്പെടുന്നു" else if (classification == PeriodClassification.MIXED) "കഠിനാധ്വാനത്തിലൂടെ ഘട്ടംഘട്ടമായ പുരോഗതിയും തൊഴിൽ മാറ്റ ആലോചനകളും ഉണ്ടാകാം" else "തൊഴിൽപരമായ സമ്മർദ്ദങ്ങൾ, സ്ഥലംമാറ്റം അല്ലെങ്കിൽ പരീക്ഷകളിൽ കൂടുതൽ കഠിനപരിശ്രമം ആവശ്യമായി വരുന്ന സാഹചര്യം കാണുന്നു"}.
            
            • ധനസ്ഥിതി, വ്യവസായം & സമ്പാദ്യം: ${pos.ashtakavargaBindus} അഷ്ടകവർഗ്ഗ ബിന്ദുക്കളുടെ ബലത്തോടെ നിൽക്കുന്ന ${lord.malayalamName}, സാമ്പത്തിക കാര്യങ്ങളിൽ ${if (classification == PeriodClassification.CHALLENGING) "അമിത ചെലവുകളും കടബാധ്യതകളും ഒഴിവാക്കി കരുതലോടെ നീങ്ങണമെന്ന് സൂചിപ്പിക്കുന്നു" else "വരുമാന വർദ്ധനവിനും ദീർഘകാല നിക്ഷേപങ്ങൾക്കും സഹായകമായ അവസരങ്ങൾ നൽകുന്നു"}.
            
            • കുടുംബം, വിവാഹം, വസ്തു-വാഹന യോഗം & വിദേശയാത്ര: ${pos.aspectedHouses.joinToString(", ")} ഭാവങ്ങളിലേക്ക് ${lord.malayalamName} ദൃഷ്ടി ചെയ്യുന്നതുവഴി കുടുംബ ഉത്തരവാദിത്തങ്ങൾ, ഗൃഹനിർമ്മാണ-വാഹന ആഗ്രഹങ്ങൾ, യാത്രകൾ എന്നിവ ഈ ദശാകാലത്ത് പ്രധാന ജീവിത വിഷയങ്ങളായി മാറുന്നു.
        """.trimIndent()
    }

    private fun generateAntardashaDetailedMalayalam(
        mahaLord: Planet,
        antarLord: Planet,
        classification: PeriodClassification,
        planetsMap: Map<Planet, PlanetPosition>
    ): List<String> {
        val mPos = planetsMap[mahaLord]
        val aPos = planetsMap[antarLord]
        val mHouse = mPos?.houseFromLagna ?: 1
        val aHouse = aPos?.houseFromLagna ?: 1
        val aRashi = aPos?.rashi?.malayalamName ?: ""
        val aNak = aPos?.nakshatra?.malayalamName ?: ""
        val aDignity = aPos?.dignity?.malayalam ?: ""
        val relDist = ((aHouse - mHouse + 12) % 12) + 1

        val careerMal = when (classification) {
            PeriodClassification.FAVORABLE ->
                "മഹാദശാനാഥനായ ${mahaLord.malayalamName} (${mHouse}-ാം ഭാവം), അപഹാരനാഥനായ ${antarLord.malayalamName} (${aHouse}-ാം ഭാവം, $aDignity) എന്നിവരുടെ അനുകൂല സംയോഗം തൊഴിൽരംഗത്ത് പുതിയ ഉത്തരവാദിത്തങ്ങൾ, പ്രൊമോഷൻ, സർക്കാർ/PSC/മത്സരപ്പരീക്ഷകളിൽ ഏകാഗ്രതയും അനുകൂല ഫലസാധ്യതയും നൽകുന്നു."
            PeriodClassification.MIXED ->
                "${mahaLord.malayalamName} ദശയിലെ ${antarLord.malayalamName} അപഹാരത്തിൽ ($aHouse-ാം ഭാവബന്ധം) തൊഴിലിൽ സ്ഥിരത നിലനിർത്താൻ അധിക പരിശ്രമം വേണ്ടിവരും. ജോലി മാറ്റത്തിനോ പുതിയ സംരംഭങ്ങൾക്കോ മുതിരുമ്പോൾ രേഖകൾ സൂക്ഷ്മമായി പരിശോധിക്കുക."
            PeriodClassification.CHALLENGING ->
                "ദശാനാഥനും അപഹാരനാഥനും തമ്മിൽ $relDist-ാം ഭാവസ്ഥിതി വരുന്നതിനാൽ തൊഴിലിടത്തിൽ മേലധികാരികളുമായുള്ള അഭിപ്രായവ്യത്യാസങ്ങൾ ഒഴിവാക്കണം. തിടുക്കത്തിലുള്ള രാജി സമർപ്പണമോ വലിയ സാമ്പത്തിക റിസ്കുകളോ ഈ കാലയളവിൽ ഒഴിവാക്കുന്നത് ഉചിതം."
        }

        val financeMal = when (classification) {
            PeriodClassification.FAVORABLE ->
                "ധനവരവ് മെച്ചപ്പെടാനും മുൻകാല നിക്ഷേപങ്ങളിൽ നിന്ന് ആദായം ലഭിക്കാനും സാധ്യതയുണ്ട്. സമ്പാദ്യ പദ്ധതികൾക്ക് തുടക്കം കുറിക്കാൻ ഉചിതമായ സമയമാണ്."
            PeriodClassification.MIXED ->
                "വരുമാനത്തോടൊപ്പം കുടുംബാവശ്യങ്ങൾക്കായുള്ള ചെലവുകളും വർദ്ധിക്കാൻ സാധ്യതയുള്ളതിനാൽ കൃത്യമായ സാമ്പത്തിക അച്ചടക്കം പാലിക്കുക."
            PeriodClassification.CHALLENGING ->
                "വലിയ വായ്പകൾ, ജാമ്യം നിൽക്കൽ, ഊഹക്കച്ചവടം എന്നിവയിൽ പ്രത്യേക ജാഗ്രത പുലർത്തേണ്ട കാലഘട്ടമാണ്."
        }

        val marriageFamilyMal = when (classification) {
            PeriodClassification.FAVORABLE ->
                "കുടുംബത്തിൽ സമാധാനവും മംഗളകർമ്മങ്ങളും നടക്കാൻ അനുകൂലമായ ഗ്രഹസ്ഥിതിയാണ്. അവിവാഹിതർക്ക് അനുയോജ്യമായ ആലോചനകൾ പുരോഗമിക്കാൻ സാധ്യതയുണ്ട്."
            PeriodClassification.MIXED ->
                "കുടുംബാംഗങ്ങളുടെ അഭിപ്രായങ്ങളെ മാനിച്ച് മുന്നോട്ട് പോകുന്നത് വഴി ആശയവിനിമയത്തിലെ ചെറിയ അസ്വാരസ്യങ്ങൾ ഒഴിവാക്കാം."
            PeriodClassification.CHALLENGING ->
                "ദാമ്പത്യത്തിലും കുടുംബബന്ധങ്ങളിലും ക്ഷമയും പരസ്പര ധാരണയും അത്യന്താപേക്ഷിതമാണ്. വാഗ്വാദങ്ങൾ ഒഴിവാക്കുക."
        }

        val propertyForeignMal =
            "${antarLord.malayalamName} ${aHouse}-ാം ഭാവത്തിൽ ($aRashi രാശി, $aNak നക്ഷത്രം) നിൽക്കുന്നതിനാൽ ${if (aHouse in listOf(3, 4, 9, 12)) "വസ്തു-വാഹന കാര്യങ്ങളിലും ദൂരയാത്ര/വിദേശ അവസരങ്ങളിലും പ്രകടമായ ചലനങ്ങൾ പ്രതീക്ഷിക്കാം." else "സ്ഥലംമാറ്റം, ഗൃഹനവീകരണം എന്നിവയുമായി ബന്ധപ്പെട്ട തീരുമാനങ്ങൾ ആലോചിച്ച് കൈക്കൊള്ളുക."}"

        val fullMal = """
            【${classification.malayalamLabel}】 — ${mahaLord.malayalamName} മഹാദശയിൽ ${antarLord.malayalamName} അപഹാരം (അന്തർദശ)
            
            • ഗ്രഹബന്ധം: മഹാദശാനാഥനായ ${mahaLord.malayalamName} ലഗ്നാൽ $mHouse-ാം ഭാവത്തിലും, അപഹാരനാഥനായ ${antarLord.malayalamName} ലഗ്നാൽ $aHouse-ാം ഭാവത്തിലും ($aRashi രാശി, $aNak നക്ഷത്രം, $aDignity) സ്ഥിതി ചെയ്യുന്നു. ദശാനാഥനിൽ നിന്ന് അപഹാരനാഥൻ $relDist-ാം ഭാവത്തിലാണ് നിലകൊള്ളുന്നത്.
            • തൊഴിൽ & പരീക്ഷാ പ്രവണത: $careerMal
            • ധനസ്ഥിതി & വ്യവസായം: $financeMal
            • വിവാഹം & കുടുംബം: $marriageFamilyMal
            • വസ്തു, വാഹനം & വിദേശയാത്ര: $propertyForeignMal
        """.trimIndent()

        return listOf(careerMal, financeMal, marriageFamilyMal, propertyForeignMal, fullMal)
    }

    private fun generatePratyantardashaMalayalam(
        mahaLord: Planet,
        antarLord: Planet,
        pratyLord: Planet,
        classification: PeriodClassification,
        relHouses: List<Int>,
        planetsMap: Map<Planet, PlanetPosition>
    ): String {
        val pPos = planetsMap[pratyLord]
        val hInfo = if (pPos != null) "${pPos.houseFromLagna}-ാം ഭാവസ്ഥിതിയും (${pPos.rashi.malayalamName}) ${pPos.dignity.malayalam} അവസ്ഥയും" else "ഗ്രഹസ്ഥിതിയും"
        return "${mahaLord.malayalamName} മഹാദശ — ${antarLord.malayalamName} അപഹാരം — ${pratyLord.malayalamName} പ്രത്യന്തരദശ (${classification.malayalamLabel}): പ്രത്യന്തരദശാനാഥനായ ${pratyLord.malayalamName}-ന്റെ $hInfo മൂലം ${relHouses.joinToString(", ")} ഭാവങ്ങളുമായി ബന്ധപ്പെട്ട ഫലങ്ങൾ ഈ സൂക്ഷ്മ കാലയളവിൽ സജീവമാകുന്നു. ${if (classification == PeriodClassification.FAVORABLE) "പ്രധാന തൊഴിൽ-കുടുംബ തീരുമാനങ്ങൾക്കും പഠന-പരീക്ഷാ തയ്യാറെടുപ്പുകൾക്കും ഈ സമയം അനുകൂലമാണ്." else if (classification == PeriodClassification.MIXED) "സാധാരണ പുരോഗതിക്കൊപ്പം ചെലവുകളിലും സമയക്രമത്തിലും ശ്രദ്ധ പുലർത്തുക." else "ആരോഗ്യ-തൊഴിൽ-സാമ്പത്തിക കാര്യങ്ങളിൽ തിടുക്കം ഒഴിവാക്കി സംയമനത്തോടെ പ്രവർത്തിക്കേണ്ട സമയമാണ്."}"
    }
}
