package com.example.engine

import com.example.model.*

/**
 * Generates personalized, calculation-driven Kerala Malayalam interpretations for:
 * Career (including 27 career categories: Govt Job, Kerala PSC, KAS, UPSC, IT, Banking, etc.),
 * Education, Marriage, Partner Personality, Partner Appearance Tendencies, Finance, Business,
 * Foreign Travel, Property/Vehicle, Family, Children, Health/Lifestyle, Spirituality, and Life Timeline.
 */
object CareerAndLifeModulesEngine {

    fun generateCareerCategories(
        lagnaRashi: Rashi,
        planetsMap: Map<Planet, PlanetPosition>,
        bhavas: List<BhavaInfo>,
        yogas: List<YogaResult>,
        currentMaha: MahadashaPeriod,
        currentAntar: AntardashaPeriod
    ): List<CareerCategoryItem> {
        val sun = planetsMap[Planet.SUN]!!
        val moon = planetsMap[Planet.MOON]!!
        val mars = planetsMap[Planet.MARS]!!
        val mercury = planetsMap[Planet.MERCURY]!!
        val jupiter = planetsMap[Planet.JUPITER]!!
        val venus = planetsMap[Planet.VENUS]!!
        val saturn = planetsMap[Planet.SATURN]!!
        val rahu = planetsMap[Planet.RAHU]!!
        val ketu = planetsMap[Planet.KETU]!!

        val lord10 = bhavas.find { it.houseNumber == 10 }?.lord ?: Planet.SUN
        val lord6 = bhavas.find { it.houseNumber == 6 }?.lord ?: Planet.MARS
        val yogaNames = yogas.filter { !it.isDosha }.map { it.nameMalayalam }.ifEmpty { listOf("ലഗ്ന-കർമ്മ ഭാവ ബന്ധം") }

        data class CatSpec(
            val id: String,
            val mal: String,
            val eng: String,
            val primaryPlanets: List<Planet>,
            val primaryHouses: List<Int>,
            val focusNoteMal: String
        )

        val specs = listOf(
            CatSpec("govt_job", "സർക്കാർ ഉദ്യോഗം (Government Job)", "Government Job", listOf(Planet.SUN, Planet.SATURN, lord10, lord6), listOf(10, 6, 11, 1), "സൂര്യന്റെയും പത്താം ഭാവാധിപന്റെയും ബലം സർക്കാർ സേവന സാധ്യതകളെ സൂചിപ്പിക്കുന്നു."),
            CatSpec("kerala_psc", "കേരള പി.എസ്.സി (Kerala PSC)", "Kerala PSC", listOf(Planet.SUN, Planet.MERCURY, Planet.SATURN, lord6), listOf(6, 10, 11, 5), "ആറാം ഭാവവും (മത്സരവിജയം) പത്താം ഭാവവും ബുധ-ശനി-രവി ബന്ധവും പി.എസ്.സി പരീക്ഷാ പ്രവണതയെ കാണിക്കുന്നു."),
            CatSpec("kas", "കെ.എ.എസ് (KAS - Kerala Administrative Service)", "KAS", listOf(Planet.SUN, Planet.JUPITER, lord10), listOf(10, 9, 5, 1), "ഭരണനിർവ്വഹണ കാരകനായ രവിയും ധർമ്മ-വ്യാഴ ബലവും ഉയർന്ന സംസ്ഥാന ഭരണ സർവീസ് അഭിരുചി നൽകുന്നു."),
            CatSpec("upsc", "യു.പി.എസ്.സി / സിവിൽ സർവീസ് (UPSC)", "UPSC / Civil Services", listOf(Planet.SUN, Planet.JUPITER, Planet.MARS, lord10), listOf(10, 9, 1, 6), "ലഗ്ന-പത്താം ഭാവ ബലവും രവി-ഗുരു-കുജ സ്വാധീനവും ദേശീയ തലത്തിലുള്ള മത്സരപ്പരീക്ഷാ ശേഷി സൂചിപ്പിക്കുന്നു."),
            CatSpec("central_govt", "കേന്ദ്ര സർക്കാർ ജോലി (Central Government)", "Central Government", listOf(Planet.SUN, Planet.JUPITER, Planet.SATURN), listOf(10, 9, 3, 11), "രവി-ഗുരു ബന്ധവും ഒമ്പതാം ഭാവ സ്വാധീനവും കേന്ദ്ര സർക്കാർ സ്ഥാപനങ്ങളിലെ തൊഴിൽ സാധ്യത നൽകുന്നു."),
            CatSpec("state_govt", "സംസ്ഥാന സർക്കാർ ജോലി (State Government)", "State Government", listOf(Planet.SUN, Planet.MOON, Planet.SATURN, lord10), listOf(10, 4, 6, 11), "ചന്ദ്ര-രവി-ശനി ബന്ധവും നാലാം ഭാവവും സ്വന്തം സംസ്ഥാനത്തിനുള്ളിലെ സ്ഥിര ഉദ്യോഗ പ്രവണത കാണിക്കുന്നു."),
            CatSpec("admin_services", "ഭരണനിർവ്വഹണ മേഖല (Administrative Services)", "Administrative Services", listOf(Planet.SUN, Planet.MARS, Planet.JUPITER), listOf(10, 1, 9, 11), "നേതൃത്വ കാരകന്മാരായ സൂര്യ-കുജ-ഗുരു ഗ്രഹങ്ങളുടെ ബലം അഡ്മിനിസ്ട്രേറ്റീവ് മികവ് നൽകുന്നു."),
            CatSpec("psu", "പൊതുമേഖലാ സ്ഥാപനങ്ങൾ (Public Sector / PSU)", "Public Sector Undertakings", listOf(Planet.SATURN, Planet.SUN, lord10), listOf(10, 6, 11, 2), "ശനിയുടെയും സൂര്യന്റെയും ദശാംശക ബലം പൊതുമേഖലാ സ്ഥാപനങ്ങളിലെ തൊഴിലിന് അനുകൂലമാണ്."),
            CatSpec("banking", "ബാങ്കിംഗ് മേഖല (Banking)", "Banking", listOf(Planet.MERCURY, Planet.JUPITER, Planet.VENUS), listOf(2, 11, 10, 5), "ധനകാരകനായ വ്യാഴവും കണക്കുകൂട്ടൽ കാരകനായ ബുധനും 2, 11 ഭാവങ്ങളും ബാങ്കിംഗ് കരിയറിനെ പിന്തുണയ്ക്കുന്നു."),
            CatSpec("finance", "ഫിനാൻസ്, അക്കൗണ്ടിംഗ് & ഓഡിറ്റിംഗ് (Finance)", "Finance & Accounting", listOf(Planet.MERCURY, Planet.JUPITER, Planet.VENUS), listOf(2, 11, 5, 10), "ബുധ-വ്യാഴ സംയോഗമോ ദൃഷ്ടിയോ സാമ്പത്തിക വിശകലന മേഖലയിൽ ശോഭിക്കാൻ സഹായിക്കുന്നു."),
            CatSpec("teaching", "അധ്യാപനം & അക്കാദമിക് രംഗം (Teaching)", "Teaching", listOf(Planet.JUPITER, Planet.MERCURY, Planet.SUN), listOf(5, 9, 2, 10), "ഗുരുവിന്റെയും ബുധന്റെയും 2, 5, 9 ഭാവ ബന്ധം മികച്ച അധ്യാപന-പരിശീലന ശേഷി നൽകുന്നു."),
            CatSpec("education_sector", "വിദ്യാഭ്യാസ ഭരണ-പരിശീലന മേഖല (Education)", "Education Sector", listOf(Planet.JUPITER, Planet.MERCURY, Planet.MOON), listOf(4, 5, 9, 10), "വിദ്യാഭ്യാസ സ്ഥാപനങ്ങൾ, അക്കാദമിക് കൗൺസിലിംഗ് എന്നിവയ്ക്ക് അനുകൂല ഗ്രഹസ്ഥിതി."),
            CatSpec("law", "നിയമം & ജുഡീഷ്യറി (Law & Legal Services)", "Law & Judiciary", listOf(Planet.JUPITER, Planet.SATURN, Planet.MARS), listOf(9, 6, 7, 10), "നീതികാരകനായ ശനിയും ധർമ്മകാരകനായ വ്യാഴവും നിയമ-വ്യവഹാര മേഖലകളിൽ തിളങ്ങാൻ സഹായിക്കുന്നു."),
            CatSpec("police", "പോലീസ് & എൻഫോഴ്സ്മെന്റ് (Police)", "Police & Enforcement", listOf(Planet.MARS, Planet.SUN, Planet.SATURN), listOf(3, 6, 10, 1), "കുജന്റെയും രവിയുടെയും കരുത്ത് യൂണിഫോംഡ് സർവീസുകളിലേക്കുള്ള ആഭിമുഖ്യം കാണിക്കുന്നു."),
            CatSpec("defence", "പ്രതിരോധ മേഖല / സൈന്യം (Defence)", "Defence Services", listOf(Planet.MARS, Planet.SUN, Planet.KETU), listOf(3, 6, 10, 8), "മൂന്ന്, ആറ്, പത്ത് ഭാവങ്ങളിലെ കുജ-രവി ബലം പ്രതിരോധ-സുരക്ഷാ വിഭാഗങ്ങൾക്ക് അനുയോജ്യമാണ്."),
            CatSpec("healthcare", "വൈദ്യശാസ്ത്രം, നഴ്സിംഗ് & ഹെൽത്ത് കെയർ (Healthcare)", "Healthcare & Medicine", listOf(Planet.SUN, Planet.MOON, Planet.MARS, Planet.RAHU), listOf(6, 8, 12, 10), "സൂര്യ-ചന്ദ്ര-കുജ-രാഹു ഗ്രഹങ്ങളും 6, 12 ഭാവങ്ങളും ചികിത്സാ-ആശുപത്രി-ഫാർമസി മേഖലകളെ സൂചിപ്പിക്കുന്നു."),
            CatSpec("it_software", "ഐ.ടി & സോഫ്റ്റ്‌വെയർ (IT & Software)", "IT & Software", listOf(Planet.MERCURY, Planet.RAHU, Planet.SATURN), listOf(3, 5, 10, 11), "ബുധന്റെയും രാഹുവിന്റെയും യുക്തിചിന്തയും സാങ്കേതിക നൈപുണ്യവും ഐ.ടി മേഖലയിൽ മികച്ച വളർച്ച നൽകുന്നു."),
            CatSpec("technology", "ആധുനിക സാങ്കേതികവിദ്യ & ഡാറ്റ (Technology)", "Technology & AI/Data", listOf(Planet.RAHU, Planet.MERCURY, Planet.VENUS), listOf(5, 10, 11, 3), "രാഹു-ബുധ ബന്ധം പുത്തൻ സാങ്കേതികവിദ്യകളിലും ഡിജിറ്റൽ മേഖലകളിലും മുന്നേറ്റം നൽകുന്നു."),
            CatSpec("engineering", "എഞ്ചിനീയറിംഗ് & നിർമ്മാണം (Engineering)", "Engineering", listOf(Planet.MARS, Planet.SATURN, Planet.MERCURY), listOf(4, 10, 3, 11), "യന്ത്ര-നിർമ്മാണ കാരകന്മാരായ കുജനും ശനിയും എഞ്ചിനീയറിംഗ് വിഭാഗങ്ങൾക്ക് കരുത്തേകുന്നു."),
            CatSpec("management", "മാനേജ്മെന്റ് & എക്സിക്യൂട്ടീവ് (Management)", "Management & HR", listOf(Planet.SUN, Planet.JUPITER, Planet.MERCURY), listOf(10, 1, 7, 11), "സംഘാടകശേഷിയും നേതൃപാടവവും കോർപ്പറേറ്റ് മാനേജ്മെന്റ് പദവികൾക്ക് സഹായകമാണ്."),
            CatSpec("business", "വ്യാപാരം & വാണിജ്യം (Business & Trade)", "Business & Commerce", listOf(Planet.MERCURY, Planet.VENUS, Planet.JUPITER), listOf(7, 2, 10, 11), "ഏഴാം ഭാവവും ബുധ-ശുക്ര ബലവും സ്വതന്ത്ര വ്യാപാര-വാണിജ്യ സാധ്യതകളെ കാണിക്കുന്നു."),
            CatSpec("entrepreneurship", "സംരംഭകത്വം & സ്റ്റാർട്ടപ്പ് (Entrepreneurship)", "Entrepreneurship", listOf(Planet.MARS, Planet.MERCURY, Planet.RAHU, lord10), listOf(1, 3, 7, 10, 11), "മൂന്നാം ഭാവത്തിലെ (പരിശ്രമം) കരുത്തും ലഗ്നബലവും സ്വന്തം സംരംഭങ്ങൾ പടുത്തുയർത്താൻ സഹായിക്കുന്നു."),
            CatSpec("research", "ഗവേഷണം & ശാസ്ത്ര പഠനം (Research)", "Research & Analytics", listOf(Planet.KETU, Planet.SATURN, Planet.JUPITER, Planet.MERCURY), listOf(8, 5, 9, 12), "എട്ടാം ഭാവവും കേതു-ശനി-ഗുരു ബന്ധവും ആഴത്തിലുള്ള ഗവേഷണ-ശാസ്ത്ര മേഖലകളെ പിന്തുണയ്ക്കുന്നു."),
            CatSpec("media", "മാധ്യമപ്രവർത്തനം & ജേണലിസം (Media)", "Media & Journalism", listOf(Planet.MERCURY, Planet.VENUS, Planet.MOON), listOf(3, 2, 10, 11), "മൂന്നാം ഭാവവും ബുധ-ശുക്ര-ചന്ദ്ര സ്വാധീനവും ദൃശ്യ-അച്ചടി-ഡിജിറ്റൽ മാധ്യമ രംഗത്തിന് ഉചിതമാണ്."),
            CatSpec("communication", "വാർത്താവിനിമയം & പബ്ലിക് റിലേഷൻസ് (Communication)", "Communication", listOf(Planet.MERCURY, Planet.VENUS, Planet.JUPITER), listOf(2, 3, 7, 11), "രണ്ടാം ഭാവവും (വാക്ക്) മൂന്നാം ഭാവവും ആശയവിനിമയ മേഖലകളിൽ വിജയം നൽകുന്നു."),
            CatSpec("creative", "കല, സിനിമ, ഡിസൈൻ & സാഹിത്യം (Creative Fields)", "Creative & Arts", listOf(Planet.VENUS, Planet.MOON, Planet.MERCURY), listOf(5, 3, 1, 11), "ശുക്രന്റെയും ചന്ദ്രന്റെയും കലാപരമായ ഊർജ്ജം സർഗ്ഗാത്മക മേഖലകളിൽ തിളങ്ങാൻ സഹായിക്കുന്നു."),
            CatSpec("foreign_job", "വിദേശ തൊഴിൽ (Foreign Employment)", "Foreign Employment", listOf(Planet.RAHU, Planet.MOON, Planet.SATURN, Planet.VENUS), listOf(12, 9, 3, 10), "ഒമ്പത്, പന്ത്രണ്ട് ഭാവങ്ങളും രാഹു-ചന്ദ്ര-ശനി ബന്ധവും വിദേശ തൊഴിൽ അവസരങ്ങളെ സൂചിപ്പിക്കുന്നു."),
            CatSpec("self_employment", "സ്വയംതൊഴിൽ & കൺസൾട്ടൻസി (Self-Employment)", "Self-Employment", listOf(lord10, Planet.MERCURY, Planet.JUPITER, Planet.SATURN), listOf(1, 3, 7, 10), "ലഗ്നം, മൂന്ന്, ഏഴ്, പത്ത് ഭാവങ്ങളുടെ ബന്ധം സ്വതന്ത്ര പ്രൊഫഷണൽ/കൺസൾട്ടൻസി സേവനങ്ങൾക്ക് അനുയോജ്യമാണ്.")
        )

        return specs.map { spec ->
            var score = 55
            for (p in spec.primaryPlanets) {
                val pos = planetsMap[p] ?: continue
                when (pos.dignity) {
                    PlanetaryDignity.EXALTED, PlanetaryDignity.MOOLATRIKONA, PlanetaryDignity.OWN_SIGN -> score += 10
                    PlanetaryDignity.FRIEND_SIGN, PlanetaryDignity.GREAT_FRIEND -> score += 5
                    PlanetaryDignity.DEBILITATED, PlanetaryDignity.ENEMY_SIGN -> score -= 7
                    else -> {}
                }
                if (pos.houseFromLagna in listOf(1, 2, 4, 5, 7, 9, 10, 11)) score += 4 else if (pos.houseFromLagna in listOf(8, 12) && "foreign" !in spec.id && "research" !in spec.id) score -= 4
            }
            val clampedScore = score.coerceIn(38, 92)
            val classification = when {
                clampedScore >= 68 -> PeriodClassification.FAVORABLE
                clampedScore <= 50 -> PeriodClassification.CHALLENGING
                else -> PeriodClassification.MIXED
            }
            val pSummary = spec.primaryPlanets.distinct().joinToString(", ") { p ->
                val pos = planetsMap[p]
                "${p.malayalamName} (${pos?.rashi?.malayalamName ?: ""}, ${pos?.houseFromLagna ?: 1}-ാം ഭാവം)"
            }
            CareerCategoryItem(
                id = spec.id,
                titleMalayalam = spec.mal,
                titleEnglish = spec.eng,
                suitabilityScore = clampedScore,
                classification = classification,
                supportingFactorsMal = "${spec.focusNoteMal} ജാതകത്തിലെ $pSummary എന്നീ ഗ്രഹനിലകൾ ഈ മേഖലയ്ക്ക് അനുകൂല ഘടകങ്ങൾ നൽകുന്നു.",
                challengingFactorsMal = if (classification == PeriodClassification.FAVORABLE) "അമിത ആത്മവിശ്വാസം ഒഴിവാക്കി തുടർച്ചയായ നൈപുണ്യ വികസനവും സമയക്രമവും പാലിക്കണം." else "മത്സരപ്പരീക്ഷകളിലും തൊഴിൽ തിരഞ്ഞെടുപ്പിലും കൂടുതൽ കഠിനാധ്വാനവും ക്ഷമയും അനിവാര്യമാണ്.",
                relevantPlanets = spec.primaryPlanets.distinct(),
                relevantHouses = spec.primaryHouses,
                relevantYogas = yogaNames.take(3),
                activeDashaSupportMal = "നിലവിലെ ${currentMaha.lord.malayalamName} മഹാദശയും ${currentAntar.antardashaLord.malayalamName} അപഹാരവും (${currentAntar.classification.malayalamLabel}) ഈ കരിയർ മേഖലയിലെ ശ്രമങ്ങളെ സ്വാധീനിക്കുന്നു.",
                transitSupportMal = "ഗോചരത്തിൽ വ്യാഴവും ശനിയും 6, 10, 11 ഭാവങ്ങളിലേക്ക് ദൃഷ്ടി ചെയ്യുകയോ സഞ്ചരിക്കുകയോ ചെയ്യുന്ന ഘട്ടങ്ങൾ തൊഴിൽ പുരോഗതിക്ക് സഹായകമാണ്.",
                stabilityAndPromotionMal = "സ്ഥിരത: ${if (clampedScore >= 65) "ഉയർന്ന കരിയർ സ്ഥിരതയും ഘട്ടംഘട്ടമായ സ്ഥാനക്കയറ്റ പ്രവണതയും കാണുന്നു." else "തുടക്കത്തിൽ തൊഴിൽ മാറ്റ സാധ്യതകൾക്ക് ശേഷം സ്ഥിരത കൈവരിക്കും."}",
                examAndSelectionNoteMal = "ശ്രദ്ധിക്കുക: ജാതകത്തിലെ ഗ്രഹനില മത്സരപ്പരീക്ഷാ അഭിരുചിയും അനുകൂല സമയവും സൂചിപ്പിക്കുന്നു; പരീക്ഷാ വിജയം വ്യക്തിയുടെ കഠിനാധ്വാനത്തെയും തയ്യാറെടുപ്പിനെയും ആശ്രയിച്ചിരിക്കും (തിരഞ്ഞെടുപ്പ് വാഗ്ദാനം ചെയ്യുന്നില്ല)."
            )
        }
    }

