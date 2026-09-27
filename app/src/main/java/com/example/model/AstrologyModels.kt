package com.example.model

enum class Gender(val malayalam: String, val english: String) {
    MALE("പുരുഷൻ", "Male"),
    FEMALE("സ്ത്രീ", "Female"),
    OTHER("മറ്റുള്ളവ", "Other")
}

enum class Planet(
    val malayalamName: String,
    val englishName: String,
    val shortCodeMal: String,
    val dashaYears: Int,
    val exaltationSignIndex: Int, // 0..11 (Mesham=0)
    val exaltationDegree: Double,
    val debilitationSignIndex: Int,
    val moolatrikonaSignIndex: Int,
    val ownSignIndices: List<Int>,
    val naisargikaBala: Double,
    val isBeneficNatural: Boolean
) {
    SUN("സൂര്യൻ (രവി)", "Sun", "ര", 6, 0, 10.0, 6, 4, listOf(4), 60.0, false),
    MOON("ചന്ദ്രൻ", "Moon", "ച", 10, 1, 3.0, 7, 1, listOf(3), 51.43, true),
    MARS("ചൊവ്വ (കുജൻ)", "Mars", "കു", 7, 9, 28.0, 3, 0, listOf(0, 7), 17.14, false),
    MERCURY("ബുധൻ", "Mercury", "ബു", 17, 5, 15.0, 11, 5, listOf(2, 5), 25.71, true),
    JUPITER("വ്യാഴം (ഗുരു)", "Jupiter", "ഗു", 16, 3, 5.0, 9, 8, listOf(8, 11), 34.29, true),
    VENUS("ശുക്രൻ", "Venus", "ശു", 20, 11, 27.0, 5, 6, listOf(1, 6), 42.86, true),
    SATURN("ശനി (മന്ദൻ)", "Saturn", "ശ", 19, 6, 20.0, 0, 10, listOf(9, 10), 8.57, false),
    RAHU("രാഹു (സർപ്പം)", "Rahu", "രാ", 18, 1, 20.0, 7, 10, listOf(10), 15.0, false),
    KETU("കേതു (ശിഖി)", "Ketu", "കേ", 7, 7, 20.0, 1, 8, listOf(7), 15.0, false),
    MANDI("മാന്ദി (ഗുളികൻ)", "Mandi", "മാ", 0, -1, 0.0, -1, -1, emptyList(), 5.0, false);

    companion object {
        val nineGrahas = listOf(SUN, MOON, MARS, MERCURY, JUPITER, VENUS, SATURN, RAHU, KETU)
        val vimshottariOrder = listOf(KETU, VENUS, SUN, MOON, MARS, RAHU, JUPITER, SATURN, MERCURY)
    }
}

enum class PlanetaryDignity(val malayalam: String, val english: String, val strengthFactor: Double) {
    EXALTED("ഉച്ചം (Exalted)", "Exalted", 1.0),
    MOOLATRIKONA("മൂലത്രികോണം (Moolatrikona)", "Moolatrikona", 0.88),
    OWN_SIGN("സ്വക്ഷേത്രം (Own Sign)", "Own Sign", 0.80),
    GREAT_FRIEND("അധിമിത്രക്ഷേത്രം", "Great Friend Sign", 0.70),
    FRIEND_SIGN("മിത്രക്ഷേത്രം (Friendly Sign)", "Friendly Sign", 0.62),
    NEUTRAL_SIGN("സമക്ഷേത്രം (Neutral Sign)", "Neutral Sign", 0.50),
    ENEMY_SIGN("ശത്രുക്ഷേത്രം (Enemy Sign)", "Enemy Sign", 0.32),
    DEBILITATED("നീചം (Debilitated)", "Debilitated", 0.15)
}

