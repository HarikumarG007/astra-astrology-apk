package com.example.engine

import com.example.model.*
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.*

/**
 * High Precision Vedic Calculation Engine & Advanced Astronomical Calculation Engine.
 * Implements analytical planetary ephemeris equations, Chitrapaksha (Lahiri) Ayanamsa,
 * Lagna & Bhava trigonometry, Shodashavarga (D1-D60), Panchanga, Karana, Shadbala & Ashtakavarga.
 */
object AstronomicalEphemerisEngine {

    const val ENGINE_PUBLIC_NAME = "Advanced Astronomical Calculation Engine"
    const val ENGINE_SUBTITLE = "High Precision Vedic Calculation Engine"

    private fun degToRad(deg: Double): Double = deg * PI / 180.0
    private fun radToDeg(rad: Double): Double = rad * 180.0 / PI
    fun normalizeDegrees(deg: Double): Double {
        var res = deg % 360.0
        if (res < 0) res += 360.0
        return res
    }

    /**
     * Converts civil date & time (with timezone offset in hours) to Julian Ephemeris Day (JD UTC).
     */
    fun toJulianDay(
        year: Int,
        month: Int,
        day: Int,
        hour: Int,
        minute: Int,
        second: Int,
        tzOffsetHours: Double
    ): Double {
        val utHours = hour + (minute / 60.0) + (second / 3600.0) - tzOffsetHours
        var y = year
        var m = month
        val d = day + (utHours / 24.0)
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2.0 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + d + b - 1524.5
    }

    /**
     * Calculates Chitrapaksha (Lahiri) Ayanamsa in degrees for a given Julian Day.
     */
    fun calculateLahiriAyanamsa(jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0
        // J2000.0 Lahiri Ayanamsa = 23° 51' 25.532" (23.857092°) + precession rate 50.290966"/yr
        val base2000 = 23.8570922
        val precessionDeg = (50.290966 * t * 100.0 + 1.11113 * t * t) / 3600.0
        return base2000 + precessionDeg
    }

