package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.KeralaLocationDatabase
import com.example.engine.AstronomicalEphemerisEngine
import com.example.engine.TransitAndCompatibilityEngine
import com.example.model.BirthData
import com.example.model.DivisionalChartType
import com.example.model.Gender
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun verifyAppNameAndBrandingRules() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Astra Astrology Malayalam", appName)
        assertEquals("harikumarg004@gmail.com", context.getString(R.string.developer_email))
        assertFalse(AstronomicalEphemerisEngine.ENGINE_PUBLIC_NAME.contains("Swiss", ignoreCase = true))
    }

    @Test
    fun verifyKeralaLocationDatabaseMalayalamAndEnglishSearch() {
        assertEquals(14, KeralaLocationDatabase.districts.size)

        val ptaMal = KeralaLocationDatabase.searchLocations("പത്തനംതിട്ട")
        assertTrue(ptaMal.isNotEmpty())
        assertEquals("Pathanamthitta", ptaMal.first().districtEnglish)

        val pandalamSearch = KeralaLocationDatabase.searchLocations("Pandalam")
        assertTrue(pandalamSearch.isNotEmpty())
        assertEquals("പന്തളം", pandalamSearch.first().nameMalayalam)
        assertEquals(9.2250, pandalamSearch.first().latitude, 0.05)

        val tvmMal = KeralaLocationDatabase.searchLocations("തിരുവനന്തപുരം")
        assertTrue(tvmMal.isNotEmpty())
        assertEquals("Thiruvananthapuram", tvmMal.first().nameEnglish)
    }

    @Test
    fun verifyCompleteJathakamCalculationAndReproducibility() {
        val sampleBirth = BirthData(
            name = "ഹരികുമാർ",
            gender = Gender.MALE,
            year = 1994,
            month = 11,
            day = 18,
            hour = 7,
            minute = 45,
            second = 0,
            placeNameMalayalam = "പന്തളം",
            placeNameEnglish = "Pandalam",
            districtMalayalam = "പത്തനംതിട്ട",
            districtEnglish = "Pathanamthitta",
            latitude = 9.2250,
            longitude = 76.6784
        )

        val report1 = TransitAndCompatibilityEngine.calculateCompleteJathakam(sampleBirth)
        val report2 = TransitAndCompatibilityEngine.calculateCompleteJathakam(sampleBirth)

        // Reproducibility check
        assertEquals(report1.lagnaRashi, report2.lagnaRashi)
        assertEquals(report1.chandraRashi, report2.chandraRashi)
        assertEquals(report1.janmaNakshatra, report2.janmaNakshatra)
        assertEquals(report1.janmaPada, report2.janmaPada)
        assertEquals(report1.panchanga.karana.nameEnglish, report2.panchanga.karana.nameEnglish)

        // All 16 Divisional Charts D1..D60 must be present
        assertEquals(16, report1.divisionalCharts.size)
        assertTrue(report1.divisionalCharts.containsKey(DivisionalChartType.D9))
        assertTrue(report1.divisionalCharts.containsKey(DivisionalChartType.D10))
        assertTrue(report1.divisionalCharts.containsKey(DivisionalChartType.D60))

        // Karana must strictly be one of the 11 classical Panchanga Karanas
        val validKaranas = setOf(
            "Bava", "Balava", "Kaulava", "Taitila", "Garaja", "Vanija", "Vishti",
            "Shakuni", "Chatushpada", "Naga", "Kimstughna"
        )
        assertTrue(validKaranas.contains(report1.panchanga.karana.nameEnglish))
        assertTrue(AstronomicalEphemerisEngine.getSimhaKaranaExplanationMalayalam().contains("സിംഹകരണം"))

        // Vimshottari Dasha -> Antardasha -> Pratyantardasha check
        assertEquals(9, report1.mahadashas.size)
        assertTrue(report1.currentMahadasha.antardashas.isNotEmpty())
        assertTrue(report1.currentAntardasha.pratyantardashas.isNotEmpty())

        // Career categories check (PSC, KAS, UPSC, Govt Job, etc.)
        assertTrue(report1.careerCategories.size >= 27)
        assertTrue(report1.careerCategories.any { it.id == "kerala_psc" })
        assertTrue(report1.careerCategories.any { it.id == "kas" })

        // Partner appearance must use traditional indication phrasing
        assertTrue(report1.partnerAppearanceReport.summaryMalayalam.contains("ജാതകത്തിലെ പരമ്പരാഗത സൂചന പ്രകാരം"))
    }
}