enum class Rashi(
    val index: Int, // 0..11
    val malayalamName: String,
    val englishName: String,
    val sanskritName: String,
    val lord: Planet,
    val elementMal: String,
    val modalityMal: String, // ചര, സ്ഥിര, ഉഭയ
    val isOdd: Boolean
) {
    MESHAM(0, "മേടം", "Aries", "Mesha", Planet.MARS, "അഗ്നി (Fire)", "ചരരാശി (Movable)", true),
    IDAVAM(1, "ഇടവം", "Taurus", "Vrishabha", Planet.VENUS, "ഭൂമി (Earth)", "സ്ഥിരരാശി (Fixed)", false),
    MITHUNAM(2, "മിഥുനം", "Gemini", "Mithuna", Planet.MERCURY, "വായു (Air)", "ഉഭയരാശി (Dual)", true),
    KARKADAKAM(3, "കർക്കടകം", "Cancer", "Karkataka", Planet.MOON, "ജലം (Water)", "ചരരാശി (Movable)", false),
    CHINGAM(4, "ചിങ്ങം", "Leo", "Simha", Planet.SUN, "അഗ്നി (Fire)", "സ്ഥിരരാശി (Fixed)", true),
    KANNI(5, "കന്നി", "Virgo", "Kanya", Planet.MERCURY, "ഭൂമി (Earth)", "ഉഭയരാശി (Dual)", false),
    THULAM(6, "തുലാം", "Libra", "Tula", Planet.VENUS, "വായു (Air)", "ചരരാശി (Movable)", true),
    VRISCHIKAM(7, "വൃശ്ചികം", "Scorpio", "Vrischika", Planet.MARS, "ജലം (Water)", "സ്ഥിരരാശി (Fixed)", false),
    DHANU(8, "ധനു", "Sagittarius", "Dhanus", Planet.JUPITER, "അഗ്നി (Fire)", "ഉഭയരാശി (Dual)", true),
    MAKARAM(9, "മകരം", "Capricorn", "Makara", Planet.SATURN, "ഭൂമി (Earth)", "ചരരാശി (Movable)", false),
    KUMBHAM(10, "കുംഭം", "Aquarius", "Kumbha", Planet.SATURN, "വായു (Air)", "സ്ഥിരരാശി (Fixed)", true),
    MEENAM(11, "മീനം", "Pisces", "Meena", Planet.JUPITER, "ജലം (Water)", "ഉഭയരാശി (Dual)", false);

    companion object {
        fun fromIndex(idx: Int): Rashi {
            val normalized = ((idx % 12) + 12) % 12
            return entries[normalized]
        }
    }
}