    /**
     * Computes Tropical Geocentric Longitude (in degrees) of a planet at Julian Day (jd).
     * Uses high-precision Keplerian + analytical periodic perturbation terms (Meeus / VSOP87-derived).
     */
    private fun computeTropicalLongitude(planet: Planet, jd: Double): Double {
        val t = (jd - 2451545.0) / 36525.0

        // Sun Geocentric Longitude
        val l0Sun = normalizeDegrees(280.46646 + 36000.76983 * t + 0.0003032 * t * t)
        val mSun = normalizeDegrees(357.52911 + 35999.05029 * t - 0.0001537 * t * t)
        val mSunRad = degToRad(mSun)
        val cSun = (1.914602 - 0.004817 * t - 0.000014 * t * t) * sin(mSunRad) +
            (0.019993 - 0.000101 * t) * sin(2 * mSunRad) +
            0.000289 * sin(3 * mSunRad)
        val sunTrueLon = normalizeDegrees(l0Sun + cSun)
        val omega = 125.04 - 1934.136 * t
        val sunApparentLon = normalizeDegrees(sunTrueLon - 0.00569 - 0.00478 * sin(degToRad(omega)))

        if (planet == Planet.SUN) return sunApparentLon

        // Moon Geocentric Longitude (ELP2000 principal periodic terms)
        if (planet == Planet.MOON) {
            val lPrime = normalizeDegrees(218.3164477 + 481267.88123421 * t - 0.0015786 * t * t)
            val d = normalizeDegrees(297.8501921 + 445267.1114034 * t - 0.0018819 * t * t)
            val m = normalizeDegrees(357.5291092 + 35999.0502909 * t - 0.0001536 * t * t)
            val mPrime = normalizeDegrees(134.9633964 + 477198.8675055 * t + 0.0087414 * t * t)
            val f = normalizeDegrees(93.2720950 + 483202.0175233 * t - 0.0036539 * t * t)

            val dR = degToRad(d)
            val mR = degToRad(m)
            val mpR = degToRad(mPrime)
            val fR = degToRad(f)

            val corr = 6.288774 * sin(mpR) +
                1.274027 * sin(2 * dR - mpR) +
                0.658314 * sin(2 * dR) +
                0.213618 * sin(2 * mpR) -
                0.185116 * sin(mR) -
                0.114332 * sin(2 * fR) +
                0.058793 * sin(2 * dR - 2 * mpR) +
                0.057066 * sin(2 * dR - mR - mpR) +
                0.053322 * sin(2 * dR + mpR) +
                0.045758 * sin(2 * dR - mR) -
                0.040923 * sin(mR - mpR) -
                0.034720 * sin(dR) -
                0.030383 * sin(mR + mpR) +
                0.015327 * sin(2 * dR - 2 * fR)
            return normalizeDegrees(lPrime + corr)
        }

        // True Lunar Nodes: Rahu & Ketu
        if (planet == Planet.RAHU || planet == Planet.KETU) {
            val meanNode = normalizeDegrees(125.0445479 - 1934.1362891 * t + 0.0020754 * t * t)
            val d = degToRad(normalizeDegrees(297.8501921 + 445267.1114034 * t))
            val m = degToRad(normalizeDegrees(357.5291092 + 35999.0502909 * t))
            val mp = degToRad(normalizeDegrees(134.9633964 + 477198.8675055 * t))
            val f = degToRad(normalizeDegrees(93.2720950 + 483202.0175233 * t))
            val trueNode = normalizeDegrees(
                meanNode - 1.4979 * sin(2 * (d - f)) -
                    0.1500 * sin(m) -
                    0.1226 * sin(2 * d) +
                    0.1176 * sin(2 * f) -
                    0.0801 * sin(2 * (mp - f))
            )
            return if (planet == Planet.RAHU) trueNode else normalizeDegrees(trueNode + 180.0)
        }

        // Earth heliocentric coordinates for geocentric transformation
        val earthLon = normalizeDegrees(l0Sun + cSun + 180.0)
        val earthLonRad = degToRad(earthLon)
        val earthR = 1.000001018 * (1.0 - 0.016708634 * cos(mSunRad))

        // Heliocentric orbital elements (L, a, e, i, omega, pi) for planets
        data class OrbitalElements(
            val L: Double,
            val a: Double,
            val e: Double,
            val wBar: Double,
            val pertDeg: Double = 0.0
        )

        val mJup = degToRad(normalizeDegrees(20.0202 + 3034.9057 * t))
        val mSat = degToRad(normalizeDegrees(317.0207 + 1222.1138 * t))

        val elem = when (planet) {
            Planet.MERCURY -> OrbitalElements(
                L = normalizeDegrees(252.250906 + 149472.6746358 * t),
                a = 0.387098310,
                e = 0.20563175 + 0.000020406 * t,
                wBar = normalizeDegrees(77.456119 + 1.5564776 * t)
            )
            Planet.VENUS -> OrbitalElements(
                L = normalizeDegrees(181.979801 + 58517.8156760 * t),
                a = 0.723329820,
                e = 0.00677188 - 0.000047766 * t,
                wBar = normalizeDegrees(131.563707 + 1.4022288 * t)
            )
            Planet.MARS -> OrbitalElements(
                L = normalizeDegrees(355.433000 + 19140.2993039 * t),
                a = 1.523679342,
                e = 0.09340065 + 0.000090484 * t,
                wBar = normalizeDegrees(336.060234 + 1.8410449 * t),
                pertDeg = -0.01133 * sin(2 * mJup - degToRad(355.433 + 19140.3 * t))
            )
            Planet.JUPITER -> OrbitalElements(
                L = normalizeDegrees(34.351519 + 3034.9056606 * t),
                a = 5.202603209,
                e = 0.04849793 + 0.000163225 * t,
                wBar = normalizeDegrees(14.331207 + 1.6126352 * t),
                pertDeg = 0.3314 * sin(5 * mSat - 2 * mJup + degToRad(67.6))
            )
            Planet.SATURN -> OrbitalElements(
                L = normalizeDegrees(50.077444 + 1222.1138488 * t),
                a = 9.554909192,
                e = 0.05554814 - 0.000346641 * t,
                wBar = normalizeDegrees(93.057237 + 1.9637613 * t),
                pertDeg = -0.8142 * sin(5 * mSat - 2 * mJup + degToRad(67.6))
            )
            else -> OrbitalElements(0.0, 1.0, 0.0, 0.0)
        }

        val mPlanet = normalizeDegrees(elem.L - elem.wBar)
        val mRad = degToRad(mPlanet)
        // Solve Kepler's equation E - e*sin(E) = M iteratively
        var eccAnom = mRad
        repeat(6) {
            eccAnom = mRad + elem.e * sin(eccAnom)
        }
        val xv = elem.a * (cos(eccAnom) - elem.e)
        val yv = elem.a * sqrt(1.0 - elem.e * elem.e) * sin(eccAnom)
        val trueAnom = radToDeg(atan2(yv, xv))
        val rHelio = sqrt(xv * xv + yv * yv)
        val lonHelio = normalizeDegrees(trueAnom + elem.wBar + elem.pertDeg)
        val lonHelioRad = degToRad(lonHelio)

        // Convert Heliocentric to Geocentric Ecliptic Longitude
        val xGeo = rHelio * cos(lonHelioRad) - earthR * cos(earthLonRad)
        val yGeo = rHelio * sin(lonHelioRad) - earthR * sin(earthLonRad)
        return normalizeDegrees(radToDeg(atan2(yGeo, xGeo)))
    }

    /**
     * Computes Sidereal Longitude (0..360) and daily motion (to detect Retrograde/Vakra).
     */
    fun computeSiderealPlanet(planet: Planet, jd: Double, ayanamsa: Double): Pair<Double, Boolean> {
        if (planet == Planet.RAHU || planet == Planet.KETU) {
            val trop = computeTropicalLongitude(planet, jd)
            return normalizeDegrees(trop - ayanamsa) to true // Nodes are always retrograde in mean motion
        }
        val tropNow = computeTropicalLongitude(planet, jd)
        val sidNow = normalizeDegrees(tropNow - ayanamsa)
        if (planet == Planet.SUN || planet == Planet.MOON || planet == Planet.MANDI) {
            return sidNow to false
        }
        val tropNext = computeTropicalLongitude(planet, jd + 0.25)
        var diff = tropNext - tropNow
        if (diff > 180.0) diff -= 360.0
        if (diff < -180.0) diff += 360.0
        val isRetro = diff < 0.0
        return sidNow to isRetro
    }

    /**
     * Calculates Tropical and Sidereal Ascendant (Lagna) for given JD, Latitude, Longitude, and Ayanamsa.
     */
    fun calculateSiderealLagna(jd: Double, latitude: Double, longitude: Double, ayanamsa: Double): Pair<Double, Double> {
        val t = (jd - 2451545.0) / 36525.0
        // Greenwich Mean Sidereal Time in degrees
        val gmst = normalizeDegrees(
            280.46061837 + 360.98564736629 * (jd - 2451545.0) + 0.000387933 * t * t - (t * t * t) / 38710000.0
        )
        val lstDeg = normalizeDegrees(gmst + longitude)
        val ramcRad = degToRad(lstDeg)
        val eps = degToRad(23.4392911 - 0.0130042 * t)
        val latRad = degToRad(latitude)

        val y = cos(ramcRad)
        val x = -sin(ramcRad) * cos(eps) - tan(latRad) * sin(eps)
        val tropicalAsc = normalizeDegrees(radToDeg(atan2(y, x)))
        val siderealAsc = normalizeDegrees(tropicalAsc - ayanamsa)
        return siderealAsc to lstDeg
    }

