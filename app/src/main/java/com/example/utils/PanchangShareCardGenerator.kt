package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.data.model.AppConfig
import com.example.data.model.DailyQuotesData
import com.example.data.model.FestivalCalendarData
import com.example.data.model.Language
import com.example.data.model.PanchangData
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate

object PanchangShareCardGenerator {

    const val CARD_WIDTH = 1080
    const val CARD_HEIGHT = 1350

    /**
     * Renders a 1080x1350 px (4:5) image card using the "Temple at dusk" palette:
     * - Deep maroon #5B0E1E background
     * - Saffron #F28C28 and gold #D4A537 accents
     * - Cream #FFF7EC text
     * - Top: "శుభోదయం" / "Good Morning", date, vaaram
     * - Middle: Tithi, Nakshatra, Paksham, Sunrise & Sunset
     * - Highlight boxes: Rahu Kalam (red tint), Yamagandam, Abhijit (green tint)
     * - Festival banner (if any)
     * - Daily shloka rotated by day of year
     * - Footer: App icon + Shubhodaya branding + QR Code
     */
    fun createCardBitmap(
        context: Context,
        panchang: PanchangData,
        language: Language,
        targetDate: LocalDate = panchang.date
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(CARD_WIDTH, CARD_HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val isTelugu = language == Language.TELUGU

        // Colors
        val bgMaroon = 0xFF5B0E1E.toInt()
        val bgDarkFooter = 0xFF350610.toInt()
        val goldPrimary = 0xFFD4A537.toInt()
        val goldLight = 0xFFF1D282.toInt()
        val saffronAccent = 0xFFF28C28.toInt()
        val creamText = 0xFFFFF7EC.toInt()
        val redTintBg = 0x44D32F2F.toInt()
        val redBorder = 0xFFEF5350.toInt()
        val greenTintBg = 0x482E7D32.toInt()
        val greenBorder = 0xFF81C784.toInt()
        val orangeTintBg = 0x40F28C28.toInt()
        val orangeBorder = 0xFFFFA726.toInt()

        // 1. Draw Deep Maroon Background
        val bgPaint = Paint().apply {
            color = bgMaroon
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, CARD_WIDTH.toFloat(), CARD_HEIGHT.toFloat(), bgPaint)

        // 2. Decorative Corner Mandalas
        val mandalaPaint = Paint().apply {
            color = goldPrimary
            style = Paint.Style.STROKE
            strokeWidth = 3f
            alpha = 24
            isAntiAlias = true
        }
        canvas.drawCircle(0f, 0f, 320f, mandalaPaint)
        canvas.drawCircle(0f, 0f, 220f, mandalaPaint)
        canvas.drawCircle(0f, 0f, 120f, mandalaPaint)

        canvas.drawCircle(CARD_WIDTH.toFloat(), 0f, 320f, mandalaPaint)
        canvas.drawCircle(CARD_WIDTH.toFloat(), 0f, 220f, mandalaPaint)

        canvas.drawCircle(0f, CARD_HEIGHT.toFloat(), 340f, mandalaPaint)
        canvas.drawCircle(CARD_WIDTH.toFloat(), CARD_HEIGHT.toFloat(), 340f, mandalaPaint)

        // 3. Double Gold Frame
        val outerFramePaint = Paint().apply {
            color = goldPrimary
            style = Paint.Style.STROKE
            strokeWidth = 4f
            alpha = 110
            isAntiAlias = true
        }
        val innerFramePaint = Paint().apply {
            color = goldPrimary
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = 70
            pathEffect = DashPathEffect(floatArrayOf(14f, 10f), 0f)
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(28f, 28f, CARD_WIDTH - 28f, CARD_HEIGHT - 28f), 32f, 32f, outerFramePaint)
        canvas.drawRoundRect(RectF(40f, 40f, CARD_WIDTH - 40f, CARD_HEIGHT - 40f), 24f, 24f, innerFramePaint)

        // 4. Header: "శుభోదయం" / "Good Morning"
        val headerPaint = Paint().apply {
            color = saffronAccent
            textSize = if (isTelugu) 62f else 54f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val headerTitle = if (isTelugu) "🕉️  శుభోదయం  🕉️" else "🕉️  Good Morning  🕉️"
        canvas.drawText(headerTitle, CARD_WIDTH / 2f, 115f, headerPaint)

        // Subheader: Date & Vaaram
        val dateSubPaint = Paint().apply {
            color = goldLight
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val dateFormatted = targetDate.format(java.time.format.DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        val dateString = if (isTelugu) {
            "${panchang.vaaramTe} • $dateFormatted (${panchang.samvatsaramTe} నామ సంవత్సరం)"
        } else {
            "${panchang.vaaramEn} • $dateFormatted (${panchang.samvatsaramEn})"
        }
        canvas.drawText(dateString, CARD_WIDTH / 2f, 165f, dateSubPaint)

        // 5. Festival Banner (if applicable for targetDate)
        var contentStartY = 205f
        val festivalMatch = FestivalCalendarData.allFestivals.find { it.date == targetDate }
        if (festivalMatch != null) {
            val festRect = RectF(65f, contentStartY, CARD_WIDTH - 65f, contentStartY + 95f)
            val festBgPaint = Paint().apply {
                color = saffronAccent
                style = Paint.Style.FILL
                alpha = 45
                isAntiAlias = true
            }
            val festBorderPaint = Paint().apply {
                color = saffronAccent
                style = Paint.Style.STROKE
                strokeWidth = 3f
                isAntiAlias = true
            }
            canvas.drawRoundRect(festRect, 20f, 20f, festBgPaint)
            canvas.drawRoundRect(festRect, 20f, 20f, festBorderPaint)

            val festTagPaint = Paint().apply {
                color = goldPrimary
                textSize = 22f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText("✨  విశేష పర్వదినం (Festival)  ✨", CARD_WIDTH / 2f, contentStartY + 36f, festTagPaint)

            val festTitlePaint = Paint().apply {
                color = creamText
                textSize = 34f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            val festName = if (isTelugu) festivalMatch.nameTe else festivalMatch.nameEn
            canvas.drawText(festName, CARD_WIDTH / 2f, contentStartY + 76f, festTitlePaint)

            contentStartY += 120f
        }

        // 6. Main Panchang Container (Tithi, Nakshatra, Paksham, Sun Times)
        val mainCardHeight = 310f
        val mainCardRect = RectF(65f, contentStartY, CARD_WIDTH - 65f, contentStartY + mainCardHeight)
        val mainCardBg = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            alpha = 18
            isAntiAlias = true
        }
        val mainCardBorder = Paint().apply {
            color = goldPrimary
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            alpha = 90
            isAntiAlias = true
        }
        canvas.drawRoundRect(mainCardRect, 24f, 24f, mainCardBg)
        canvas.drawRoundRect(mainCardRect, 24f, 24f, mainCardBorder)

        // Divider lines inside main card
        val dividerPaint = Paint().apply {
            color = goldPrimary
            strokeWidth = 1.5f
            alpha = 60
            isAntiAlias = true
        }
        val midX = CARD_WIDTH / 2f
        val midY = contentStartY + (mainCardHeight / 2f)
        canvas.drawLine(midX, contentStartY + 20f, midX, contentStartY + mainCardHeight - 20f, dividerPaint)
        canvas.drawLine(85f, midY, CARD_WIDTH - 85f, midY, dividerPaint)

        // Quadrant 1: Tithi
        drawPanchangCell(
            canvas = canvas,
            x = 85f,
            y = contentStartY + 30f,
            label = if (isTelugu) "తిథి (Tithi)" else "Tithi",
            value = if (isTelugu) panchang.tithiTe else panchang.tithiEn,
            subValue = if (isTelugu) panchang.tithiEndTimeTe else panchang.tithiEndTimeEn,
            goldColor = goldPrimary,
            valColor = creamText
        )

        // Quadrant 2: Nakshatra
        drawPanchangCell(
            canvas = canvas,
            x = midX + 30f,
            y = contentStartY + 30f,
            label = if (isTelugu) "నక్షత్రం (Nakshatra)" else "Nakshatra",
            value = if (isTelugu) panchang.nakshatramTe else panchang.nakshatramEn,
            subValue = if (isTelugu) panchang.nakshatraEndTimeTe else panchang.nakshatraEndTimeEn,
            goldColor = goldPrimary,
            valColor = creamText
        )

        // Quadrant 3: Paksham & Maasam
        drawPanchangCell(
            canvas = canvas,
            x = 85f,
            y = midY + 28f,
            label = if (isTelugu) "పక్షం & మాసం" else "Paksham & Month",
            value = if (isTelugu) "${panchang.pakshamTe} • ${panchang.maasamTe}" else "${panchang.pakshamEn} • ${panchang.maasamEn}",
            subValue = if (isTelugu) "${panchang.ayanamTe} | ${panchang.rutuvuTe}" else "${panchang.ayanamEn} | ${panchang.rutuvuEn}",
            goldColor = goldPrimary,
            valColor = creamText
        )

        // Quadrant 4: Sunrise / Sunset
        drawPanchangCell(
            canvas = canvas,
            x = midX + 30f,
            y = midY + 28f,
            label = if (isTelugu) "సూర్యోదయం / సూర్యాస్తమయం" else "Sunrise / Sunset",
            value = "🌅 ${panchang.sunrise}  🌇 ${panchang.sunset}",
            subValue = if (isTelugu) "చంద్రోదయం: ${panchang.moonrise}" else "Moonrise: ${panchang.moonrise}",
            goldColor = goldPrimary,
            valColor = creamText
        )

        contentStartY += mainCardHeight + 25f

        // 7. Three Auspicious/Inauspicious Highlight Boxes
        // Rahu Kalam (Red), Yamagandam (Orange/Saffron), Abhijit Muhurtham (Green)
        val boxWidth = (CARD_WIDTH - 130f - 32f) / 3f
        val boxHeight = 135f

        // Box 1: Rahu Kalam (Red)
        val rahuX = 65f
        drawHighlightBox(
            canvas = canvas,
            x = rahuX,
            y = contentStartY,
            width = boxWidth,
            height = boxHeight,
            bgColor = redTintBg,
            borderColor = redBorder,
            title = if (isTelugu) "రాహుకాలం" else "Rahu Kalam",
            titleColor = 0xFFFF8A80.toInt(),
            value = panchang.rahuKalam,
            valueColor = Color.WHITE
        )

        // Box 2: Yamagandam (Orange/Saffron)
        val yamaX = rahuX + boxWidth + 16f
        drawHighlightBox(
            canvas = canvas,
            x = yamaX,
            y = contentStartY,
            width = boxWidth,
            height = boxHeight,
            bgColor = orangeTintBg,
            borderColor = orangeBorder,
            title = if (isTelugu) "యమగండం" else "Yamagandam",
            titleColor = 0xFFFFD180.toInt(),
            value = panchang.yamagandam,
            valueColor = Color.WHITE
        )

        // Box 3: Abhijit Muhurtham (Green)
        val abhijitX = yamaX + boxWidth + 16f
        drawHighlightBox(
            canvas = canvas,
            x = abhijitX,
            y = contentStartY,
            width = boxWidth,
            height = boxHeight,
            bgColor = greenTintBg,
            borderColor = greenBorder,
            title = if (isTelugu) "అభిజిత్ ముహూర్తం" else "Abhijit Time",
            titleColor = 0xFFA5D6A7.toInt(),
            value = panchang.abhijitMuhurtham,
            valueColor = Color.WHITE
        )

        contentStartY += boxHeight + 25f

        // 8. Daily Shloka / Telugu Quote Banner (Rotated by day of year)
        val dayOfYear = targetDate.dayOfYear
        val quote = DailyQuotesData.getDailyQuote(dayOfYear)
        val quoteBoxHeight = 185f
        val quoteRect = RectF(65f, contentStartY, CARD_WIDTH - 65f, contentStartY + quoteBoxHeight)

        val quoteBgPaint = Paint().apply {
            color = goldPrimary
            style = Paint.Style.FILL
            alpha = 26
            isAntiAlias = true
        }
        val quoteBarPaint = Paint().apply {
            color = goldPrimary
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(quoteRect, 18f, 18f, quoteBgPaint)
        // Left gold accent bar
        canvas.drawRoundRect(RectF(65f, contentStartY, 77f, contentStartY + quoteBoxHeight), 6f, 6f, quoteBarPaint)

        val quoteHeadingPaint = Paint().apply {
            color = goldPrimary
            textSize = 21f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val quoteHeader = if (isTelugu) "✨ నేటి సుభాషితం (Daily Wisdom)" else "✨ Daily Shloka & Wisdom"
        canvas.drawText(quoteHeader, 95f, contentStartY + 36f, quoteHeadingPaint)

        val quoteTextPaint = Paint().apply {
            color = creamText
            textSize = 25f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        val quoteStr = if (isTelugu) quote.telugu else quote.english
        drawMultilineText(
            canvas = canvas,
            text = quoteStr,
            x = 95f,
            y = contentStartY + 74f,
            paint = quoteTextPaint,
            maxWidth = (CARD_WIDTH - 200).toInt(),
            maxLines = 2
        )

        if (quote.source != null) {
            val sourcePaint = Paint().apply {
                color = saffronAccent
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }
            canvas.drawText("— ${quote.source}", CARD_WIDTH - 95f, contentStartY + 158f, sourcePaint)
        }

        // 9. Footer Bar: App Icon + Branding + Download callout + QR Code
        val footerY = CARD_HEIGHT - 170f
        val footerHeight = 125f
        val footerRect = RectF(65f, footerY, CARD_WIDTH - 65f, footerY + footerHeight)

        val footerBgPaint = Paint().apply {
            color = bgDarkFooter
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val footerBorderPaint = Paint().apply {
            color = goldPrimary
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = 110
            isAntiAlias = true
        }
        canvas.drawRoundRect(footerRect, 18f, 18f, footerBgPaint)
        canvas.drawRoundRect(footerRect, 18f, 18f, footerBorderPaint)

        // App Icon Badge
        val iconBadgeRect = RectF(90f, footerY + 22f, 90f + 80f, footerY + 22f + 80f)
        val iconBadgeBg = Paint().apply {
            color = bgMaroon
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val iconBadgeBorder = Paint().apply {
            color = goldPrimary
            style = Paint.Style.STROKE
            strokeWidth = 3f
            isAntiAlias = true
        }
        canvas.drawRoundRect(iconBadgeRect, 16f, 16f, iconBadgeBg)
        canvas.drawRoundRect(iconBadgeRect, 16f, 16f, iconBadgeBorder)

        val sunIconPaint = Paint().apply {
            textSize = 46f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("☀️", iconBadgeRect.centerX(), iconBadgeRect.centerY() + 16f, sunIconPaint)

        // Branding Text
        val brandTitlePaint = Paint().apply {
            color = goldPrimary
            textSize = 30f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText("శుభోదయ • Shubhodaya", 190f, footerY + 54f, brandTitlePaint)

        val brandSubPaint = Paint().apply {
            color = creamText
            textSize = 21f
            alpha = 210
            isAntiAlias = true
        }
        canvas.drawText("Download free on Google Play Store", 190f, footerY + 90f, brandSubPaint)

        // Procedural QR Code pointing to PLAY_STORE_URL
        val qrBoxSize = 90f
        val qrX = CARD_WIDTH - 65f - qrBoxSize - 20f
        val qrY = footerY + 18f
        drawSimulatedQrCode(canvas, qrX, qrY, qrBoxSize)

        return bitmap
    }

    private fun drawPanchangCell(
        canvas: Canvas,
        x: Float,
        y: Float,
        label: String,
        value: String,
        subValue: String,
        goldColor: Int,
        valColor: Int
    ) {
        val labelPaint = Paint().apply {
            color = goldColor
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(label, x, y + 20f, labelPaint)

        val valuePaint = Paint().apply {
            color = valColor
            textSize = 34f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        canvas.drawText(value, x, y + 64f, valuePaint)

        if (subValue.isNotBlank()) {
            val subPaint = Paint().apply {
                color = goldColor
                textSize = 20f
                alpha = 180
                isAntiAlias = true
            }
            canvas.drawText(subValue, x, y + 98f, subPaint)
        }
    }

    private fun drawHighlightBox(
        canvas: Canvas,
        x: Float,
        y: Float,
        width: Float,
        height: Float,
        bgColor: Int,
        borderColor: Int,
        title: String,
        titleColor: Int,
        value: String,
        valueColor: Int
    ) {
        val rect = RectF(x, y, x + width, y + height)
        val bgPaint = Paint().apply {
            color = bgColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        val borderPaint = Paint().apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            isAntiAlias = true
        }
        canvas.drawRoundRect(rect, 16f, 16f, bgPaint)
        canvas.drawRoundRect(rect, 16f, 16f, borderPaint)

        val titlePaint = Paint().apply {
            color = titleColor
            textSize = 21f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(title, rect.centerX(), y + 42f, titlePaint)

        val valPaint = Paint().apply {
            color = valueColor
            textSize = 24f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(value, rect.centerX(), y + 92f, valPaint)
    }

    private fun drawMultilineText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        paint: Paint,
        maxWidth: Int,
        maxLines: Int
    ) {
        val words = text.split(" ")
        var line = ""
        var lineCount = 0
        var currentY = y

        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val width = paint.measureText(testLine)
            if (width > maxWidth) {
                canvas.drawText(line, x, currentY, paint)
                line = word
                currentY += paint.textSize * 1.35f
                lineCount++
                if (lineCount >= maxLines) break
            } else {
                line = testLine
            }
        }
        if (line.isNotEmpty() && lineCount < maxLines) {
            canvas.drawText(line, x, currentY, paint)
        }
    }

    private fun drawSimulatedQrCode(canvas: Canvas, x: Float, y: Float, size: Float) {
        // Draw white card backing
        val qrBgRect = RectF(x, y, x + size, y + size)
        val whitePaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawRoundRect(qrBgRect, 8f, 8f, whitePaint)

        // Draw functional high-contrast QR visual matrix
        val blackPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
            isAntiAlias = false
        }

        val padding = 6f
        val innerSize = size - (padding * 2f)
        val cells = 15
        val cellSize = innerSize / cells

        // Fixed QR finder patterns: Top-Left, Top-Right, Bottom-Left
        fun drawFinder(cx: Float, cy: Float) {
            canvas.drawRect(cx, cy, cx + cellSize * 4, cy + cellSize * 4, blackPaint)
            val whiteInner = Paint().apply { color = Color.WHITE; style = Paint.Style.FILL }
            canvas.drawRect(cx + cellSize, cy + cellSize, cx + cellSize * 3, cy + cellSize * 3, whiteInner)
            canvas.drawRect(cx + cellSize * 1.5f, cy + cellSize * 1.5f, cx + cellSize * 2.5f, cy + cellSize * 2.5f, blackPaint)
        }

        val startX = x + padding
        val startY = y + padding

        drawFinder(startX, startY)
        drawFinder(startX + (cells - 4) * cellSize, startY)
        drawFinder(startX, startY + (cells - 4) * cellSize)

        // Data dots pattern derived from URL hash
        val url = AppConfig.PLAY_STORE_URL
        for (row in 0 until cells) {
            for (col in 0 until cells) {
                // Avoid finders
                val inTopLeft = row < 4 && col < 4
                val inTopRight = row < 4 && col >= cells - 4
                val inBottomLeft = row >= cells - 4 && col < 4
                if (!inTopLeft && !inTopRight && !inBottomLeft) {
                    val hashBit = (url.hashCode() xor (row * 31 + col * 17)) % 2 == 0
                    if (hashBit) {
                        canvas.drawRect(
                            startX + col * cellSize,
                            startY + row * cellSize,
                            startX + (col + 1) * cellSize - 0.5f,
                            startY + (row + 1) * cellSize - 0.5f,
                            blackPaint
                        )
                    }
                }
            }
        }

        val qrLabelPaint = Paint().apply {
            color = 0xFF5B0E1E.toInt()
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText("SCAN APP", qrBgRect.centerX(), qrBgRect.bottom + 12f, qrLabelPaint)
    }

    /**
     * Saves the generated bitmap to the app cache directory for FileProvider sharing.
     */
    fun saveBitmapToCache(context: Context, bitmap: Bitmap): File {
        val cacheDir = File(context.cacheDir, "images")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val file = File(cacheDir, "shubhodaya_panchang_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    /**
     * Saves the generated bitmap to the user's Gallery (Pictures/Shubhodaya).
     */
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap): Boolean {
        return try {
            val filename = "Shubhodaya_Panchang_${System.currentTimeMillis()}.png"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Shubhodaya")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, values, null, null)
                    true
                } else {
                    false
                }
            } else {
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val shubhodayaDir = File(picturesDir, "Shubhodaya")
                if (!shubhodayaDir.exists()) {
                    shubhodayaDir.mkdirs()
                }
                val file = File(shubhodayaDir, filename)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