enum class Nakshatra(
    val index: Int, // 0..26
    val malayalamName: String,
    val englishName: String,
    val lord: Planet,
    val deityMal: String,
    val symbolMal: String,
    val ganaMal: String, // ദേവഗണം, മനുഷ്യഗണം, അസുരഗണം
    val yoniMal: String, // പുരുഷയോനി / സ്ത്രീയോനി
    val animalMal: String,
    val nadiMal: String, // ആദി, മദ്ധ്യ, അന്ത്യ
    val elementMal: String,
    val rajjuMal: String, // ശിരസ്സ്, കണ്ഠം, ഉദരം, തുട, പാദം
    val vrikshaMal: String,
    val pakshiMal: String,
    val characteristicsMal: String
) {
    ASWATHI(0, "അശ്വതി", "Ashwini", Planet.KETU, "അശ്വിനീദേവകൾ", "കുതിരയുടെ ശിരസ്സ്", "ദേവഗണം", "പുരുഷയോനി", "കുതിര", "ആദിനാഡി", "ഭൂമി", "പാദരജ്ജു", "കാഞ്ഞിരം", "പുള്ള്", "ചുറുചുറുക്ക്, നേതൃപാടവം, വേഗത്തിൽ തീരുമാനമെടുക്കാനുള്ള കഴിവ്, സ്വതന്ത്ര ചിന്താഗതി, സത്യസന്ധത."),
    BHARANI(1, "ഭരണി", "Bharani", Planet.VENUS, "യമധർമ്മൻ", "യോനി / കുംഭം", "മനുഷ്യഗണം", "പുരുഷയോനി", "ആന", "മദ്ധ്യനാഡി", "ഭൂമി", "തുടരജ്ജു", "നെല്ലി", "കാക്ക", "ധൈര്യം, കലാപരമായ അഭിരുചി, നിശ്ചയദാർഢ്യം, ഉത്തരവാദിത്തബോധം, ആത്മവിശ്വാസം."),
    KARTHIKA(2, "കാർത്തിക", "Krittika", Planet.SUN, "അഗ്നിദേവൻ", "അഗ്നിജ്വാല / കത്തി", "അസുരഗണം", "സ്ത്രീയോനി", "ആട്", "അന്ത്യനാഡി", "ഭൂമി", "ഉദരരജ്ജു", "അത്തി", "മയിൽ", "അഭിമാനബോധം, ഭരണശേഷി, വ്യക്തമായ കാഴ്ചപ്പാട്, തേജസ്സ്, സത്യനിഷ്ഠ."),
    ROHINI(3, "രോഹിണി", "Rohini", Planet.MOON, "ബ്രഹ്മാവ്", "രഥം / വണ്ടി", "മനുഷ്യഗണം", "പുരുഷയോനി", "സർപ്പം", "അന്ത്യനാഡി", "ഭൂമി", "കണ്ഠരജ്ജു", "ഞാവൽ", "മൂങ്ങ", "സൗമ്യത, ആകർഷണീയമായ വ്യക്തിത്വം, കുടുംബസ്നേഹം, സ്ഥിരത, കലാവാസന."),
    MAKAYIRAM(4, "മകയിരം", "Mrigashirsha", Planet.MARS, "ചന്ദ്രദേവൻ", "മാൻ തല", "ദേവഗണം", "സ്ത്രീയോനി", "സർപ്പം", "മദ്ധ്യനാഡി", "ഭൂമി", "ശിരോരജ്ജു", "കരിങ്ങാലി", "കോഴി", "അന്വേഷണത്വര, ബുദ്ധിസാമർത്ഥ്യം, യാത്രാതാല്പര്യം, ഉത്സാഹം, സംഭാഷണചാതുര്യം."),
    THIRUVATHIRA(5, "തിരുവാതിര", "Ardra", Planet.RAHU, "രുദ്രൻ (ശിവൻ)", "കണ്ണുനീർത്തുള്ളി / രത്നം", "മനുഷ്യഗണം", "സ്ത്രീയോനി", "ശ്വാനൻ", "ആദിനാഡി", "ജലം", "കണ്ഠരജ്ജു", "കരിമരം", "ചെമ്പോത്ത്", "तीവ്രമായ ബുദ്ധിശക്തി, ഗവേഷണ താല്പര്യം, പ്രതിസന്ധികളെ അതിജീവിക്കാനുള്ള കരുത്ത്."),
    PUNARTHAM(6, "പുണർതം", "Punarvasu", Planet.JUPITER, "അദിതി", "അമ്പും ആവനാഴിയും", "ദേവഗണം", "സ്ത്രീയോനി", "പൂച്ച", "ആദിനാഡി", "ജലം", "ഉദരരജ്ജു", "മുള", "അരയന്നം", "ധാർമ്മികബോധം, ശുഭാപ്തിവിശ്വാസം, പാണ്ഡിത്യം, അധ്യാപന-ഉപദേശക കഴിവ്."),
    POOYAM(7, "പൂയം", "Pushya", Planet.SATURN, "ബൃഹസ്പതി", "താമര / പശുവിൻ അകിട്", "ദേവഗണം", "പുരുഷയോനി", "ആട്", "മദ്ധ്യനാഡി", "ജലം", "തുടരജ്ജു", "അരയാൽ", "നീർക്കാക്ക", "ക്ഷമ, പരോപകാര താല്പര്യം, പാരമ്പര്യബോധം, സ്ഥിരതയുള്ള തൊഴിൽ മനോഭാവം."),
    AYILYAM(8, "ആയില്യം", "Ashlesha", Planet.MERCURY, "നാഗദേവതകൾ", "ചുരുണ്ട സർപ്പം", "അസുരഗണം", "പുരുഷയോനി", "പൂച്ച", "അന്ത്യനാഡി", "ജലം", "പാദരജ്ജു", "പുന്ന", "ചകോരം", "സൂക്ഷ്മനിരീക്ഷണം, ആഴത്തിലുള്ള ചിന്ത, നയതന്ത്രജ്ഞത, ആത്മീയ-ശാസ്ത്ര അഭിരുചി."),
    MAKAM(9, "മകം", "Magha", Planet.KETU, "പിതൃക്കൾ", "രാജസിംഹാസനം", "അസുരഗണം", "പുരുഷയോനി", "എലി", "അന്ത്യനാഡി", "ജലം", "പാദരജ്ജു", "പേരാൽ", "കഴുകൻ", "കുലീനത, നേതൃഗുണം, പാരമ്പര്യത്തോടുള്ള ആദരവ്, ഉയർന്ന പദവികളോടുള്ള താല്പര്യം."),
    POORAM(10, "പൂരം", "Purva Phalguni", Planet.VENUS, "ഭഗൻ (ആദിത്യൻ)", "കട്ടിലിന്റെ മുൻകാലുകൾ", "മനുഷ്യഗണം", "സ്ത്രീയോനി", "എലി", "മദ്ധ്യനാഡി", "ജലം", "തുടരജ്ജു", "പ്ലാശ്", "പെൺകഴുകൻ", "സഹൃദയത്വം, കലാപ്രേമം, സാമൂഹിക സ്വീകാര്യത, സൗഹൃദമനോഭാവം."),
    UTHRAM(11, "ഉത്രം", "Uttara Phalguni", Planet.SUN, "ആര്യമാവ്", "കട്ടിലിന്റെ പിൻകാലുകൾ", "മനുഷ്യഗണം", "പുരുഷയോനി", "പശു", "ആദിനാഡി", "അഗ്നി", "ഉദരരജ്ജു", "ഇത്തി", "വണ്ടു", "വിശ്വസ്തത, സ്ഥിരത, സേവനമനോഭാവം, സംഘാടകശേഷി, സത്യസന്ധമായ ഇടപെടൽ."),
    ATHAM(12, "അത്തം", "Hasta", Planet.MOON, "സവിാവ് (സൂര്യൻ)", "കൈപ്പത്തി", "ദേവഗണം", "സ്ത്രീയോനി", "എരുമ", "ആദിനാഡി", "അഗ്നി", "കണ്ഠരജ്ജു", "അമ്പഴം", "പരുന്ത്", "കരവിരുതുകൾ, വ്യാപാര-സാങ്കേതിക നൈപുണ്യം, വിവേകം, സൗമ്യമായ പെരുമാറ്റം."),
    CHITHIRA(13, "ചിത്തിര", "Chitra", Planet.MARS, "ത്വഷ്ടാവ് (വിശ്വകർമ്മാവ്)", "തിളങ്ങുന്ന രത്നം / മുത്ത്", "അസുരഗണം", "സ്ത്രീയോനി", "കടുവ", "മദ്ധ്യനാഡി", "അഗ്നി", "ശിരോരജ്ജു", "കൂവളം", "മരംകൊത്തി", "ശില്പ-നിർമ്മാണ-എഞ്ചിനീയറിംഗ് വൈദഗ്ധ്യം, ആകർഷകമായ ശൈലി, ക്രമബദ്ധമായ പ്രവർത്തനം."),
    CHOTHI(14, "ചോതി", "Swati", Planet.RAHU, "വായുദേവൻ", "തളിരില / പവിഴം", "ദേവഗണം", "പുരുഷയോനി", "എരുമ", "അന്ത്യനാഡി", "അഗ്നി", "കണ്ഠരജ്ജു", "നീർമരുത്", "തേനീച്ച", "സ്വാതന്ത്ര്യബോധം, നയചാതുര്യം, വാണിജ്യ വിജയം, സമാധാനപ്രിയത."),
    VISHAKHAM(15, "വിശാഖം", "Vishakha", Planet.JUPITER, "ഇന്ദ്രാഗ്നികൾ", "തോരണം / കുശവന്റെ ചക്രം", "അസുരഗണം", "സ്ത്രീയോനി", "കടുവ", "അന്ത്യനാഡി", "അഗ്നി", "ഉദരരജ്ജു", "വയ്യങ്കതവ്", "ചെങ്കൊക്ക്", "ലക്ഷ്യബോധം, കഠിനാധ്വാനം, ആശയവിനിമയ മികവ്, നീതിബോധം."),
    ANIZHAM(16, "അനിഴം", "Anuradha", Planet.SATURN, "മിത്രൻ", "താമരപ്പൂവ്", "ദേവഗണം", "സ്ത്രീയോനി", "മാൻ", "മദ്ധ്യനാഡി", "അഗ്നി", "തുടരജ്ജു", "ഇലഞ്ഞി", "വാനമ്പാടി", "സൗഹൃദനിഷ്ഠ, സംഘടനാപാടവം, വിദേശബന്ധങ്ങൾ, സഹനശക്തി."),
    THRIKKETTA(17, "തൃക്കേട്ട", "Jyeshtha", Planet.MERCURY, "ദേവേന്ദ്രൻ", "കുട / കുണ്ഡലം", "അസുരഗണം", "പുരുഷയോനി", "മാൻ", "ആദിനാഡി", "വായു", "പാദരജ്ജു", "വെട്ടി", "ചക്രവാകം", "പക്വത, സംരക്ഷണമനോഭാവം, ഭരണതന്ത്രജ്ഞത, ആഴത്തിലുള്ള അറിവ്."),
    MOOLAM(18, "മൂലം", "Mula", Planet.KETU, "നിരൃതി", "വേരുകളുടെ കൂട്ടം", "അസുരഗണം", "പുരുഷയോനി", "ശ്വാനൻ", "ആദിനാഡി", "വായു", "പാദരജ്ജു", "പയിന", "ചെമ്പോത്ത്", "അടിസ്ഥാന തത്വങ്ങളിലേക്കുള്ള അന്വേഷണം, ആത്മീയത, സത്യസന്ധമായ നിലപാടുകൾ."),
    POORADAM(19, "പൂരാടം", "Purva Ashadha", Planet.VENUS, "ജലദേവത (അപഃ)", "മുറം / ആനക്കൊമ്പ്", "മനുഷ്യഗണം", "പുരുഷയോനി", "വാനരൻ", "മദ്ധ്യനാഡി", "വായു", "തുടരജ്ജു", "വഞ്ചി", "കൗജം", "ആത്മവിശ്വാസം, സംവാദശേഷി, കലാപരമായ ഔന്നത്യം, ഉത്സാഹം."),
    UTHRADAM(20, "ഉത്രാടം", "Uttara Ashadha", Planet.SUN, "വിശ്വദേവകൾ", "ആനക്കൊമ്പ് / പലക", "മനുഷ്യഗണം", "പുരുഷയോനി", "കീരി", "അന്ത്യനാഡി", "വായു", "ഉദരരജ്ജു", "പ്ലാവ്", "മണ്ണാത്തിപ്പുള്ള്", "സ്ഥിരോത്സാഹം, മാന്യത, നേതൃത്വം, ഉത്തരവാദിത്തപൂർണ്ണമായ ജീവിതശൈലി."),
    THIRUVONAM(21, "തിരുവോണം", "Shravana", Planet.MOON, "മഹാവിഷ്ണു", "മൂന്ന് കാൽപ്പാടുകൾ / ചെവി", "ദേവഗണം", "പുരുഷയോനി", "വാനരൻ", "അന്ത്യനാഡി", "വായു", "കണ്ഠരജ്ജു", "എരുക്ക്", "കോഴി", "ശ്രവണ-പഠന മികവ്, സംസ്കാരം, കരുണ, ജനസമ്മതി, കുടുംബഭദ്രത."),
    AVITTAM(22, "അവിട്ടം", "Dhanishta", Planet.MARS, "അഷ്ടവസുക്കൾ", "മൃദംഗം / തുടടി", "അസുരഗണം", "സ്ത്രീയോനി", "സിംഹം", "മദ്ധ്യനാഡി", "വായു", "ശിരോരജ്ജു", "വന്നി", "മയിൽ", "സംഗീത-കലാ അഭിരുചി, കർമ്മനിരത, സമ്പാദ്യശീലം, ധീരത."),
    CHATHAYAM(23, "ചതയം", "Shatabhisha", Planet.RAHU, "വരുണൻ", "വൃത്തം / നൂറു ഭിഷഗ്വരന്മാർ", "അസുരഗണം", "സ്ത്രീയോനി", "കുതിര", "ആദിനാഡി", "ആകാശം", "കണ്ഠരജ്ജു", "കടമ്പ്", "മയിൽ", "ശാസ്ത്ര-വൈദ്യ-സാങ്കേതിക ബുദ്ധി, സ്വതന്ത്ര ചിന്ത, രഹസ്യങ്ങൾ ഗ്രഹിക്കാനുള്ള കഴിവ്."),
    POORURUTTATHI(24, "പൂരൂരുട്ടാതി", "Purva Bhadrapada", Planet.JUPITER, "അജൈകപാദ്", "വാൾ / കട്ടിലിന്റെ മുൻഭാഗം", "മനുഷ്യഗണം", "പുരുഷയോനി", "സിംഹം", "ആദിനാഡി", "ആകാശം", "ഉദരരജ്ജു", "തേന്മാവ്", "മൂങ്ങ", "ദാർശനിക ചിന്ത, ആദർശനിഷ്ഠ, സാമ്പത്തിക അച്ചടക്കം, അധ്യാപന മികവ്."),
    UTHRATTATHI(25, "ഉത്രട്ടാതി", "Uttara Bhadrapada", Planet.SATURN, "അഹിർബുധ്ന്യൻ", "ഇരട്ടകൾ / കട്ടിലിന്റെ പിൻഭാഗം", "മനുഷ്യഗണം", "സ്ത്രീയോനി", "പശു", "മദ്ധ്യനാഡി", "ആകാശം", "തുടരജ്ജു", "കരിമ്പന", "കോട്ടാൻ", "ഗാംഭീര്യം, ആത്മസംയമനം, ജ്ഞാനം, ദീർഘവീക്ഷണം, കുടുംബസ്നേഹം."),
    REVATHI(26, "രേവതി", "Revati", Planet.MERCURY, "പൂഷാവ്", "മത്സ്യം / മദ്ദളം", "ദേവഗണം", "സ്ത്രീയോനി", "ആന", "അന്ത്യനാഡി", "ആകാശം", "പാദരജ്ജു", "ഇരിപ്പ", "പ്രാവ്", "ദയാശീലം, സൃഷ്ടിപരമായ പ്രതിഭ, ആത്മീയത, വിദേശ-സമുദ്ര ബന്ധങ്ങൾ.");

    companion object {
        fun fromIndex(idx: Int): Nakshatra {
            val normalized = ((idx % 27) + 27) % 27
            return entries[normalized]
        }
    }
}