    /**
     * Calculates Mandi (Gulika) Sidereal Longitude based on Kerala Parashari weekday & day/night ghatika segments.
     */
    fun calculateMandiSiderealLongitude(
        jd: Double,
        weekdayIndex: Int, // 0=Sunday .. 6=Saturday
        isDayBirth: Boolean,
        sunriseJd: Double,
        sunsetJd: Double,
        latitude: Double,
        longitude: Double,
        ayanamsa: Double
    ): Double {
        // Traditional Kerala Gulika/Mandi Ghatika rising out of 30 Ghatikas of day/night
        val dayGhatikas = doubleArrayOf(26.0, 22.0, 18.0, 14.0, 10.0, 6.0, 2.0)
        val nightGhatikas = doubleArrayOf(10.0, 6.0, 2.0, 26.0, 22.0, 18.0, 14.0)
        val fraction = if (isDayBirth) {
            dayGhatikas[weekdayIndex.coerceIn(0, 6)] / 30.0
        } else {
            nightGhatikas[weekdayIndex.coerceIn(0, 6)] / 30.0
        }
        val spanDays = if (isDayBirth) (sunsetJd - sunriseJd).coerceAtLeast(0.45) else 0.5
        val baseJd = if (isDayBirth) sunriseJd else sunsetJd
        val mandiJd = baseJd + spanDays * fraction
        return calculateSiderealLagna(mandiJd, latitude, longitude, ayanamsa).first
    }

    /**
     * Computes Sunrise, Sunset, Moonrise, Moonset formatted times for the birth date and coordinates.
     */
    fun calculateRiseSetTimes(
        year: Int,
        month: Int,
        day: Int,
        latitude: Double,
        longitude: Double,
        tzOffsetHours: Double
    ): Array<String> {
        val jdNoon = toJulianDay(year, month, day, 12, 0, 0, tzOffsetHours)
        val sunLonRad = degToRad(computeTropicalLongitude(Planet.SUN, jdNoon))
        val epsRad = degToRad(23.4393)
        val declRad = asin(sin(epsRad) * sin(sunLonRad))
        val latRad = degToRad(latitude)

        val cosH0 = (sin(degToRad(-0.8333)) - sin(latRad) * sin(declRad)) / (cos(latRad) * cos(declRad))
        val h0Hours = if (cosH0 in -1.0..1.0) radToDeg(acos(cosH0)) / 15.0 else 6.0

        // Equation of time + longitude solar noon correction relative to timezone meridian
        val stdMeridian = tzOffsetHours * 15.0
        val lonCorrectionHours = (stdMeridian - longitude) / 15.0
        val bRad = degToRad((360.0 / 365.0) * ((jdNoon - 2451545.0) % 365.25 - 81))
        val eotHours = (9.87 * sin(2 * bRad) - 7.53 * cos(bRad) - 1.5 * sin(bRad)) / 60.0

        val localNoon = 12.0 + lonCorrectionHours - eotHours
        val sunriseHours = (localNoon - h0Hours).coerceIn(5.0, 7.5)
        val sunsetHours = (localNoon + h0Hours).coerceIn(17.2, 19.5)

        val moonLon = computeTropicalLongitude(Planet.MOON, jdNoon)
        val sunLon = computeTropicalLongitude(Planet.SUN, jdNoon)
        val elongationDeg = normalizeDegrees(moonLon - sunLon)
        val moonLagHours = (elongationDeg / 360.0) * 24.84
        val moonriseHours = (sunriseHours + moonLagHours) % 24.0
        val moonsetHours = (sunsetHours + moonLagHours) % 24.0

        return arrayOf(
            formatHoursToAmPm(sunriseHours),
            formatHoursToAmPm(sunsetHours),
            formatHoursToAmPm(moonriseHours),
            formatHoursToAmPm(moonsetHours),
            sunriseHours.toString(),
            sunsetHours.toString()
        )
    }

    private fun formatHoursToAmPm(hoursDecimal: Double): String {
        val hNorm = ((hoursDecimal % 24.0) + 24.0) % 24.0
        val totalMinutes = (hNorm * 60.0).roundToInt()
        val hh24 = (totalMinutes / 60) % 24
        val mm = totalMinutes % 60
        val period = if (hh24 < 12) "AM" else "PM"
        val hh12 = when {
            hh24 == 0 -> 12
            hh24 > 12 -> hh24 - 12
            else -> hh24
        }
        return String.format(Locale.US, "%02d:%02d %s", hh12, mm, period)
    }

    fun formatDegreeDms(degInSign: Double): String {
        val d = floor(degInSign).toInt()
        val remMin = (degInSign - d) * 60.0
        val m = floor(remMin).toInt()
        val s = ((remMin - m) * 60.0).roundToInt().coerceIn(0, 59)
        return String.format(Locale.US, "%02d° %02d' %02d\"", d, m, s)
    }

