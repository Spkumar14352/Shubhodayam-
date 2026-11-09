package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.data.model.AppConfig
import com.example.data.model.GreetingPattern
import com.example.data.model.GreetingTemplate
import com.example.data.model.Language
import java.io.File
import java.io.FileOutputStream
import kotlin.math.cos
import kotlin.math.sin

object GreetingCardGenerator {

    const val CARD_SIZE = 1080

    data class GreetingCardParams(
        val template: GreetingTemplate,
        val messageText: String,
        val senderName: String,
        val textColor: Long = 0xFFFFF7EC,
        val fontSizeMultiplier: Float = 1.0f,
        val language: Language = Language.TELUGU,
        val photoBitmap: Bitmap? = null,
        val photoZoom: Float = 1.0f,
        val photoPanX: Float = 0f,
        val photoPanY: Float = 0f,
        val isPremium: Boolean = false
    )

    fun createCardBitmap(params: GreetingCardParams): Bitmap {
        val bitmap = Bitmap.createBitmap(CARD_SIZE, CARD_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val template = params.template

        // 1. Draw Gradient Background
        val bgShader = LinearGradient(
            0f, 0f, CARD_SIZE.toFloat(), CARD_SIZE.toFloat(),
            intArrayOf(
                template.startColor.toInt(),
                template.midColor.toInt(),
                template.endColor.toInt()
            ),
            floatArrayOf(0.0f, 0.55f, 1.0f),
            Shader.TileMode.CLAMP
        )
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = bgShader
        }
        canvas.drawRect(0f, 0f, CARD_SIZE.toFloat(), CARD_SIZE.toFloat(), bgPaint)

        // 2. Draw Vector Pattern Geometry
        drawPattern(canvas, template)

        // 3. Draw Double Gold Border & Corners
        drawBordersAndCorners(canvas, template)

        // 4. Header: Occasion / Category Title
        val isTelugu = params.language == Language.TELUGU
        val occasionTitle = if (isTelugu) template.titleTe else template.titleEn
        val subTitle = if (isTelugu) template.titleEn else template.titleTe

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = template.primaryColor.toInt()
            textSize = 52f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(8f, 0f, 4f, 0x88000000.toInt())
        }
        canvas.drawText(occasionTitle, CARD_SIZE / 2f, 140f, titlePaint)