enum class DivisionalChartType(
    val code: String,
    val division: Int,
    val malayalamName: String,
    val englishName: String,
    val significationMal: String
) {
    D1("D1", 1, "രാശിചക്രം (Rashi)", "Rashi Chart", "ശരീരം, പൊതുവായ ജീവിതഗതി, വ്യക്തിത്വം"),
    D2("D2", 2, "ഹോര (Hora)", "Hora Chart", "ധനസ്ഥിതി, സമ്പത്ത്, കുടുംബ വരുമാനം"),
    D3("D3", 3, "ദ്രേക്കാണം (Drekkana)", "Drekkana Chart", "സഹോദരങ്ങൾ, ധൈര്യം, പരിശ്രമം"),
    D4("D4", 4, "ചതുർത്ഥാംശം (Chaturthamsa)", "Chaturthamsa", "ഭാഗ്യം, ഭൂമി, ഗൃഹം, സ്ഥിരസ്വത്തുക്കൾ"),
    D7("D7", 7, "സപ്താംശം (Saptamsa)", "Saptamsa", "സന്താനങ്ങൾ, സർഗ്ഗശേഷി, വംശപരമ്പര"),
    D9("D9", 9, "നവാംശകം (Navamsa)", "Navamsa", "വിവാഹം, പങ്കാളി, ധർമ്മം, ഗ്രഹങ്ങളുടെ അന്തർബലം"),
    D10("D10", 10, "ദശാംശം (Dasamsa)", "Dasamsa", "തൊഴിൽ, കരിയർ, അധികാരം, കർമ്മവിജയം"),
    D12("D12", 12, "ദ്വാദശാംശം (Dwadashamsa)", "Dwadashamsa", "മാതാപിതാക്കൾ, പൂർവ്വിക പാരമ്പര്യം"),
    D16("D16", 16, "ഷോഡശാംശം (Shodasamsa)", "Shodasamsa", "വാഹനങ്ങൾ, സുഖസൗകര്യങ്ങൾ, മാനസിക സന്തോഷം"),
    D20("D20", 20, "വിംശാംശം (Vimshamsa)", "Vimshamsa", "ആത്മീയ പുരോഗതി, ഉപാസന, ഭക്തി"),
    D24("D24", 24, "ചതുർവിംശാംശം (Chaturvimshamsa)", "Chaturvimshamsa", "വിദ്യാഭ്യാസം, ഉന്നത പഠനം, പാണ്ഡിത്യം"),
    D27("D27", 27, "ഭാംശം / സപ്തവിംശാംശം (Bhamsa)", "Bhamsa", "ശാരീരിക-മാനസിക കരുത്ത്, സഹജമായ കഴിവുകൾ"),
    D30("D30", 30, "ത്രിംശാംശം (Trimshamsa)", "Trimshamsa", "വെല്ലുവിളികൾ, ദോഷങ്ങൾ, പ്രതിരോധശേഷി"),
    D40("D40", 40, "ഖവേദാംശം (Khavedamsa)", "Khavedamsa", "ശുഭാശുഭ ഫലങ്ങൾ, മാതൃപരമ്പരയുടെ സ്വാധീനം"),
    D45("D45", 45, "അക്ഷവേദാംശം (Akshavedamsa)", "Akshavedamsa", "സദാചാരം, നേതൃമികവ്, പിതൃപരമ്പരയുടെ സ്വാധീനം"),
    D60("D60", 60, "ഷഷ്ട്യാംശം (Shashtiamsa)", "Shashtiamsa", "സൂക്ഷ്മ കർമ്മഫലം, പൂർവ്വജന്മ സുകൃതം")
}