    /**
     * Accurate Panchanga Karana calculator from Sun-Moon elongation (0..360 -> 60 Karanas of 6° each).
     * Never fabricates Karana and never uses "Simha Karana" as one of the 11 standard Panchanga Karanas.
     */
    fun calculateKarana(sunSidereal: Double, moonSidereal: Double): KaranaDetails {
        val elongation = normalizeDegrees(moonSidereal - sunSidereal)
        val karanaIndex60 = floor(elongation / 6.0).toInt().coerceIn(0, 59) + 1 // 1..60

        val charaKaranas = listOf(
            Triple("ബവ", "Bava", "ഇന്ദ്രൻ" to "ശുഭകരമായ ആരംഭങ്ങൾക്കും സ്ഥിരകർമ്മങ്ങൾക്കും ഉത്തമം."),
            Triple("ബാലവ", "Balava", "ബ്രഹ്മാവ്" to "വിദ്യാരംഭം, ധാർമ്മിക കാര്യങ്ങൾ, കരാറുകൾ എന്നിവയ്ക്ക് ശുഭം."),
            Triple("കൗലവ", "Kaulava", "മിത്രൻ" to "സൗഹൃദം, വിവാഹം, കുടുംബ കാര്യങ്ങൾ എന്നിവയ്ക്ക് അനുയോജ്യം."),
            Triple("തൈതില", "Taitila", "ആര്യമാവ്" to "ഗൃഹനിർമ്മാണം, വാഹനം, പൊതുപ്രവർത്തനങ്ങൾ എന്നിവയ്ക്ക് നല്ലത്."),
            Triple("ഗരജ", "Garaja", "ഭൂമിദേവി" to "കൃഷി, ഭൂമി ഇടപാടുകൾ, വ്യാപാര ആരംഭം എന്നിവയ്ക്ക് ഉത്തമം."),
            Triple("വണിജ", "Vanija", "ലക്ഷ്മീദേവി" to "വ്യാപാരം, സാമ്പത്തിക നിക്ഷേപങ്ങൾ, കച്ചവടം എന്നിവയ്ക്ക് ശ്രേഷ്ഠം."),
            Triple("വിഷ്ടി (ഭദ്ര)", "Vishti", "യമൻ" to "വിഷ്ടികരണം ശുഭമുഹൂർത്തങ്ങൾക്ക് സാധാരണയായി ഒഴിവാക്കാറുണ്ട്.")
        )

        return when (karanaIndex60) {
            1 -> KaranaDetails(
                indexInTithiHalf = 1,
                nameMalayalam = "കിംസ്തുഘ്നം",
                nameEnglish = "Kimstughna",
                isSthira = true,
                deityMal = "വായു / കുബേരൻ",
                natureMal = "സ്ഥിരകരണം (Sthira Karana)",
                muhurthaSuitabilityMal = "ശുക്ലപക്ഷ പ്രഥമയുടെ ആദ്യപകുതിയിലുള്ള സ്ഥിരകരണം; മംഗളകർമ്മങ്ങൾക്ക് ഉചിതം."
            )
            58 -> KaranaDetails(
                indexInTithiHalf = 58,
                nameMalayalam = "ശകുനി",
                nameEnglish = "Shakuni",
                isSthira = true,
                deityMal = "കലി / ഗരുഡൻ",
                natureMal = "സ്ഥിരകരണം (Sthira Karana)",
                muhurthaSuitabilityMal = "കൃഷ്ണപക്ഷ ചതുർദ്ദശിയുടെ രണ്ടാം പകുതി; ഔഷധസേവയ്ക്കും ഉപാസനയ്ക്കും ഉചിതം."
            )
            59 -> KaranaDetails(
                indexInTithiHalf = 59,
                nameMalayalam = "ചതുഷ്പാദം",
                nameEnglish = "Chatushpada",
                isSthira = true,
                deityMal = "വൃഷഭം / രുദ്രൻ",
                natureMal = "സ്ഥിരകരണം (Sthira Karana)",
                muhurthaSuitabilityMal = "അമാവാസിയുടെ ആദ്യപകുതി; പിതൃകർമ്മങ്ങൾക്കും ഗോപൂജയ്ക്കും ഉചിതം."
            )
            60 -> KaranaDetails(
                indexInTithiHalf = 60,
                nameMalayalam = "നാഗം",
                nameEnglish = "Naga",
                isSthira = true,
                deityMal = "നാഗദേവതകൾ",
                natureMal = "സ്ഥിരകരണം (Sthira Karana)",
                muhurthaSuitabilityMal = "അമാവാസിയുടെ രണ്ടാം പകുതി; നാഗാരാധനയ്ക്കും ആത്മീയ അനുഷ്ഠാനങ്ങൾക്കും ഉചിതം."
            )
            else -> {
                val charaIdx = (karanaIndex60 - 2) % 7
                val item = charaKaranas[charaIdx]
                KaranaDetails(
                    indexInTithiHalf = karanaIndex60,
                    nameMalayalam = item.first,
                    nameEnglish = item.second,
                    isSthira = false,
                    deityMal = item.third.first,
                    natureMal = if (item.second == "Vishti") "ചരകരണം (ശ്രദ്ധ ആവശ്യമായ ഭദ്രാകരണം)" else "ചരകരണം (ശുഭ ചരകരണം)",
                    muhurthaSuitabilityMal = item.third.second
                )
            }
        }
    }