        val subPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = template.accentColor.toInt()
            textSize = 28f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            alpha = 230
        }
        canvas.drawText(subTitle, CARD_SIZE / 2f, 185f, subPaint)

        // Subtle divider below header
        val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFD4A537.toInt()
            strokeWidth = 3f
            alpha = 180
        }
        canvas.drawLine(CARD_SIZE * 0.28f, 215f, CARD_SIZE * 0.72f, 215f, dividerPaint)
        canvas.drawCircle(CARD_SIZE / 2f, 215f, 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFF28C28.toInt()
        })

        // 5. User Photo or Decorative Centerpiece
        val hasPhoto = params.photoBitmap != null
        val photoCenterY = 370f
        val photoRadius = 130f

        if (hasPhoto) {
            drawCircularUserPhoto(
                canvas = canvas,
                photo = params.photoBitmap!!,
                centerX = CARD_SIZE / 2f,
                centerY = photoCenterY,
                radius = photoRadius,
                zoom = params.photoZoom,
                panX = params.photoPanX,
                panY = params.photoPanY,
                borderColor = template.primaryColor.toInt()
            )
        } else {
            // Draw a decorative glowing auspicious medallion
            drawDecorativeEmblem(
                canvas = canvas,
                centerX = CARD_SIZE / 2f,
                centerY = 340f,
                radius = 100f,
                template = template
            )
        }

        // 6. Main Greeting Message
        val messageStartY = if (hasPhoto) 560f else 500f
        val messageAvailableHeight = 360f

        val msgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = params.textColor.toInt()
            textSize = (38f * params.fontSizeMultiplier).coerceIn(26f, 54f)
            textAlign = Paint.Align.CENTER
            typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
            setShadowLayer(6f, 0f, 3f, 0xAA000000.toInt())
        }

        val maxWidth = (CARD_SIZE - 220).toFloat()
        val wrappedLines = wrapText(params.messageText, maxWidth, msgPaint)
        val lineHeight = msgPaint.textSize * 1.45f
        val totalTextHeight = wrappedLines.size * lineHeight

        val messageCenterY = messageStartY + (messageAvailableHeight / 2f)
        var currentY = messageCenterY - (totalTextHeight / 2f) + (msgPaint.textSize * 0.75f)

        for (line in wrappedLines) {
            canvas.drawText(line, CARD_SIZE / 2f, currentY, msgPaint)
            currentY += lineHeight
        }

        // 7. Sender Signature ("– from <name>" / "– ఇట్లు, <పేరు>")
        if (params.senderName.isNotBlank()) {
            val sigPrefix = if (isTelugu) "– ఇట్లు, " else "– from "
            val sigText = "$sigPrefix${params.senderName}"

            val sigPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = template.primaryColor.toInt()
                textSize = 34f
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
                setShadowLayer(6f, 0f, 3f, 0x88000000.toInt())
            }
            canvas.drawText(sigText, CARD_SIZE / 2f, 950f, sigPaint)
        }

        // 8. Watermark (unless isPremium is true)
        if (!params.isPremium) {
            val watermarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFFFFF7EC.toInt()
                textSize = 22f
                textAlign = Paint.Align.CENTER
                alpha = 150
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            }
            canvas.drawText("Made with Shubhodaya App", CARD_SIZE / 2f, 1030f, watermarkPaint)
        }

        return bitmap
    }

    private fun drawCircularUserPhoto(
        canvas: Canvas,
        photo: Bitmap,
        centerX: Float,
        centerY: Float,
        radius: Float,
        zoom: Float,
        panX: Float,
        panY: Float,
        borderColor: Int
    ) {
        val circlePath = Path().apply {
            addCircle(centerX, centerY, radius, Path.Direction.CW)
        }

        // Outer glow
        val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = 14f
            alpha = 100
        }
        canvas.drawCircle(centerX, centerY, radius + 4f, glowPaint)

        // Clip and draw image
        canvas.save()
        canvas.clipPath(circlePath)

        val matrix = Matrix()
        val scale = (radius * 2f / minOf(photo.width, photo.height)) * zoom.coerceIn(0.5f, 3.0f)
        val scaledW = photo.width * scale
        val scaledH = photo.height * scale

        val left = centerX - (scaledW / 2f) + panX
        val top = centerY - (scaledH / 2f) + panY

        matrix.postScale(scale, scale)
        matrix.postTranslate(left, top)

        val photoPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        canvas.drawBitmap(photo, matrix, photoPaint)
        canvas.restore()

        // Gold border ring
        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        canvas.drawCircle(centerX, centerY, radius, ringPaint)

        // Subtle inner rim
        val innerRimPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFF7EC.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = 200
        }
        canvas.drawCircle(centerX, centerY, radius - 4f, innerRimPaint)
    }

    private fun drawDecorativeEmblem(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        template: GreetingTemplate
    ) {
        val emblemPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = template.primaryColor.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 4f
            alpha = 180
        }
        canvas.drawCircle(centerX, centerY, radius, emblemPaint)
        canvas.drawCircle(centerX, centerY, radius * 0.75f, emblemPaint)

        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = template.accentColor.toInt()
            style = Paint.Style.FILL
            alpha = 70
        }
        canvas.drawCircle(centerX, centerY, radius * 0.75f, fillPaint)

        // Sacred 8 petals or star dots
        val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFD4A537.toInt()
            style = Paint.Style.FILL
        }
        for (i in 0 until 8) {
            val angle = Math.toRadians((i * 45).toDouble())
            val px = centerX + (radius * 0.88f * cos(angle)).toFloat()
            val py = centerY + (radius * 0.88f * sin(angle)).toFloat()
            canvas.drawCircle(px, py, 6f, dotPaint)
        }

        // Center sacred symbol (Diya flame or sparkle)
        val flamePath = Path().apply {
            moveTo(centerX, centerY - 35f)
            quadTo(centerX + 22f, centerY, centerX, centerY + 30f)
            quadTo(centerX - 22f, centerY, centerX, centerY - 35f)
            close()
        }
        val flamePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFF28C28.toInt()
            style = Paint.Style.FILL
        }
        canvas.drawPath(flamePath, flamePaint)

        val innerFlame = Path().apply {
            moveTo(centerX, centerY - 20f)
            quadTo(centerX + 12f, centerY + 5f, centerX, centerY + 24f)
            quadTo(centerX - 12f, centerY + 5f, centerX, centerY - 20f)
            close()
        }
        canvas.drawPath(innerFlame, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0xFFFFD700.toInt()
            style = Paint.Style.FILL
        })
    }

    private fun drawPattern(canvas: Canvas, template: GreetingTemplate) {
        val patternColor = template.primaryColor.toInt()
        val accentColor = template.accentColor.toInt()

        when (template.pattern) {
            GreetingPattern.MANDALA -> drawMandalaPattern(canvas, patternColor, accentColor)
            GreetingPattern.DIYA -> drawDiyaPattern(canvas, patternColor, accentColor)
            GreetingPattern.RANGOLI -> drawRangoliPattern(canvas, patternColor, accentColor)
            GreetingPattern.FLOWER -> drawFlowerPattern(canvas, patternColor, accentColor)
            GreetingPattern.STARS -> drawStarsPattern(canvas, patternColor, accentColor)
            GreetingPattern.SUN -> drawSunPattern(canvas, patternColor, accentColor)
        }
    }

    private fun drawMandalaPattern(canvas: Canvas, color: Int, accent: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            alpha = 45
        }

        val cx = CARD_SIZE / 2f
        val cy = CARD_SIZE / 2f

        // Concentric rings
        for (r in listOf(180f, 260f, 350f, 440f, 520f)) {
            canvas.drawCircle(cx, cy, r, paint)
        }

        // Radial rays and petals
        for (i in 0 until 16) {
            val angle = Math.toRadians((i * 22.5).toDouble())
            val x1 = cx + (180f * cos(angle)).toFloat()
            val y1 = cy + (180f * sin(angle)).toFloat()
            val x2 = cx + (480f * cos(angle)).toFloat()
            val y2 = cy + (480f * sin(angle)).toFloat()
            canvas.drawLine(x1, y1, x2, y2, paint)

            val dotX = cx + (300f * cos(angle)).toFloat()
            val dotY = cy + (300f * sin(angle)).toFloat()
            canvas.drawCircle(dotX, dotY, 4f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = accent
                alpha = 60
                style = Paint.Style.FILL
            })
        }
    }

    private fun drawDiyaPattern(canvas: Canvas, color: Int, accent: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            alpha = 50
        }

        // Draw graceful arches with diya motifs in four corners
        val positions = listOf(
            Pair(150f, 150f),
            Pair(CARD_SIZE - 150f, 150f),
            Pair(150f, CARD_SIZE - 150f),
            Pair(CARD_SIZE - 150f, CARD_SIZE - 150f)
        )

        for ((px, py) in positions) {
            canvas.drawCircle(px, py, 70f, paint)
            canvas.drawCircle(px, py, 95f, paint)

            // Small diya
            val diya = Path().apply {
                moveTo(px - 30f, py + 10f)
                quadTo(px, py + 35f, px + 30f, py + 10f)
                close()
            }
            canvas.drawPath(diya, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                style = Paint.Style.FILL
                alpha = 60
            })
            // Flame
            val flame = Path().apply {
                moveTo(px, py - 20f)
                quadTo(px + 12f, py, px, py + 10f)
                quadTo(px - 12f, py, px, py - 20f)
                close()
            }
            canvas.drawPath(flame, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = accent
                style = Paint.Style.FILL
                alpha = 80
            })
        }
    }

    private fun drawRangoliPattern(canvas: Canvas, color: Int, accent: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = 50
        }

        val cx = CARD_SIZE / 2f
        val cy = CARD_SIZE / 2f

        // Draw 8-point geometric star pattern
        val path = Path()
        val numPoints = 8
        val innerR = 220f
        val outerR = 420f

        for (i in 0 until (numPoints * 2)) {
            val angle = Math.toRadians((i * (360.0 / (numPoints * 2))).toDouble())
            val r = if (i % 2 == 0) outerR else innerR
            val px = cx + (r * cos(angle)).toFloat()
            val py = cy + (r * sin(angle)).toFloat()
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        path.close()
        canvas.drawPath(path, paint)

        // Outer petal loops
        for (i in 0 until numPoints) {
            val angle = Math.toRadians((i * (360.0 / numPoints)).toDouble())
            val px = cx + (outerR * cos(angle)).toFloat()
            val py = cy + (outerR * sin(angle)).toFloat()
            canvas.drawCircle(px, py, 35f, paint)
        }
    }

    private fun drawFlowerPattern(canvas: Canvas, color: Int, accent: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            alpha = 45
        }

        val cx = CARD_SIZE / 2f
        val cy = CARD_SIZE / 2f

        // Overlapping lotus petal rings
        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30).toDouble())
            val px = cx + (160f * cos(angle)).toFloat()
            val py = cy + (160f * sin(angle)).toFloat()

            val petal = Path().apply {
                moveTo(cx, cy)
                quadTo(px, py - 60f, px + (120f * cos(angle)).toFloat(), py + (120f * sin(angle)).toFloat())
                quadTo(px, py + 60f, cx, cy)
            }
            canvas.drawPath(petal, paint)
        }
    }

    private fun drawStarsPattern(canvas: Canvas, color: Int, accent: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            style = Paint.Style.FILL
            alpha = 60
        }

        // Draw scatter of 4-point twinkling stars
        val coords = listOf(
            Pair(160f, 180f), Pair(320f, 120f), Pair(880f, 190f), Pair(750f, 110f),
            Pair(120f, 480f), Pair(940f, 460f), Pair(180f, 850f), Pair(890f, 860f),
            Pair(260f, 960f), Pair(820f, 980f), Pair(480f, 1010f), Pair(600f, 80f)
        )

        for ((x, y) in coords) {
            val star = Path().apply {
                moveTo(x, y - 18f)
                quadTo(x, y, x + 18f, y)
                quadTo(x, y, x, y + 18f)
                quadTo(x, y, x - 18f, y)
                quadTo(x, y, x, y - 18f)
                close()
            }
            canvas.drawPath(star, paint)
        }

        // Crescent moon in top corner
        val moonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = accent
            style = Paint.Style.FILL
            alpha = 80
        }
        val moonPath = Path().apply {
            addCircle(170f, 160f, 40f, Path.Direction.CW)
            op(Path().apply {
                addCircle(185f, 150f, 35f, Path.Direction.CW)
            }, Path.Op.DIFFERENCE)
        }
        canvas.drawPath(moonPath, moonPaint)
    }

    private fun drawSunPattern(canvas: Canvas, color: Int, accent: Int) {
        val cx = CARD_SIZE / 2f
        val cy = 180f

        val rayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = accent
            strokeWidth = 3f
            alpha = 50
        }

        for (i in 0 until 24) {
            val angle = Math.toRadians((i * 15).toDouble())
            val x1 = cx + (120f * cos(angle)).toFloat()
            val y1 = cy + (120f * sin(angle)).toFloat()
            val x2 = cx + (420f * cos(angle)).toFloat()
            val y2 = cy + (420f * sin(angle)).toFloat()
            canvas.drawLine(x1, y1, x2, y2, rayPaint)
        }
    }

    private fun drawBordersAndCorners(canvas: Canvas, template: GreetingTemplate) {
        val goldColor = 0xFFD4A537.toInt()

        val outerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = goldColor
            style = Paint.Style.STROKE
            strokeWidth = 6f
            alpha = 220
        }
        val innerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = goldColor
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = 150
        }

        // Outer border
        canvas.drawRect(40f, 40f, (CARD_SIZE - 40).toFloat(), (CARD_SIZE - 40).toFloat(), outerPaint)
        // Inner border
        canvas.drawRect(56f, 56f, (CARD_SIZE - 56).toFloat(), (CARD_SIZE - 56).toFloat(), innerPaint)

        // Corner knot ornaments
        val cornerOffsets = listOf(
            Pair(40f, 40f),
            Pair((CARD_SIZE - 40).toFloat(), 40f),
            Pair(40f, (CARD_SIZE - 40).toFloat()),
            Pair((CARD_SIZE - 40).toFloat(), (CARD_SIZE - 40).toFloat())
        )

        val cornerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = template.primaryColor.toInt()
            style = Paint.Style.FILL
        }

        for ((cx, cy) in cornerOffsets) {
            canvas.drawCircle(cx, cy, 14f, cornerPaint)
            canvas.drawCircle(cx, cy, 8f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = template.accentColor.toInt()
                style = Paint.Style.FILL
            })
        }
    }

    private fun wrapText(text: String, maxWidth: Float, paint: Paint): List<String> {
        val lines = mutableListOf<String>()
        val paragraphs = text.split("\n")

        for (paragraph in paragraphs) {
            val words = paragraph.split(" ")
            var currentLine = StringBuilder()

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val width = paint.measureText(testLine)

                if (width <= maxWidth) {
                    currentLine = StringBuilder(testLine)
                } else {
                    if (currentLine.isNotEmpty()) {
                        lines.add(currentLine.toString())
                    }
                    currentLine = StringBuilder(word)
                }
            }

            if (currentLine.isNotEmpty()) {
                lines.add(currentLine.toString())
            }
        }
        return lines
    }

    fun saveBitmapToCache(context: Context, bitmap: Bitmap): File {
        val dir = File(context.cacheDir, "greetings").apply { mkdirs() }
        val file = File(dir, "greeting_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    fun saveToGallery(context: Context, bitmap: Bitmap, title: String): Uri? {
        val filename = "Shubhodaya_Greeting_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Shubhodaya")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            resolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
        }
        return uri
    }

    fun shareGreetingCard(
        context: Context,
        bitmap: Bitmap,
        specificPackage: String? = null
    ) {
        val file = saveBitmapToCache(context, bitmap)
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val shareText = "Make your own greetings free: ${AppConfig.PLAY_STORE_URL}"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (specificPackage != null) {
                setPackage(specificPackage)
            }
        }

        val chooser = Intent.createChooser(intent, "Share Greeting Card").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }
}
