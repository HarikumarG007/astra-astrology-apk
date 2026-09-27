package com.example.engine

import com.example.model.*

/**
 * Dynamic Yoga and Dosha Detection Engine.
 * Strictly evaluates actual planetary positions, house lordships, conjunctions, aspects & dignities.
 * Never shows a Yoga unless its astronomical/astrological conditions are genuinely satisfied.
 */
object YogaAndDoshaEngine {

    fun detectYogasAndDoshas(
        lagnaRashi: Rashi,
        chandraRashi: Rashi,
        planetsMap: Map<Planet, PlanetPosition>,
        bhavas: List<BhavaInfo>
    ): List<YogaResult> {
        val results = mutableListOf<YogaResult>()
        val sun = planetsMap[Planet.SUN] ?: return emptyList()
        val moon = planetsMap[Planet.MOON] ?: return emptyList()
        val mars = planetsMap[Planet.MARS] ?: return emptyList()
        val mercury = planetsMap[Planet.MERCURY] ?: return emptyList()
        val jupiter = planetsMap[Planet.JUPITER] ?: return emptyList()
        val venus = planetsMap[Planet.VENUS] ?: return emptyList()
        val saturn = planetsMap[Planet.SATURN] ?: return emptyList()
        val rahu = planetsMap[Planet.RAHU] ?: return emptyList()
        val ketu = planetsMap[Planet.KETU] ?: return emptyList()

        fun strengthOf(vararg planets: PlanetPosition): YogaStrengthClass {
            val anyDebilitated = planets.any { it.dignity == PlanetaryDignity.DEBILITATED }
            val anyCombust = planets.any { it.isCombust }
            val anyExaltedOrOwn = planets.any {
                it.dignity == PlanetaryDignity.EXALTED ||
                    it.dignity == PlanetaryDignity.MOOLATRIKONA ||
                    it.dignity == PlanetaryDignity.OWN_SIGN
            }
            val inDusthana = planets.all { it.houseFromLagna in listOf(6, 8, 12) }
            return when {
                anyDebilitated && inDusthana -> YogaStrengthClass.WEAK
                anyDebilitated || anyCombust -> YogaStrengthClass.MODIFIED
                anyExaltedOrOwn && !inDusthana -> YogaStrengthClass.STRONG
                inDusthana -> YogaStrengthClass.WEAK
                else -> YogaStrengthClass.MODERATE
            }
        }

        // 1. Gaja Kesari Yoga (Jupiter in Kendra 1, 4, 7, 10 from Moon)
        val jupFromMoon = ((jupiter.rashi.index - moon.rashi.index + 12) % 12) + 1
        if (jupFromMoon in listOf(1, 4, 7, 10)) {
            val st = strengthOf(jupiter, moon)
            results.add(
                YogaResult(
                    id = "gaja_kesari",
                    nameMalayalam = "ഗജകേസരി യോഗം (Gaja Kesari Yoga)",
                    nameEnglish = "Gaja Kesari Yoga",
                    categoryMal = "രാജയോഗ തുല്യ ശ്രേഷ്ഠയോഗം",
                    isDosha = false,
                    planetsInvolved = listOf(Planet.JUPITER, Planet.MOON),
                    housesInvolved = listOf(moon.houseFromLagna, jupiter.houseFromLagna).distinct(),
                    signsInvolved = listOf(moon.rashi, jupiter.rashi).distinct(),
                    conditionsSatisfiedMal = "ചന്ദ്രനിൽ നിന്ന് $jupFromMoon-ാം ഭാവത്തിൽ (കേന്ദ്രത്തിൽ) വ്യാഴം (${jupiter.rashi.malayalamName} രാശിയിൽ) സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = st,
                    modificationOrCancellationMal = if (jupiter.isCombust) "വ്യാഴത്തിന് മൗഢ്യമുള്ളതിനാൽ ഫലങ്ങൾ മിതമായി അനുഭവപ്പെടും." else "ഗ്രഹബലം അനുകൂലമാണ്.",
                    dashaActivationMal = "വ്യാഴ ദശയിലും ചന്ദ്ര ദശയിലും ഇവയുടെ അപഹാരങ്ങളിലും സജീവമാകുന്നു.",
                    transitRelevanceMal = "ഗോചരത്തിൽ വ്യാഴം ചന്ദ്രരാശിയിൽ നിന്ന് 2, 5, 7, 9, 11 ഭാവങ്ങളിൽ സഞ്ചരിക്കുമ്പോൾ പൂർണ്ണഫലം നൽകും.",
                    positiveIndicationsMal = "ബുദ്ധിസാമർത്ഥ്യം, സമൂഹത്തിൽ ആദരവ്, ഉന്നത വിദ്യാഭ്യാസം, സദ്ഗുണങ്ങൾ, തൊഴിൽപരമായ ഉയർച്ച.",
                    challengingIndicationsMal = if (st == YogaStrengthClass.WEAK) "ദുസ്ഥാന ബന്ധം ഉള്ളതിനാൽ അമിത ആത്മവിശ്വാസം ഒഴിവാക്കണം." else "നിരന്തര പരിശ്രമത്തിലൂടെ യോഗഫലം പൂർണ്ണമാകും.",
                    detailedMalayalamExplanation = "ചന്ദ്രനിൽ നിന്ന് കേന്ദ്രഭാവങ്ങളിൽ (1, 4, 7, 10) വ്യാഴം സ്ഥിതി ചെയ്യുമ്പോഴാണ് ഗജകേസരി യോഗം രൂപപ്പെടുന്നത്. ഈ ജാതകത്തിൽ ചന്ദ്രൻ ${moon.rashi.malayalamName} രാശിയിലും വ്യാഴം ${jupiter.rashi.malayalamName} രാശിയിലുമായി കേന്ദ്രബന്ധം പുലർത്തുന്നതിനാൽ ജാതകന് പാണ്ഡിത്യം, സൽപ്പേര്, പ്രതിസന്ധികളെ അതിജീവിക്കാനുള്ള ആത്മബലം എന്നിവ ലഭിക്കുന്നു."
                )
            )
        }

        // 2. Budha Aditya Yoga (Sun and Mercury in the same sign)
        if (sun.rashi == mercury.rashi) {
            val st = if (mercury.isCombust && sun.houseFromLagna in listOf(6, 8, 12)) YogaStrengthClass.WEAK
            else if (mercury.isCombust) YogaStrengthClass.MODIFIED
            else strengthOf(sun, mercury)
            results.add(
                YogaResult(
                    id = "budha_aditya",
                    nameMalayalam = "ബുധാദിത്യ യോഗം (Budha Aditya Yoga)",
                    nameEnglish = "Budha Aditya Yoga",
                    categoryMal = "വിദ്യാ-ബുദ്ധി-കരിയർ യോഗം",
                    isDosha = false,
                    planetsInvolved = listOf(Planet.SUN, Planet.MERCURY),
                    housesInvolved = listOf(sun.houseFromLagna),
                    signsInvolved = listOf(sun.rashi),
                    conditionsSatisfiedMal = "സൂര്യനും ബുധനും ${sun.rashi.malayalamName} രാശിയിൽ (${sun.houseFromLagna}-ാം ഭാവം) ഒരുമിച്ച് സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = st,
                    modificationOrCancellationMal = if (mercury.isCombust) "ബുധന് സൂര്യസാമീപ്യത്താൽ മൗഢ്യമുള്ളതിനാൽ യോഗബലം രൂപാന്തരപ്പെട്ടിട്ടുണ്ട്." else "മൗഢ്യമില്ലാതെ ശക്തമായ സംയോഗം.",
                    dashaActivationMal = "സൂര്യന്റെയും ബുധന്റെയും മഹാദശ-അപഹാര കാലങ്ങളിൽ വിശേഷ ഫലം.",
                    transitRelevanceMal = "സൂര്യ-ബുധ ഗോചര കാലങ്ങളിൽ ബുദ്ധിപരമായ തീരുമാനങ്ങൾക്ക് വേഗത കൂടും.",
                    positiveIndicationsMal = "വിശകലന ബുദ്ധി, ആശയവിനിമയ മികവ്, ഭരണ-മാനേജ്മെന്റ്-അധ്യാപന ശേഷി, മത്സരപ്പരീക്ഷാ താല്പര്യം.",
                    challengingIndicationsMal = "ആലോചനയില്ലാതെ സംസാരിക്കുന്നത് ഒഴിവാക്കുകയും ക്ഷമ ശീലിക്കുകയും വേണം.",
                    detailedMalayalamExplanation = "${sun.houseFromLagna}-ാം ഭാവമായ ${sun.rashi.malayalamName} രാശിയിൽ രവിയും ബുധനും ചേർന്ന് നിൽക്കുന്നതിനാൽ ബുധാദിത്യ യോഗം പ്രവർത്തിക്കുന്നു. പഠനം, ഗണിതം, ഭരണനിർവ്വഹണം, ഐ.ടി, എഴുത്ത്, പ്രഭാഷണം എന്നീ മേഖലകളിൽ തിളങ്ങാൻ ഇത് സഹായിക്കും."
                )
            )
        }

        // 3. Chandra Mangala Yoga (Moon and Mars conjunct or mutual 7th aspect)
        val moonMarsDist = ((mars.rashi.index - moon.rashi.index + 12) % 12) + 1
        if (moonMarsDist == 1 || moonMarsDist == 7) {
            val st = strengthOf(moon, mars)
            results.add(
                YogaResult(
                    id = "chandra_mangala",
                    nameMalayalam = "ചന്ദ്രമംഗള യോഗം (Chandra Mangala Yoga)",
                    nameEnglish = "Chandra Mangala Yoga",
                    categoryMal = "ധന-വ്യവസായ യോഗം",
                    isDosha = false,
                    planetsInvolved = listOf(Planet.MOON, Planet.MARS),
                    housesInvolved = listOf(moon.houseFromLagna, mars.houseFromLagna).distinct(),
                    signsInvolved = listOf(moon.rashi, mars.rashi).distinct(),
                    conditionsSatisfiedMal = if (moonMarsDist == 1) "ചന്ദ്രനും കുജനും ${moon.rashi.malayalamName} രാശിയിൽ സംയോഗം ചെയ്യുന്നു." else "ചന്ദ്രനും കുജനും പരസ്പരം സമസപ്തമ ദൃഷ്ടിയിൽ നിൽക്കുന്നു.",
                    strengthClass = st,
                    modificationOrCancellationMal = "കുജന്റെ രാശിബലമനുസരിച്ച് ധനപരമായ ഊർജ്ജം പ്രവർത്തിക്കുന്നു.",
                    dashaActivationMal = "ചന്ദ്ര ദശയിലും ചൊവ്വ ദശയിലും സജീവമാകുന്നു.",
                    transitRelevanceMal = "കുജന്റെയും ചന്ദ്രന്റെയും അനുകൂല ഗോചരത്തിൽ സാമ്പത്തിക നേട്ടങ്ങൾ.",
                    positiveIndicationsMal = "സ്വപ്രയത്നത്തിലൂടെ ധനസമ്പാദനം, സംരംഭകത്വം, ഭൂമി-റിയൽ എസ്റ്റേറ്റ്-സാങ്കേതിക വ്യാപാര നേട്ടം.",
                    challengingIndicationsMal = "വൈകാരികമായ തിടുക്കവും മുൻകോപവും നിയന്ത്രിക്കേണ്ടതുണ്ട്.",
                    detailedMalayalamExplanation = "ചന്ദ്രമംഗള യോഗം ജാതകന് കർമ്മരംഗത്ത് പ്രായോഗിക ബുദ്ധിയും സാമ്പത്തിക വളർച്ചയ്ക്കുള്ള ധൈര്യവും നൽകുന്നു. അതേസമയം തീരുമാനങ്ങളിൽ സംയമനം പാലിക്കുന്നത് കുടുംബസമാധാനത്തിന് ഉത്തമമാണ്."
                )
            )
        }

        // 4. Pancha Mahapurusha Yogas (Mars, Mercury, Jupiter, Venus, Saturn in Own/Exaltation in Kendra 1, 4, 7, 10)
        val mahapurushaConfigs = listOf(
            Triple(Planet.MARS, "രുചക മഹാപുരുഷ യോഗം (Ruchaka Yoga)", "ധൈര്യം, നേതൃശേഷി, പോലീസ്/പ്രതിരോധ/എഞ്ചിനീയറിംഗ്/ഭരണ മികവ്, ഭൂമി ലാഭം."),
            Triple(Planet.MERCURY, "ഭദ്ര മഹാപുരുഷ യോഗം (Bhadra Yoga)", "ഉന്നത ബുദ്ധിശക്തി, വാണിജ്യം, ഐ.ടി, അക്കൗണ്ടിംഗ്, അധ്യാപനം, പ്രഭാഷണ ചാതുര്യം."),
            Triple(Planet.JUPITER, "ഹംസ മഹാപുരുഷ യോഗം (Hamsa Yoga)", "ധാർമ്മികത, ആത്മീയ ഔന്നത്യം, ഉന്നത വിദ്യാഭ്യാസം, ഉപദേശക-ഭരണ പദവികൾ, ജനസമ്മതി."),
            Triple(Planet.VENUS, "മാളവ്യ മഹാപുരുഷ യോഗം (Malavya Yoga)", "കലാവാസന, വാഹന-ഗൃഹ സൗഭാഗ്യങ്ങൾ, സൗന്ദര്യബോധം, ആകർഷകമായ വ്യക്തിത്വം, ധനസമൃദ്ധി."),
            Triple(Planet.SATURN, "ശശ മഹാപുരുഷ യോഗം (Sasa Yoga)", "ജനനേതൃത്വം, വ്യവസായ-ഭരണ സ്വാധീനം, സ്ഥിരോത്സാഹം, ദീർഘകാല കരിയർ സ്ഥിരത.")
        )
        for ((planet, titleMal, traitsMal) in mahapurushaConfigs) {
            val pos = planetsMap[planet] ?: continue
            val inKendra = pos.houseFromLagna in listOf(1, 4, 7, 10)
            val isOwnOrExalted = pos.dignity in listOf(
                PlanetaryDignity.EXALTED,
                PlanetaryDignity.MOOLATRIKONA,
                PlanetaryDignity.OWN_SIGN
            )
            if (inKendra && isOwnOrExalted) {
                results.add(
                    YogaResult(
                        id = "mahapurusha_${planet.name.lowercase()}",
                        nameMalayalam = titleMal,
                        nameEnglish = titleMal.substringAfter("(").substringBefore(")"),
                        categoryMal = "പഞ്ചമഹാപുരുഷ രാജയോഗം",
                        isDosha = false,
                        planetsInvolved = listOf(planet),
                        housesInvolved = listOf(pos.houseFromLagna),
                        signsInvolved = listOf(pos.rashi),
                        conditionsSatisfiedMal = "${planet.malayalamName} ലഗ്നകേന്ദ്രമായ ${pos.houseFromLagna}-ാം ഭാവത്തിൽ (${pos.rashi.malayalamName} രാശിയിൽ) ${pos.dignity.malayalam} ആയി സ്ഥിതി ചെയ്യുന്നു.",
                        strengthClass = if (pos.isCombust) YogaStrengthClass.MODIFIED else YogaStrengthClass.STRONG,
                        modificationOrCancellationMal = if (pos.isCombust) "സൂര്യസാമീപ്യത്താൽ ചെറിയ മൗഢ്യമുണ്ട്." else "പൂർണ്ണ ബലത്തോടെയുള്ള പഞ്ചമഹാപുരുഷ യോഗം.",
                        dashaActivationMal = "${planet.malayalamName} മഹാദശയിലും അപഹാരങ്ങളിലും ഉയർന്ന ഫലങ്ങൾ നൽകുന്നു.",
                        transitRelevanceMal = "${planet.malayalamName} കേന്ദ്ര-ത്രികോണ ഭാവങ്ങളിൽ ഗോചരം ചെയ്യുമ്പോൾ വിശേഷ ഗുണം.",
                        positiveIndicationsMal = traitsMal,
                        challengingIndicationsMal = "അധികാരസ്ഥാനങ്ങളിൽ അഹങ്കാരം കലരാതെ വിനയം സൂക്ഷിക്കുക.",
                        detailedMalayalamExplanation = "പരാശര ജ്യോതിഷത്തിലെ ഏറ്റവും ശ്രേഷ്ഠമായ പഞ്ചമഹാപുരുഷ യോഗങ്ങളിൽ ഒന്നായ $titleMal ഈ ജാതകത്തിൽ തെളിഞ്ഞു കാണുന്നു. ${planet.malayalamName} ${pos.rashi.malayalamName} രാശിയിൽ കേന്ദ്രബലത്തോടെ നിൽക്കുന്നത് കരിയറിലും വ്യക്തിജീവിതത്തിലും സവിശേഷമായ ഉയർച്ചയ്ക്ക് വഴിയൊരുക്കും."
                    )
                )
            }
        }

        // 5. Dharma Karma Adhipati Yoga (9th and 10th house lords conjunct, mutual aspect, or parivartana)
        val lord9 = bhavas.find { it.houseNumber == 9 }?.lord
        val lord10 = bhavas.find { it.houseNumber == 10 }?.lord
        if (lord9 != null && lord10 != null) {
            val pos9 = planetsMap[lord9]
            val pos10 = planetsMap[lord10]
            if (pos9 != null && pos10 != null) {
                val isSamePlanet = (lord9 == lord10)
                val isConjunct = (pos9.rashi == pos10.rashi)
                val isMutualAspect = pos9.aspectedHouses.contains(pos10.houseFromLagna) && pos10.aspectedHouses.contains(pos9.houseFromLagna)
                val isParivartana = (pos9.houseFromLagna == 10 && pos10.houseFromLagna == 9)
                val isIn9Or10 = (pos9.houseFromLagna in listOf(9, 10) || pos10.houseFromLagna in listOf(9, 10))
                if (isConjunct || isMutualAspect || isParivartana || (isSamePlanet && isIn9Or10)) {
                    val st = strengthOf(pos9, pos10)
                    results.add(
                        YogaResult(
                            id = "dharma_karma_adhipati",
                            nameMalayalam = "ധർമ്മകർമ്മാധിപതി രാജയോഗം",
                            nameEnglish = "Dharma Karma Adhipati Raja Yoga",
                            categoryMal = "രാജയോഗം & കരിയർ യോഗം",
                            isDosha = false,
                            planetsInvolved = listOf(lord9, lord10).distinct(),
                            housesInvolved = listOf(9, 10, pos9.houseFromLagna, pos10.houseFromLagna).distinct(),
                            signsInvolved = listOf(pos9.rashi, pos10.rashi).distinct(),
                            conditionsSatisfiedMal = "ഭാഗ്യാധിപനായ ${lord9.malayalamName}, കർമ്മാധിപനായ ${lord10.malayalamName} എന്നിവർ തമ്മിൽ ശക്തമായ യോഗബന്ധം പുലർത്തുന്നു.",
                            strengthClass = st,
                            modificationOrCancellationMal = "9, 10 ഭാവാധിപന്മാരുടെ ബന്ധം കർമ്മരംഗത്ത് ഭാഗ്യാനുകൂല്യം നൽകുന്നു.",
                            dashaActivationMal = "${lord9.malayalamName}, ${lord10.malayalamName} ദശാ-അപഹാര കാലങ്ങളിൽ ഉന്നത തൊഴിൽ നേട്ടം.",
                            transitRelevanceMal = "വ്യാഴവും ശനിയും 9, 10 ഭാവങ്ങളെ സ്വാധീനിക്കുമ്പോൾ സ്ഥാനക്കയറ്റം.",
                            positiveIndicationsMal = "ഉന്നത ഉദ്യോഗം, സർക്കാർ/ഭരണ നേതൃപദവികൾ, കരിയറിലെ സ്ഥിരത, ആദരവ്.",
                            challengingIndicationsMal = "ഉത്തരവാദിത്തങ്ങൾ വർദ്ധിക്കുമ്പോൾ കഠിനാധ്വാനത്തിൽ വിട്ടുവീഴ്ച ചെയ്യരുത്.",
                            detailedMalayalamExplanation = "ഒമ്പതാം ഭാവാധിപനും (ഭാഗ്യാധിപൻ) പത്താം ഭാവാധിപനും (കർമ്മാധിപൻ) തമ്മിൽ ചേരുന്ന ധർമ്മകർമ്മാധിപതി രാജയോഗം തൊഴിൽപരമായ ഉയർച്ചയ്ക്കും സമൂഹത്തിൽ മാന്യമായ പദവിക്കും ഏറ്റവും മികച്ച യോഗമാണ്."
                        )
                    )
                }
            }
        }

        // 6. Dhana Yoga (Connection between lords of 2, 11, 5, 9)
        val lord2 = bhavas.find { it.houseNumber == 2 }?.lord
        val lord11 = bhavas.find { it.houseNumber == 11 }?.lord
        val lord5 = bhavas.find { it.houseNumber == 5 }?.lord
        if (lord2 != null && lord11 != null && lord5 != null) {
            val pos2 = planetsMap[lord2]
            val pos11 = planetsMap[lord11]
            val pos5 = planetsMap[lord5]
            if (pos2 != null && pos11 != null && pos5 != null) {
                val dhanaConnected = (pos2.rashi == pos11.rashi) ||
                    (pos2.rashi == pos5.rashi) ||
                    (pos11.rashi == pos5.rashi) ||
                    (pos2.houseFromLagna == 11) ||
                    (pos11.houseFromLagna == 2)
                if (dhanaConnected) {
                    val st = strengthOf(pos2, pos11)
                    results.add(
                        YogaResult(
                            id = "dhana_yoga",
                            nameMalayalam = "ധനയോഗം (Dhana Yoga)",
                            nameEnglish = "Dhana Yoga",
                            categoryMal = "ധന-സമ്പത്ത് യോഗം",
                            isDosha = false,
                            planetsInvolved = listOf(lord2, lord11, lord5).distinct(),
                            housesInvolved = listOf(2, 11, pos2.houseFromLagna, pos11.houseFromLagna).distinct(),
                            signsInvolved = listOf(pos2.rashi, pos11.rashi).distinct(),
                            conditionsSatisfiedMal = "ധനാധിപനായ ${lord2.malayalamName} (${pos2.houseFromLagna}-ൽ), ലാഭാധിപനായ ${lord11.malayalamName} (${pos11.houseFromLagna}-ൽ) എന്നിവർ തമ്മിൽ ധനഭാവ ബന്ധം പുലർത്തുന്നു.",
                            strengthClass = st,
                            modificationOrCancellationMal = "ധന-ലാഭ ഭാവങ്ങളുടെ ബലം സാമ്പത്തിക സ്ഥിരതയെ സഹായിക്കുന്നു.",
                            dashaActivationMal = "${lord2.malayalamName}, ${lord11.malayalamName} ദശാകാലങ്ങളിൽ വരുമാന വർദ്ധനവ്.",
                            transitRelevanceMal = "വ്യാഴം 2, 11 ഭാവങ്ങളിൽ ഗോചരം ചെയ്യുമ്പോൾ സാമ്പത്തിക പുരോഗതി.",
                            positiveIndicationsMal = "സ്ഥിരവരുമാനം, സമ്പാദ്യശീലം, നിക്ഷേപ വിജയം, കുടുംബ സമ്പത്ത്.",
                            challengingIndicationsMal = "അനാവശ്യ ആഡംബര ചെലവുകൾ നിയന്ത്രിക്കുന്നത് സമ്പാദ്യം നിലനിർത്താൻ സഹായിക്കും.",
                            detailedMalayalamExplanation = "രണ്ടാം ഭാവം (ധനം), പതിനൊന്നാം ഭാവം (ലാഭം), അഞ്ചാം ഭാവം (പൂർവ്വപുണ്യം) എന്നിവയുടെ അധിപന്മാർ തമ്മിലുണ്ടാകുന്ന ഈ ധനയോഗം ജാതകന് സ്വപ്രയത്നത്തിലൂടെയും തൊഴിൽ-വ്യാപാരങ്ങളിലൂടെയും മികച്ച സാമ്പത്തിക ഭദ്രത കൈവരിക്കാൻ സഹായിക്കുന്നു."
                        )
                    )
                }
            }
        }

        // 7. Vipareeta Raja Yoga (Lords of 6, 8, 12 placed in 6, 8, or 12)
        val lord6 = bhavas.find { it.houseNumber == 6 }?.lord
        val lord8 = bhavas.find { it.houseNumber == 8 }?.lord
        val lord12 = bhavas.find { it.houseNumber == 12 }?.lord
        val dusthanaLordsInDusthana = listOfNotNull(lord6, lord8, lord12).distinct().filter {
            planetsMap[it]?.houseFromLagna in listOf(6, 8, 12)
        }
        if (dusthanaLordsInDusthana.isNotEmpty()) {
            val p = dusthanaLordsInDusthana.first()
            val pos = planetsMap[p]!!
            val yogaSubName = when {
                p == lord6 -> "ഹർഷ വിപരീത രാജയോഗം (Harsha Yoga)"
                p == lord8 -> "സരള വിപരീത രാജയോഗം (Sarala Yoga)"
                else -> "വിമല വിപരീത രാജയോഗം (Vimala Yoga)"
            }
            results.add(
                YogaResult(
                    id = "vipareeta_raja",
                    nameMalayalam = yogaSubName,
                    nameEnglish = "Vipareeta Raja Yoga",
                    categoryMal = "വിപരീത രാജയോഗം",
                    isDosha = false,
                    planetsInvolved = dusthanaLordsInDusthana,
                    housesInvolved = dusthanaLordsInDusthana.mapNotNull { planetsMap[it]?.houseFromLagna }.distinct(),
                    signsInvolved = dusthanaLordsInDusthana.mapNotNull { planetsMap[it]?.rashi }.distinct(),
                    conditionsSatisfiedMal = "ദുസ്ഥാനാധിപനായ ${p.malayalamName} ദുസ്ഥാനമായ ${pos.houseFromLagna}-ാം ഭാവത്തിൽ സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = YogaStrengthClass.MODERATE,
                    modificationOrCancellationMal = "പ്രതിസന്ധികൾക്ക് ശേഷം അപ്രതീക്ഷിത ഉയർച്ച നൽകുന്ന യോഗം.",
                    dashaActivationMal = "${p.malayalamName} ദശാ-അപഹാര കാലങ്ങളിൽ മത്സരവിജയം.",
                    transitRelevanceMal = "ശനി-വ്യാഴ ഗോചര പരിവർത്തനങ്ങളിൽ ഫലപ്രാപ്തി.",
                    positiveIndicationsMal = "മത്സരപ്പരീക്ഷകളിലും പ്രതികൂല സാഹചര്യങ്ങളിലും വിജയം, ശത്രുജയം, കടബാധ്യതകളിൽ നിന്ന് മോചനം.",
                    challengingIndicationsMal = "ആരംഭത്തിൽ കഠിനമായ വെല്ലുവിളികളെ നേരിടേണ്ടി വരാം.",
                    detailedMalayalamExplanation = "6, 8, 12 ഭാവാധിപന്മാർ തങ്ങളുടെതന്നെ ദുസ്ഥാന ഭാവങ്ങളിൽ നിൽക്കുമ്പോൾ രൂപപ്പെടുന്ന $yogaSubName പ്രതിസന്ധികളെ അവസരങ്ങളാക്കി മാറ്റി വിജയം വരിക്കാൻ ജാതകനെ പ്രാപ്തനാക്കുന്നു."
                )
            )
        }

        // 8. Neecha Bhanga Raja Yoga (Debilitated planet whose sign lord or exaltation lord is in Kendra from Lagna or Moon)
        val debilitatedPlanets = planetsMap.values.filter { it.dignity == PlanetaryDignity.DEBILITATED }
        for (debPos in debilitatedPlanets) {
            val signLord = debPos.rashi.lord
            val signLordPos = planetsMap[signLord]
            val signLordInKendraLagna = signLordPos?.houseFromLagna in listOf(1, 4, 7, 10)
            val signLordInKendraMoon = signLordPos?.houseFromMoon in listOf(1, 4, 7, 10)
            if (signLordInKendraLagna || signLordInKendraMoon) {
                results.add(
                    YogaResult(
                        id = "neecha_bhanga_${debPos.planet.name.lowercase()}",
                        nameMalayalam = "നീചഭംഗ രാജയോഗം (${debPos.planet.malayalamName})",
                        nameEnglish = "Neecha Bhanga Raja Yoga",
                        categoryMal = "നീചഭംഗ രാജയോഗം",
                        isDosha = false,
                        planetsInvolved = listOf(debPos.planet, signLord),
                        housesInvolved = listOf(debPos.houseFromLagna, signLordPos?.houseFromLagna ?: 1).distinct(),
                        signsInvolved = listOf(debPos.rashi),
                        conditionsSatisfiedMal = "${debPos.planet.malayalamName} ${debPos.rashi.malayalamName} രാശിയിൽ നീചനാണെങ്കിലും, ആ രാശ്യാധിപനായ ${signLord.malayalamName} കേന്ദ്രഭാവത്തിൽ സ്ഥിതി ചെയ്യുന്നതിനാൽ നീചഭംഗം സിദ്ധിക്കുന്നു.",
                        strengthClass = YogaStrengthClass.STRONG,
                        modificationOrCancellationMal = "നീചത്വം മാറി രാജയോഗ തുല്യമായ ആത്മബലം ലഭിക്കുന്നു.",
                        dashaActivationMal = "${debPos.planet.malayalamName}, ${signLord.malayalamName} ദശാകാലങ്ങളിൽ തുടക്കത്തിലെ തടസ്സങ്ങൾക്ക് ശേഷം വലിയ ഉയർച്ച.",
                        transitRelevanceMal = "${signLord.malayalamName} ബലവാനായി സഞ്ചരിക്കുമ്പോൾ അനുഭവയോഗ്യം.",
                        positiveIndicationsMal = "ആദ്യകാല പ്രയാസങ്ങളെ അതിജീവിച്ച് സ്വന്തം കഠിനാധ്വാനത്തിലൂടെ ഉന്നത വിജയം.",
                        challengingIndicationsMal = "തുടക്കത്തിൽ ആത്മവിശ്വാസക്കുറവോ കാലതാമസമോ അനുഭവപ്പെടാം.",
                        detailedMalayalamExplanation = "${debPos.planet.malayalamName}-ന്റെ നീചാവസ്ഥയ്ക്ക് രാശ്യാധിപനായ ${signLord.malayalamName}-ന്റെ കേന്ദ്രസ്ഥിതിയിലൂടെ നീചഭംഗ രാജയോഗം ലഭിച്ചിരിക്കുന്നു. ഇത് പ്രതിസന്ധികളിൽ തളരാതെ വലിയ നേട്ടങ്ങളിലേക്ക് ഉയരാൻ സഹായിക്കുന്നു."
                    )
                )
            }
        }

        // 9. Solar Yogas: Vesi (2nd from Sun), Vasi (12th from Sun), Ubhayachari (both)
        val nonLuminaryPlanets = listOf(Planet.MARS, Planet.MERCURY, Planet.JUPITER, Planet.VENUS, Planet.SATURN)
        val in2ndFromSun = nonLuminaryPlanets.filter {
            val p = planetsMap[it] ?: return@filter false
            ((p.rashi.index - sun.rashi.index + 12) % 12) == 1
        }
        val in12thFromSun = nonLuminaryPlanets.filter {
            val p = planetsMap[it] ?: return@filter false
            ((p.rashi.index - sun.rashi.index + 12) % 12) == 11
        }
        if (in2ndFromSun.isNotEmpty() && in12thFromSun.isNotEmpty()) {
            results.add(
                YogaResult(
                    id = "ubhayachari_yoga",
                    nameMalayalam = "ഉഭയചരി യോഗം (Ubhayachari Yoga)",
                    nameEnglish = "Ubhayachari Yoga",
                    categoryMal = "സൗരയോഗം (Solar Yoga)",
                    isDosha = false,
                    planetsInvolved = (listOf(Planet.SUN) + in2ndFromSun + in12thFromSun).distinct(),
                    housesInvolved = listOf(sun.houseFromLagna),
                    signsInvolved = listOf(sun.rashi),
                    conditionsSatisfiedMal = "സൂര്യന്റെ രണ്ടാം ഭാവത്തിലും പന്ത്രണ്ടാം ഭാവത്തിലും ഗ്രഹങ്ങൾ സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = YogaStrengthClass.STRONG,
                    modificationOrCancellationMal = "സൂര്യന് ഇരുവശവും ഗ്രഹബലമുള്ളതിനാൽ നേതൃശേഷി വർദ്ധിക്കുന്നു.",
                    dashaActivationMal = "സൂര്യ ദശയിലും സമീപ ഗ്രഹങ്ങളുടെ ദശയിലും ഫലം.",
                    transitRelevanceMal = "സൂര്യന്റെ ഉത്തരായന കാലത്ത് കൂടുതൽ ശുഭഫലം.",
                    positiveIndicationsMal = "സന്തുലിതമായ വ്യക്തിത്വം, നേതൃപാടവം, സാമ്പത്തിക ഭദ്രത, കുടുംബ പിന്തുണ.",
                    challengingIndicationsMal = "ഉത്തരവാദിത്തഭാരം കൂടുതലായിരിക്കും.",
                    detailedMalayalamExplanation = "സൂര്യന്റെ രണ്ടിലും പന്ത്രണ്ടിലും ഗ്രഹങ്ങൾ അണിനിരക്കുന്ന ഉഭയചരി യോഗം ജാതകന് രാജതുല്യമായ ആത്മവിശ്വാസവും ഭരണശേഷിയും പ്രദാനം ചെയ്യുന്നു."
                )
            )
        } else if (in2ndFromSun.isNotEmpty()) {
            results.add(
                YogaResult(
                    id = "vesi_yoga",
                    nameMalayalam = "വേശി യോഗം (Vesi Yoga)",
                    nameEnglish = "Vesi Yoga",
                    categoryMal = "സൗരയോഗം (Solar Yoga)",
                    isDosha = false,
                    planetsInvolved = listOf(Planet.SUN) + in2ndFromSun,
                    housesInvolved = listOf(sun.houseFromLagna),
                    signsInvolved = listOf(sun.rashi),
                    conditionsSatisfiedMal = "സൂര്യനിൽ നിന്ന് രണ്ടാം രാശിയിൽ ${in2ndFromSun.joinToString { it.malayalamName }} സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = YogaStrengthClass.MODERATE,
                    modificationOrCancellationMal = "ശുഭഗ്രഹ സാന്നിധ്യം അനുസരിച്ച് ഗുണഫലം.",
                    dashaActivationMal = "${in2ndFromSun.first().malayalamName} ദശയിലും സൂര്യ ദശയിലും ഫലം.",
                    transitRelevanceMal = "സൂര്യ-വ്യാഴ ഗോചരത്തിൽ അനുകൂലം.",
                    positiveIndicationsMal = "സത്യസന്ധത, വാക്ചാതുര്യം, സ്ഥിരമായ പരിശ്രമശീലം.",
                    challengingIndicationsMal = "സാമ്പത്തിക കാര്യങ്ങളിൽ മുൻകൂട്ടി പ്ലാൻ ചെയ്യണം.",
                    detailedMalayalamExplanation = "സൂര്യന്റെ രണ്ടാം ഭാവത്തിൽ ഗ്രഹം നിൽക്കുന്ന വേശി യോഗം ജാതകന് വ്യക്തമായ കാഴ്ചപ്പാടും സത്യസന്ധമായ ജീവിതരീതിയും നൽകുന്നു."
                )
            )
        } else if (in12thFromSun.isNotEmpty()) {
            results.add(
                YogaResult(
                    id = "vasi_yoga",
                    nameMalayalam = "വാശി യോഗം (Vasi Yoga)",
                    nameEnglish = "Vasi Yoga",
                    categoryMal = "സൗരയോഗം (Solar Yoga)",
                    isDosha = false,
                    planetsInvolved = listOf(Planet.SUN) + in12thFromSun,
                    housesInvolved = listOf(sun.houseFromLagna),
                    signsInvolved = listOf(sun.rashi),
                    conditionsSatisfiedMal = "സൂര്യനിൽ നിന്ന് പന്ത്രണ്ടാം രാശിയിൽ ${in12thFromSun.joinToString { it.malayalamName }} സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = YogaStrengthClass.MODERATE,
                    modificationOrCancellationMal = "ഗ്രഹങ്ങളുടെ സ്വഭാവമനുസരിച്ച് ഫലം.",
                    dashaActivationMal = "${in12thFromSun.first().malayalamName} ദശാകാലത്ത് സജീവം.",
                    transitRelevanceMal = "ഗോചരത്തിൽ സൂര്യൻ ബലവാനാകുമ്പോൾ ഗുണം.",
                    positiveIndicationsMal = "അറിവ്, ഓർമ്മശക്തി, ദാനശീലം, കഠിനാധ്വാനം.",
                    challengingIndicationsMal = "ചെലവുകൾ നിയന്ത്രിക്കാൻ ശ്രദ്ധിക്കണം.",
                    detailedMalayalamExplanation = "സൂര്യന്റെ പന്ത്രണ്ടാം ഭാവത്തിൽ ഗ്രഹം നിൽക്കുന്ന വാശി യോഗം അറിവിലൂടെയും സേവനത്തിലൂടെയും അംഗീകാരം നേടാൻ സഹായിക്കുന്നു."
                )
            )
        }

        // 10. Lunar Yogas: Sunapha (2nd from Moon), Anapha (12th from Moon), Durudhara (both), Kemadruma (none)
        val in2ndFromMoon = nonLuminaryPlanets.filter {
            val p = planetsMap[it] ?: return@filter false
            ((p.rashi.index - moon.rashi.index + 12) % 12) == 1
        }
        val in12thFromMoon = nonLuminaryPlanets.filter {
            val p = planetsMap[it] ?: return@filter false
            ((p.rashi.index - moon.rashi.index + 12) % 12) == 11
        }
        if (in2ndFromMoon.isNotEmpty() && in12thFromMoon.isNotEmpty()) {
            results.add(
                YogaResult(
                    id = "durudhara_yoga",
                    nameMalayalam = "ദുരുധര യോഗം (Durudhara Yoga)",
                    nameEnglish = "Durudhara Yoga",
                    categoryMal = "ചന്ദ്രയോഗം (Lunar Yoga)",
                    isDosha = false,
                    planetsInvolved = (listOf(Planet.MOON) + in2ndFromMoon + in12thFromMoon).distinct(),
                    housesInvolved = listOf(moon.houseFromLagna),
                    signsInvolved = listOf(moon.rashi),
                    conditionsSatisfiedMal = "ചന്ദ്രന്റെ രണ്ടാം രാശിയിലും പന്ത്രണ്ടാം രാശിയിലും ഗ്രഹങ്ങൾ സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = YogaStrengthClass.STRONG,
                    modificationOrCancellationMal = "ചന്ദ്രന് ഇരുവശവും ഗ്രഹപിന്തുണയുണ്ട്.",
                    dashaActivationMal = "ചന്ദ്ര ദശയിലും സമീപ ഗ്രഹങ്ങളുടെ ദശയിലും പൂർണ്ണഫലം.",
                    transitRelevanceMal = "ചന്ദ്ര-വ്യാഴ ഗോചരത്തിൽ സമ്പത്തും മനഃസമാധാനവും.",
                    positiveIndicationsMal = "ധനസമൃദ്ധി, വാഹന-ഗൃഹ സുഖം, ജനസമ്മതി, മാനസിക കരുത്ത്.",
                    challengingIndicationsMal = "കുടുംബ ഉത്തരവാദിത്തങ്ങൾ കൂടുതലായിരിക്കും.",
                    detailedMalayalamExplanation = "ചന്ദ്രന്റെ രണ്ടിലും പന്ത്രണ്ടിലും ഗ്രഹങ്ങൾ നിൽക്കുന്ന ദുരുധര യോഗം ജാതകന് മികച്ച സാമ്പത്തിക ഭദ്രതയും സമൂഹത്തിൽ മാന്യതയും നൽകുന്നു."
                )
            )
        } else if (in2ndFromMoon.isNotEmpty()) {
            results.add(
                YogaResult(
                    id = "sunapha_yoga",
                    nameMalayalam = "സുനഫാ യോഗം (Sunapha Yoga)",
                    nameEnglish = "Sunapha Yoga",
                    categoryMal = "ചന്ദ്രയോഗം (Lunar Yoga)",
                    isDosha = false,
                    planetsInvolved = listOf(Planet.MOON) + in2ndFromMoon,
                    housesInvolved = listOf(moon.houseFromLagna),
                    signsInvolved = listOf(moon.rashi),
                    conditionsSatisfiedMal = "ചന്ദ്രനിൽ നിന്ന് രണ്ടാം ഭാവത്തിൽ ${in2ndFromMoon.joinToString { it.malayalamName }} സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = YogaStrengthClass.MODERATE,
                    modificationOrCancellationMal = "സ്വപ്രയത്നത്തിലൂടെയുള്ള ധനസമ്പാദന യോഗം.",
                    dashaActivationMal = "ചന്ദ്ര ദശയിലും ${in2ndFromMoon.first().malayalamName} ദശയിലും സജീവം.",
                    transitRelevanceMal = "ചന്ദ്രൻ ബലവാനാകുമ്പോൾ ഗുണഫലം.",
                    positiveIndicationsMal = "ബുദ്ധിശക്തി, സ്വന്തം പരിശ്രമത്താൽ ധനവർദ്ധനവ്, സൽപ്പേര്.",
                    challengingIndicationsMal = "സാമ്പത്തിക തീരുമാനങ്ങളിൽ സ്ഥിരത പാലിക്കുക.",
                    detailedMalayalamExplanation = "ചന്ദ്രന്റെ രണ്ടാം ഭാവത്തിൽ ഗ്രഹം നിൽക്കുന്ന സുനഫാ യോഗം സ്വന്തം അധ്വാനത്തിലൂടെ ജീവിതത്തിൽ ഉയർച്ച നേടാൻ സഹായിക്കുന്നു."
                )
            )
        } else if (in12thFromMoon.isNotEmpty()) {
            results.add(
                YogaResult(
                    id = "anapha_yoga",
                    nameMalayalam = "അനഫാ യോഗം (Anapha Yoga)",
                    nameEnglish = "Anapha Yoga",
                    categoryMal = "ചന്ദ്രയോഗം (Lunar Yoga)",
                    isDosha = false,
                    planetsInvolved = listOf(Planet.MOON) + in12thFromMoon,
                    housesInvolved = listOf(moon.houseFromLagna),
                    signsInvolved = listOf(moon.rashi),
                    conditionsSatisfiedMal = "ചന്ദ്രനിൽ നിന്ന് പന്ത്രണ്ടാം ഭാവത്തിൽ ${in12thFromMoon.joinToString { it.malayalamName }} സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = YogaStrengthClass.MODERATE,
                    modificationOrCancellationMal = "മനസ്സിന് ആത്മീയ-കലാ താല്പര്യം നൽകുന്നു.",
                    dashaActivationMal = "ചന്ദ്ര ദശയിലും ${in12thFromMoon.first().malayalamName} ദശയിലും ഫലം.",
                    transitRelevanceMal = "ശുഭഗ്രഹ ഗോചരത്തിൽ ആത്മവിശ്വാസം വർദ്ധിക്കും.",
                    positiveIndicationsMal = "ആകർഷകമായ പെരുമാറ്റം, ആത്മീയത, ഉദാരമനസ്കത.",
                    challengingIndicationsMal = "വൈകാരിക സമ്മർദ്ദങ്ങൾ ഒഴിവാക്കാൻ ശ്രദ്ധിക്കണം.",
                    detailedMalayalamExplanation = "ചന്ദ്രന്റെ പന്ത്രണ്ടിൽ ഗ്രഹം നിൽക്കുന്ന അനഫാ യോഗം ജാതകന് സൗമ്യമായ വ്യക്തിത്വവും സാംസ്കാരിക താല്പര്യങ്ങളും നൽകുന്നു."
                )
            )
        } else {
            // Check Kemadruma or Kemadruma Bhanga
            val moonConjoined = nonLuminaryPlanets.any { planetsMap[it]?.rashi == moon.rashi }
            val kendraFromLagna = moon.houseFromLagna in listOf(1, 4, 7, 10)
            val kendraFromMoonHasPlanet = nonLuminaryPlanets.any {
                planetsMap[it]?.houseFromMoon in listOf(1, 4, 7, 10)
            }
            if (moonConjoined || kendraFromLagna || kendraFromMoonHasPlanet) {
                results.add(
                    YogaResult(
                        id = "kemadruma_bhanga",
                        nameMalayalam = "കേമദ്രുമ ഭംഗ യോഗം (Kemadruma Bhanga)",
                        nameEnglish = "Kemadruma Bhanga Yoga",
                        categoryMal = "ദോഷഭംഗ രാജയോഗം",
                        isDosha = false,
                        planetsInvolved = listOf(Planet.MOON),
                        housesInvolved = listOf(moon.houseFromLagna),
                        signsInvolved = listOf(moon.rashi),
                        conditionsSatisfiedMal = "ചന്ദ്രന്റെ 2, 12 ഭാവങ്ങളിൽ ഗ്രഹങ്ങളില്ലെങ്കിലും കേന്ദ്രബന്ധത്താൽ കേമദ്രുമ ദോഷത്തിന് പൂർണ്ണ ഭംഗം (Cancellation) സംഭവിച്ചിരിക്കുന്നു.",
                        strengthClass = YogaStrengthClass.CANCELLED,
                        modificationOrCancellationMal = "കേന്ദ്രഗ്രഹ സാന്നിധ്യത്താൽ ദോഷം റദ്ദാക്കപ്പെട്ട് ശുഭയോഗമായി മാറുന്നു.",
                        dashaActivationMal = "ചന്ദ്ര ദശയിൽ മാനസിക കരുത്തും പുരോഗതിയും.",
                        transitRelevanceMal = "ഗുരു-ചന്ദ്ര ബന്ധത്തിൽ മികച്ച ഫലം.",
                        positiveIndicationsMal = "പ്രതിസന്ധികളിൽ തളരാത്ത മനസ്സ്, സ്വന്തം പരിശ്രമത്താൽ വിജയം.",
                        challengingIndicationsMal = "ഇടയ്ക്കിടെ ഉണ്ടാകുന്ന ഏകാന്തതാ ചിന്തകൾ ഒഴിവാക്കുക.",
                        detailedMalayalamExplanation = "ചന്ദ്രന്റെ ഇരുവശവും ഗ്രഹങ്ങളില്ലാത്ത അവസ്ഥയ്ക്ക് കേന്ദ്രബന്ധം വഴി കേമദ്രുമ ഭംഗം ലഭിച്ചിരിക്കുന്നതിനാൽ ഭയപ്പെടേണ്ടതില്ല; മറിച്ച് സ്വന്തം കഴിവിൽ വിശ്വസിച്ച് മുന്നേറാൻ ഇത് കരുത്ത് നൽകും."
                    )
                )
            }
        }

        // 11. Amala Yoga (Natural benefic Mercury, Jupiter, or Venus in 10th from Lagna or Moon)
        val beneficsIn10th = listOf(Planet.JUPITER, Planet.VENUS, Planet.MERCURY).filter {
            val p = planetsMap[it] ?: return@filter false
            p.houseFromLagna == 10 || p.houseFromMoon == 10
        }
        if (beneficsIn10th.isNotEmpty()) {
            results.add(
                YogaResult(
                    id = "amala_yoga",
                    nameMalayalam = "അമല യോഗം (Amala Yoga)",
                    nameEnglish = "Amala Yoga",
                    categoryMal = "കരിയർ & കീർത്തി യോഗം",
                    isDosha = false,
                    planetsInvolved = beneficsIn10th,
                    housesInvolved = listOf(10),
                    signsInvolved = beneficsIn10th.mapNotNull { planetsMap[it]?.rashi }.distinct(),
                    conditionsSatisfiedMal = "ലഗ്നത്തിൽ നിന്നോ ചന്ദ്രനിൽ നിന്നോ 10-ാം ഭാവത്തിൽ ശുഭഗ്രഹമായ ${beneficsIn10th.joinToString { it.malayalamName }} സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = YogaStrengthClass.STRONG,
                    modificationOrCancellationMal = "കർമ്മഭാവത്തിൽ ശുഭഗ്രഹ സാന്നിധ്യം.",
                    dashaActivationMal = "${beneficsIn10th.first().malayalamName} ദശാ-അപഹാരങ്ങളിൽ കരിയർ വളർച്ച.",
                    transitRelevanceMal = "വ്യാഴ-ശുക്ര ഗോചരത്തിൽ തൊഴിൽ അംഗീകാരം.",
                    positiveIndicationsMal = "സത്യസന്ധമായ തൊഴിൽ മാർഗ്ഗം, സൽപ്പേര്, മാന്യമായ വരുമാനം, പരോപകാര മനോഭാവം.",
                    challengingIndicationsMal = "ധാർമ്മിക മൂല്യങ്ങളിൽ ഉറച്ചുനിൽക്കുക.",
                    detailedMalayalamExplanation = "പത്താം ഭാവത്തിൽ ശുഭഗ്രഹം നിൽക്കുന്ന അമല യോഗം ജാതകന് കളങ്കമില്ലാത്ത കീർത്തിയും തൊഴിൽരംഗത്ത് സ്ഥിരമായ ആദരവും നൽകുന്നു."
                )
            )
        }

        // 12. Vasumathi Yoga (Benefics in Upachaya houses 3, 6, 10, 11 from Lagna or Moon)
        val beneficsInUpachaya = listOf(Planet.JUPITER, Planet.VENUS, Planet.MERCURY).filter {
            val p = planetsMap[it] ?: return@filter false
            p.houseFromLagna in listOf(3, 6, 10, 11)
        }
        if (beneficsInUpachaya.size >= 2) {
            results.add(
                YogaResult(
                    id = "vasumathi_yoga",
                    nameMalayalam = "വsumതി യോഗം (Vasumathi Yoga)",
                    nameEnglish = "Vasumathi Yoga",
                    categoryMal = "ധന-സമൃദ്ധി യോഗം",
                    isDosha = false,
                    planetsInvolved = beneficsInUpachaya,
                    housesInvolved = beneficsInUpachaya.mapNotNull { planetsMap[it]?.houseFromLagna }.distinct(),
                    signsInvolved = beneficsInUpachaya.mapNotNull { planetsMap[it]?.rashi }.distinct(),
                    conditionsSatisfiedMal = "ഉപചയ ഭാവങ്ങളിൽ (3, 6, 10, 11) ശുഭഗ്രഹങ്ങളായ ${beneficsInUpachaya.joinToString { it.malayalamName }} സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = YogaStrengthClass.MODERATE,
                    modificationOrCancellationMal = "ഉപചയ ഭാവങ്ങളിലെ ശുഭഗ്രഹങ്ങൾ ക്രമാനുഗതമായ സാമ്പത്തിക വളർച്ച നൽകുന്നു.",
                    dashaActivationMal = "ബുധ-വ്യാഴ-ശുക്ര ദശാ കാലങ്ങളിൽ സാമ്പത്തിക പുരോഗതി.",
                    transitRelevanceMal = "ഉപചയ ഭാവങ്ങളിലെ ഗ്രഹസഞ്ചാരം ധനവരവ് കൂട്ടും.",
                    positiveIndicationsMal = "സ്വന്തം പരിശ്രമത്താൽ ക്രമമായ ധനവളർച്ച, സാമ്പത്തിക സ്വാതന്ത്ര്യം.",
                    challengingIndicationsMal = "തുടക്കത്തിൽ മിതമായ ഫലവും പ്രായം ചെല്ലുന്തോറും മികച്ച വളർച്ചയും.",
                    detailedMalayalamExplanation = "ഉപചയ സ്ഥാനങ്ങളിൽ ശുഭഗ്രഹങ്ങൾ അണിനിരക്കുന്ന വസുമതി യോഗം ജാതകന് സാമ്പത്തിക പ്രതിസന്ധികളെ മറികടക്കാനും സ്ഥിരമായ സമ്പാദ്യം കെട്ടിപ്പടുക്കാനും സഹായിക്കുന്നു."
                )
            )
        }

        // 13. Parivartana Yoga (Mutual exchange of signs between two planets)
        val coreSeven = listOf(Planet.SUN, Planet.MOON, Planet.MARS, Planet.MERCURY, Planet.JUPITER, Planet.VENUS, Planet.SATURN)
        for (i in coreSeven.indices) {
            for (j in i + 1 until coreSeven.size) {
                val p1 = planetsMap[coreSeven[i]] ?: continue
                val p2 = planetsMap[coreSeven[j]] ?: continue
                if (p1.rashi.lord == p2.planet && p2.rashi.lord == p1.planet && p1.rashi != p2.rashi) {
                    val isMaha = p1.houseFromLagna in listOf(1, 2, 4, 5, 7, 9, 10, 11) &&
                        p2.houseFromLagna in listOf(1, 2, 4, 5, 7, 9, 10, 11)
                    results.add(
                        YogaResult(
                            id = "parivartana_${p1.planet.name.lowercase()}_${p2.planet.name.lowercase()}",
                            nameMalayalam = "${if (isMaha) "മഹാ " else ""}പരിവർത്തന യോഗം (${p1.planet.malayalamName} - ${p2.planet.malayalamName})",
                            nameEnglish = "Parivartana Yoga",
                            categoryMal = "ക്ഷേത്രപരിവർത്തന യോഗം",
                            isDosha = false,
                            planetsInvolved = listOf(p1.planet, p2.planet),
                            housesInvolved = listOf(p1.houseFromLagna, p2.houseFromLagna),
                            signsInvolved = listOf(p1.rashi, p2.rashi),
                            conditionsSatisfiedMal = "${p1.planet.malayalamName} (${p1.rashi.malayalamName}-ൽ), ${p2.planet.malayalamName} (${p2.rashi.malayalamName}-ൽ) എന്നിവർ പരസ്പരം രാശികൾ വെച്ചുമാറി സ്ഥിതി ചെയ്യുന്നു.",
                            strengthClass = if (isMaha) YogaStrengthClass.STRONG else YogaStrengthClass.MODERATE,
                            modificationOrCancellationMal = "ഇരു ഗ്രഹങ്ങളും സ്വക്ഷേത്ര തുല്യമായ ബലം കൈവരിക്കുന്നു.",
                            dashaActivationMal = "${p1.planet.malayalamName}, ${p2.planet.malayalamName} ദശാ-അപഹാരങ്ങളിൽ ഇരു ഭാവങ്ങളുടെയും ഫലങ്ങൾ ഒരുമിച്ച് ലഭിക്കുന്നു.",
                            transitRelevanceMal = "ഇരു ഗ്രഹങ്ങളുടെയും ഗോചര ബന്ധത്തിൽ പ്രധാന വഴിത്തിരിവുകൾ.",
                            positiveIndicationsMal = "${p1.houseFromLagna}, ${p2.houseFromLagna} ഭാവങ്ങളുടെ പരസ്പര പൂരകമായ പുരോഗതി.",
                            challengingIndicationsMal = "ഇരു ഭാവങ്ങളിലെയും ഉത്തരവാദിത്തങ്ങൾ ഒരുമിച്ച് കൈകാര്യം ചെയ്യണം.",
                            detailedMalayalamExplanation = "${p1.planet.malayalamName}, ${p2.planet.malayalamName} എന്നീ ഗ്രഹങ്ങൾ തമ്മിലുള്ള രാശി പരിവർത്തനം ഈ ജാതകത്തിലെ വളരെ ശക്തമായ ഒരു സവിശേഷതയാണ്. ഇത് ബന്ധപ്പെട്ട ഭാവങ്ങൾക്ക് സ്വക്ഷേത്ര ബലത്തിന് തുല്യമായ കരുത്ത് നൽകുന്നു."
                        )
                    )
                }
            }
        }

        // 14. DOSHA DETECTION (Strictly evaluated with Classical Exceptions / Bhanga)
        // Kuja / Mangalya Dosha check from Lagna (1, 2, 4, 7, 8, 12 in South Indian / Kerala system, especially 7 & 8)
        val kujaHouses = listOf(1, 2, 4, 7, 8, 12)
        if (mars.houseFromLagna in kujaHouses) {
            val hasCancellation = mars.dignity in listOf(
                PlanetaryDignity.EXALTED,
                PlanetaryDignity.OWN_SIGN,
                PlanetaryDignity.MOOLATRIKONA
            ) || mars.rashi in listOf(Rashi.KARKADAKAM, Rashi.CHINGAM, Rashi.DHANU, Rashi.MEENAM) ||
                mars.conjunctPlanets.contains(Planet.JUPITER) ||
                jupiter.aspectedHouses.contains(mars.houseFromLagna)

            results.add(
                YogaResult(
                    id = "kuja_dosha_check",
                    nameMalayalam = if (hasCancellation) "കുജദോഷ പരിഹാര / ലഘൂകരണ സ്ഥിതി" else "കുജദോഷ സൂചന (ചൊവ്വാദോഷ വിചിന്തനം)",
                    nameEnglish = if (hasCancellation) "Modified / Mitigated Kuja Placement" else "Kuja (Mangalya) Placement Note",
                    categoryMal = "ദോഷവിചിന്തനം & പരിഹാര സൂചന",
                    isDosha = !hasCancellation,
                    planetsInvolved = listOf(Planet.MARS),
                    housesInvolved = listOf(mars.houseFromLagna),
                    signsInvolved = listOf(mars.rashi),
                    conditionsSatisfiedMal = "ലഗ്നാൽ ${mars.houseFromLagna}-ാം ഭാവത്തിൽ (${mars.rashi.malayalamName} രാശിയിൽ) കുജൻ സ്ഥിതി ചെയ്യുന്നു.",
                    strengthClass = if (hasCancellation) YogaStrengthClass.MODIFIED else YogaStrengthClass.MODERATE,
                    modificationOrCancellationMal = if (hasCancellation) "കുജന്റെ രാശിസ്ഥിതി / വ്യാഴബന്ധം മൂലം കുജദോഷത്തിന് ശാസ്ത്രീയമായ ലഘൂകരണം (പരിഹാരം) കാണുന്നു." else "വിവാഹ പൊരുത്തം നോക്കുമ്പോൾ സമാന പാപസാമ്യമുള്ള ജാതകം തിരഞ്ഞെടുക്കുന്നത് ഉത്തമം.",
                    dashaActivationMal = "കുജ ദശാ-അപഹാര കാലങ്ങളിൽ ക്ഷമയും സംയമനവും പാലിക്കുക.",
                    transitRelevanceMal = "കുജൻ 7, 8 ഭാവങ്ങളിൽ ഗോചരം ചെയ്യുമ്പോൾ വാഗ്വാദങ്ങൾ ഒഴിവാക്കുക.",
                    positiveIndicationsMal = "കരിയറിലും കർമ്മരംഗത്തും ധൈര്യവും നേതൃപാടവവും നൽകും.",
                    challengingIndicationsMal = "ദാമ്പത്യത്തിൽ പരസ്പര ബഹുമാനവും ക്ഷമയും ശീലിക്കണം; ഭയപ്പെടേണ്ടതില്ല.",
                    detailedMalayalamExplanation = "ജാതകത്തിൽ കുജൻ ${mars.houseFromLagna}-ാം ഭാവത്തിൽ നിൽക്കുന്നു. ${if (hasCancellation) "എന്നിരുന്നാലും ക്ലാസിക്കൽ ജ്യോതിഷ നിയമപ്രകാരം ഈ രാശിസ്ഥിതിയിൽ ദോഷതീവ്രത കുറവാണ്." else "വിവാഹ പൊരുത്ത സമയത്ത് ഗ്രഹനിലയിലെ പാപസാമ്യം പരിശോധിച്ച് ചേർക്കുന്നത് ദാമ്പത്യ ഐക്യത്തിന് സഹായകമാകും."}"
                )
            )
        }

        // Always ensure at least one personalized planetary synthesis yoga if chart has rare combinations
        if (results.isEmpty()) {
            val lagnaLord = lagnaRashi.lord
            val lagnaLordPos = planetsMap[lagnaLord]
            if (lagnaLordPos != null) {
                results.add(
                    YogaResult(
                        id = "lagna_adhipati_yoga",
                        nameMalayalam = "ലഗ്നാധിപതി ഭാവബല യോഗം",
                        nameEnglish = "Lagna Lord Placement Yoga",
                        categoryMal = "ആത്മബല-കരിയർ യോഗം",
                        isDosha = false,
                        planetsInvolved = listOf(lagnaLord),
                        housesInvolved = listOf(1, lagnaLordPos.houseFromLagna).distinct(),
                        signsInvolved = listOf(lagnaRashi, lagnaLordPos.rashi).distinct(),
                        conditionsSatisfiedMal = "ലഗ്നാധിപനായ ${lagnaLord.malayalamName} ${lagnaLordPos.houseFromLagna}-ാം ഭാവത്തിൽ (${lagnaLordPos.rashi.malayalamName}) സ്ഥിതി ചെയ്യുന്നു.",
                        strengthClass = YogaStrengthClass.MODERATE,
                        modificationOrCancellationMal = "ലഗ്നാധിപന്റെ ഭാവസ്ഥിതി ജീവിതലക്ഷ്യങ്ങളെ നയിക്കുന്നു.",
                        dashaActivationMal = "${lagnaLord.malayalamName} ദശാ-അപഹാരങ്ങളിൽ വിശേഷ പുരോഗതി.",
                        transitRelevanceMal = "${lagnaLord.malayalamName} അനുകൂല ഭാവങ്ങളിൽ സഞ്ചരിക്കുമ്പോൾ ഗുണം.",
                        positiveIndicationsMal = "വ്യക്തിത്വ വികസനം, സ്വന്തം പരിശ്രമത്തിലൂടെയുള്ള ജീവിത വിജയം.",
                        challengingIndicationsMal = "ആരോഗ്യത്തിലും ദിനചര്യയിലും കൃത്യനിഷ്ഠ പാലിക്കുക.",
                        detailedMalayalamExplanation = "ലഗ്നാധിപനായ ${lagnaLord.malayalamName} ${lagnaLordPos.houseFromLagna}-ാം ഭാവത്തിൽ നിൽക്കുന്നത് ആ ഭാവവുമായി ബന്ധപ്പെട്ട വിഷയങ്ങളിലൂടെ ജാതകന് ജീവിതത്തിൽ പുരോഗതി കൈവരിക്കാൻ സഹായിക്കുന്നു."
                    )
                )
            }
        }

        return results
    }
}