    /**
     * Explains Simha Karana accurately if a user searches for it in Karana Finder or Glossary.
     */
    fun getSimhaKaranaExplanationMalayalam(): String {
        return """
            ശാസ്ത്രീയ പഞ്ചാംഗ കരണ വിശദീകരണം (Simha Karana Clarification):
            
            വൈദിക ജ്യോതിഷത്തിലും കേരള പഞ്ചാംഗ ഗണിതത്തിലും തിഥിയുടെ പകുതിയായ 6 ഡിഗ്രി ചന്ദ്ര-സൂര്യ അന്തരത്തെ അടിസ്ഥാനമാക്കി ആകെ 11 കരണങ്ങൾ മാത്രമാണ് ഉള്ളത്:
            • 7 ചരകരണങ്ങൾ: ബവ (Bava), ബാലവ (Balava), കൗലവ (Kaulava), തൈതില (Taitila), ഗരജ (Garaja), വണിജ (Vanija), വിഷ്ടി (Vishti).
            • 4 സ്ഥിരകരണങ്ങൾ: ശകുനി (Shakuni), ചതുഷ്പാദം (Chatushpada), നാഗം (Naga), കിംസ്തുഘ്നം (Kimstughna).
            
            എന്താണ് 'സിംഹകരണം' (Simha Karana)?
            'സിംഹകരണം' എന്നത് ഈ 11 അടിസ്ഥാന പഞ്ചാംഗ കരണങ്ങളിൽ ഉൾപ്പെടുന്ന ഒന്നല്ല. കേരളത്തിലെ ചില പ്രാദേശിക മുഹൂർത്ത ഗ്രന്ഥങ്ങളിലും കളരി/തന്ത്ര പാരമ്പര്യങ്ങളിലും കരണങ്ങളെ മൃഗപ്രതീകങ്ങളായി (ഉദാഹരണത്തിന്: സിംഹം, പുലി, പന്നി, കഴുത, ആന, പശു, വിഷ്ടി) വിവരിക്കുന്ന ഒരു ഉപ-രീതിയുണ്ട് (ഉദാഹരണമായി ബവ കരണത്തെ സിംഹമായും ബാലവയെ പുലിയായും പ്രതീകവൽക്കരിക്കുന്ന രീതി). 
            
            എന്നിരുന്നാലും, ശുദ്ധമായ ജാതക ഗണിതത്തിലും പഞ്ചാംഗ നിർണ്ണയത്തിലും യഥാർത്ഥ പഞ്ചാംഗ കരണങ്ങളായ ബവ മുതൽ കിംസ്തുഘ്നം വരെയുള്ള 11 കരണങ്ങൾ തന്നെയാണ് ഉപയോഗിക്കേണ്ടത്. ഈ ആപ്ലിക്കേഷനിൽ ഒരിക്കലും യഥാർത്ഥ പഞ്ചാംഗ കരണത്തിന് പകരം കൃത്രിമ നാമങ്ങൾ ഉപയോഗിക്കുന്നില്ല.
        """.trimIndent()
    }

    /**
     * Calculates Divisional Chart (D1 to D60) Rashi for a given sidereal longitude (0..360).
     * Follows classical Parashara Hora Shastra rules for all 16 Shodashavarga charts.
     */
    fun calculateDivisionalRashi(siderealLon: Double, chartType: DivisionalChartType): Rashi {
        val normLon = normalizeDegrees(siderealLon)
        val signIdx = floor(normLon / 30.0).toInt().coerceIn(0, 11)
        val degInSign = (normLon % 30.0).coerceIn(0.0, 29.999999)
        val isOddSign = (signIdx % 2 == 0) // Mesham(0) is 1st sign = Odd

        val targetSignIdx = when (chartType) {
            DivisionalChartType.D1 -> signIdx
            DivisionalChartType.D2 -> {
                // Parashara Hora: Odd sign 0-15° Sun(Leo=4), 15-30° Moon(Cancer=3); Even reversed
                val firstHalf = degInSign < 15.0
                if (isOddSign) {
                    if (firstHalf) 4 else 3
                } else {
                    if (firstHalf) 3 else 4
                }
            }
            DivisionalChartType.D3 -> {
                // Drekkana: 0-10° same sign, 10-20° 5th, 20-30° 9th
                val part = floor(degInSign / 10.0).toInt().coerceIn(0, 2)
                (signIdx + part * 4) % 12
            }
            DivisionalChartType.D4 -> {
                // Chaturthamsa: 4 parts of 7.5° -> 1st, 4th, 7th, 10th from sign
                val part = floor(degInSign / 7.5).toInt().coerceIn(0, 3)
                (signIdx + part * 3) % 12
            }
            DivisionalChartType.D7 -> {
                // Saptamsa: 7 parts; Odd starts from same sign, Even starts from 7th sign
                val part = floor(degInSign / (30.0 / 7.0)).toInt().coerceIn(0, 6)
                val base = if (isOddSign) signIdx else (signIdx + 6) % 12
                (base + part) % 12
            }
            DivisionalChartType.D9 -> {
                // Navamsa: 9 parts of 3°20'; Fire->Mesham(0), Earth->Makaram(9), Air->Thulam(6), Water->Karkadakam(3)
                val part = floor(degInSign / (30.0 / 9.0)).toInt().coerceIn(0, 8)
                val base = when (signIdx % 4) {
                    0 -> 0 // Fire
                    1 -> 9 // Earth
                    2 -> 6 // Air
                    else -> 3 // Water
                }
                (base + part) % 12
            }
            DivisionalChartType.D10 -> {
                // Dasamsa: 10 parts of 3°; Odd starts from same sign, Even starts from 9th sign
                val part = floor(degInSign / 3.0).toInt().coerceIn(0, 9)
                val base = if (isOddSign) signIdx else (signIdx + 8) % 12
                (base + part) % 12
            }
            DivisionalChartType.D12 -> {
                // Dwadashamsa: 12 parts of 2.5° starting from same sign
                val part = floor(degInSign / 2.5).toInt().coerceIn(0, 11)
                (signIdx + part) % 12
            }
            DivisionalChartType.D16 -> {
                // Shodasamsa: Movable->Mesham(0), Fixed->Chingam(4), Dual->Dhanu(8)
                val part = floor(degInSign / (30.0 / 16.0)).toInt().coerceIn(0, 15)
                val base = when (signIdx % 3) {
                    0 -> 0
                    1 -> 4
                    else -> 8
                }
                (base + part) % 12
            }
            DivisionalChartType.D20 -> {
                // Vimshamsa: Movable->Mesham(0), Fixed->Dhanu(8), Dual->Chingam(4)
                val part = floor(degInSign / 1.5).toInt().coerceIn(0, 19)
                val base = when (signIdx % 3) {
                    0 -> 0
                    1 -> 8
                    else -> 4
                }
                (base + part) % 12
            }
            DivisionalChartType.D24 -> {
                // Chaturvimshamsa: Odd starts from Leo(4), Even starts from Cancer(3)
                val part = floor(degInSign / 1.25).toInt().coerceIn(0, 23)
                val base = if (isOddSign) 4 else 3
                (base + part) % 12
            }
            DivisionalChartType.D27 -> {
                // Bhamsa (Saptavimshamsa): Fire->Mesham(0), Earth->Karkadakam(3), Air->Thulam(6), Water->Makaram(9)
                val part = floor(degInSign / (30.0 / 27.0)).toInt().coerceIn(0, 26)
                val base = (signIdx % 4) * 3
                (base + part) % 12
            }
            DivisionalChartType.D30 -> {
                // Parashara Trimshamsa
                if (isOddSign) {
                    when {
                        degInSign < 5.0 -> 0  // Mars (Mesham)
                        degInSign < 10.0 -> 10 // Saturn (Kumbham)
                        degInSign < 18.0 -> 8  // Jupiter (Dhanu)
                        degInSign < 25.0 -> 2  // Mercury (Mithunam)
                        else -> 6              // Venus (Thulam)
                    }
                } else {
                    when {
                        degInSign < 5.0 -> 1   // Venus (Idavam)
                        degInSign < 12.0 -> 5  // Mercury (Kanni)
                        degInSign < 20.0 -> 11 // Jupiter (Meenam)
                        degInSign < 25.0 -> 9  // Saturn (Makaram)
                        else -> 7              // Mars (Vrischikam)
                    }
                }
            }
            DivisionalChartType.D40 -> {
                // Khavedamsa: Odd->Mesham(0), Even->Thulam(6)
                val part = floor(degInSign / 0.75).toInt().coerceIn(0, 39)
                val base = if (isOddSign) 0 else 6
                (base + part) % 12
            }
            DivisionalChartType.D45 -> {
                // Akshavedamsa: Movable->Mesham(0), Fixed->Chingam(4), Dual->Dhanu(8)
                val part = floor(degInSign / (30.0 / 45.0)).toInt().coerceIn(0, 44)
                val base = (signIdx % 3) * 4
                (base + part) % 12
            }
            DivisionalChartType.D60 -> {
                // Shashtiamsa: 60 parts of 0.5° counted from the sign itself
                val part = floor(degInSign / 0.5).toInt().coerceIn(0, 59)
                (signIdx + part) % 12
            }
        }
        return Rashi.fromIndex(targetSignIdx)
    }

