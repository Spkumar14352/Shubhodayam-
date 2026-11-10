package com.example.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.text.TextUtils
import com.example.data.model.AppConfig
import com.example.data.model.Language
import com.example.data.model.ShortlistedName

object BabyNamesShareCardGenerator {
    private const val WIDTH = PanchangShareCardGenerator.CARD_WIDTH
    private const val HEIGHT = PanchangShareCardGenerator.CARD_HEIGHT
    private const val MARGIN = 80f
    private const val MAX_ROWS = 10
    private const val ROW_HEIGHT = 92f

    private const val MAROON = 0xFF5B0E1E.toInt()
    private const val FOOTER = 0xFF350610.toInt()
    private const val GOLD = 0xFFD4A537.toInt()
    private const val GOLD_LIGHT = 0xFFF1D282.toInt()
    private const val SAFFRON = 0xFFF28C28.toInt()
    private const val CREAM = 0xFFFFF7EC.toInt()

    fun createCardBitmap(names: List<ShortlistedName>, title: String, language: Language): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val isTelugu = language == Language.TELUGU

        canvas.drawColor(MAROON)
        canvas.drawRoundRect(
            RectF(28f, 28f, WIDTH - 28f, HEIGHT - 28f),
            32f,
            32f,
            strokePaint(GOLD, 4f)
        )

        val titlePaint = textPaint(SAFFRON, 58f, bold = true, center = true)
        while (titlePaint.measureText(title) > WIDTH - 2 * MARGIN) titlePaint.textSize -= 2f
        canvas.drawText(title, WIDTH / 2f, 130f, titlePaint)
        canvas.drawLine(MARGIN, 170f, WIDTH - MARGIN, 170f, strokePaint(GOLD, 2f))

        val namePaint = textPaint(CREAM, 40f, bold = true)
        val secondaryPaint = textPaint(GOLD_LIGHT, 28f)
        val meaningPaint = textPaint(CREAM, 26f)
        val contentWidth = WIDTH - 2 * MARGIN

        var y = 230f
        names.take(MAX_ROWS).forEach { item ->
            val primary = if (isTelugu) item.telugu else item.name
            val secondary = if (isTelugu) item.name else item.telugu
            canvas.drawText(primary, MARGIN, y, namePaint)
            canvas.drawText(secondary, MARGIN + namePaint.measureText(primary) + 20f, y, secondaryPaint)

            val meaning = if (isTelugu) item.meaningTe.ifBlank { item.meaningEn } else item.meaningEn.ifBlank { item.meaningTe }
            val tagged = if (item.isAiSuggested) "$meaning  (AI)" else meaning
            val fitted = TextUtils.ellipsize(tagged, meaningPaint, contentWidth, TextUtils.TruncateAt.END)
            canvas.drawText(fitted.toString(), MARGIN, y + 38f, meaningPaint)
            y += ROW_HEIGHT
        }

        val hidden = names.size - MAX_ROWS
        if (hidden > 0) {
            val more = if (isTelugu) "+ మరో $hidden పేర్లు" else "+ $hidden more names"
            canvas.drawText(more, WIDTH / 2f, y, textPaint(GOLD, 28f, bold = true, center = true))
        }

        drawFooter(canvas)
        return bitmap
    }

    private fun drawFooter(canvas: Canvas) {
        val top = HEIGHT - 170f
        val rect = RectF(65f, top, WIDTH - 65f, top + 125f)
        canvas.drawRoundRect(rect, 18f, 18f, Paint().apply { color = FOOTER; isAntiAlias = true })
        canvas.drawRoundRect(rect, 18f, 18f, strokePaint(GOLD, 2f))

        canvas.drawText("శుభోదయ • Shubhodaya", 95f, top + 54f, textPaint(GOLD, 32f, bold = true))

        val urlPaint = textPaint(CREAM, 22f)
        val url = TextUtils.ellipsize(AppConfig.PLAY_STORE_URL, urlPaint, WIDTH - 190f, TextUtils.TruncateAt.END)
        canvas.drawText(url.toString(), 95f, top + 94f, urlPaint)
    }

    private fun strokePaint(paintColor: Int, width: Float) = Paint().apply {
        color = paintColor
        style = Paint.Style.STROKE
        strokeWidth = width
        isAntiAlias = true
    }

    private fun textPaint(
        paintColor: Int,
        size: Float,
        bold: Boolean = false,
        center: Boolean = false
    ) = TextPaint().apply {
        color = paintColor
        textSize = size
        isAntiAlias = true
        if (bold) typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        if (center) textAlign = Paint.Align.CENTER
    }
}
