package com.example.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import com.example.data.model.AppConfig
import com.example.data.model.Language
import com.example.data.model.NumerologyProfile
import com.example.data.numerology.NumerologyCalculator
import com.example.data.numerology.NumerologyData
import java.time.format.DateTimeFormatter

object NumerologyShareCardGenerator {
    private const val WIDTH = 1080
    private const val HEIGHT = 1350
    private const val MARGIN = 72f

    private const val MAROON = 0xFF5B0E1E.toInt()
    private const val MAROON_DARK = 0xFF350610.toInt()
    private const val GOLD = 0xFFD4A537.toInt()
    private const val GOLD_LIGHT = 0xFFF1D282.toInt()
    private const val SAFFRON = 0xFFF28C28.toInt()
    private const val CREAM = 0xFFFFF7EC.toInt()
    private const val MUTED_CREAM = 0xFFD7C7B7.toInt()

    fun createCardBitmap(
        profile: NumerologyProfile,
        language: Language
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val isTelugu = language == Language.TELUGU

        // Background
        canvas.drawColor(MAROON)

        // Outer ornate border
        canvas.drawRoundRect(
            RectF(28f, 28f, WIDTH - 28f, HEIGHT - 28f),
            32f,
            32f,
            strokePaint(GOLD, 4f)
        )
        canvas.drawRoundRect(
            RectF(38f, 38f, WIDTH - 38f, HEIGHT - 38f),
            24f,
            24f,
            strokePaint(GOLD.and(0x44FFFFFF), 1.5f)
        )

        // Header Title
        val headerTitle = if (isTelugu) "శుభోదయ సంఖ్యా శాస్త్రం" else "Shubhodaya Numerology"
        val titlePaint = textPaint(SAFFRON, 48f, bold = true, center = true)
        canvas.drawText(headerTitle, WIDTH / 2f, 100f, titlePaint)

        // Subtitle / Profile Name & DOB
        val namePaint = textPaint(CREAM, 38f, bold = true, center = true)
        canvas.drawText(profile.fullName.uppercase(), WIDTH / 2f, 155f, namePaint)

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val dobText = "${if (isTelugu) "జనన తేదీ:" else "DOB:"} ${profile.birthDate.format(formatter)}"
        val dobPaint = textPaint(GOLD_LIGHT, 24f, bold = false, center = true)
        canvas.drawText(dobText, WIDTH / 2f, 195f, dobPaint)

        canvas.drawLine(MARGIN, 220f, WIDTH - MARGIN, 220f, strokePaint(GOLD, 2f))

        // Calculations
        val moolank = NumerologyCalculator.calculateMoolank(profile.birthDate.dayOfMonth)
        val bhagyankRes = NumerologyCalculator.calculateBhagyank(profile.birthDate)
        val nameRes = NumerologyCalculator.calculateNameNumber(profile.fullName)
        val moolankInfo = NumerologyData.getInfo(moolank)
        val bhagyankInfo = NumerologyData.getInfo(bhagyankRes.singleDigit)
        val nameInfo = NumerologyData.getInfo(nameRes.singleDigit)

        // Draw 3 Big Number Cards
        val cardWidth = (WIDTH - 2 * MARGIN - 40f) / 3f
        val cardHeight = 220f
        val cardY = 245f

        val cardsData = listOf(
            Triple(if (isTelugu) "మూలాంకం" else "Moolank", moolank.toString(), if (isTelugu) moolankInfo.planetNameTe else moolankInfo.planetNameEn),
            Triple(if (isTelugu) "భాగ్యాంకం" else "Bhagyank", if (bhagyankRes.masterNumber != null) "${bhagyankRes.singleDigit} (${bhagyankRes.masterNumber})" else bhagyankRes.singleDigit.toString(), if (isTelugu) bhagyankInfo.planetNameTe else bhagyankInfo.planetNameEn),
            Triple(if (isTelugu) "నామ సంఖ్య" else "Name No.", "${nameRes.singleDigit} (${nameRes.compoundTotal})", if (isTelugu) nameInfo.planetNameTe else nameInfo.planetNameEn)
        )

        cardsData.forEachIndexed { i, (label, value, planet) ->
            val left = MARGIN + i * (cardWidth + 20f)
            val rect = RectF(left, cardY, left + cardWidth, cardY + cardHeight)

            // Card background
            val bgPaint = Paint().apply {
                color = MAROON_DARK
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(rect, 20f, 20f, bgPaint)
            canvas.drawRoundRect(rect, 20f, 20f, strokePaint(GOLD, 2f))

            // Label
            val lblPaint = textPaint(GOLD_LIGHT, 22f, bold = true, center = true)
            canvas.drawText(label, rect.centerX(), rect.top + 38f, lblPaint)

            // Big Number
            val valPaint = textPaint(SAFFRON, 52f, bold = true, center = true)
            canvas.drawText(value, rect.centerX(), rect.top + 105f, valPaint)

            // Planet
            val plPaint = textPaint(CREAM, 18f, bold = false, center = true)
            val shortPlanet = planet.substringBefore(" (")
            canvas.drawText(shortPlanet, rect.centerX(), rect.top + 150f, plPaint)
        }

        // Section: Strengths & Personality
        val sectionTitleY = cardY + cardHeight + 45f
        val secPaint = textPaint(GOLD_LIGHT, 26f, bold = true)
        canvas.drawText(if (isTelugu) "వ్యక్తిత్వ విశేషాలు (Key Qualities):" else "Core Qualities & Nature:", MARGIN, sectionTitleY, secPaint)

        val natureText = if (isTelugu) moolankInfo.natureTe else moolankInfo.natureEn
        val bodyPaint = textPaint(CREAM, 22f)
        val textLines = wrapText(natureText, bodyPaint, WIDTH - 2 * MARGIN)
        var textY = sectionTitleY + 35f
        textLines.take(3).forEach { line ->
            canvas.drawText(line, MARGIN, textY, bodyPaint)
            textY += 32f
        }

        // Lucky Attributes Box
        val luckyBoxY = textY + 25f
        val luckyBoxHeight = 250f
        val luckyRect = RectF(MARGIN, luckyBoxY, WIDTH - MARGIN, luckyBoxY + luckyBoxHeight)
        canvas.drawRoundRect(luckyRect, 20f, 20f, Paint().apply { color = MAROON_DARK; style = Paint.Style.FILL })
        canvas.drawRoundRect(luckyRect, 20f, 20f, strokePaint(GOLD, 1.5f))

        val luckyTitlePaint = textPaint(SAFFRON, 26f, bold = true)
        canvas.drawText(if (isTelugu) "మీ అదృష్ట అంశాలు (Your Lucky Elements)" else "Your Lucky Elements", MARGIN + 24f, luckyBoxY + 45f, luckyTitlePaint)

        val luckyItems = listOf(
            (if (isTelugu) "అదృష్ట రోజులు: " else "Lucky Days: ") to (if (isTelugu) moolankInfo.luckyDaysTe.joinToString() else moolankInfo.luckyDaysEn.joinToString()),
            (if (isTelugu) "అదృష్ట రంగులు: " else "Lucky Colors: ") to (if (isTelugu) moolankInfo.luckyColorsTe.joinToString() else moolankInfo.luckyColorsEn.joinToString()),
            (if (isTelugu) "అదృష్ట సంఖ్యలు: " else "Lucky Numbers: ") to moolankInfo.luckyNumbers.joinToString(),
            (if (isTelugu) "సంప్రదాయ రత్నం: " else "Gemstone: ") to (if (isTelugu) moolankInfo.gemstoneTe else moolankInfo.gemstoneEn)
        )

        var lY = luckyBoxY + 90f
        luckyItems.forEach { (lbl, valStr) ->
            val lPaint = textPaint(GOLD_LIGHT, 21f, bold = true)
            val vPaint = textPaint(CREAM, 21f, bold = false)
            canvas.drawText(lbl, MARGIN + 24f, lY, lPaint)
            val lblW = lPaint.measureText(lbl)
            canvas.drawText(valStr, MARGIN + 24f + lblW, lY, vPaint)
            lY += 36f
        }

        // Today for you tip
        val today = java.time.LocalDate.now()
        val pYear = NumerologyCalculator.calculatePersonalYear(profile.birthDate.dayOfMonth, profile.birthDate.monthValue, today.year)
        val pDay = NumerologyCalculator.calculatePersonalDay(pYear, today)
        val tip = NumerologyData.getDailyTip(pDay)
        val tipText = if (isTelugu) tip.first else tip.second

        val tipBoxY = luckyBoxY + luckyBoxHeight + 25f
        val tipRect = RectF(MARGIN, tipBoxY, WIDTH - MARGIN, tipBoxY + 140f)
        canvas.drawRoundRect(tipRect, 16f, 16f, Paint().apply { color = MAROON_DARK; style = Paint.Style.FILL })
        canvas.drawRoundRect(tipRect, 16f, 16f, strokePaint(SAFFRON, 1.5f))

        val tipHeaderPaint = textPaint(SAFFRON, 23f, bold = true)
        canvas.drawText("${if (isTelugu) "నేటి సందేశం" else "Today's Tip"} (Personal Day $pDay):", MARGIN + 20f, tipBoxY + 38f, tipHeaderPaint)

        val tipBodyPaint = textPaint(CREAM, 20f)
        val tipLines = wrapText(tipText, tipBodyPaint, WIDTH - 2 * MARGIN - 40f)
        var tY = tipBoxY + 70f
        tipLines.take(2).forEach { line ->
            canvas.drawText(line, MARGIN + 20f, tY, tipBodyPaint)
            tY += 28f
        }

        // Disclaimer
        val discPaint = textPaint(MUTED_CREAM, 17f, bold = false, center = true)
        val disclaimer = if (isTelugu) "వినోదం మరియు సంప్రదాయ మార్గదర్శనం కొరకు మాత్రమే." else "For entertainment and traditional guidance only."
        canvas.drawText(disclaimer, WIDTH / 2f, HEIGHT - 110f, discPaint)

        // Footer Bar
        val footerPaint = Paint().apply {
            color = MAROON_DARK
            style = Paint.Style.FILL
        }
        canvas.drawRect(RectF(0f, HEIGHT - 85f, WIDTH.toFloat(), HEIGHT.toFloat()), footerPaint)
        canvas.drawLine(0f, HEIGHT - 85f, WIDTH.toFloat(), HEIGHT - 85f, strokePaint(GOLD, 1f))

        val appPaint = textPaint(GOLD_LIGHT, 24f, bold = true, center = true)
        canvas.drawText("Shubhodaya • Daily Telugu Panchang & Vedic Wisdom", WIDTH / 2f, HEIGHT - 45f, appPaint)

        val subPaint = textPaint(CREAM, 18f, bold = false, center = true)
        canvas.drawText("Download free on Google Play: ${AppConfig.PLAY_STORE_URL}", WIDTH / 2f, HEIGHT - 20f, subPaint)

        return bitmap
    }

    private fun wrapText(text: String, paint: TextPaint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = StringBuilder()

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = StringBuilder(testLine)
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
                currentLine = StringBuilder(word)
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine.toString())
        return lines
    }

    private fun textPaint(color: Int, size: Float, bold: Boolean = false, center: Boolean = false): TextPaint =
        TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            textSize = size
            typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            if (center) textAlign = Paint.Align.CENTER
        }

    private fun strokePaint(color: Int, width: Float): Paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = width
        }
}