data class KeralaLocation(
    val id: String,
    val nameMalayalam: String,
    val nameEnglish: String,
    val districtMalayalam: String,
    val districtEnglish: String,
    val categoryMalayalam: String, // ജില്ലാ ആസ്ഥാനം, കോർപ്പറേഷൻ, മുനിസിപ്പാലിറ്റി, താലൂക്ക്, ഗ്രാമപഞ്ചായത്ത്, പട്ടണം
    val talukMalayalam: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String = "Asia/Kolkata",
    val timezoneOffsetHours: Double = 5.5
)

data class BirthData(
    val name: String = "ജാതകൻ / ജാതക",
    val gender: Gender = Gender.MALE,
    val year: Int = 1996,
    val month: Int = 5, // 1..12
    val day: Int = 15,  // 1..31
    val hour: Int = 9,  // 0..23
    val minute: Int = 30, // 0..59
    val second: Int = 0,
    val placeNameMalayalam: String = "തിരുവനന്തപുരം",
    val placeNameEnglish: String = "Thiruvananthapuram",
    val districtMalayalam: String = "തിരുവനന്തപുരം",
    val districtEnglish: String = "Thiruvananthapuram",
    val state: String = "Kerala (കേരളം)",
    val country: String = "India (ഇന്ത്യ)",
    val latitude: Double = 8.5241,
    val longitude: Double = 76.9366,
    val timezoneId: String = "Asia/Kolkata",
    val timezoneOffsetHours: Double = 5.5
)