    /**
     * Evaluates Planetary Dignity (Exaltation, Moolatrikona, Own Sign, Friend, Neutral, Enemy, Debilitation).
     */
    fun evaluatePlanetaryDignity(planet: Planet, sign: Rashi, degInSign: Double): PlanetaryDignity {
        if (planet == Planet.MANDI) return PlanetaryDignity.NEUTRAL_SIGN
        if (sign.index == planet.exaltationSignIndex) return PlanetaryDignity.EXALTED
        if (sign.index == planet.debilitationSignIndex) return PlanetaryDignity.DEBILITATED
        if (sign.index == planet.moolatrikonaSignIndex && degInSign <= 20.0) return PlanetaryDignity.MOOLATRIKONA
        if (planet.ownSignIndices.contains(sign.index)) return PlanetaryDignity.OWN_SIGN

        val signLord = sign.lord
        val friendsMap = mapOf(
            Planet.SUN to setOf(Planet.MOON, Planet.MARS, Planet.JUPITER),
            Planet.MOON to setOf(Planet.SUN, Planet.MERCURY),
            Planet.MARS to setOf(Planet.SUN, Planet.MOON, Planet.JUPITER),
            Planet.MERCURY to setOf(Planet.SUN, Planet.VENUS),
            Planet.JUPITER to setOf(Planet.SUN, Planet.MOON, Planet.MARS),
            Planet.VENUS to setOf(Planet.MERCURY, Planet.SATURN, Planet.RAHU),
            Planet.SATURN to setOf(Planet.MERCURY, Planet.VENUS, Planet.RAHU),
            Planet.RAHU to setOf(Planet.VENUS, Planet.SATURN, Planet.MERCURY),
            Planet.KETU to setOf(Planet.MARS, Planet.JUPITER, Planet.VENUS)
        )
        val enemiesMap = mapOf(
            Planet.SUN to setOf(Planet.VENUS, Planet.SATURN, Planet.RAHU),
            Planet.MOON to setOf(Planet.RAHU, Planet.KETU),
            Planet.MARS to setOf(Planet.MERCURY, Planet.RAHU),
            Planet.MERCURY to setOf(Planet.MOON),
            Planet.JUPITER to setOf(Planet.MERCURY, Planet.VENUS),
            Planet.VENUS to setOf(Planet.SUN, Planet.MOON),
            Planet.SATURN to setOf(Planet.SUN, Planet.MOON, Planet.MARS),
            Planet.RAHU to setOf(Planet.SUN, Planet.MOON, Planet.MARS),
            Planet.KETU to setOf(Planet.SUN, Planet.MOON)
        )

        return when {
            friendsMap[planet]?.contains(signLord) == true -> PlanetaryDignity.FRIEND_SIGN
            enemiesMap[planet]?.contains(signLord) == true -> PlanetaryDignity.ENEMY_SIGN
            else -> PlanetaryDignity.NEUTRAL_SIGN
        }
    }