    fun generateAllLifeDomainReports(
        birthData: BirthData,
        lagnaRashi: Rashi,
        chandraRashi: Rashi,
        janmaNakshatra: Nakshatra,
        planetsMap: Map<Planet, PlanetPosition>,
        bhavas: List<BhavaInfo>,
        divisionalCharts: Map<DivisionalChartType, DivisionalChartData>,
        yogas: List<YogaResult>,
        currentMaha: MahadashaPeriod,
        currentAntar: AntardashaPeriod
    ): Map<String, LifeDomainReport> {
        val sun = planetsMap[Planet.SUN]!!
        val moon = planetsMap[Planet.MOON]!!
        val mars = planetsMap[Planet.MARS]!!
        val mercury = planetsMap[Planet.MERCURY]!!
        val jupiter = planetsMap[Planet.JUPITER]!!
        val venus = planetsMap[Planet.VENUS]!!
        val saturn = planetsMap[Planet.SATURN]!!
        val rahu = planetsMap[Planet.RAHU]!!
        val ketu = planetsMap[Planet.KETU]!!

        fun houseLord(h: Int): Planet = bhavas.find { it.houseNumber == h }?.lord ?: Planet.JUPITER
        fun housePos(h: Int): PlanetPosition = planetsMap[houseLord(h)] ?: jupiter

        val lord2Pos = housePos(2)
        val lord4Pos = housePos(4)
        val lord5Pos = housePos(5)
        val lord6Pos = housePos(6)
        val lord7Pos = housePos(7)
        val lord9Pos = housePos(9)
        val lord10Pos = housePos(10)
        val lord11Pos = housePos(11)
        val lord12Pos = housePos(12)

        // Darakaraka (Planet with lowest degree in sign among 7 core planets)
        val darakaraka = listOf(sun, moon, mars, mercury, jupiter, venus, saturn).minByOrNull { it.degreeInSign } ?: venus

        val reports = mutableMapOf<String, LifeDomainReport>()

        // 1. Career General Report
        reports["career"] = LifeDomainReport(
            domainId = "career",
            titleMalayalam = "തൊഴിൽ, കരിയർ & സർക്കാർ ജോലി വിശകലനം",
            titleEnglish = "Career, Profession & Government Service",
            classification = currentAntar.classification,
            keyHouses = listOf(10, 6, 2, 11),
            keyPlanets = listOf(lord10Pos.planet, Planet.SUN, Planet.SATURN, Planet.MERCURY, Planet.JUPITER).distinct(),
            summaryMalayalam = "${lagnaRashi.malayalamName} ലഗ്നജാതകത്തിൽ പത്താം ഭാവാധിപനായ ${lord10Pos.planet.malayalamName} ${lord10Pos.houseFromLagna}-ാം ഭാവത്തിൽ (${lord10Pos.rashi.malayalamName} രാശിയിൽ, ${lord10Pos.dignity.malayalam}) സ്ഥിതി ചെയ്യുന്നു. ദശാംശകത്തിൽ (D10) ${lord10Pos.dasamsaRashi.malayalamName} രാശിയിലാണ് കർമ്മാധിപൻ നിൽക്കുന്നത്.",
            detailedSections = listOf(
                "10-ാം ഭാവവും കർമ്മാധിപ ബലവും" to "പത്താം ഭാവാധിപനായ ${lord10Pos.planet.malayalamName} ${lord10Pos.houseFromLagna}-ൽ നിൽക്കുന്നതും കർമ്മകാരകനായ ശനി ${saturn.houseFromLagna}-ാം ഭാവത്തിൽ (${saturn.rashi.malayalamName}) നിൽക്കുന്നതും തൊഴിൽരംഗത്ത് സ്വന്തം അധ്വാനത്തിലൂടെ ഉയർച്ച നേടാനുള്ള കഴിവ് നൽകുന്നു.",
                "സർക്കാർ ജോലി, കേരള PSC, KAS & മത്സരപ്പരീക്ഷാ പ്രവണത" to "അധികാരകാരകനായ സൂര്യൻ ${sun.houseFromLagna}-ാം ഭാവത്തിലും (${sun.rashi.malayalamName}), ആറാം ഭാവാധിപനായ ${lord6Pos.planet.malayalamName} ${lord6Pos.houseFromLagna}-ാം ഭാവത്തിലും സ്ഥിതി ചെയ്യുന്നു. ഇത് കേരള പി.എസ്.സി, കെ.എ.എസ്, കേന്ദ്ര-സംസ്ഥാന സർക്കാർ പരീക്ഷകൾ എന്നിവയിൽ കൃത്യമായ പരിശീലനത്തിലൂടെ മുന്നേറാനുള്ള യോഗസൂചന നൽകുന്നു.",
                "പ്രൊമോഷൻ, തൊഴിൽ സ്ഥിരത & ജോലി മാറ്റം" to "ഇപ്പോഴത്തെ ${currentMaha.lord.malayalamName} മഹാദശയിലെ ${currentAntar.antardashaLord.malayalamName} അപഹാരത്തിൽ (${currentAntar.startDateFormatted} - ${currentAntar.endDateFormatted}) തൊഴിൽപരമായി ${currentAntar.classification.malayalamLabel} ആണ് കാണുന്നത്."
            ),
            supportivePeriodsMal = "${lord10Pos.planet.malayalamName}, ${lord11Pos.planet.malayalamName}, ${Planet.JUPITER.malayalamName}, ${Planet.SUN.malayalamName} എന്നീ ഗ്രഹങ്ങളുടെ ദശാ-അപഹാര കാലങ്ങളും വ്യാഴം 2, 5, 9, 10, 11 ഭാവങ്ങളിൽ സഞ്ചരിക്കുന്ന ഗോചര കാലവും കരിയറിന് ഏറ്റവും അനുകൂലമാണ്.",
            cautionPeriodsMal = "അഷ്ടമാധിപന്റെയോ വ്യയാധിപന്റെയോ അപഹാര കാലങ്ങളിൽ തിടുക്കത്തിലുള്ള തൊഴിൽ മാറ്റം ഒഴിവാക്കുക."
        )

        // 2. Education Module
        reports["education"] = LifeDomainReport(
            domainId = "education",
            titleMalayalam = "വിദ്യാഭ്യാസം, ഉന്നത പഠനം & ഗവേഷണം",
            titleEnglish = "Education, Higher Studies & Competitive Exams",
            classification = if (mercury.dignity != PlanetaryDignity.DEBILITATED && jupiter.dignity != PlanetaryDignity.DEBILITATED) PeriodClassification.FAVORABLE else PeriodClassification.MIXED,
            keyHouses = listOf(4, 5, 9),
            keyPlanets = listOf(Planet.MERCURY, Planet.JUPITER, lord4Pos.planet, lord5Pos.planet).distinct(),
            summaryMalayalam = "വിദ്യാകാരകനായ ബുധൻ ${mercury.houseFromLagna}-ാം ഭാവത്തിലും (${mercury.rashi.malayalamName}), ജ്ഞാനകാരകനായ വ്യാഴം ${jupiter.houseFromLagna}-ാം ഭാവത്തിലും (${jupiter.rashi.malayalamName}) സ്ഥിതി ചെയ്യുന്നു. ചതുർവിംശാംശകത്തിൽ (D24) ബുധൻ ${AstronomicalEphemerisEngine.calculateDivisionalRashi(mercury.siderealLongitude, DivisionalChartType.D24).malayalamName} രാശിയിലാണ്.",
            detailedSections = listOf(
                "പഠന പ്രവണതയും ബുദ്ധിശക്തിയും (4, 5 ഭാവങ്ങൾ)" to "നാലാം ഭാവാധിപനായ ${lord4Pos.planet.malayalamName} ${lord4Pos.houseFromLagna}-ലും അഞ്ചാം ഭാവാധിപനായ ${lord5Pos.planet.malayalamName} ${lord5Pos.houseFromLagna}-ലും നിൽക്കുന്നത് കാര്യങ്ങൾ വേഗത്തിൽ ഗ്രഹിക്കാനുള്ള ബുദ്ധിശക്തിയും പ്രായോഗിക അറിവും നൽകുന്നു.",
                "ഉന്നത വിദ്യാഭ്യാസവും പ്രൊഫഷണൽ പഠനവും (9-ാം ഭാവം)" to "ഒമ്പതാം ഭാവാധിപനായ ${lord9Pos.planet.malayalamName} ${lord9Pos.houseFromLagna}-ാം ഭാവത്തിൽ (${lord9Pos.rashi.malayalamName}) നിൽക്കുന്നതിനാൽ ബിരുദാനന്തര പഠനം, സാങ്കേതിക-മാനേജ്മെന്റ്-ഗവേഷണ വിഷയങ്ങൾ എന്നിവയിൽ മികച്ച സാധ്യത കാണുന്നു.",
                "ശ്രദ്ധിക്കേണ്ട ഘടകങ്ങൾ" to "മനസ്സിന്റെ ഏകാഗ്രത നിലനിർത്താൻ ചന്ദ്രന്റെയും ബുധന്റെയും ബലമനുസരിച്ച് കൃത്യമായ പഠന ടൈംടേബിൾ പിന്തുടരുന്നത് മത്സരപ്പരീക്ഷകളിൽ വലിയ ഗുണം ചെയ്യും."
            ),
            supportivePeriodsMal = "${Planet.MERCURY.malayalamName}, ${Planet.JUPITER.malayalamName}, ${lord5Pos.planet.malayalamName}, ${lord9Pos.planet.malayalamName} എന്നീ ഗ്രഹങ്ങളുടെ ദശാ-അപഹാര കാലങ്ങൾ പഠനത്തിന് ഏറ്റവും അനുകൂലമാണ്.",
            cautionPeriodsMal = "രാഹു-കേതു-ശനി അപഹാരങ്ങളിൽ പഠനത്തിൽ അലസതയോ ശ്രദ്ധാഭംഗമോ വരാതെ സൂക്ഷിക്കണം."
        )

        // 3. Marriage Module
        reports["marriage"] = LifeDomainReport(
            domainId = "marriage",
            titleMalayalam = "വിവാഹം, ദാമ്പത്യം & കുടുംബജീവിതം",
            titleEnglish = "Marriage, Relationship & Marital Harmony",
            classification = if (lord7Pos.houseFromLagna !in listOf(6, 8, 12)) PeriodClassification.FAVORABLE else PeriodClassification.MIXED,
            keyHouses = listOf(7, 2, 4, 8, 12),
            keyPlanets = listOf(lord7Pos.planet, Planet.VENUS, Planet.JUPITER, darakaraka.planet).distinct(),
            summaryMalayalam = "ഏഴാം ഭാവാധിപനായ (കളത്രാധിപൻ) ${lord7Pos.planet.malayalamName} ${lord7Pos.houseFromLagna}-ാം ഭാവത്തിലും (${lord7Pos.rashi.malayalamName}), കളത്രകാരകനായ ശുക്രൻ ${venus.houseFromLagna}-ാം ഭാവത്തിലും (${venus.rashi.malayalamName}), ദാരകാരകനായ ${darakaraka.planet.malayalamName} ${darakaraka.rashi.malayalamName} രാശിയിലും സ്ഥിതി ചെയ്യുന്നു. നവാംശകത്തിൽ (D9) ഏഴാം ഭാവാധിപൻ ${lord7Pos.navamsaRashi.malayalamName} രാശിയിലാണ്.",
            detailedSections = listOf(
                "വിവാഹ പ്രവണതയും ദാമ്പത്യ സ്വഭാവവും" to "ഏഴാം ഭാവാധിപനായ ${lord7Pos.planet.malayalamName}-ന്റെയും ശുക്രന്റെയും സ്ഥിതി ഉത്തരവാദിത്തബോധവും കുടുംബസ്നേഹവുമുള്ള ദാമ്പത്യ ബന്ധത്തെ സൂചിപ്പിക്കുന്നു. നവാംശകത്തിലെ (D9) ഗ്രഹനില ദാമ്പത്യത്തിന്റെ ആന്തരിക ബലത്തിന് പിന്തുണ നൽകുന്നു.",
                "വിവാഹത്തിന് അനുകൂലമായ കാലഘട്ട സൂചനകൾ" to "കളത്രാധിപനായ ${lord7Pos.planet.malayalamName}, ശുക്രൻ, വ്യാഴം, രണ്ടാം ഭാവാധിപനായ ${lord2Pos.planet.malayalamName} എന്നിവയുടെ ദശാ-അപഹാര കാലങ്ങളും വ്യാഴം ജന്മരാശിയിൽ നിന്നോ ലഗ്നത്തിൽ നിന്നോ 2, 5, 7, 9, 11 ഭാവങ്ങളിൽ സഞ്ചരിക്കുന്ന ഗോചര കാലവും വിവാഹ ആലോചനകൾക്ക് അനുകൂലമായ സമയജാലകങ്ങളാണ് (Timing Windows).",
                "പരിചയപ്പെടൽ സാഹചര്യവും കുടുംബ പങ്കാളിത്തവും" to "${if (lord7Pos.houseFromLagna in listOf(1, 2, 4, 9, 11)) "കുടുംബാംഗങ്ങളുടെയും ബന്ധുക്കളുടെയും പൂർണ്ണ പിന്തുണയോടെയുള്ള പരമ്പരാഗത വിവാഹ ആലോചനകൾക്കാണ് കൂടുതൽ സാധ്യത." else "തൊഴിൽ, വിദ്യാഭ്യാസം അല്ലെങ്കിൽ ദൂരദേശ ബന്ധങ്ങൾ വഴി പരിചയപ്പെടാനുള്ള സാധ്യതയും ജാതകം കാണിക്കുന്നു."}"
            ),
            supportivePeriodsMal = "${lord7Pos.planet.malayalamName}, ${Planet.VENUS.malayalamName}, ${Planet.JUPITER.malayalamName} ദശാ-അപഹാര കാലങ്ങൾ.",
            cautionPeriodsMal = "വിവാഹ തീയതികൾ ജ്യോതിഷപരമായി മുൻകൂട്ടി ഉറപ്പുനൽകുന്നതല്ല; ഇരു ജാതകങ്ങളുടെയും പൊരുത്തവും ദശാസന്ധിയും പരിശോധിച്ച് തീരുമാനമെടുക്കുക."
        )

        // 4. Partner Personality Module
        reports["partner_personality"] = LifeDomainReport(
            domainId = "partner_personality",
            titleMalayalam = "പങ്കാളിയുടെ സ്വഭാവസവിശേഷതകൾ (Partner Personality)",
            titleEnglish = "Partner Personality & Temperament",
            classification = PeriodClassification.FAVORABLE,
            keyHouses = listOf(7, 9),
            keyPlanets = listOf(lord7Pos.planet, Planet.VENUS, darakaraka.planet).distinct(),
            summaryMalayalam = "ഏഴാം ഭാവമായ ${bhavas.find { it.houseNumber == 7 }?.rashi?.malayalamName ?: ""} രാശിയുടെയും അതിന്റെ അധിപനായ ${lord7Pos.planet.malayalamName}-ന്റെയും ദാരകാരകനായ ${darakaraka.planet.malayalamName}-ന്റെയും സ്വഭാവമനുസരിച്ചുള്ള പരമ്പരാഗത സൂചനകൾ:",
            detailedSections = listOf(
                "വ്യക്തിത്വവും സ്വഭാവരീതിയും (Personality & Temperament)" to "ഏഴാം ഭാവാധിപനായ ${lord7Pos.planet.malayalamName}-ന്റെ സ്വാധീനത്താൽ പങ്കാളിക്ക് ആത്മാർത്ഥത, വ്യക്തമായ അഭിപ്രായങ്ങൾ, കുടുംബത്തോട് ചേർന്നുനിൽക്കുന്ന മനോഭാവം എന്നിവ ഉണ്ടാകാൻ സാധ്യതയുണ്ട്.",
                "ആശയവിനിമയവും വൈകാരിക പ്രകൃതവും" to "നവാംശകത്തിലെ ഗ്രഹസ്ഥിതി പ്രകാരം പ്രായോഗിക ബുദ്ധിയും സ്നേഹത്തോടെയുള്ള സംഭാഷണ ശൈലിയും പങ്കാളിയിൽ കാണാം. പ്രതിസന്ധി ഘട്ടങ്ങളിൽ ഉറച്ച പിന്തുണ നൽകുന്ന പ്രകൃതമായിരിക്കും.",
                "വിദ്യാഭ്യാസം, കരിയർ & സാമ്പത്തിക കാഴ്ചപ്പാട്" to "പങ്കാളിക്ക് മികച്ച വിദ്യാഭ്യാസ പശ്ചാത്തലവും സ്വന്തം കാലിൽ നിൽക്കാനുള്ള തൊഴിൽ അഭിരുചിയും സമ്പാദ്യശീലവും ഉണ്ടാകാനുള്ള സാധ്യത ജാതകം സൂചിപ്പിക്കുന്നു."
            ),
            supportivePeriodsMal = "പരസ്പര ബഹുമാനവും തുറന്ന ആശയവിനിമയവും ദാമ്പത്യ വിജയത്തിന് കരുത്താകും.",
            cautionPeriodsMal = "ചെറിയ അഭിപ്രായവ്യത്യാസങ്ങളിൽ വാശി ഒഴിവാക്കി ക്ഷമയോടെ സംസാരിക്കുക."
        )

        // 5. Partner Appearance Tendencies Module (Strictly non-discriminatory & traditional indications only)
        reports["partner_appearance"] = LifeDomainReport(
            domainId = "partner_appearance",
            titleMalayalam = "പങ്കാളിയുടെ രൂപസവിശേഷതകൾ — പരമ്പരാഗത സൂചനകൾ",
            titleEnglish = "Partner Appearance Tendencies (Traditional Indications)",
            classification = PeriodClassification.FAVORABLE,
            keyHouses = listOf(7),
            keyPlanets = listOf(lord7Pos.planet, Planet.VENUS).distinct(),
            summaryMalayalam = "ജാതകത്തിലെ പരമ്പരാഗത സൂചന പ്രകാരം ഏഴാം ഭാവരാശിയുടെയും (${bhavas.find { it.houseNumber == 7 }?.rashi?.malayalamName ?: ""}) ഏഴാം ഭാവാധിപനായ ${lord7Pos.planet.malayalamName}-ന്റെയും അടിസ്ഥാനത്തിലുള്ള പൊതുവായ രൂപസവിശേഷതാ സാധ്യതകൾ താഴെ നൽകുന്നു (ഇവ ജ്യോതിഷപരമായ സൂചനകൾ മാത്രമാണ്, ഉറപ്പായ വസ്തുതകളല്ല):",
            detailedSections = listOf(
                "ശരീരപ്രകൃതവും ഉയരവും (Height & Body Build - സാധ്യത)" to "ജാതകത്തിലെ പരമ്പരാഗത സൂചന പ്രകാരം ${lord7Pos.rashi.modalityMal} സ്വഭാവമുള്ള രാശിബന്ധം വരുന്നതിനാൽ മിതമായതോ സാമാന്യം നല്ലതോ ആയ ഉയരവും സന്തുലിതമായ ശരീരപ്രകൃതവും ഉണ്ടാകാൻ സാധ്യത കൽപ്പിക്കുന്നു.",
                "മുഖഭാവം, കണ്ണുകൾ & തലമുടി (Face, Eyes & Hair - സൂചന)" to "ജാതകത്തിലെ പരമ്പരാഗത സൂചന പ്രകാരം ${lord7Pos.planet.malayalamName}, ശുക്രൻ എന്നിവരുടെ സ്വാധീനത്താൽ പ്രസന്നമായ മുഖഭാവവും ആകർഷകവും തിളക്കമുള്ളതുമായ കണ്ണുകളും വൃത്തിയുള്ള കേശാലങ്കാര ശൈലിയും ഉണ്ടാകാനുള്ള സൂചന കാണുന്നു.",
                "വസ്ത്രധാരണ ശൈലിയും പൊതുഭാവവും (Style & Overall Grace)" to "ലളിതവും എന്നാൽ മാന്യവും ആകർഷകവുമായ വസ്ത്രധാരണ ശൈലിയും ആത്മവിശ്വാസം നിറഞ്ഞ പെരുമാറ്റവും പങ്കാളിയുടെ സവിശേഷതയായിരിക്കാൻ സാധ്യതയുണ്ട്."
            ),
            supportivePeriodsMal = "ശ്രദ്ധിക്കുക: ബാഹ്യരൂപത്തേക്കാൾ സ്വഭാവഗുണത്തിനും മാനസിക ഐക്യത്തിനുമാണ് കേരള ജ്യോതിഷ പാരമ്പര്യം പ്രാധാന്യം നൽകുന്നത്.",
            cautionPeriodsMal = "ഈ വിവരങ്ങൾ ജാതകത്തിലെ പരമ്പരാഗത സൂചന പ്രകാരം മാത്രമുള്ള സാധ്യതകളാണ്; വ്യക്തിപരവും പാരമ്പര്യവുമായ ഘടകങ്ങൾക്കനുസരിച്ച് മാറ്റങ്ങൾ സ്വാഭാവികമാണ്."
        )

        // 6. Finance Module
        reports["finance"] = LifeDomainReport(
            domainId = "finance",
            titleMalayalam = "ധനസ്ഥിതി, സമ്പാദ്യം & ആസ്തികൾ (Finance)",
            titleEnglish = "Finance, Wealth & Savings",
            classification = if (lord2Pos.houseFromLagna !in listOf(6, 8, 12) || lord11Pos.houseFromLagna !in listOf(6, 8, 12)) PeriodClassification.FAVORABLE else PeriodClassification.MIXED,
            keyHouses = listOf(2, 11, 5, 9),
            keyPlanets = listOf(lord2Pos.planet, lord11Pos.planet, Planet.JUPITER, Planet.VENUS).distinct(),
            summaryMalayalam = "ധനാധിപനായ ${lord2Pos.planet.malayalamName} ${lord2Pos.houseFromLagna}-ാം ഭാവത്തിലും (${lord2Pos.rashi.malayalamName}), ലാഭാധിപനായ ${lord11Pos.planet.malayalamName} ${lord11Pos.houseFromLagna}-ാം ഭാവത്തിലും (${lord11Pos.rashi.malayalamName}), ധനകാരകനായ വ്യാഴം ${jupiter.houseFromLagna}-ാം ഭാവത്തിലും സ്ഥിതി ചെയ്യുന്നു.",
            detailedSections = listOf(
                "വരുമാന പ്രവണതയും സാമ്പത്തിക ഭദ്രതയും" to "രണ്ടും പതിനൊന്നും ഭാവാധിപന്മാരുടെ സ്ഥിതിയും ഹോര ചക്രത്തിലെ (D2) ഗ്രഹനിലയും സ്വപ്രയത്നത്തിലൂടെയുള്ള സ്ഥിരവരുമാനത്തെയും ഘട്ടംഘട്ടമായ ആസ്തി വളർച്ചയെയും സൂചിപ്പിക്കുന്നു.",
                "സമ്പാദ്യവും നിക്ഷേപങ്ങളും" to "ഭൂമി, സ്വർണ്ണം, സുരക്ഷിതമായ ബാങ്ക് നിക്ഷേപങ്ങൾ എന്നിവയിലൂടെ ദീർഘകാല സമ്പത്ത് സൃഷ്ടിക്കുന്നതാണ് ഈ ജാതകത്തിന് കൂടുതൽ അനുയോജ്യം."
            ),
            supportivePeriodsMal = "${lord2Pos.planet.malayalamName}, ${lord11Pos.planet.malayalamName}, ${Planet.JUPITER.malayalamName} ദശാ-അപഹാര കാലങ്ങൾ.",
            cautionPeriodsMal = "എട്ട്, പന്ത്രണ്ട് ഭാവാധിപന്മാരുടെ അപഹാരങ്ങളിൽ ഊഹക്കച്ചവടവും വലിയ വായ്പകളും ഒഴിവാക്കുക."
        )

        // 7. Business Module
        reports["business"] = LifeDomainReport(
            domainId = "business",
            titleMalayalam = "വ്യവസായം, വാണിജ്യം & സംരംഭകത്വം (Business)",
            titleEnglish = "Business, Trade & Entrepreneurship",
            classification = if (mercury.houseFromLagna in listOf(1, 2, 3, 4, 5, 7, 9, 10, 11)) PeriodClassification.FAVORABLE else PeriodClassification.MIXED,
            keyHouses = listOf(7, 10, 11, 2),
            keyPlanets = listOf(Planet.MERCURY, lord7Pos.planet, lord10Pos.planet, lord11Pos.planet).distinct(),
            summaryMalayalam = "വാണിജ്യകാരകനായ ബുധൻ ${mercury.houseFromLagna}-ാം ഭാവത്തിലും ഏഴാം ഭാവാധിപനായ ${lord7Pos.planet.malayalamName} ${lord7Pos.houseFromLagna}-ാം ഭാവത്തിലും നിൽക്കുന്നു.",
            detailedSections = listOf(
                "ബിസിനസ്സ് & സംരംഭകത്വ അഭിരുചി" to "2, 7, 10, 11 ഭാവങ്ങളുടെ ബന്ധം സ്വതന്ത്ര സംരംഭങ്ങൾ, സേവന മേഖല, കൺസൾട്ടൻസി അല്ലെങ്കിൽ വ്യാപാരം എന്നിവയിൽ പ്രവർത്തിക്കാനുള്ള പ്രായോഗിക ബുദ്ധി നൽകുന്നു.",
                "പങ്കാളിത്ത വ്യവസായം (Partnership)" to "${if (lord7Pos.dignity in listOf(PlanetaryDignity.DEBILITATED, PlanetaryDignity.ENEMY_SIGN)) "പങ്കാളിത്ത വ്യാപാരത്തേക്കാൾ സ്വന്തം നിയന്ത്രണത്തിലുള്ള സംരംഭങ്ങളാണ് കൂടുതൽ സുരക്ഷിതം." else "വിശ്വസ്തരായ പങ്കാളികളുമായി വ്യക്തമായ കരാറുകളോടെ വ്യവസായം നടത്താൻ അനുകൂലമാണ്."}"
            ),
            supportivePeriodsMal = "ബുധൻ, ശുക്രൻ, പത്താം ഭാവാധിപൻ, പതിനൊന്നാം ഭാവാധിപൻ എന്നിവയുടെ ദശാകാലങ്ങൾ.",
            cautionPeriodsMal = "വലിയ മൂലധന വിപുലീകരണത്തിന് മുൻപ് വിപണി സാധ്യതകളും ദശാകാലവും പരിശോധിക്കുക."
        )

        // 8. Foreign Travel Module
        reports["foreign"] = LifeDomainReport(
            domainId = "foreign",
            titleMalayalam = "വിദേശയാത്ര, വിദേശ തൊഴിൽ & സ്ഥലംമാറ്റം",
            titleEnglish = "Foreign Travel, Overseas Career & Relocation",
            classification = if (lord12Pos.houseFromLagna in listOf(1, 3, 7, 9, 10, 11, 12) || rahu.houseFromLagna in listOf(3, 7, 9, 10, 12)) PeriodClassification.FAVORABLE else PeriodClassification.MIXED,
            keyHouses = listOf(3, 9, 12),
            keyPlanets = listOf(Planet.RAHU, lord9Pos.planet, lord12Pos.planet, Planet.MOON).distinct(),
            summaryMalayalam = "ഒമ്പതാം ഭാവാധിപനായ ${lord9Pos.planet.malayalamName} ${lord9Pos.houseFromLagna}-ലും, പന്ത്രണ്ടാം ഭാവാധിപനായ ${lord12Pos.planet.malayalamName} ${lord12Pos.houseFromLagna}-ലും, വിദേശകാരകനായ രാഹു ${rahu.houseFromLagna}-ാം ഭാവത്തിലും സ്ഥിതി ചെയ്യുന്നു.",
            detailedSections = listOf(
                "ഹ്രസ്വ-ദീർഘദൂര യാത്രകളും വിദേശ അവസരങ്ങളും" to "മൂന്നാം ഭാവം (ചെറുയാത്രകൾ), ഒമ്പതാം ഭാവം (ദീർഘദൂര യാത്രകൾ), പന്ത്രണ്ടാം ഭാവം (വിദേശവാസം) എന്നിവയുടെ ഗ്രഹനിലയനുസരിച്ച് തൊഴിൽ-പഠന ആവശ്യങ്ങൾക്കായി ജന്മനാട്ടിൽ നിന്ന് മാറി താമസിക്കാനോ വിദേശയാത്രകൾ നടത്താനോ സാധ്യത കാണുന്നു.",
                "വിദേശ സ്ഥിരതാമസ പ്രവണത" to "${if (lord4Pos.houseFromLagna in listOf(9, 12) || rahu.houseFromLagna in listOf(4, 9, 12)) "ദീർഘകാല വിദേശവാസത്തിനോ അന്യസംസ്ഥാന/വിദേശ തൊഴിലിനോ ശക്തമായ ഗ്രഹസൂചനയുണ്ട്." else "വിദേശ/ദൂരദേശ തൊഴിലിന് ശേഷം ജന്മനാട്ടിൽ തിരിച്ചെത്തി സ്ഥിരതാമസമാക്കാനുള്ള പ്രവണതയാണ് കൂടുതൽ."}"
            ),
            supportivePeriodsMal = "രാഹു, ${lord9Pos.planet.malayalamName}, ${lord12Pos.planet.malayalamName} ദശാ-അപഹാര കാലങ്ങൾ.",
            cautionPeriodsMal = "വിദേശ യാത്രാ രേഖകളും തൊഴിൽ കരാറുകളും കൃത്യമായി പരിശോധിച്ച് ഉറപ്പുവരുത്തുക."
        )

        // 9. Property / Home / Vehicle Module
        reports["property"] = LifeDomainReport(
            domainId = "property",
            titleMalayalam = "ഭൂമി, ഗൃഹം & വാഹന യോഗം (Property & Vehicles)",
            titleEnglish = "Property, Home & Vehicles",
            classification = if (lord4Pos.dignity != PlanetaryDignity.DEBILITATED) PeriodClassification.FAVORABLE else PeriodClassification.MIXED,
            keyHouses = listOf(4, 2, 11),
            keyPlanets = listOf(lord4Pos.planet, Planet.MARS, Planet.VENUS).distinct(),
            summaryMalayalam = "സുഖസ്ഥാനാധിപനായ (4-ാം ഭാവാധിപൻ) ${lord4Pos.planet.malayalamName} ${lord4Pos.houseFromLagna}-ാം ഭാവത്തിലും, ഭൂമികാരകനായ കുജൻ ${mars.houseFromLagna}-ാം ഭാവത്തിലും, വാഹനകാരകനായ ശുക്രൻ ${venus.houseFromLagna}-ാം ഭാവത്തിലും സ്ഥിതി ചെയ്യുന്നു. ചതുർത്ഥാംശകത്തിൽ (D4) നാലാം ഭാവാധിപൻ ${AstronomicalEphemerisEngine.calculateDivisionalRashi(lord4Pos.siderealLongitude, DivisionalChartType.D4).malayalamName} രാശിയിലാണ്.",
            detailedSections = listOf(
                "സ്വന്തം വീടും ഭൂമിയും (Home & Land)" to "നാലാം ഭാവാധിപനായ ${lord4Pos.planet.malayalamName}-ന്റെയും കുജന്റെയും ബലമനുസരിച്ച് സ്വപ്രയത്നത്തിലൂടെ സ്വന്തമായി ഭൂമിയും മനോഹരമായ ഗൃഹവും നിർമ്മിക്കാൻ യോഗമുണ്ട്.",
                "വാഹന യോഗം (Vehicles - D16 Shodasamsa)" to "ശുക്രന്റെ ${venus.rashi.malayalamName} രാശിസ്ഥിതിയും ഷോഡശാംശക (D16) ബലവും വാഹന സൗഭാഗ്യത്തെയും ഗൃഹോപകരണ സമൃദ്ധിയെയും സൂചിപ്പിക്കുന്നു."
            ),
            supportivePeriodsMal = "${lord4Pos.planet.malayalamName}, ${Planet.MARS.malayalamName}, ${Planet.VENUS.malayalamName} ദശാ-അപഹാര കാലങ്ങൾ.",
            cautionPeriodsMal = "വസ്തു ഇടപാടുകളിൽ ആധാരങ്ങളും നിയമവശങ്ങളും സൂക്ഷ്മമായി പരിശോധിക്കുക."
        )

        // 10. Family Module
        reports["family"] = LifeDomainReport(
            domainId = "family",
            titleMalayalam = "കുടുംബം, മാതാപിതാക്കൾ & സഹോദരങ്ങൾ (Family)",
            titleEnglish = "Family, Parents & Siblings",
            classification = PeriodClassification.FAVORABLE,
            keyHouses = listOf(2, 3, 4, 9),
            keyPlanets = listOf(Planet.SUN, Planet.MOON, Planet.MARS, lord2Pos.planet).distinct(),
            summaryMalayalam = "കുടുംബസ്ഥാനമായ രണ്ടാം ഭാവത്തിന്റെ അധിപൻ ${lord2Pos.planet.malayalamName}, മാതൃകാരകനായ ചന്ദ്രൻ (${moon.rashi.malayalamName}), പിതൃകാരകനായ സൂര്യൻ (${sun.rashi.malayalamName}), സഹോദരകാരകനായ കുജൻ (${mars.rashi.malayalamName}) എന്നിവരുടെ സ്ഥിതി കുടുംബബന്ധങ്ങളെ നിർണ്ണയിക്കുന്നു.",
            detailedSections = listOf(
                "മാതാപിതാക്കളും പൂർവ്വിക അനുഗ്രഹവും (D12 Dwadashamsa)" to "സൂര്യന്റെയും ചന്ദ്രന്റെയും ഒമ്പത്-നാല് ഭാവങ്ങളുടെയും ബലം മാതാപിതാക്കളോടുള്ള സ്നേഹവും കുടുംബ ഉത്തരവാദിത്തങ്ങൾ നിറവേറ്റാനുള്ള താല്പര്യവും കാണിക്കുന്നു.",
                "സഹോദരങ്ങളും ഗാർഹിക അന്തരീക്ഷവും" to "മൂന്നാം ഭാവത്തിന്റെയും കുജന്റെയും സ്ഥിതി സഹോദരങ്ങളുമായുള്ള സഹകരണത്തെയും കുടുംബത്തിലെ ഐക്യത്തെയും സൂചിപ്പിക്കുന്നു."
            ),
            supportivePeriodsMal = "രണ്ടാം ഭാവാധിപന്റെയും നാലാം ഭാവാധിപന്റെയും ദശാകാലങ്ങളിൽ കുടുംബത്തിൽ മംഗളകർമ്മങ്ങൾ നടക്കും.",
            cautionPeriodsMal = "കുടുംബ ചർച്ചകളിൽ സൗമ്യമായ ഭാഷ ഉപയോഗിക്കുന്നത് ഐക്യം വർദ്ധിപ്പിക്കും."
        )

        // 11. Children Module (No medical fertility claims)
        reports["children"] = LifeDomainReport(
            domainId = "children",
            titleMalayalam = "സന്താനചിന്ത & സർഗ്ഗശേഷി (Children)",
            titleEnglish = "Children & Creative Legacy (Traditional Interpretation)",
            classification = if (jupiter.dignity != PlanetaryDignity.DEBILITATED) PeriodClassification.FAVORABLE else PeriodClassification.MIXED,
            keyHouses = listOf(5, 9),
            keyPlanets = listOf(lord5Pos.planet, Planet.JUPITER).distinct(),
            summaryMalayalam = "പഞ്ചമാധിപനായ (5-ാം ഭാവാധിപൻ) ${lord5Pos.planet.malayalamName} ${lord5Pos.houseFromLagna}-ാം ഭാവത്തിലും (${lord5Pos.rashi.malayalamName}), പുത്രകാരകനായ വ്യാഴം ${jupiter.houseFromLagna}-ാം ഭാവത്തിലും, സപ്താംശകത്തിൽ (D7) വ്യാഴം ${AstronomicalEphemerisEngine.calculateDivisionalRashi(jupiter.siderealLongitude, DivisionalChartType.D7).malayalamName} രാശിയിലും സ്ഥിതി ചെയ്യുന്നു.",
            detailedSections = listOf(
                "പരമ്പരാഗത ജ്യോതിഷ സൂചന" to "അഞ്ചാം ഭാവാധിപന്റെയും വ്യാഴത്തിന്റെയും സപ്താംശക (D7) രാശിയുടെയും ബലമനുസരിച്ച് സന്താനങ്ങളിലൂടെ സന്തോഷവും അഭിമാനവും ലഭിക്കാനുള്ള പരമ്പരാഗത സൂചന കാണുന്നു. കുട്ടികളുടെ വിദ്യാഭ്യാസത്തിനും ഭാവിക്കും ജാതകൻ വലിയ പ്രാധാന്യം നൽകും.",
                "ശ്രദ്ധിക്കുക (നിരാകരണം)" to "ഇത് പരമ്പരാഗത ജ്യോതിഷ ഗ്രന്ഥങ്ങളെ അടിസ്ഥാനമാക്കിയുള്ള പൊതുവായ ഭാവവിചിന്തനം മാത്രമാണ്; വൈദ്യശാസ്ത്രപരമായ കാര്യങ്ങൾക്ക് ഡോക്ടറുടെ ഉപദേശം മാത്രമാണ് ആധാരം."
            ),
            supportivePeriodsMal = "${lord5Pos.planet.malayalamName}, ${Planet.JUPITER.malayalamName} ദശാ-അപഹാര കാലങ്ങൾ.",
            cautionPeriodsMal = "കുട്ടികളുമായുള്ള ആശയവിനിമയത്തിൽ സൗഹൃദപരമായ സമീപനം സ്വീകരിക്കുക."
        )

        // 12. Health & Lifestyle Module (Strict compliance: general lifestyle tendencies only)
        reports["health"] = LifeDomainReport(
            domainId = "health",
            titleMalayalam = "ആരോഗ്യ-ജീവിതശൈലി സൂചനകൾ (Lifestyle Tendencies)",
            titleEnglish = "General Astrological Lifestyle & Wellness Tendencies",
            classification = PeriodClassification.FAVORABLE,
            keyHouses = listOf(1, 6),
            keyPlanets = listOf(lagnaRashi.lord, Planet.SUN, Planet.MOON).distinct(),
            summaryMalayalam = "${lagnaRashi.malayalamName} ലഗ്നത്തിന്റെയും (${lagnaRashi.elementMal} തത്വം) ചന്ദ്രരാശിയുടെയും അടിസ്ഥാനത്തിലുള്ള പൊതുവായ ദിനചര്യ-ജീവിതശൈലി പ്രവണതകൾ:",
            detailedSections = listOf(
                "ജീവിതശൈലി സന്തുലിതാവസ്ഥ" to "ലഗ്നാധിപനായ ${lagnaRashi.lord.malayalamName}-ന്റെ സ്വഭാവമനുസരിച്ച് കൃത്യസമയത്തുള്ള ഭക്ഷണം, മതിയായ ഉറക്കം, മിതമായ വ്യായാമം, യോഗ/ധ്യാനം എന്നിവ ശീലിക്കുന്നത് ശാരീരിക-മാനസിക ഉന്മേഷം നിലനിർത്താൻ സഹായിക്കും.",
                "പ്രധാന വൈദ്യശാസ്ത്ര നിരാകരണം (Medical Disclaimer)" to "ഈ വിഭാഗം ജ്യോതിഷപരമായ പൊതു ജീവിതശൈലി സൂചനകൾ മാത്രമാണ് നൽകുന്നത്. ഇത് രോഗനിർണ്ണയമോ ചികിത്സയോ അല്ല. ആരോഗ്യപ്രശ്നങ്ങൾക്ക് എപ്പോഴും അംഗീകൃത ഡോക്ടർമാരുടെ സേവനം തേടേണ്ടതാണ്; ഒരിക്കലും വൈദ്യോപദേശപ്രകാരമുള്ള മരുന്നുകൾ നിർത്തരുത്."
            ),
            supportivePeriodsMal = "ലഗ്നാധിപന്റെയും വ്യാഴത്തിന്റെയും അനുകൂല കാലഘട്ടങ്ങൾ.",
            cautionPeriodsMal = "അമിത ജോലിഭാരവും മാനസിക സമ്മർദ്ദവും ഒഴിവാക്കി വിശ്രമത്തിന് സമയം കണ്ടെത്തുക."
        )

        // 13. Spirituality Module
        reports["spirituality"] = LifeDomainReport(
            domainId = "spirituality",
            titleMalayalam = "ആത്മീയത, ധർമ്മം & ഉപാസന (Spirituality)",
            titleEnglish = "Spirituality, Dharma & Inner Growth",
            classification = PeriodClassification.FAVORABLE,
            keyHouses = listOf(9, 5, 12),
            keyPlanets = listOf(Planet.JUPITER, Planet.KETU, lord9Pos.planet).distinct(),
            summaryMalayalam = "ധർമ്മസ്ഥാനമായ ഒമ്പതാം ഭാവത്തിന്റെ അധിപൻ ${lord9Pos.planet.malayalamName}, ജ്ഞാനകാരകനായ വ്യാഴം, മോക്ഷകാരകനായ കേതു, വിംശാംശക ചക്രം (D20) എന്നിവ ആത്മീയ താല്പര്യങ്ങളെ വെളിപ്പെടുത്തുന്നു.",
            detailedSections = listOf(
                "ധാർമ്മിക ബോധവും ആത്മീയ പുരോഗതിയും" to "ജന്മനക്ഷത്രമായ ${janmaNakshatra.malayalamName} നക്ഷത്രത്തിന്റെ അധിദേവതയായ ${janmaNakshatra.deityMal}-ന്റെയും ഒമ്പതാം ഭാവാധിപന്റെയും സ്വാധീനം ജാതകന് ഈശ്വരവിശ്വാസം, സത്യസന്ധത, തീർത്ഥാടന താല്പര്യം, ദാർശനിക ചിന്ത എന്നിവ നൽകുന്നു."
            ),
            supportivePeriodsMal = "വ്യാഴം, കേതു, ഒമ്പതാം ഭാവാധിപൻ എന്നിവയുടെ ദശാകാലങ്ങൾ.",
            cautionPeriodsMal = "മനഃസമാധാനത്തിനായി ദിവസവും അല്പസമയം പ്രാർത്ഥനയ്ക്കോ ധ്യാനത്തിനോ മാറ്റിവെക്കുന്നത് ഉത്തമം."
        )

        return reports
    }