data class PlanetPosition(
    val planet: Planet,
    val siderealLongitude: Double, // 0..360
    val rashi: Rashi,
    val degreeInSign: Double, // 0..30
    val formattedDegree: String, // e.g. 14° 22' 18"
    val houseFromLagna: Int, // 1..12
    val houseFromMoon: Int,  // 1..12
    val nakshatra: Nakshatra,
    val pada: Int, // 1..4
    val isRetrograde: Boolean,
    val isCombust: Boolean,
    val dignity: PlanetaryDignity,
    val navamsaRashi: Rashi,
    val dasamsaRashi: Rashi,
    val ownedHouses: List<Int>,
    val aspectedHouses: List<Int>,
    val conjunctPlanets: List<Planet>,
    val shadbalaVirupas: Double,
    val shadbalaRupas: Double,
    val shadbalaPercentage: Int,
    val ashtakavargaBindus: Int // 0..8
)

data class BhavaInfo(
    val houseNumber: Int, // 1..12
    val rashi: Rashi,
    val lord: Planet,
    val lordPlacedHouse: Int,
    val midpointDegree: Double,
    val occupants: List<Planet>,
    val aspectingPlanets: List<Planet>,
    val sarvashtakavargaBindus: Int,
    val bhavaStrengthScore: Int, // 0..100
    val significationMal: String,
    val summaryMal: String
)

data class KaranaDetails(
    val indexInTithiHalf: Int, // 1..60
    val nameMalayalam: String,
    val nameEnglish: String,
    val isSthira: Boolean,
    val deityMal: String,
    val natureMal: String,
    val muhurthaSuitabilityMal: String
)