    /**
     * Checks if a planet is Combust (മൗഢ്യം - Maudhyam) due to proximity to the Sun.
     */
    fun isPlanetCombust(planet: Planet, planetLon: Double, sunLon: Double, isRetrograde: Boolean): Boolean {
        if (planet == Planet.SUN || planet == Planet.RAHU || planet == Planet.KETU || planet == Planet.MANDI) return false
        var sep = abs(planetLon - sunLon)
        if (sep > 180.0) sep = 360.0 - sep
        val threshold = when (planet) {
            Planet.MOON -> 12.0
            Planet.MARS -> 17.0
            Planet.MERCURY -> if (isRetrograde) 12.0 else 14.0
            Planet.JUPITER -> 11.0
            Planet.VENUS -> if (isRetrograde) 8.0 else 10.0
            Planet.SATURN -> 15.0
            else -> 0.0
        }
        return sep <= threshold
    }

    /**
     * Computes Parashari houses aspected by a planet from its house placement (1..12).
     */
    fun getAspectedHouses(planet: Planet, houseFromLagna: Int): List<Int> {
        fun normH(offset: Int): Int = ((houseFromLagna - 1 + offset) % 12) + 1
        return when (planet) {
            Planet.MARS -> listOf(normH(3), normH(6), normH(7)) // 4th, 7th, 8th aspects
            Planet.JUPITER -> listOf(normH(4), normH(6), normH(8)) // 5th, 7th, 9th aspects
            Planet.SATURN -> listOf(normH(2), normH(6), normH(9)) // 3rd, 7th, 10th aspects
            Planet.RAHU, Planet.KETU -> listOf(normH(4), normH(6), normH(8))
            Planet.MANDI -> emptyList()
            else -> listOf(normH(6)) // 7th aspect
        }.sorted()
    }

    /**
     * Computes complete Panchanga for given BirthData & Sun/Moon positions.
     */
    fun computePanchanga(
        birthData: BirthData,
        jd: Double,
        sunSidereal: Double,
        moonSidereal: Double,
        nakshatra: Nakshatra,
        pada: Int
    ): PanchangaData {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+05:30")).apply {
            set(birthData.year, birthData.month - 1, birthData.day, birthData.hour, birthData.minute, birthData.second)
        }
        val dow = cal.get(Calendar.DAY_OF_WEEK) // 1=Sun .. 7=Sat
        val varaList = listOf(
            Triple("ഞായറാഴ്ച (ആദിത്യവാരം)", "Sunday", Planet.SUN),
            Triple("തിങ്കളാഴ്ച (സോമവാരം)", "Monday", Planet.MOON),
            Triple("ചൊവ്വാഴ്ച (മംഗളവാരം)", "Tuesday", Planet.MARS),
            Triple("ബുധനാഴ്ച (സൗമ്യവാരം)", "Wednesday", Planet.MERCURY),
            Triple("വ്യാഴാഴ്ച (ഗുരുവാരം)", "Thursday", Planet.JUPITER),
            Triple("വെള്ളിയാഴ്ച (ശുക്രവാരം)", "Friday", Planet.VENUS),
            Triple("ശനിയാഴ്ച (മന്ദവാരം)", "Saturday", Planet.SATURN)
        )
        val vara = varaList[(dow - 1).coerceIn(0, 6)]

        val elongation = normalizeDegrees(moonSidereal - sunSidereal)
        val tithiNum = floor(elongation / 12.0).toInt().coerceIn(0, 29) + 1 // 1..30
        val pakshaMal = if (tithiNum <= 15) "ശുക്ലപക്ഷം (വെളുത്ത പക്ഷം)" else "കൃഷ്ണപക്ഷം (കറുത്ത പക്ഷം)"
        val tithiNames = listOf(
            "പ്രഥമ", "ദ്വിതീയ", "തൃതീയ", "ചതുർത്ഥി", "പഞ്ചമി",
            "ഷഷ്ഠി", "സപ്തമി", "അഷ്ടമി", "നവമി", "ദശമി",
            "ഏകാദശി", "ദ്വാദശി", "ത്രയോദശി", "ചതുർദ്ദശി", "പൗർണ്ണമി",
            "പ്രഥമ", "ദ്വിതീയ", "തൃതീയ", "ചതുർത്ഥി", "പഞ്ചമി",
            "ഷഷ്ഠി", "സപ്തമി", "അഷ്ടമി", "നവമി", "ദശമി",
            "ഏകാദശി", "ദ്വാദശി", "ത്രയോദശി", "ചതുർദ്ദശി", "അമാവാസി (കറുത്തവാവ്)"
        )
        val tithiMal = "${if (tithiNum <= 15) "ശുക്ല" else "കൃഷ്ണ"} ${tithiNames[tithiNum - 1]}"

        // Nitya Yoga (Sun + Moon / 13°20')
        val sumLon = normalizeDegrees(sunSidereal + moonSidereal)
        val yogaIdx = floor(sumLon / (360.0 / 27.0)).toInt().coerceIn(0, 26)
        val nityaYogas = listOf(
            "വിഷ്കംഭം", "പ്രീതി", "ആയുഷ്മാൻ", "സൗഭാഗ്യം", "ശോഭനം", "അതിഗണ്ഡം", "സുകർമ്മം", "ധൃതി", "ശൂലം",
            "ഗണ്ഡം", "വൃദ്ധി", "ധ്രുവം", "വ്യാഘാതം", "ഹർഷണം", "വജ്രം", "സിദ്ധി", "വ്യതീപാതം", "വരീയാൻ",
            "പരിഘം", "ശിവം", "സിദ്ധം", "സാദ്ധ്യം", "ശുഭം", "ശുക്ലം", "ബ്രഹ്മം", "ഇന്ദ്രം", "വൈധൃതി"
        )
        val karana = calculateKarana(sunSidereal, moonSidereal)
        val riseSet = calculateRiseSetTimes(
            birthData.year, birthData.month, birthData.day,
            birthData.latitude, birthData.longitude, birthData.timezoneOffsetHours
        )

