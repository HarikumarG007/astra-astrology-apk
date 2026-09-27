package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AstraAdMobBanner
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AdMobManager
import com.example.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdMobManager.initialize(this)
        enableEdgeToEdge()
        setContent {
            val astroViewModel: AstraAstrologyViewModel = viewModel()
            val adminConfig by astroViewModel.adminConfig.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = adminConfig.isDarkYinYangTheme) {
                AstraAstrologyMainApp(viewModel = astroViewModel)
            }
        }
    }
}

@Composable
fun AstraAstrologyMainApp(
    viewModel: AstraAstrologyViewModel
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val jathakamSubTab by viewModel.jathakamSubTab.collectAsStateWithLifecycle()
    val predictionSubTab by viewModel.predictionSubTab.collectAsStateWithLifecycle()
    val moreSubScreen by viewModel.moreSubScreen.collectAsStateWithLifecycle()
    val birthData by viewModel.birthData.collectAsStateWithLifecycle()
    val jathakamReport by viewModel.jathakamReport.collectAsStateWithLifecycle()
    val partnerBirthData by viewModel.partnerBirthData.collectAsStateWithLifecycle()
    val compatibilityReport by viewModel.compatibilityReport.collectAsStateWithLifecycle()
    val savedHoroscopes by viewModel.savedHoroscopes.collectAsStateWithLifecycle()
    val validationError by viewModel.validationError.collectAsStateWithLifecycle()
    val showSaveOptInDialog by viewModel.showSaveOptInDialog.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val adminConfig by viewModel.adminConfig.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatusMessage()
        }
    }

    // BackHandler for secondary screens and non-Home tabs
    BackHandler(enabled = currentTab != MainNavTab.HOME || moreSubScreen != MoreSubScreen.MENU) {
        viewModel.handleBackNavigation()
    }

    // Opt-in Save Horoscope Confirmation Dialog (OFF BY DEFAULT)
    if (showSaveOptInDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissSaveHoroscopePrompt() },
            title = {
                Text(
                    text = "നിങ്ങളുടെ ജാതക വിവരങ്ങൾ സംരക്ഷിക്കണോ?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "സ്വകാര്യതാ സംരക്ഷണ നിയമപ്രകാരം ഈ ആപ്പ് നിങ്ങളുടെ ജാതക വിവരങ്ങൾ സ്വയമേവ സംരക്ഷിക്കുന്നില്ല. നിങ്ങളുടെ ഉപകരണത്തിൽ മാത്രം സുരക്ഷിതമായി സൂക്ഷിക്കാൻ ആഗ്രഹിക്കുന്നുവെങ്കിൽ 'സംരക്ഷിക്കുക' തിരഞ്ഞെടുക്കുക.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmSaveHoroscope() },
                    modifier = Modifier.testTag("dialog_btn_save_confirm")
                ) {
                    Text("സംരക്ഷിക്കുക")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.dismissSaveHoroscopePrompt() },
                    modifier = Modifier.testTag("dialog_btn_save_decline")
                ) {
                    Text("വേണ്ട")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Column {
                // Non-intrusive Google Mobile Ads Banner Slot when enabled
                if (adminConfig.adsMasterEnabled && adminConfig.bannerAdEnabled) {
                    AstraAdMobBanner()
                }

                NavigationBar(
                    modifier = Modifier.testTag("main_bottom_navigation")
                ) {
                    val navItems = listOf(
                        Triple(MainNavTab.HOME, Icons.Filled.Home, Icons.Outlined.Home),
                        Triple(MainNavTab.JATHAKAM, Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome),
                        Triple(MainNavTab.PREDICTIONS, Icons.Filled.Analytics, Icons.Outlined.Analytics),
                        Triple(MainNavTab.DASHA, Icons.Filled.Timeline, Icons.Outlined.Timeline),
                        Triple(MainNavTab.MORE, Icons.Filled.GridView, Icons.Outlined.GridView)
                    )
                    navItems.forEach { (tab, filledIcon, outlinedIcon) ->
                        val selected = (currentTab == tab)
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (tab == MainNavTab.MORE && currentTab == MainNavTab.MORE) {
                                    viewModel.navigateToMoreScreen(MoreSubScreen.MENU)
                                } else {
                                    viewModel.selectMainTab(tab)
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) filledIcon else outlinedIcon,
                                    contentDescription = tab.englishLabel
                                )
                            },
                            label = {
                                Text(
                                    text = tab.malayalamLabel,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainNavTab.HOME -> HomeScreen(
                    report = jathakamReport,
                    isDarkTheme = adminConfig.isDarkYinYangTheme,
                    onToggleTheme = {
                        viewModel.updateAdminConfig { it.copy(isDarkYinYangTheme = !it.isDarkYinYangTheme) }
                    },
                    onNavigateTab = { viewModel.selectMainTab(it) },
                    onNavigateJathakamSub = { viewModel.selectJathakamSubTab(it) },
                    onNavigatePredictionSub = { viewModel.selectPredictionSubTab(it) },
                    onNavigateMoreSub = { viewModel.navigateToMoreScreen(it) },
                    onRequestSavePrompt = { viewModel.requestSaveHoroscopePrompt() }
                )

                MainNavTab.JATHAKAM -> JathakamScreen(
                    birthData = birthData,
                    report = jathakamReport,
                    selectedSubTab = jathakamSubTab,
                    validationError = validationError,
                    onSelectSubTab = { viewModel.selectJathakamSubTab(it) },
                    onSelectKeralaLocation = { viewModel.applyKeralaLocation(it, isForPartner = false) },
                    onCalculateJathakam = { data, navAfter ->
                        viewModel.updateAndCalculateBirthData(data, navigateToChartAfter = navAfter)
                    },
                    onRequestSaveHoroscope = { viewModel.requestSaveHoroscopePrompt() }
                )

                MainNavTab.PREDICTIONS -> PredictionsScreen(
                    report = jathakamReport,
                    selectedSubTab = predictionSubTab,
                    onSelectSubTab = { viewModel.selectPredictionSubTab(it) },
                    rewardedAdEnabled = adminConfig.adsMasterEnabled && adminConfig.rewardedAdEnabled,
                    isRewardedUnlocked = adminConfig.rewardedPredictionUnlocked,
                    onUnlockRewarded = { viewModel.unlockRewardedPrediction() },
                    onShowMessage = { viewModel.showStatusMessage(it) }
                )

                MainNavTab.DASHA -> DashaScreen(report = jathakamReport)

                MainNavTab.MORE -> {
                    when (moreSubScreen) {
                        MoreSubScreen.MENU -> MoreMenuGridView(
                            onSelectScreen = { viewModel.navigateToMoreScreen(it) }
                        )
                        MoreSubScreen.PANCHANGA -> PanchangaFullModuleView(
                            report = jathakamReport,
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.KARANA -> KaranaCalculatorAndExplanationView(
                            report = jathakamReport,
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.YOGAS_DOSHAS -> YogasAndDoshasFullView(
                            report = jathakamReport,
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.TRANSIT -> TransitGocharaFullView(
                            report = jathakamReport,
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.COMPATIBILITY -> CompatibilityPoruthamView(
                            person1BirthData = birthData,
                            partnerBirthData = partnerBirthData,
                            compatibilityReport = compatibilityReport,
                            onUpdatePartnerBirthData = { viewModel.updatePartnerBirthData(it) },
                            onSelectPartnerLocation = { viewModel.applyKeralaLocation(it, isForPartner = true) },
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.MUHURTHAM -> MuhurthamModuleView(
                            report = jathakamReport,
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.ASTROLOGY_TOOLS -> AstrologyToolsAndGlossaryView(
                            report = jathakamReport,
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.COMPLETE_REPORT_PDF -> CompleteReportAndPdfView(
                            report = jathakamReport,
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.SAVED_AND_PRIVACY -> SavedHoroscopesAndPrivacyControlView(
                            savedList = savedHoroscopes,
                            onRequestSaveCurrent = { viewModel.requestSaveHoroscopePrompt() },
                            onLoadSaved = { viewModel.loadSavedHoroscope(it) },
                            onDeleteSingle = { viewModel.deleteSingleSavedHoroscope(it) },
                            onDeleteAll = { viewModel.deleteAllSavedHoroscopes() },
                            onClearSession = { viewModel.clearCurrentSessionData() },
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.PRIVACY_POLICY -> PrivacyPolicyView(
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.TERMS_AND_DATA_SAFETY -> TermsAndDataSafetyView(
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.ABOUT_AND_CONTACT -> AboutAndContactView(
                            onOpenAdminPanel = { viewModel.navigateToMoreScreen(MoreSubScreen.ADMIN_PANEL) },
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.MENU) }
                        )
                        MoreSubScreen.ADMIN_PANEL -> SecureAdminPanelView(
                            adminConfig = adminConfig,
                            onAuthenticate = { email, token -> viewModel.authenticateAdmin(email, token) },
                            onUpdateConfig = { viewModel.updateAdminConfig(it) },
                            onLogout = { viewModel.logoutAdmin() },
                            onBack = { viewModel.navigateToMoreScreen(MoreSubScreen.ABOUT_AND_CONTACT) }
                        )
                    }
                }
            }
        }
    }
}