    fun generateLifeTimeline(
        birthData: BirthData,
        mahadashas: List<MahadashaPeriod>,
        yogas: List<YogaResult>
    ): List<LifeTimelineStage> {
        val stages = listOf(
            "0–10" to "ബാല്യകാലവും പ്രാഥമിക വിദ്യാഭ്യാസവും (0–10 വയസ്സ്)",
            "10–20" to "കൗമാരവും വിദ്യാഭ്യാസ അടിത്തറയും (10–20 വയസ്സ്)",
            "20–30" to "ഉന്നത പഠനം, കരിയർ ആരംഭം & യൗവനം (20–30 വയസ്സ്)",
            "30–40" to "തൊഴിൽ സ്ഥിരത, കുടുംബം & സാമ്പത്തിക വളർച്ച (30–40 വയസ്സ്)",
            "40–50" to "നേതൃത്വം, ആസ്തി വികസനം & ഉത്തരവാദിത്തങ്ങൾ (40–50 വയസ്സ്)",
            "50–60" to "അനുഭവസമ്പത്ത്, സാമൂഹിക ആദരവ് & പക്വത (50–60 വയസ്സ്)",
            "60+" to "സമാധാനപൂർണ്ണമായ വിശ്രമജീവിതവും ആത്മീയതയും (60+ വയസ്സ്)"
        )
        val yogaNames = yogas.take(2).joinToString(", ") { it.nameMalayalam }.ifEmpty { "ലഗ്ന-രാശി ബലം" }
        val millisPerYear = 365.2425 * 24.0 * 3600.0 * 1000.0
        val birthEpoch = mahadashas.firstOrNull()?.startEpochMillis ?: System.currentTimeMillis()

        return stages.mapIndexed { idx, (rangeStr, titleMal) ->
            val midAgeYears = idx * 10 + 5
            val targetEpoch = birthEpoch + (midAgeYears * millisPerYear).toLong()
            val activeMaha = mahadashas.find { targetEpoch in it.startEpochMillis..it.endEpochMillis }
                ?: mahadashas.getOrElse(idx.coerceAtMost(mahadashas.lastIndex)) { mahadashas.first() }
            val activeAntar = activeMaha.antardashas.find { targetEpoch in it.startEpochMillis..it.endEpochMillis }
                ?: activeMaha.antardashas.firstOrNull()

            val mahaName = activeMaha.lord.malayalamName
            val antarName = activeAntar?.antardashaLord?.malayalamName ?: activeMaha.lord.malayalamName

            LifeTimelineStage(
                ageRange = rangeStr,
                titleMalayalam = titleMal,
                dominantMahadashaMal = "$mahaName മഹാദശ (${activeMaha.startDateFormatted} - ${activeMaha.endDateFormatted})",
                dominantAntardashaMal = "$antarName അപഹാര ഘട്ടം",
                majorTransitNoteMal = if (idx == 2 || idx == 5) "വ്യാഴവട്ട പരിവർത്തനങ്ങളും ശനിയുടെ പ്രധാന രാശിമാറ്റങ്ങളും ഈ ദശകത്തിൽ നിർണ്ണായക വഴിത്തിരിവുകൾ സൃഷ്ടിക്കുന്നു." else "വ്യാഴത്തിന്റെയും ശനിയുടെയും ഭാവസഞ്ചാരം ഈ കാലഘട്ടത്തിലെ കർമ്മഫലങ്ങളെ രൂപപ്പെടുത്തുന്നു.",
                activeYogasMal = yogaNames,
                classification = activeMaha.classification,
                detailedMalayalamNarrative = "$rangeStr വയസ്സ് വരെയുള്ള കാലഘട്ടത്തിൽ പ്രധാനമായും $mahaName മഹാദശയുടെ (${activeMaha.classification.malayalamLabel}) സ്വാധീനമാണ് ജാതകത്തിൽ പ്രവർത്തിക്കുന്നത്. ${activeMaha.lordPlacementSummaryMal}. ഈ ഘട്ടത്തിൽ ${when (idx) {
                    0 -> "കുടുംബാന്തരീക്ഷം, പ്രാഥമിക ആരോഗ്യ-പഠന ശീലങ്ങൾ എന്നിവ രൂപപ്പെടുന്നു."
                    1 -> "വിദ്യാഭ്യാസ താല്പര്യങ്ങൾ, മത്സരബുദ്ധി, കരിയർ ദിശാബോധം എന്നിവ വികസിക്കുന്നു."
                    2 -> "ഉന്നത വിദ്യാഭ്യാസം, ആദ്യ തൊഴിൽ അവസരങ്ങൾ, മത്സരപ്പരീക്ഷകൾ, വിവാഹ ആലോചനകൾ എന്നിവ സജീവമാകുന്നു."
                    3 -> "കരിയറിലെ സ്ഥിരത, സ്ഥാനക്കയറ്റം, ഗൃഹനിർമ്മാണം, കുടുംബ ഉത്തരവാദിത്തങ്ങൾ എന്നിവയ്ക്ക് പ്രാധാന്യം ലഭിക്കുന്നു."
                    4 -> "തൊഴിൽരംഗത്ത് നേതൃപദവികൾ, സാമ്പത്തിക നിക്ഷേപങ്ങൾ, മക്കളുടെ വിദ്യാഭ്യാസ പുരോഗതി എന്നിവ പ്രധാന വിഷയങ്ങളാകുന്നു."
                    5 -> "ദീർഘകാല കർമ്മഫലങ്ങളുടെ സഫലീകരണം, കുടുംബത്തിലെ മംഗളകർമ്മങ്ങൾ, സാമൂഹിക അംഗീകാരം എന്നിവ അനുഭവപ്പെടുന്നു."
                    else -> "ആത്മീയ ചിന്ത, കുടുംബാംഗങ്ങളോടൊപ്പമുള്ള സമാധാന ജീവിതം, ഉപദേശക സ്ഥാനം എന്നിവയ്ക്ക് മുൻതൂക്കം ലഭിക്കുന്നു."
                }}"
            )
        }
    }

    fun generatePeriodicHoroscope(
        lagnaRashi: Rashi,
        chandraRashi: Rashi,
        janmaNakshatra: Nakshatra,
        currentMaha: MahadashaPeriod,
        currentAntar: AntardashaPeriod,
        currentPraty: PratyantardashaPeriod,
        transitReport: TransitAnalysisReport
    ): PeriodicHoroscopeReport {
        return PeriodicHoroscopeReport(
            dailyMal = "ഇന്നത്തെ വ്യക്തിഗത ഫലം (${janmaNakshatra.malayalamName} നക്ഷത്രം, ${chandraRashi.malayalamName} കൂറ്, ${lagnaRashi.malayalamName} ലഗ്നം): നിലവിൽ ${currentMaha.lord.malayalamName} ദശയിൽ ${currentAntar.antardashaLord.malayalamName} അപഹാരവും ${currentPraty.pratyantardashaLord.malayalamName} പ്രത്യന്തരദശയും നടക്കുന്നു. തൊഴിൽപരമായ കാര്യങ്ങളിൽ ആസൂത്രണത്തോടെ നീങ്ങുന്നത് ഫലം നൽകും. ആശയവിനിമയത്തിൽ സൗമ്യത പാലിക്കുക.",
            weeklyMal = "ഈ ആഴ്ചയിലെ ഫലം: ${chandraRashi.malayalamName} രാശിക്കാർക്ക് ${currentAntar.antardashaLord.malayalamName} അപഹാര സ്വാധീനത്താൽ കരിയറിലും പഠനത്തിലും പുതിയ അവസരങ്ങൾ തെളിയും. വാരാന്ത്യത്തിൽ കുടുംബാവശ്യങ്ങൾക്കായി സമയം മാറ്റിവെക്കും. സാമ്പത്തിക ഇടപാടുകളിൽ മിതത്വം പാലിക്കുക.",
            monthlyMal = "ഈ മാസത്തെ ഫലം: സൂര്യന്റെയും ചന്ദ്രന്റെയും ബുധ-ശുക്രന്മാരുടെയും രാശിമാറ്റം ${lagnaRashi.malayalamName} ലഗ്നക്കാർക്ക് തൊഴിൽരംഗത്ത് ക്രിയാത്മകമായ ചലനങ്ങൾ നൽകും. മത്സരപ്പരീക്ഷകൾക്ക് തയ്യാറെടുക്കുന്നവർക്ക് ഏകാഗ്രത വർദ്ധിക്കുന്ന മാസമാണ്.",
            yearlyMal = "ഈ വർഷത്തെ സമഗ്ര ഫലം: ${currentMaha.lord.malayalamName} മഹാദശയും വ്യാഴ-ശനി-രാഹു ഗോചര നിലയും ചേർന്ന് ഈ വർഷം കരിയർ, സാമ്പത്തിക സമ്പാദ്യം, കുടുംബ കാര്യങ്ങൾ എന്നിവയിൽ പ്രധാന തീരുമാനങ്ങൾക്ക് വഴിയൊരുക്കും. ${transitReport.dashaTransitCombinedMal}"
        )
    }

    fun generateMuhurthamGuide(panchanga: PanchangaData): List<MuhurthamCategoryInfo> {
        val isVishti = panchanga.karana.nameEnglish == "Vishti"
        val isRiktaTithi = (panchanga.tithiNumber % 15) in listOf(4, 9, 14)
        val baseClass = if (isVishti || isRiktaTithi) PeriodClassification.MIXED else PeriodClassification.FAVORABLE

        val items = listOf(
            Triple("marriage", "വിവാഹ മുഹൂർത്തം (Marriage Muhurtham)", "രോഹിണി, മകയിരം, മകം, ഉത്രം, അത്തം, ചോതി, അനിഴം, മൂലം, ഉത്രാടം, ഉത്രട്ടാതി, രേവതി"),
            Triple("grihapravesham", "ഗൃഹപ്രവേശം & ഗൃഹാരംഭം (Housewarming)", "രോഹിണി, മകയിരം, ഉത്രം, അത്തം, ചിത്തിര, അനിഴം, ഉത്രാടം, തിരുവോണം, അവിട്ടം, ഉത്രട്ടാതി, രേവതി"),
            Triple("property", "വസ്തു വാങ്ങൽ / രജിസ്ട്രേഷൻ (Property)", "അശ്വതി, രോഹിണി, മകയിരം, പുണർതം, പൂയം, ഉത്രം, അത്തം, ചോതി, അനിഴം, തിരുവോണം"),
            Triple("travel", "യാത്രാരംഭ മുഹൂർത്തം (Travel)", "അശ്വതി, മകയിരം, പുണർതം, പൂയം, അത്തം, അനിഴം, തിരുവോണം, അവിട്ടം, രേവതി"),
            Triple("business", "വ്യാപാര / സംരംഭ ആരംഭം (Business Opening)", "അശ്വതി, രോഹിണി, പുണർതം, പൂയം, ഉത്രം, അത്തം, ചിത്തിര, ചോതി, അനിഴം, ഉത്രാടം, തിരുവോണം, രേവതി"),
            Triple("education", "വിദ്യാരംഭം & പഠന തുടക്കം (Education)", "അശ്വതി, രോഹിണി, മകയിരം, പുണർതം, പൂയം, ഉത്രം, അത്തം, ചിത്തിര, ചോതി, തിരുവോണം, രേവതി"),
            Triple("beginnings", "പ്രധാന ശുഭകാര്യ ആരംഭങ്ങൾ (Important Beginnings)", "രോഹിണി, പുണർതം, പൂയം, ഉത്രം, അത്തം, അനിഴം, ഉത്രാടം, തിരുവോണം, ഉത്രട്ടാതി, രേവതി")
        )

        return items.map { (id, titleMal, favNaks) ->
            MuhurthamCategoryInfo(
                id = id,
                titleMalayalam = titleMal,
                titleEnglish = titleMal.substringAfter("(").substringBefore(")"),
                currentDaySuitability = baseClass,
                favorableNakshatrasMal = favNaks,
                favorableTithisAndKaranasMal = "ദ്വിതീയ, തൃതീയ, പഞ്ചമി, സപ്തമി, ദശമി, ഏകാദശി, ത്രയോദശി തിഥികളും; ബവ, ബാലവ, കൗലവ, തൈതില, ഗരജ, വണിജ കരണങ്ങളും ശുഭം (വിഷ്ടികരണവും രാഹുകാലവും ഒഴിവാക്കുക).",
                currentPanchangaAssessmentMal = "തിരഞ്ഞെടുത്ത സമയം: ${panchanga.varaMalayalam}, ${panchanga.tithiMalayalam}, ${panchanga.nakshatra.malayalamName} നക്ഷത്രം, ${panchanga.karana.nameMalayalam} കരണം. രാഹുകാലം (${panchanga.rahuKalamFormatted}) ഒഴിവാക്കി അഭിജിത്ത് മുഹൂർത്തമോ (${panchanga.abhijitMuhurthamFormatted}) ശുഭലഗ്നമോ സ്വീകരിക്കുക.",
                traditionalGuidelinesMal = "ശ്രദ്ധിക്കുക: മുഹൂർത്ത തിരഞ്ഞെടുപ്പ് പരമ്പരാഗത പഞ്ചാംഗ ശുദ്ധിയെ അടിസ്ഥാനമാക്കിയുള്ളതാണ്; ഇത് ഫലങ്ങളുടെ ഉറപ്പായ വാഗ്ദാനമല്ല."
            )
        }
    }
}
