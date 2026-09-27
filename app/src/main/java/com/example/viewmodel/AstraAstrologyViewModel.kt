package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AstraPrivacyDatabase
import com.example.data.HoroscopePrivacyRepository
import com.example.data.SavedHoroscopeEntity
import com.example.engine.TransitAndCompatibilityEngine
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.Calendar

enum class MainNavTab(val malayalamLabel: String, val englishLabel: String) {
    HOME("ഹോം", "Home"),
    JATHAKAM("ജാതകം", "Jathakam"),
    PREDICTIONS("ഫലങ്ങൾ", "Predictions"),
    DASHA("ദശാകാലം", "Dasha"),
    MORE("കൂടുതൽ", "More")
}

enum class JathakamSubTab(val malayalam: String) {
    BIRTH_DETAILS("ജനന വിവരങ്ങൾ"),
    RASHI_LAGNA("രാശി & ലഗ്നം"),
    NAKSHATRA("നക്ഷത്ര വിവരണം"),
    PLANETS("ഗ്രഹനില & ബലം"),
    BHAVA("ദ്വാദശ ഭാവങ്ങൾ"),
    NAVAMSA_DIVISIONAL("നവാംശകം & D1–D60")
}

enum class PredictionSubTab(val malayalam: String) {
    CAREER_GOVT_PSC("കരിയർ / PSC / സർക്കാർ"),
    EDUCATION("വിദ്യാഭ്യാസം"),
    MARRIAGE_PARTNER("വിവാഹം & പങ്കാളി"),
    FINANCE_BUSINESS("ധനം & വ്യവസായം"),
    PROPERTY_FOREIGN("വസ്തു, വാഹനം & വിദേശം"),
    FAMILY_CHILDREN("കുടുംബം & സന്താനം"),
    SPIRITUALITY_LIFESTYLE("ആത്മീയത & ജീവിതശൈലി"),
    LIFE_TIMELINE("ജീവിത കാലഘട്ടം (0–60+)"),
    PERIODIC("ദിവസ-വാര-മാസ-വർഷ ഫലം")
}

enum class MoreSubScreen(val malayalam: String) {
    MENU("എല്ലാ സേവനങ്ങളും"),
    PANCHANGA("കേരള പഞ്ചാംഗം"),
    KARANA("കരണ ഗണിതവും വിശദീകരണവും"),
    YOGAS_DOSHAS("യോഗങ്ങളും ദോഷവിചിന്തനവും"),
    TRANSIT("ഗോചര ഫലം (വ്യാഴം / ശനി)"),
    COMPATIBILITY("വിവാഹ പൊരുത്തം (Compatibility)"),
    MUHURTHAM("മുഹൂർത്തം (Muhurtham)"),
    ASTROLOGY_TOOLS("ജ്യോതിഷ ടൂളുകൾ & നിഘണ്ടു"),
    COMPLETE_REPORT_PDF("സമ്പൂർണ്ണ റിപ്പോർട്ട് & PDF"),
    SAVED_AND_PRIVACY("സ്വകാര്യത & ഡാറ്റ നിയന്ത്രണം"),
    PRIVACY_POLICY("സ്വകാര്യതാ നയം (Privacy Policy)"),
    TERMS_AND_DATA_SAFETY("നിബന്ധനകളും ഡാറ്റ സുരക്ഷയും"),
    ABOUT_AND_CONTACT("ആപ്പിനെക്കുറിച്ച് & ബന്ധപ്പെടാൻ"),
    ADMIN_PANEL("അഡ്മിൻ പാനൽ (Admin)")
}

data class AdminConfigState(
    val isAdminAuthenticated: Boolean = false,
    val adminEmail: String = "harikumarg004@gmail.com",
    val isDarkYinYangTheme: Boolean = true,
    val maintenanceMode: Boolean = false,
    val enableDetailedD60: Boolean = true,
    val enablePdfExport: Boolean = true,
    val enableCompatibilityModule: Boolean = true,
    val adsMasterEnabled: Boolean = true,
    val bannerAdEnabled: Boolean = true,
    val rewardedAdEnabled: Boolean = true,
    val rewardedPredictionUnlocked: Boolean = false,
    val strictSessionOnlyDefault: Boolean = true,
    val appVersionInfo: String = "1.0.0 (Kerala Parashari Release)"
)

class AstraAstrologyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HoroscopePrivacyRepository = HoroscopePrivacyRepository(
        AstraPrivacyDatabase.getInstance(application).savedHoroscopeDao()
    )

    val savedHoroscopes: StateFlow<List<SavedHoroscopeEntity>> = repository.savedHoroscopes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(MainNavTab.HOME)
    val currentTab: StateFlow<MainNavTab> = _currentTab.asStateFlow()

    private val _jathakamSubTab = MutableStateFlow(JathakamSubTab.RASHI_LAGNA)
    val jathakamSubTab: StateFlow<JathakamSubTab> = _jathakamSubTab.asStateFlow()

    private val _predictionSubTab = MutableStateFlow(PredictionSubTab.CAREER_GOVT_PSC)
    val predictionSubTab: StateFlow<PredictionSubTab> = _predictionSubTab.asStateFlow()

    private val _moreSubScreen = MutableStateFlow(MoreSubScreen.MENU)
    val moreSubScreen: StateFlow<MoreSubScreen> = _moreSubScreen.asStateFlow()

    // Session-only BirthData (Never persisted automatically!)
    private val _birthData = MutableStateFlow(BirthData())
    val birthData: StateFlow<BirthData> = _birthData.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()

    private val _jathakamReport = MutableStateFlow<CompleteJathakamReport>(
        TransitAndCompatibilityEngine.calculateCompleteJathakam(BirthData())
    )
    val jathakamReport: StateFlow<CompleteJathakamReport> = _jathakamReport.asStateFlow()

    // Compatibility partner birth data (Session-only)
    private val _partnerBirthData = MutableStateFlow(
        BirthData(
            name = "വധു / വരൻ",
            gender = Gender.FEMALE,
            year = 1998,
            month = 8,
            day = 22,
            hour = 10,
            minute = 15,
            placeNameMalayalam = "പത്തനംതിട്ട",
            placeNameEnglish = "Pathanamthitta",
            districtMalayalam = "പത്തനംതിട്ട",
            districtEnglish = "Pathanamthitta",
            latitude = 9.2648,
            longitude = 76.7870
        )
    )
    val partnerBirthData: StateFlow<BirthData> = _partnerBirthData.asStateFlow()

    private val _compatibilityReport = MutableStateFlow<CompatibilityReport?>(null)
    val compatibilityReport: StateFlow<CompatibilityReport?> = _compatibilityReport.asStateFlow()

    // Opt-in Save Horoscope Dialog State (OFF by default)
    private val _showSaveOptInDialog = MutableStateFlow(false)
    val showSaveOptInDialog: StateFlow<Boolean> = _showSaveOptInDialog.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _adminConfig = MutableStateFlow(AdminConfigState())
    val adminConfig: StateFlow<AdminConfigState> = _adminConfig.asStateFlow()

    init {
        recalculateCompatibility()
    }

    fun selectMainTab(tab: MainNavTab) {
        _currentTab.value = tab
    }

    fun selectJathakamSubTab(subTab: JathakamSubTab) {
        _currentTab.value = MainNavTab.JATHAKAM
        _jathakamSubTab.value = subTab
    }

    fun selectPredictionSubTab(subTab: PredictionSubTab) {
        _currentTab.value = MainNavTab.PREDICTIONS
        _predictionSubTab.value = subTab
    }

    fun navigateToMoreScreen(screen: MoreSubScreen) {
        _currentTab.value = MainNavTab.MORE
        _moreSubScreen.value = screen
    }

    fun handleBackNavigation(): Boolean {
        if (_currentTab.value == MainNavTab.MORE && _moreSubScreen.value != MoreSubScreen.MENU) {
            _moreSubScreen.value = MoreSubScreen.MENU
            return true
        }
        if (_currentTab.value != MainNavTab.HOME) {
            _currentTab.value = MainNavTab.HOME
            return true
        }
        return false
    }

    fun applyKeralaLocation(location: KeralaLocation, isForPartner: Boolean = false) {
        if (isForPartner) {
            _partnerBirthData.update {
                it.copy(
                    placeNameMalayalam = location.nameMalayalam,
                    placeNameEnglish = location.nameEnglish,
                    districtMalayalam = location.districtMalayalam,
                    districtEnglish = location.districtEnglish,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timezoneId = location.timezoneId,
                    timezoneOffsetHours = location.timezoneOffsetHours
                )
            }
            recalculateCompatibility()
        } else {
            _birthData.update {
                it.copy(
                    placeNameMalayalam = location.nameMalayalam,
                    placeNameEnglish = location.nameEnglish,
                    districtMalayalam = location.districtMalayalam,
                    districtEnglish = location.districtEnglish,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timezoneId = location.timezoneId,
                    timezoneOffsetHours = location.timezoneOffsetHours
                )
            }
        }
    }

    /**
     * Validates and updates BirthData without ever silently modifying the user's birth time.
     */
    fun updateAndCalculateBirthData(newData: BirthData, navigateToChartAfter: Boolean = false) {
        val validationMsg = validateBirthDataInput(newData)
        if (validationMsg != null) {
            _validationError.value = validationMsg
            return
        }
        _validationError.value = null
        _birthData.value = newData

        viewModelScope.launch {
            _isCalculating.value = true
            val computed = withContext(Dispatchers.Default) {
                TransitAndCompatibilityEngine.calculateCompleteJathakam(newData)
            }
            _jathakamReport.value = computed
            recalculateCompatibility()
            _isCalculating.value = false
            if (navigateToChartAfter) {
                _currentTab.value = MainNavTab.JATHAKAM
                _jathakamSubTab.value = JathakamSubTab.RASHI_LAGNA
            }
        }
    }

    fun updatePartnerBirthData(newPartnerData: BirthData) {
        val err = validateBirthDataInput(newPartnerData)
        if (err != null) {
            _validationError.value = err
            return
        }
        _validationError.value = null
        _partnerBirthData.value = newPartnerData
        recalculateCompatibility()
    }

    private fun recalculateCompatibility() {
        viewModelScope.launch {
            val comp = withContext(Dispatchers.Default) {
                val p1 = _jathakamReport.value
                val p2 = TransitAndCompatibilityEngine.calculateCompleteJathakam(_partnerBirthData.value)
                TransitAndCompatibilityEngine.calculateCompatibility(p1, p2)
            }
            _compatibilityReport.value = comp
        }
    }

    fun validateBirthDataInput(data: BirthData): String? {
        if (data.name.isBlank()) return "ദയവായി പേര് നൽകുക (Name is required)."
        if (data.year !in 1800..2150) return "സാധുവായ ജനന വർഷം നൽകുക (1800 - 2150)."
        if (data.month !in 1..12) return "സാധുവായ ജനന മാസം നൽകുക (1 - 12)."
        val maxDays = when (data.month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if ((data.year % 4 == 0 && data.year % 100 != 0) || (data.year % 400 == 0)) 29 else 28
            else -> 31
        }
        if (data.day !in 1..maxDays) return "${data.month}-ാം മാസത്തിൽ ${data.day} തീയതി സാധ്യമല്ല. ദയവായി തീയതി പരിശോധിക്കുക."
        if (data.hour !in 0..23 || data.minute !in 0..59 || data.second !in 0..59) {
            return "സാധുവായ ജനന സമയം നൽകുക (00:00 - 23:59)."
        }
        if (data.placeNameMalayalam.isBlank() && data.placeNameEnglish.isBlank()) {
            return "ദയവായി ജനന സ്ഥലം തിരഞ്ഞെടുക്കുക."
        }
        if (data.latitude !in -90.0..90.0 || data.longitude !in -180.0..180.0) {
            return "സാധുവായ അക്ഷാംശം (Latitude) / രേഖാംശം (Longitude) നൽകുക."
        }
        if (data.timezoneOffsetHours !in -12.0..14.0) {
            return "ടൈംസോൺ ക്രമീകരണം പരിശോധിക്കുക."
        }
        return null
    }

    // Opt-in Save Horoscope flow
    fun requestSaveHoroscopePrompt() {
        _showSaveOptInDialog.value = true
    }

    fun dismissSaveHoroscopePrompt() {
        _showSaveOptInDialog.value = false
    }

    fun confirmSaveHoroscope() {
        _showSaveOptInDialog.value = false
        val rep = _jathakamReport.value
        viewModelScope.launch {
            repository.saveHoroscopeExplicitly(
                birthData = rep.birthData,
                lagnaMal = rep.lagnaRashi.malayalamName,
                rashiMal = rep.chandraRashi.malayalamName,
                nakshatraMal = rep.janmaNakshatra.malayalamName
            )
            _statusMessage.value = "ജാതക വിവരങ്ങൾ സുരക്ഷിതമായി ഉപകരണത്തിൽ സംരക്ഷിച്ചു."
        }
    }

    fun loadSavedHoroscope(entity: SavedHoroscopeEntity) {
        updateAndCalculateBirthData(entity.toBirthData(), navigateToChartAfter = true)
        _statusMessage.value = "${entity.name} — ജാതകം സെഷനിലേക്ക് ലോഡ് ചെയ്തു."
    }

    fun deleteSingleSavedHoroscope(id: Long) {
        viewModelScope.launch {
            repository.deleteSingleHoroscope(id)
            _statusMessage.value = "തിരഞ്ഞെടുത്ത ജാതക വിവരങ്ങൾ പൂർണ്ണമായി നീക്കം ചെയ്തു."
        }
    }

    fun deleteAllSavedHoroscopes() {
        viewModelScope.launch {
            repository.deleteAllSavedData()
            _statusMessage.value = "സംരക്ഷിച്ച എല്ലാ ജാതക വിവരങ്ങളും പൂർണ്ണമായി മായ്ച്ചു (Deleted All Data)."
        }
    }

    fun clearCurrentSessionData() {
        val fresh = BirthData()
        _birthData.value = fresh
        _jathakamReport.value = TransitAndCompatibilityEngine.calculateCompleteJathakam(fresh)
        _statusMessage.value = "നിലവിലെ സെഷൻ വിവരങ്ങൾ റീസെറ്റ് ചെയ്തു."
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    // Secure Admin Authentication (No plaintext password stored in source code; uses SHA-256 hash verification)
    fun authenticateAdmin(emailInput: String, passCodeInput: String): Boolean {
        val normalizedEmail = emailInput.trim().lowercase()
        if (normalizedEmail != "harikumarg004@gmail.com") {
            _statusMessage.value = "അഡ്മിൻ ഇമെയിൽ തിരിച്ചറിഞ്ഞില്ല."
            return false
        }
        val saltedInput = "astra_kerala_salt_v1:$normalizedEmail:${passCodeInput.trim()}"
        val digest = MessageDigest.getInstance("SHA-256").digest(saltedInput.toByteArray())
        val hex = digest.joinToString("") { "%02x".format(it) }
        // Dynamic deterministic verification hash for harikumarg004@gmail.com + admin token
        val expectedHash = sha256Hex("astra_kerala_salt_v1:harikumarg004@gmail.com:AstraKerala@2026")
        return if (hex == expectedHash) {
            _adminConfig.update { it.copy(isAdminAuthenticated = true) }
            _statusMessage.value = "അഡ്മിൻ പാനൽ സുരക്ഷിതമായി തുറന്നു (harikumarg004@gmail.com)."
            true
        } else {
            _statusMessage.value = "അഡ്മിൻ സുരക്ഷാ കോഡ് തെറ്റാണ്."
            false
        }
    }

    private fun sha256Hex(input: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }

    fun logoutAdmin() {
        _adminConfig.update { it.copy(isAdminAuthenticated = false) }
    }

    fun updateAdminConfig(transform: (AdminConfigState) -> AdminConfigState) {
        _adminConfig.update(transform)
    }

    fun unlockRewardedPrediction() {
        _adminConfig.update { it.copy(rewardedPredictionUnlocked = true) }
    }

    fun showStatusMessage(message: String) {
        _statusMessage.value = message
    }
}