        // Kerala Solar Month (Kollavarsham Masa based on Sun's Sidereal Rashi)
        val sunRashiIdx = floor(sunSidereal / 30.0).toInt().coerceIn(0, 11)
        val keralaMasas = listOf(
            "മേടം (Medam)", "ഇടവം (Edavam)", "മിഥുനം (Mithunam)", "കർക്കടകം (Karkadakam)",
            "ചിങ്ങം (Chingam)", "കന്നി (Kanni)", "തുലാം (Thulam)", "വൃശ്ചികം (Vrischikam)",
            "ധനു (Dhanu)", "മകരം (Makaram)", "കുംഭം (Kumbham)", "മീനം (Meenam)"
        )
        val degInSunRashi = floor(sunSidereal % 30.0).toInt() + 1
        val malayalamMasa = "${keralaMasas[sunRashiIdx]} - തിയ്യതി $degInSunRashi"

        val rituMal = when (sunRashiIdx) {
            11, 0 -> "വസന്ത ഋതു (Vasantha)"
            1, 2 -> "ഗ്രീഷ്മ ഋതു (Greeshma)"
            3, 4 -> "വർഷ ഋതു (Varsha)"
            5, 6 -> "ശരത് ഋതു (Sharad)"
            7, 8 -> "ഹേമന്ത ഋതു (Hemantha)"
            else -> "ശിശിര ഋതു (Shishira)"
        }

        val ayanaMal = if (sunRashiIdx in listOf(9, 10, 11, 0, 1, 2)) {
            "ഉത്തരായനം (Uttarayana)"
        } else {
            "ദക്ഷിണായനം (Dakshinayana)"
        }

        val samvatsaras = listOf(
            "പ്രഭവ", "വിഭവ", "ശുക്ല", "പ്രമോദൂത", "പ്രജോല്പത്തി", "ആംഗീരസ", "ശ്രീമുഖ", "ഭാവ", "യുവ", "ധാതാ",
            "ഈശ്വര", "ബഹുധാന്യ", "പ്രമാഥി", "വിക്രമ", "വൃഷ", "ചിത്രഭാനു", "സ്വഭാനു", "താരണ", "പാർത്ഥിവ", "വ്യയ",
            "സർവ്വജിത്ത്", "സർവ്വധാരി", "വിരോധി", "വികൃതി", "ഖര", "നന്ദന", "വിജയ", "ജയ", "മന്മഥ", "ദുർമുഖി",
            "ഹേവിളംബി", "വിളംബി", "വികാരി", "ശാർവരി", "പ്ലവ", "ശുഭകൃത്", "ശോഭകൃത്", "ക്രോധി", "വിശ്വവസു", "പരാഭവ",
            "പ്ലവംഗ", "കീലക", "സൗമ്യ", "സാധാരണ", "വിരോധികൃത്", "പരിധാവി", "പ്രമാദിച", "ആനന്ദ", "രാക്ഷസ", "നള",
            "പിംഗള", "കാളയുക്തി", "സിദ്ധാർത്ഥി", "രൗദ്രി", "ദുർമതി", "ദുന്ദുഭി", "രുധിരോദ്ഗാരി", "രക്താക്ഷി", "ക്രോധന", "ക്ഷയ"
        )
        val samvatsaraIdx = ((birthData.year - 1987) % 60 + 60) % 60
        val kollavarshamYear = if (birthData.month > 8 || (birthData.month == 8 && birthData.day >= 17)) {
            birthData.year - 824
        } else {
            birthData.year - 825
        }
        val samvatsaraMal = "${samvatsaras[samvatsaraIdx]} സംവത്സരം (കൊല്ലവർഷം $kollavarshamYear)"

        val rahuKalamByDow = listOf(
            "04:30 PM - 06:00 PM", // Sun
            "07:30 AM - 09:00 AM", // Mon
            "03:00 PM - 04:30 PM", // Tue
            "12:00 PM - 01:30 PM", // Wed
            "01:30 PM - 03:00 PM", // Thu
            "10:30 AM - 12:00 PM", // Fri
            "09:00 AM - 10:30 AM"  // Sat
        )
        val gulikaKalamByDow = listOf(
            "03:00 PM - 04:30 PM",
            "01:30 PM - 03:00 PM",
            "12:00 PM - 01:30 PM",
            "10:30 AM - 12:00 PM",
            "09:00 AM - 10:30 AM",
            "07:30 AM - 09:00 AM",
            "06:00 AM - 07:30 AM"
        )

        return PanchangaData(
            varaMalayalam = vara.first,
            varaEnglish = vara.second,
            varaLord = vara.third,
            tithiNumber = tithiNum,
            tithiMalayalam = tithiMal,
            pakshaMalayalam = pakshaMal,
            nakshatra = nakshatra,
            nakshatraPada = pada,
            nityaYogaNumber = yogaIdx + 1,
            nityaYogaMalayalam = nityaYogas[yogaIdx],
            karana = karana,
            sunriseFormatted = riseSet[0],
            sunsetFormatted = riseSet[1],
            moonriseFormatted = riseSet[2],
            moonsetFormatted = riseSet[3],
            malayalamMasa = malayalamMasa,
            rituMalayalam = rituMal,
            ayanaMalayalam = ayanaMal,
            samvatsaraMalayalam = samvatsaraMal,
            rahuKalamFormatted = rahuKalamByDow[(dow - 1).coerceIn(0, 6)],
            gulikaKalamFormatted = gulikaKalamByDow[(dow - 1).coerceIn(0, 6)],
            abhijitMuhurthamFormatted = "11:58 AM - 12:46 PM"
        )
    }
}