data class PanchangaData(
    val varaMalayalam: String,
    val varaEnglish: String,
    val varaLord: Planet,
    val tithiNumber: Int, // 1..30
    val tithiMalayalam: String,
    val pakshaMalayalam: String,
    val nakshatra: Nakshatra,
    val nakshatraPada: Int,
    val nityaYogaNumber: Int, // 1..27
    val nityaYogaMalayalam: String,
    val karana: KaranaDetails,
    val sunriseFormatted: String,
    val sunsetFormatted: String,
    val moonriseFormatted: String,
    val moonsetFormatted: String,
    val malayalamMasa: String,
    val rituMalayalam: String,
    val ayanaMalayalam: String,
    val samvatsaraMalayalam: String,
    val rahuKalamFormatted: String,
    val gulikaKalamFormatted: String,
    val abhijitMuhurthamFormatted: String
)

enum class PeriodClassification(val malayalamLabel: String, val englishLabel: String) {
    FAVORABLE("അനുകൂലമായ കാലഘട്ടം", "Favorable Period"),
    MIXED("മിശ്രഫല കാലഘട്ടം", "Mixed Period"),
    CHALLENGING("ശ്രദ്ധ ആവശ്യമായ കാലഘട്ടം", "Caution / Challenging Period")
}

data class PratyantardashaPeriod(
    val mahadashaLord: Planet,
    val antardashaLord: Planet,
    val pratyantardashaLord: Planet,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val startDateFormatted: String,
    val endDateFormatted: String,
    val isCurrent: Boolean,
    val classification: PeriodClassification,
    val relevantHouses: List<Int>,
    val relevantYogas: List<String>,
    val transitSupportMal: String,
    val interpretationMal: String
)

data class AntardashaPeriod(
    val mahadashaLord: Planet,
    val antardashaLord: Planet,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val startDateFormatted: String,
    val endDateFormatted: String,
    val durationMonthsFormatted: String,
    val isCurrent: Boolean,
    val classification: PeriodClassification,
    val relevantHouses: List<Int>,
    val careerAndPscMal: String,
    val financeAndBusinessMal: String,
    val marriageAndFamilyMal: String,
    val propertyAndForeignMal: String,
    val detailedInterpretationMal: String,
    val pratyantardashas: List<PratyantardashaPeriod>
)

data class MahadashaPeriod(
    val lord: Planet,
    val startEpochMillis: Long,
    val endEpochMillis: Long,
    val startDateFormatted: String,
    val endDateFormatted: String,
    val durationYears: Double,
    val isCurrent: Boolean,
    val elapsedPercentage: Int,
    val remainingYearsFormatted: String,
    val classification: PeriodClassification,
    val lordPlacementSummaryMal: String,
    val detailedInterpretationMal: String,
    val antardashas: List<AntardashaPeriod>
)

enum class YogaStrengthClass(val malayalam: String, val english: String) {
    STRONG("ശക്തം (Strong)", "Strong"),
    MODERATE("മിതമായ ശക്തി (Moderate)", "Moderate"),
    WEAK("ദുർബലം (Weak)", "Weak"),
    MODIFIED("രൂപാന്തരപ്പെട്ടത് (Modified)", "Modified"),
    CANCELLED("ഭംഗം വന്നത് (Cancelled)", "Cancelled")
}

data class YogaResult(
    val id: String,
    val nameMalayalam: String,
    val nameEnglish: String,
    val categoryMal: String, // രാജയോഗം, ധനയോഗം, കരിയർ യോഗം, വിവാഹ യോഗം, ദോഷവിചിന്തനം etc.
    val isDosha: Boolean,
    val planetsInvolved: List<Planet>,
    val housesInvolved: List<Int>,
    val signsInvolved: List<Rashi>,
    val conditionsSatisfiedMal: String,
    val strengthClass: YogaStrengthClass,
    val modificationOrCancellationMal: String,
    val dashaActivationMal: String,
    val transitRelevanceMal: String,
    val positiveIndicationsMal: String,
    val challengingIndicationsMal: String,
    val detailedMalayalamExplanation: String
)

data class PlanetTransitItem(
    val planet: Planet,
    val transitRashi: Rashi,
    val degreeInSign: Double,
    val houseFromMoon: Int,
    val houseFromLagna: Int,
    val transitNakshatra: Nakshatra,
    val transitPada: Int,
    val isFavorableFromMoon: Boolean,
    val classification: PeriodClassification,
    val effectMalayalam: String
)

data class TransitAnalysisReport(
    val calculationDateFormatted: String,
    val planetTransits: List<PlanetTransitItem>,
    val jupiterTransitDetailedMal: String,
    val saturnTransitDetailedMal: String,
    val rahuKetuTransitDetailedMal: String,
    val dashaTransitCombinedMal: String
)

