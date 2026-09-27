package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.engine.AstronomicalEphemerisEngine
import com.example.model.*
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

/**
 * Generates multi-page Kerala-style Malayalam Horoscope PDF reports with proper Malayalam font rendering,
 * Kerala Rashi & Navamsa charts, Dasha timelines, Yogas, Career, Marriage, Finance, Life Timeline, and Disclaimer.
 */
object JathakamPdfGenerator {

    private const val PAGE_WIDTH = 595 // A4 width in points
    private const val PAGE_HEIGHT = 842 // A4 height in points
    private const val MARGIN = 34f

    fun generatePdfFile(context: Context, report: CompleteJathakamReport): File {
        val pdfDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
        val outFile = File(pdfDir, "Astra_Jathakam_${System.currentTimeMillis()}.pdf")

        val malTypeface = try {
            ResourcesCompat.getFont(context, R.font.noto_sans_malayalam) ?: Typeface.DEFAULT
        } catch (_: Exception) {
            Typeface.DEFAULT
        }
        val boldTypeface = Typeface.create(malTypeface, Typeface.BOLD)

        val pdfDocument = PdfDocument()

        val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTypeface
            textSize = 15f
            color = Color.rgb(13, 19, 34)
        }
        val sectionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = boldTypeface
            textSize = 11.5f
            color = Color.rgb(142, 84, 12)
        }
        val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = malTypeface
            textSize = 9.2f
            color = Color.rgb(30, 35, 45)
        }
        val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = malTypeface
            textSize = 8f
            color = Color.rgb(100, 108, 120)
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.2f
            color = Color.rgb(184, 134, 11)
        }
        val headerBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = Color.rgb(250, 244, 230)
        }

        var pageNumber = 1
        var currentPage = pdfDocument.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
        var canvas = currentPage.canvas
        var yPos = MARGIN

        fun drawPageDecoration(c: Canvas, pageNum: Int) {
            c.drawRect(18f, 18f, PAGE_WIDTH - 18f, PAGE_HEIGHT - 18f, borderPaint)
            c.drawText(
                "Astra Astrology Malayalam • Contact: harikumarg004@gmail.com • ${AstronomicalEphemerisEngine.ENGINE_PUBLIC_NAME}",
                MARGIN,
                PAGE_HEIGHT - 24f,
                footerPaint
            )
            c.drawText("Page $pageNum", PAGE_WIDTH - 75f, PAGE_HEIGHT - 24f, footerPaint)
        }

        fun startNewPageIfNeeded(requiredHeight: Float) {
            if (yPos + requiredHeight > PAGE_HEIGHT - 46f) {
                drawPageDecoration(canvas, pageNumber)
                pdfDocument.finishPage(currentPage)
                pageNumber++
                currentPage = pdfDocument.startPage(PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create())
                canvas = currentPage.canvas
                yPos = MARGIN
            }
        }

        fun drawMultilineBlock(text: String, paint: TextPaint, spacingAfter: Float = 8f) {
            val contentWidth = (PAGE_WIDTH - 2 * MARGIN).toInt()
            val layout = StaticLayout.Builder.obtain(text, 0, text.length, paint, contentWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(2f, 1.08f)
                .build()
            startNewPageIfNeeded(layout.height + spacingAfter)
            canvas.save()
            canvas.translate(MARGIN, yPos)
            layout.draw(canvas)
            canvas.restore()
            yPos += layout.height + spacingAfter
        }

        fun drawSectionHeader(title: String) {
            startNewPageIfNeeded(34f)
            canvas.drawRoundRect(RectF(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos + 22f), 4f, 4f, headerBgPaint)
            canvas.drawText(title, MARGIN + 8f, yPos + 15f, sectionPaint)
            yPos += 28f
        }

        // Main Report Title Header
        canvas.drawRoundRect(RectF(MARGIN, yPos, PAGE_WIDTH - MARGIN, yPos + 54f), 6f, 6f, headerBgPaint)
        canvas.drawText("Astra Astrology Malayalam — സമ്പൂർണ്ണ കേരള ജാതക റിപ്പോർട്ട്", MARGIN + 10f, yPos + 22f, titlePaint)
        canvas.drawText(
            "Developer & Contact: harikumarg004@gmail.com | Calculation System: ${AstronomicalEphemerisEngine.ENGINE_SUBTITLE}",
            MARGIN + 10f,
            yPos + 41f,
            footerPaint
        )
        yPos += 64f

        // 1. Birth Details & Panchanga
        drawSectionHeader("1. ജനന വിവരങ്ങളും പഞ്ചാംഗവും (Birth Details & Kerala Panchanga)")
        val b = report.birthData
        val p = report.panchanga
        drawMultilineBlock(
            """
            പേര്: ${b.name} (${b.gender.malayalam})
            ജനന തീയതി & സമയം: ${String.format(Locale.US, "%02d/%02d/%04d, %02d:%02d:%02d", b.day, b.month, b.year, b.hour, b.minute, b.second)} (${b.timezoneId})
            ജനന സ്ഥലം: ${b.placeNameMalayalam} (${b.placeNameEnglish}), ജില്ല: ${b.districtMalayalam}, ${b.state}
            അക്ഷാംശം / രേഖാംശം: ${String.format(Locale.US, "%.4f° N, %.4f° E", b.latitude, b.longitude)}
            ലഗ്നം: ${report.lagnaRashi.malayalamName} (${AstronomicalEphemerisEngine.formatDegreeDms(report.lagnaDegreeInSign)}) • ലഗ്ന നക്ഷത്രം: ${report.lagnaNakshatra.malayalamName} (പാദം ${report.lagnaPada})
            ചന്ദ്രരാശി (കൂറ്): ${report.chandraRashi.malayalamName} • ജന്മനക്ഷത്രം: ${report.janmaNakshatra.malayalamName} (പാദം ${report.janmaPada})
            വാരം: ${p.varaMalayalam} • തിഥി: ${p.tithiMalayalam} (${p.pakshaMalayalam})
            കരണം: ${p.karana.nameMalayalam} (${p.karana.nameEnglish}) • നിത്യയോഗം: ${p.nityaYogaMalayalam}
            മലയാള മാസം: ${p.malayalamMasa} • സംവത്സരം: ${p.samvatsaraMalayalam} • അയനം: ${p.ayanaMalayalam}
            സൂര്യോദയം: ${p.sunriseFormatted} • സൂര്യാസ്തമയം: ${p.sunsetFormatted} • വിംശോത്തരി ദശാ ശിഷ്ടം: ${report.birthDashaBalanceMal}
            """.trimIndent(),
            bodyPaint
        )

        // 2. Kerala Rashi (D1) & Navamsa (D9) Charts Drawn Visually Side by Side
        drawSectionHeader("2. രാശിചക്രവും (D1) നവാംശക ചക്രവും (D9) — Kerala Style Charts")
        startNewPageIfNeeded(185f)
        drawKeralaChartOnPdfCanvas(
            canvas = canvas,
            left = MARGIN,
            top = yPos,
            size = 165f,
            title = "രാശിചക്രം (D1)",
            lagnaRashi = report.lagnaRashi,
            planetSigns = report.planets.associate { it.planet to it.rashi },
            textPaint = bodyPaint,
            borderPaint = borderPaint
        )
        val d9Chart = report.divisionalCharts[DivisionalChartType.D9]!!
        drawKeralaChartOnPdfCanvas(
            canvas = canvas,
            left = MARGIN + 190f,
            top = yPos,
            size = 165f,
            title = "നവാംശകം (D9)",
            lagnaRashi = d9Chart.lagnaRashi,
            planetSigns = d9Chart.planetSigns,
            textPaint = bodyPaint,
            borderPaint = borderPaint
        )
        yPos += 176f

        // 3. Planetary Positions & Shadbala
        drawSectionHeader("3. ഗ്രഹനില, നക്ഷത്ര പാദം & ഷഡ്ബല സംഗ്രഹം (Planetary Positions)")
        val planetLines = report.planets.joinToString("\n") { pos ->
            "• ${pos.planet.malayalamName}: ${pos.rashi.malayalamName} (${pos.formattedDegree}) | ഭാവം: ${pos.houseFromLagna} | നക്ഷത്രം: ${pos.nakshatra.malayalamName} (${pos.pada}) | ${pos.dignity.malayalam}${if (pos.isRetrograde) " [വക്രം]" else ""}${if (pos.isCombust) " [മൗഢ്യം]" else ""} | ഷഡ്ബലം: ${String.format(Locale.US, "%.1f", pos.shadbalaRupas)} രൂപം (${pos.shadbalaPercentage}%)"
        }
        drawMultilineBlock(planetLines, bodyPaint)

        // 4. Divisional Charts Summary (D1 to D60)
        drawSectionHeader("4. ഷോഡശവർഗ്ഗ ചക്ര സംഗ്രഹം (D1 – D60 Divisional Charts)")
        val divLines = report.divisionalCharts.values.joinToString("\n") { d ->
            "• ${d.chartType.code} ${d.chartType.malayalamName}: ലഗ്നം ${d.lagnaRashi.malayalamName}, സൂര്യൻ ${d.planetSigns[Planet.SUN]?.malayalamName}, ചന്ദ്രൻ ${d.planetSigns[Planet.MOON]?.malayalamName}, വ്യാഴം ${d.planetSigns[Planet.JUPITER]?.malayalamName}, ശുക്രൻ ${d.planetSigns[Planet.VENUS]?.malayalamName}, ശനി ${d.planetSigns[Planet.SATURN]?.malayalamName}"
        }
        drawMultilineBlock(divLines, bodyPaint)

        // 5. Yogas & Doshas
        drawSectionHeader("5. ജാതകത്തിലെ യോഗങ്ങളും ദോഷവിചിന്തനവും (Detected Yogas & Doshas)")
        report.yogasAndDoshas.forEach { yoga ->
            drawMultilineBlock(
                "★ ${yoga.nameMalayalam} [${yoga.strengthClass.malayalam}]\n   വ്യവസ്ഥ: ${yoga.conditionsSatisfiedMal}\n   ഫലം: ${yoga.detailedMalayalamExplanation}\n   ദശാ സജീവത: ${yoga.dashaActivationMal}",
                bodyPaint,
                6f
            )
        }

        // 6. Mahadasha, Antardasha & Pratyantardasha
        drawSectionHeader("6. വിംശോത്തരി മഹാദശ - അപഹാരം - പ്രത്യന്തരദശ ഫലങ്ങൾ")
        drawMultilineBlock(
            """
            നിലവിലെ മഹാദശ: ${report.currentMahadasha.lord.malayalamName} (${report.currentMahadasha.startDateFormatted} - ${report.currentMahadasha.endDateFormatted}) [${report.currentMahadasha.classification.malayalamLabel}]
            നിലവിലെ അപഹാരം (അന്തർദശ): ${report.currentAntardasha.antardashaLord.malayalamName} (${report.currentAntardasha.startDateFormatted} - ${report.currentAntardasha.endDateFormatted}) [${report.currentAntardasha.classification.malayalamLabel}]
            നിലവിലെ പ്രത്യന്തരദശ: ${report.currentPratyantardasha.pratyantardashaLord.malayalamName} (${report.currentPratyantardasha.startDateFormatted} - ${report.currentPratyantardasha.endDateFormatted})
            
            ${report.currentMahadasha.detailedInterpretationMal}
            
            ${report.currentAntardasha.detailedInterpretationMal}
            """.trimIndent(),
            bodyPaint
        )

        // 7. Transit Analysis
        drawSectionHeader("7. ഗോചര ഫലങ്ങൾ (വ്യാഴം, ശനി, രാഹു-കേതു & ദശാ സംയോജനം)")
        drawMultilineBlock(
            "${report.transitReport.jupiterTransitDetailedMal}\n\n${report.transitReport.saturnTransitDetailedMal}\n\n${report.transitReport.dashaTransitCombinedMal}",
            bodyPaint
        )

        // 8. Career, Government Job & PSC
        drawSectionHeader("8. തൊഴിൽ, സർക്കാർ ജോലി, കേരള PSC & KAS വിശകലനം")
        drawMultilineBlock(report.careerGeneralReport.summaryMalayalam, bodyPaint)
        report.careerGeneralReport.detailedSections.forEach { (h, txt) ->
            drawMultilineBlock("• $h: $txt", bodyPaint, 5f)
        }
        val topCareers = report.careerCategories.take(6).joinToString("\n") { c ->
            "• ${c.titleMalayalam} [${c.classification.malayalamLabel}]: ${c.supportingFactorsMal}"
        }
        drawMultilineBlock(topCareers, bodyPaint)

        // 9. Education, Marriage, Partner Personality & Appearance, Finance, Business, Property, Foreign, Family, Children
        val domainList = listOf(
            report.educationReport,
            report.marriageReport,
            report.partnerPersonalityReport,
            report.partnerAppearanceReport,
            report.financeReport,
            report.businessReport,
            report.propertyVehicleReport,
            report.foreignTravelReport,
            report.familyReport,
            report.childrenReport,
            report.spiritualityReport,
            report.healthLifestyleReport
        )
        domainList.forEachIndexed { idx, dom ->
            drawSectionHeader("${9 + idx}. ${dom.titleMalayalam}")
            drawMultilineBlock(dom.summaryMalayalam, bodyPaint, 4f)
            dom.detailedSections.forEach { (sub, txt) ->
                drawMultilineBlock("• $sub: $txt", bodyPaint, 4f)
            }
            drawMultilineBlock("അനുകൂല കാലം: ${dom.supportivePeriodsMal} | ശ്രദ്ധിക്കേണ്ടത്: ${dom.cautionPeriodsMal}", bodyPaint, 6f)
        }

        // Life Timeline
        drawSectionHeader("21. ജീവിത കാലഘട്ട അവലോകനം (0–10 മുതൽ 60+ വരെ)")
        report.lifeTimeline.forEach { stage ->
            drawMultilineBlock(
                "• ${stage.titleMalayalam} [${stage.classification.malayalamLabel}]: ${stage.dominantMahadashaMal}. ${stage.detailedMalayalamNarrative}",
                bodyPaint,
                5f
            )
        }

        // Astrology Disclaimer
        drawSectionHeader("22. ജ്യോതിഷ നിരാകരണവും അറിയിപ്പും (Astrology Disclaimer)")
        drawMultilineBlock(
            """
            അറിയിപ്പ് (Disclaimer): Astra Astrology Malayalam നൽകുന്ന ജാതക ഗണിതവും ഫലപ്രവചനങ്ങളും പരമ്പരാഗത കേരള-പരാശര വൈദിക ജ്യോതിഷ തത്വങ്ങളെ അടിസ്ഥാനമാക്കിയുള്ള സൂചനകൾ മാത്രമാണ്. ഭാവി സംഭവങ്ങളുടെയോ പരീക്ഷാ വിജയങ്ങളുടെയോ വിവാഹ തീയതികളുടെയോ ഉറപ്പായ വാഗ്ദാനമല്ല. വൈദ്യശാസ്ത്രപരമോ നിയമപരമോ സാമ്പത്തികമോ ആയ കാര്യങ്ങൾക്ക് അതത് മേഖലകളിലെ അംഗീകൃത വിദഗ്ദ്ധരുടെ ഉപദേശം തേടേണ്ടതാണ്.
            Application: Astra Astrology Malayalam | Developer & Support Contact: harikumarg004@gmail.com
            """.trimIndent(),
            bodyPaint
        )

        drawPageDecoration(canvas, pageNumber)
        pdfDocument.finishPage(currentPage)

        FileOutputStream(outFile).use { fos ->
            pdfDocument.writeTo(fos)
        }
        pdfDocument.close()
        return outFile
    }

    private fun drawKeralaChartOnPdfCanvas(
        canvas: Canvas,
        left: Float,
        top: Float,
        size: Float,
        title: String,
        lagnaRashi: Rashi,
        planetSigns: Map<Planet, Rashi>,
        textPaint: TextPaint,
        borderPaint: Paint
    ) {
        val cell = size / 4f
        canvas.drawRect(left, top, left + size, top + size, borderPaint)
        // Outer 12 cells coordinates (row, col) for Rashi 0..11 (Mesham at row 0, col 1)
        val rashiCoords = listOf(
            0 to 1, 0 to 2, 0 to 3,
            1 to 3, 2 to 3, 3 to 3,
            3 to 2, 3 to 1, 3 to 0,
            2 to 0, 1 to 0, 0 to 0
        )
        rashiCoords.forEachIndexed { rashiIdx, (r, c) ->
            val x = left + c * cell
            val y = top + r * cell
            canvas.drawRect(x, y, x + cell, y + cell, borderPaint)
            val rashi = Rashi.fromIndex(rashiIdx)
            val occupants = mutableListOf<String>()
            if (lagnaRashi == rashi) occupants.add("ല")
            planetSigns.forEach { (planet, pRashi) ->
                if (pRashi == rashi) occupants.add(planet.shortCodeMal)
            }
            val label = occupants.joinToString(" ")
            if (label.isNotEmpty()) {
                canvas.drawText(label.take(10), x + 4f, y + cell / 2f, textPaint)
                if (label.length > 10) {
                    canvas.drawText(label.drop(10), x + 4f, y + cell / 2f + 11f, textPaint)
                }
            }
        }
        canvas.drawText(title, left + cell + 8f, top + size / 2f, textPaint)
    }

    /**
     * Renders pages of a generated PDF file into Bitmaps for in-app preview.
     */
    fun renderPdfPagesForPreview(pdfFile: File): List<Bitmap> {
        val bitmaps = mutableListOf<Bitmap>()
        try {
            val pfd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(pfd)
            val count = renderer.pageCount
            for (i in 0 until count) {
                val page = renderer.openPage(i)
                val bmp = Bitmap.createBitmap(page.width * 2, page.height * 2, Bitmap.Config.ARGB_8888)
                bmp.eraseColor(Color.WHITE)
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                bitmaps.add(bmp)
            }
            renderer.close()
            pfd.close()
        } catch (_: Exception) {
        }
        return bitmaps
    }

    fun sharePdf(context: Context, pdfFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Astra Astrology Malayalam — സമ്പൂർണ്ണ ജാതക റിപ്പോർട്ട്")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Jathakam PDF Report"))
    }

    /**
     * Generates a Full HD / 2K (2048x2048) lossless PNG file of the Astra Astrology Malayalam logo
     * so users can save or share the Full HD emblem directly from the app.
     */
    fun generateFullHdLogoFile(context: Context): File {
        val outDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
        val logoFile = File(outDir, "Astra_Astrology_Malayalam_Logo_FullHD.png")
        val sizePx = 2048
        val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.rgb(11, 15, 25)) // #0B0F19 deep obsidian background

        val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.img_app_icon, null)
        if (drawable != null) {
            drawable.setBounds(0, 0, sizePx, sizePx)
            drawable.draw(canvas)
        }

        // Subtle outer golden ring frame for Full HD finish
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 20f
            color = Color.rgb(229, 193, 88)
        }
        canvas.drawRect(24f, 24f, sizePx - 24f, sizePx - 24f, ringPaint)

        FileOutputStream(logoFile).use { out ->
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }
        return logoFile
    }

    fun shareFullHdLogo(context: Context, logoFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            logoFile
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Astra Astrology Malayalam — Full HD Logo (2048x2048 PNG)")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Full HD Logo PNG"))
    }

    /**
     * Copies the currently installed APK package of this app to a shareable/exportable file.
     */
    fun exportInstalledApkFile(context: Context): File {
        val outDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
        val targetApk = File(outDir, "app-debug.apk")
        val sourceApk = File(context.applicationInfo.sourceDir)
        sourceApk.inputStream().use { input ->
            FileOutputStream(targetApk).use { output ->
                input.copyTo(output)
            }
        }
        return targetApk
    }

    fun shareApkFile(context: Context, apkFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.android.package-archive"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Astra Astrology Malayalam — app-debug.apk")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share APK File"))
    }

    /**
     * Uploads a file to litterbox.catbox.moe (72h temporary hosting) and returns the direct download link.
     */
    fun uploadFileToLitterbox(file: File, uploadFileName: String): Result<String> {
        return try {
            val boundary = "----AstraBoundary" + System.currentTimeMillis()
            val url = java.net.URL("https://litterbox.catbox.moe/resources/internals/api.php")
            val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                doInput = true
                useCaches = false
                connectTimeout = 45000
                readTimeout = 90000
                setRequestProperty("User-Agent", "AstraAstrologyMalayalam/1.0")
                setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            }

            conn.outputStream.buffered().use { out ->
                fun writeText(text: String) {
                    out.write(text.toByteArray(Charsets.UTF_8))
                }

                // reqtype field
                writeText("--$boundary\r\n")
                writeText("Content-Disposition: form-data; name=\"reqtype\"\r\n\r\n")
                writeText("fileupload\r\n")

                // time field
                writeText("--$boundary\r\n")
                writeText("Content-Disposition: form-data; name=\"time\"\r\n\r\n")
                writeText("72h\r\n")

                // fileToUpload field
                writeText("--$boundary\r\n")
                writeText("Content-Disposition: form-data; name=\"fileToUpload\"; filename=\"$uploadFileName\"\r\n")
                writeText("Content-Type: application/octet-stream\r\n\r\n")

                file.inputStream().buffered().use { input ->
                    input.copyTo(out)
                }
                writeText("\r\n--$boundary--\r\n")
                out.flush()
            }

            val code = conn.responseCode
            val responseText = (if (code in 200..299) conn.inputStream else conn.errorStream)
                ?.bufferedReader(Charsets.UTF_8)
                ?.readText()
                ?.trim()
                .orEmpty()

            if (code in 200..299 && responseText.startsWith("https://")) {
                Result.success(responseText)
            } else {
                Result.failure(IllegalStateException("HTTP $code: $responseText"))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}
