package com.example.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import com.example.data.model.AppConfig

/** A share card with a title, label/value rows and notes, in the app's maroon and gold style. */
object InfoCardGenerator {
    private const val WIDTH = PanchangShareCardGenerator.CARD_WIDTH
    private const val HEIGHT = PanchangShareCardGenerator.CARD_HEIGHT
    private const val MARGIN = 80f

    private const val MAROON = 0xFF5B0E1E.toInt()
    private const val FOOTER = 0xFF350610.toInt()
    private const val GOLD = 0xFFD4A537.toInt()
    private const val GOLD_LIGHT = 0xFFF1D282.toInt()
    private const val SAFFRON = 0xFFF28C28.toInt()
    private const val CREAM = 0xFFFFF7EC.toInt()

    fun createCard(
        title: String,
        subtitle: String,
        rows: List<Pair<String, String>>,
        notes: List<String>
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val contentWidth = WIDTH - 2 * MARGIN

        canvas.drawColor(MAROON)
        canvas.drawRoundRect(RectF(28f, 28f, WIDTH - 28f, HEIGHT - 28f), 32f, 32f, stroke(GOLD, 4f))

        val titlePaint = text(SAFFRON, 56f, bold = true, center = true)
        while (titlePaint.measureText(title) > contentWidth) titlePaint.textSize -= 2f
        canvas.drawText(title, WIDTH / 2f, 140f, titlePaint)
        canvas.drawText(subtitle, WIDTH / 2f, 195f, text(GOLD_LIGHT, 32f, center = true))
        canvas.drawLine(MARGIN, 230f, WIDTH - MARGIN, 230f, stroke(GOLD, 2f))

        val labelPaint = text(GOLD_LIGHT, 28f)
        val valuePaint = text(CREAM, 38f, bold = true)
        var y = 300f
        rows.forEach { (label, value) ->
            canvas.drawText(label, MARGIN, y, labelPaint)
            val fitted = TextUtils.ellipsize(value, valuePaint, contentWidth, TextUtils.TruncateAt.END)
            canvas.drawText(fitted.toString(), MARGIN, y + 48f, valuePaint)
            y += 104f
        }

        val notePaint = text(CREAM, 26f)
        val footerTop = HEIGHT - 170f
        notes.forEach { note ->
            val layout = StaticLayout.Builder.obtain(note, 0, note.length, notePaint, contentWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .build()
            if (y + layout.height > footerTop - 20f) return@forEach
            canvas.save()
            canvas.translate(MARGIN, y)
            layout.draw(canvas)
            canvas.restore()
            y += layout.height + 16f
        }

        val footer = RectF(65f, footerTop, WIDTH - 65f, footerTop + 125f)
        canvas.drawRoundRect(footer, 18f, 18f, Paint().apply { color = FOOTER; isAntiAlias = true })
        canvas.drawRoundRect(footer, 18f, 18f, stroke(GOLD, 2f))
        canvas.drawText("శుభోదయ • Shubhodaya", 95f, footerTop + 54f, text(GOLD, 32f, bold = true))
        val urlPaint = text(CREAM, 22f)
        val url = TextUtils.ellipsize(AppConfig.PLAY_STORE_URL, urlPaint, WIDTH - 190f, TextUtils.TruncateAt.END)
        canvas.drawText(url.toString(), 95f, footerTop + 94f, urlPaint)

        return bitmap
    }

    private fun stroke(paintColor: Int, width: Float) = Paint().apply {
        color = paintColor
        style = Paint.Style.STROKE
        strokeWidth = width
        isAntiAlias = true
    }

    private fun text(paintColor: Int, size: Float, bold: Boolean = false, center: Boolean = false) = TextPaint().apply {
        color = paintColor
        textSize = size
        isAntiAlias = true
        if (bold) typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        if (center) textAlign = Paint.Align.CENTER
    }
}