data class CareerCategoryItem(
    val id: String,
    val titleMalayalam: String,
    val titleEnglish: String,
    val suitabilityScore: Int, // 25..95
    val classification: PeriodClassification,
    val supportingFactorsMal: String,
    val challengingFactorsMal: String,
    val relevantPlanets: List<Planet>,
    val relevantHouses: List<Int>,
    val relevantYogas: List<String>,
    val activeDashaSupportMal: String,
    val transitSupportMal: String,
    val stabilityAndPromotionMal: String,
    val examAndSelectionNoteMal: String
)

data class LifeDomainReport(
    val domainId: String,
    val titleMalayalam: String,
    val titleEnglish: String,
    val classification: PeriodClassification,
    val keyHouses: List<Int>,
    val keyPlanets: List<Planet>,
    val summaryMalayalam: String,
    val detailedSections: List<Pair<String, String>>,
    val supportivePeriodsMal: String,
    val cautionPeriodsMal: String
)

data class LifeTimelineStage(
    val ageRange: String, // "0–10", "10–20", "20–30", "30–40", "40–50", "50–60", "60+"
    val titleMalayalam: String,
    val dominantMahadashaMal: String,
    val dominantAntardashaMal: String,
    val majorTransitNoteMal: String,
    val activeYogasMal: String,
    val classification: PeriodClassification,
    val detailedMalayalamNarrative: String
)

data class PeriodicHoroscopeReport(
    val dailyMal: String,
    val weeklyMal: String,
    val monthlyMal: String,
    val yearlyMal: String
)

data class MuhurthamCategoryInfo(
    val id: String,
    val titleMalayalam: String,
    val titleEnglish: String,
    val currentDaySuitability: PeriodClassification,
    val favorableNakshatrasMal: String,
    val favorableTithisAndKaranasMal: String,
    val currentPanchangaAssessmentMal: String,
    val traditionalGuidelinesMal: String
)

data class PoruthamFactorResult(
    val nameMalayalam: String,
    val nameEnglish: String,
    val statusMalayalam: String, // ഉത്തമം, മദ്ധ്യമം, വർജ്ജ്യം / പരിഹാരസാധ്യം
    val isMatched: Boolean,
    val explanationMalayalam: String
)

data class CompatibilityReport(
    val person1Name: String,
    val person1Nakshatra: Nakshatra,
    val person1Rashi: Rashi,
    val person2Name: String,
    val person2Nakshatra: Nakshatra,
    val person2Rashi: Rashi,
    val overallClassification: PeriodClassification,
    val poruthamFactors: List<PoruthamFactorResult>,
    val kujaDoshaComparisonMal: String,
    val dashaSandhiAnalysisMal: String,
    val navamsaAndPlanetaryHarmonyMal: String,
    val detailedMalayalamSummary: String
)

data class DivisionalChartData(
    val chartType: DivisionalChartType,
    val lagnaRashi: Rashi,
    val planetSigns: Map<Planet, Rashi>,
    val houseSummaryMal: String
)

data class ValidationCheckResult(
    val isValid: Boolean,
    val checksPassedCount: Int,
    val totalChecksCount: Int,
    val ayanamsaUsedDegrees: String,
    val julianDayFormatted: String,
    val siderealTimeFormatted: String,
    val validationDetailsMal: List<String>
)

data class CompleteJathakamReport(
    val birthData: BirthData,
    val lagnaRashi: Rashi,
    val lagnaDegreeInSign: Double,
    val lagnaNakshatra: Nakshatra,
    val lagnaPada: Int,
    val chandraRashi: Rashi,
    val janmaNakshatra: Nakshatra,
    val janmaPada: Int,
    val panchanga: PanchangaData,
    val planets: List<PlanetPosition>,
    val bhavas: List<BhavaInfo>,
    val divisionalCharts: Map<DivisionalChartType, DivisionalChartData>,
    val birthDashaBalanceMal: String,
    val mahadashas: List<MahadashaPeriod>,
    val currentMahadasha: MahadashaPeriod,
    val currentAntardasha: AntardashaPeriod,
    val currentPratyantardasha: PratyantardashaPeriod,
    val yogasAndDoshas: List<YogaResult>,
    val transitReport: TransitAnalysisReport,
    val careerCategories: List<CareerCategoryItem>,
    val careerGeneralReport: LifeDomainReport,
    val educationReport: LifeDomainReport,
    val marriageReport: LifeDomainReport,
    val partnerPersonalityReport: LifeDomainReport,
    val partnerAppearanceReport: LifeDomainReport,
    val financeReport: LifeDomainReport,
    val businessReport: LifeDomainReport,
    val foreignTravelReport: LifeDomainReport,
    val propertyVehicleReport: LifeDomainReport,
    val familyReport: LifeDomainReport,
    val childrenReport: LifeDomainReport,
    val healthLifestyleReport: LifeDomainReport,
    val spiritualityReport: LifeDomainReport,
    val lifeTimeline: List<LifeTimelineStage>,
    val periodicHoroscope: PeriodicHoroscopeReport,
    val muhurthamList: List<MuhurthamCategoryInfo>,
    val validationSummary: ValidationCheckResult
)
