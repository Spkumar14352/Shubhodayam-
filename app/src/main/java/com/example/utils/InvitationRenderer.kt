package com.example.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.example.data.invitation.Invitation
import com.example.data.invitation.InvitationLanguage
import com.example.data.invitation.InvitationPage
import com.example.data.invitation.InvitationTemplate
import com.example.data.invitation.Occasion
import com.example.data.invitation.PhotoFrame
import com.example.data.invitation.TemplateFont
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/** Draws one invitation page. All sizes scale with the page width, so any resolution works. */
object InvitationRenderer {
    const val WATERMARK = "Made with Shubhodaya"
    private const val LEAF_GREEN = 0xFF2E7D32.toInt()
    private const val MARIGOLD = 0xFFF57C00.toInt()
    private const val MARIGOLD_LIGHT = 0xFFFFB300.toInt()

    private val TELUGU_MONTHS = listOf(
        "జనవరి", "ఫిబ్రవరి", "మార్చి", "ఏప్రిల్", "మే", "జూన్", "జూలై", "ఆగస్టు", "సెప్టెంబర్", "అక్టోబర్", "నవంబర్", "డిసెంబర్"
    )
    private val TELUGU_WEEKDAYS = listOf("సోమవారం", "మంగళవారం", "బుధవారం", "గురువారం", "శుక్రవారం", "శనివారం", "ఆదివారం")
    private val TIME = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

    fun render(
        page: InvitationPage,
        invitation: Invitation,
        template: InvitationTemplate,
        width: Int,
        height: Int,
        watermark: Boolean,
        photo: Bitmap?
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val u = width / 1080f

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), Paint().apply {
            shader = LinearGradient(0f, 0f, 0f, height.toFloat(), template.backgroundTop, template.backgroundBottom, Shader.TileMode.CLAMP)
        })
        drawBorder(canvas, template, width.toFloat(), height.toFloat(), u)

        var top = 120f * u
        var bottom = height - 120f * u
        if ("toranam" in template.motifs) { drawToranam(canvas, width.toFloat(), 70f * u, u); top += 90f * u }
        if ("marigold" in template.motifs) { drawMarigoldGarland(canvas, width.toFloat(), 70f * u, u); top += 90f * u }
        if ("balloons" in template.motifs) drawBalloons(canvas, width.toFloat(), u)
        if ("stars" in template.motifs) drawStars(canvas, width.toFloat(), height.toFloat(), template.accentColor, u)
        if (page == InvitationPage.COVER && "kalasam" in template.motifs) {
            drawKalasam(canvas, width / 2f, top + 110f * u, u, template.accentColor)
            top += 240f * u
        }
        when {
            "kolam" in template.motifs -> { drawKolam(canvas, width / 2f, bottom - 60f * u, u, template.accentColor); bottom -= 150f * u }
            "lotus" in template.motifs -> { drawLotus(canvas, width / 2f, bottom - 40f * u, u, template.accentColor); bottom -= 130f * u }
        }
        if ("diya" in template.motifs) {
            drawDiya(canvas, 170f * u, height - 170f * u, u)
            drawDiya(canvas, width - 170f * u, height - 170f * u, u)
        }
        if (watermark) bottom -= 60f * u

        val blocks = blocksFor(page, invitation, template, photo, u)
        drawBlocks(canvas, blocks, 150f * u, width - 150f * u, top, bottom)

        if (watermark) drawWatermark(canvas, width.toFloat(), height.toFloat(), u, template.textColor)
        return bitmap
    }

    // ---------- Content ----------

    private sealed interface Block {
        fun measure(width: Float, scale: Float): Float
        fun draw(canvas: Canvas, left: Float, top: Float, width: Float, scale: Float)
    }

    private class TextBlock(
        val text: String,
        val typeface: Typeface,
        val size: Float,
        val color: Int,
        val gapAfter: Float
    ) : Block {
        private fun layout(width: Float, scale: Float): StaticLayout {
            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                this.typeface = this@TextBlock.typeface
                textSize = size * scale
                color = this@TextBlock.color
            }
            return StaticLayout.Builder.obtain(text, 0, text.length, paint, width.toInt())
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0f, 1.1f)
                .build()
        }

        override fun measure(width: Float, scale: Float) = layout(width, scale).height + gapAfter * scale

        override fun draw(canvas: Canvas, left: Float, top: Float, width: Float, scale: Float) {
            canvas.save()
            canvas.translate(left, top)
            layout(width, scale).draw(canvas)
            canvas.restore()
        }
    }

    private class PhotoBlock(
        val photo: Bitmap,
        val frame: PhotoFrame,
        val size: Float,
        val zoom: Float,
        val offsetX: Float,
        val offsetY: Float,
        val ringColor: Int,
        val gapAfter: Float
    ) : Block {
        override fun measure(width: Float, scale: Float) = size * scale * (if (frame == PhotoFrame.ARCH) 1.25f else 1f) + gapAfter * scale

        override fun draw(canvas: Canvas, left: Float, top: Float, width: Float, scale: Float) {
            val side = size * scale
            val frameHeight = if (frame == PhotoFrame.ARCH) side * 1.25f else side
            val rect = RectF(left + (width - side) / 2f, top, left + (width + side) / 2f, top + frameHeight)
            val path = framePath(frame, rect)
            canvas.save()
            canvas.clipPath(path)
            // Same transform as the editor: cover-crop, then zoom about the centre and pan by a fraction of the frame.
            val cover = max(rect.width() / photo.width, rect.height() / photo.height)
            canvas.translate(rect.centerX() + offsetX * rect.width(), rect.centerY() + offsetY * rect.height())
            canvas.scale(cover * zoom, cover * zoom)
            canvas.drawBitmap(photo, -photo.width / 2f, -photo.height / 2f, Paint(Paint.FILTER_BITMAP_FLAG))
            canvas.restore()
            canvas.drawPath(path, Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 8f * scale * (width / 780f)
                color = ringColor
            })
        }
    }

    private class QrBlock(val link: String, val size: Float, val caption: TextBlock, val gapAfter: Float) : Block {
        override fun measure(width: Float, scale: Float) = size * scale + caption.measure(width, scale) + gapAfter * scale

        override fun draw(canvas: Canvas, left: Float, top: Float, width: Float, scale: Float) {
            val side = size * scale
            val x = left + (width - side) / 2f
            canvas.drawRoundRect(RectF(x, top, x + side, top + side), side * 0.06f, side * 0.06f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE })
            val matrix = QRCodeWriter().encode(link, BarcodeFormat.QR_CODE, 0, 0, mapOf(EncodeHintType.MARGIN to 1))
            val cell = side / matrix.width
            val black = Paint().apply { color = Color.BLACK }
            for (row in 0 until matrix.height) for (col in 0 until matrix.width) {
                if (matrix.get(col, row)) canvas.drawRect(x + col * cell, top + row * cell, x + (col + 1) * cell, top + (row + 1) * cell, black)
            }
            caption.draw(canvas, left, top + side + 8f * scale, width, scale)
        }
    }

    private fun drawBlocks(canvas: Canvas, blocks: List<Block>, left: Float, right: Float, top: Float, bottom: Float) {
        val width = right - left
        val available = bottom - top
        // Shrink everything together until it fits, then centre vertically.
        var scale = 1f
        var total = blocks.sumOf { it.measure(width, scale).toDouble() }.toFloat()
        while (total > available && scale > 0.5f) {
            scale *= 0.92f
            total = blocks.sumOf { it.measure(width, scale).toDouble() }.toFloat()
        }
        var y = top + max(0f, (available - total) / 2f)
        blocks.forEach { block ->
            block.draw(canvas, left, y, width, scale)
            y += block.measure(width, scale)
        }
    }

    private fun blocksFor(
        page: InvitationPage,
        invitation: Invitation,
        template: InvitationTemplate,
        photo: Bitmap?,
        u: Float
    ): List<Block> {
        val heading = typeface(template.headingFont, bold = true)
        val body = typeface(template.bodyFont, bold = false)
        val bodyBold = typeface(template.bodyFont, bold = true)
        val lang = invitation.language
        val isWedding = invitation.occasion == Occasion.WEDDING

        fun text(value: String, size: Float, color: Int = template.textColor, face: Typeface = body, gap: Float = 18f): Block? =
            value.trim().takeIf { it.isNotEmpty() }?.let { TextBlock(it, face, size * u, color, gap * u) }

        fun pair(te: String, en: String) = when (lang) {
            InvitationLanguage.TELUGU -> te
            InvitationLanguage.ENGLISH -> en
            InvitationLanguage.BILINGUAL -> "$te\n$en"
        }

        val blocks = mutableListOf<Block?>()
        for (slot in template.slots[page].orEmpty()) {
            when (slot) {
                "deity" -> if (invitation.showDeityLine) blocks += text(invitation.deityLine, 34f, template.accentColor, bodyBold, 26f)
                "title" -> {
                    val (te, en) = title(invitation.occasion)
                    when (lang) {
                        InvitationLanguage.TELUGU -> blocks += text(te, 92f, template.headingColor, heading, 24f)
                        InvitationLanguage.ENGLISH -> blocks += text(en, 80f, template.headingColor, heading, 24f)
                        InvitationLanguage.BILINGUAL -> {
                            blocks += text(te, 88f, template.headingColor, heading, 6f)
                            blocks += text(en, 44f, template.accentColor, heading, 26f)
                        }
                    }
                }
                "photo" -> if (photo != null) {
                    blocks += PhotoBlock(
                        photo, invitation.photoFrame, 380f * u, invitation.photoScale,
                        invitation.photoOffsetX, invitation.photoOffsetY, template.accentColor, 30f * u
                    )
                }
                "names" -> if (page == InvitationPage.COVER) {
                    val names = if (isWedding && invitation.secondaryName.isNotBlank()) {
                        "${invitation.primaryName.trim()}\n&\n${invitation.secondaryName.trim()}"
                    } else {
                        invitation.primaryName
                    }
                    blocks += text(names, 76f, template.headingColor, heading, 28f)
                } else {
                    blocks += parentLines(invitation, lang).map { text(it, 34f, gap = 16f) }
                }
                "date" -> blocks += text(dateLine(invitation.date, invitation.time, lang), 40f, template.textColor, bodyBold, 10f)
                "text" -> blocks += text(invitation.invitationText.ifBlank { defaultText(invitation.occasion, lang) }, 32f, gap = 30f)
                "muhurtham" -> if (invitation.date != null) {
                    blocks += text(pair("ముహూర్తం", "Muhurtham"), 36f, template.headingColor, heading, 6f)
                    val lagnam = invitation.lagnam.trim().takeIf { it.isNotEmpty() }?.let {
                        "\n" + pair("$it లగ్నం", "$it Lagnam").replace("\n", " • ")
                    }.orEmpty()
                    blocks += text(dateLine(invitation.date, invitation.time, lang) + lagnam, 34f, gap = 26f)
                }
                "venue" -> if (invitation.venueName.isNotBlank() || invitation.venueAddress.isNotBlank()) {
                    blocks += text(pair("వేదిక", "Venue"), 36f, template.headingColor, heading, 6f)
                    blocks += text(listOf(invitation.venueName, invitation.venueAddress).filter { it.isNotBlank() }.joinToString("\n"), 32f, gap = 24f)
                }
                "qr" -> if (invitation.mapsLink.isNotBlank()) {
                    val caption = TextBlock(pair("దారి కోసం స్కాన్ చేయండి", "Scan for directions"), body, 24f * u, template.textColor, 0f)
                    blocks += QrBlock(invitation.mapsLink.trim(), 200f * u, caption, 24f * u)
                }
                "compliments" -> if (invitation.compliments.isNotBlank()) {
                    blocks += text(pair("శుభాభినందనలతో", "With best compliments from"), 30f, template.accentColor, bodyBold, 4f)
                    blocks += text(invitation.compliments, 32f, gap = 10f)
                }
                "events" -> invitation.events.filter { it.name.isNotBlank() }.forEach { event ->
                    blocks += text(event.name, 44f, template.headingColor, heading, 4f)
                    blocks += text(dateLine(event.date, event.time, lang), 32f, gap = 34f)
                }
            }
        }
        return blocks.filterNotNull()
    }

    internal fun parentLines(invitation: Invitation, lang: InvitationLanguage): List<String> {
        fun line(name: String, parents: String, isSon: Boolean): String {
            if (name.isBlank()) return ""
            if (parents.isBlank()) return name.trim()
            val te = "${parents.trim()} గారి ${if (isSon) "కుమారుడు" else "కుమార్తె"}"
            val en = "${if (isSon) "S/o" else "D/o"} ${parents.trim()}"
            val relation = when (lang) {
                InvitationLanguage.TELUGU -> te
                InvitationLanguage.ENGLISH -> en
                InvitationLanguage.BILINGUAL -> "$te\n$en"
            }
            return "${name.trim()}\n$relation"
        }
        return if (invitation.occasion == Occasion.WEDDING) {
            listOf(line(invitation.primaryName, invitation.primaryParents, false), line(invitation.secondaryName, invitation.secondaryParents, true))
        } else {
            listOf(line(invitation.primaryName, invitation.primaryParents, invitation.childIsBoy))
        }.filter { it.isNotBlank() }
    }

    internal fun dateLine(date: LocalDate?, time: LocalTime?, lang: InvitationLanguage): String {
        if (date == null) return ""
        val te = "${date.dayOfMonth} ${TELUGU_MONTHS[date.monthValue - 1]} ${date.year}, ${TELUGU_WEEKDAYS[date.dayOfWeek.value - 1]}" +
            (time?.let { " • ${it.format(TIME)}" } ?: "")
        val en = "${date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)}, ${date.dayOfMonth} " +
            "${date.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)} ${date.year}" + (time?.let { " • ${it.format(TIME)}" } ?: "")
        return when (lang) {
            InvitationLanguage.TELUGU -> te
            InvitationLanguage.ENGLISH -> en
            InvitationLanguage.BILINGUAL -> "$te\n$en"
        }
    }

    fun title(occasion: Occasion): Pair<String, String> = when (occasion) {
        Occasion.WEDDING -> "శుభలేఖ" to "Wedding Invitation"
        Occasion.GRIHA_PRAVESAM -> "గృహప్రవేశ ఆహ్వానం" to "Griha Pravesam Invitation"
        Occasion.UPANAYANAM -> "ఉపనయన మహోత్సవం" to "Upanayanam"
        Occasion.NAMAKARANAM -> "నామకరణ మహోత్సవం" to "Naming Ceremony"
        Occasion.HALF_SAREE -> "ఓణీల / పంచెల వేడుక" to "Half-saree / Dhoti Ceremony"
        Occasion.BIRTHDAY -> "పుట్టినరోజు వేడుక" to "Birthday Celebration"
    }

    fun defaultText(occasion: Occasion, lang: InvitationLanguage): String {
        val (te, en) = when (occasion) {
            Occasion.WEDDING ->
                "భగవంతుని కృపతో, పెద్దల ఆశీస్సులతో జరుగు మా పిల్లల వివాహ మహోత్సవానికి మీరు సకుటుంబ సమేతంగా విచ్చేసి వధూవరులను ఆశీర్వదించవలసిందిగా ప్రార్థన." to
                    "With the blessings of the Almighty and our elders, we cordially invite you and your family to the wedding of our children and to bless the couple."
            Occasion.GRIHA_PRAVESAM ->
                "భగవంతుని కృపతో మా నూతన గృహ ప్రవేశ మహోత్సవం జరుపుకొనుచున్నాము. మీరు సకుటుంబ సమేతంగా విచ్చేసి మమ్మల్ని ఆశీర్వదించవలసిందిగా ప్రార్థన." to
                    "With the blessings of the Almighty, we are performing the Griha Pravesam of our new home. We cordially invite you and your family to grace the occasion."
            Occasion.UPANAYANAM ->
                "మా కుమారుని ఉపనయన మహోత్సవానికి మీరు సకుటుంబ సమేతంగా విచ్చేసి ఆశీర్వదించవలసిందిగా ప్రార్థన." to
                    "We are delighted to invite you and your family to the Upanayanam of our son. Kindly grace the occasion with your presence and blessings."
            Occasion.NAMAKARANAM ->
                "మా చిన్నారి నామకరణ మహోత్సవానికి మీరు సకుటుంబంగా విచ్చేసి ఆశీర్వదించవలసిందిగా ప్రార్థన." to
                    "With joy we invite you to the naming ceremony of our little one. Please join us and bless the child."
            Occasion.HALF_SAREE ->
                "మా చిన్నారి వేడుకకు మీరు సకుటుంబ సమేతంగా విచ్చేసి ఆశీర్వదించవలసిందిగా ప్రార్థన." to
                    "We joyfully invite you and your family to our child's ceremony. Kindly attend and bless our child."
            Occasion.BIRTHDAY ->
                "మా ప్రియమైన వారి పుట్టినరోజు వేడుకకు మీరు తప్పక విచ్చేయవలసిందిగా ఆహ్వానం." to
                    "Please join us to celebrate the birthday of our dear one."
        }
        return when (lang) {
            InvitationLanguage.TELUGU -> te
            InvitationLanguage.ENGLISH -> en
            InvitationLanguage.BILINGUAL -> "$te\n\n$en"
        }
    }

    private fun typeface(font: TemplateFont, bold: Boolean): Typeface {
        val style = if (bold) Typeface.BOLD else Typeface.NORMAL
        return when (font) {
            TemplateFont.SERIF -> Typeface.create(Typeface.SERIF, style)
            TemplateFont.SANS -> Typeface.create(Typeface.SANS_SERIF, style)
            TemplateFont.CONDENSED -> Typeface.create("sans-serif-condensed", style)
            TemplateFont.CURSIVE -> Typeface.create("cursive", style)
        }
    }

    private fun framePath(frame: PhotoFrame, rect: RectF) = Path().apply {
        if (frame == PhotoFrame.CIRCLE) {
            addOval(rect, Path.Direction.CW)
        } else {
            val radius = rect.width() / 2f
            moveTo(rect.left, rect.bottom)
            lineTo(rect.left, rect.top + radius)
            arcTo(RectF(rect.left, rect.top, rect.right, rect.top + 2 * radius), 180f, 180f)
            lineTo(rect.right, rect.bottom)
            close()
        }
    }

    // ---------- Borders ----------

    private fun stroke(color: Int, width: Float) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = width
        this.color = color
    }

    private fun fill(color: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }

    private fun drawBorder(canvas: Canvas, template: InvitationTemplate, w: Float, h: Float, u: Float) {
        val c = template.borderColor
        val m = 36f * u
        val outer = RectF(m, m, w - m, h - m)
        val inner = RectF(m + 18f * u, m + 18f * u, w - m - 18f * u, h - m - 18f * u)
        val corners = listOf(outer.left to outer.top, outer.right to outer.top, outer.left to outer.bottom, outer.right to outer.bottom)
        when (template.border) {
            "line" -> canvas.drawRect(outer, stroke(c, 3f * u))
            "kalyanam" -> {
                canvas.drawRect(outer, stroke(c, 5f * u))
                canvas.drawRect(inner, stroke(c, 2f * u))
                val dot = fill(c)
                var x = inner.left + 40f * u
                while (x < inner.right - 30f * u) {
                    canvas.drawCircle(x, (outer.top + inner.top) / 2f, 3.5f * u, dot)
                    canvas.drawCircle(x, (outer.bottom + inner.bottom) / 2f, 3.5f * u, dot)
                    x += 40f * u
                }
                corners.forEach { (x0, y0) -> drawRangoliFlower(canvas, x0, y0, 34f * u, c, template.accentColor) }
            }
            "temple" -> {
                canvas.drawRect(outer, stroke(c, 8f * u))
                canvas.drawRect(inner, stroke(c, 2.5f * u))
                val teeth = Path()
                var x = inner.left
                while (x < inner.right - 1f) {
                    val step = min(36f * u, inner.right - x)
                    teeth.moveTo(x, inner.top); teeth.lineTo(x + step / 2f, inner.top + 22f * u); teeth.lineTo(x + step, inner.top)
                    teeth.moveTo(x, inner.bottom); teeth.lineTo(x + step / 2f, inner.bottom - 22f * u); teeth.lineTo(x + step, inner.bottom)
                    x += step
                }
                canvas.drawPath(teeth, stroke(c, 2.5f * u))
                corners.forEach { (x0, y0) -> drawGopuram(canvas, x0, y0, 70f * u, c) }
            }
            "floral" -> {
                canvas.drawRoundRect(outer, 30f * u, 30f * u, stroke(c, 3f * u))
                corners.forEach { (x0, y0) -> drawFlower(canvas, x0, y0, 46f * u, c, template.accentColor) }
                var x = outer.left + 150f * u
                while (x < outer.right - 120f * u) {
                    drawFlower(canvas, x, outer.top, 14f * u, c, template.accentColor)
                    drawFlower(canvas, x, outer.bottom, 14f * u, c, template.accentColor)
                    x += 150f * u
                }
            }
            "royal" -> {
                val band = Path().apply {
                    addRect(outer, Path.Direction.CW)
                    addRect(RectF(m + 26f * u, m + 26f * u, w - m - 26f * u, h - m - 26f * u), Path.Direction.CCW)
                }
                canvas.drawPath(band, fill(c))
                canvas.drawRect(
                    RectF(m + 40f * u, m + 40f * u, w - m - 40f * u, h - m - 40f * u),
                    stroke(c, 2f * u).apply { pathEffect = DashPathEffect(floatArrayOf(18f * u, 10f * u), 0f) }
                )
                corners.forEach { (x0, y0) -> drawDiamond(canvas, x0, y0, 40f * u, template.accentColor, c) }
            }
            "kolam" -> {
                canvas.drawRect(outer, stroke(c, 2.5f * u))
                corners.forEach { (x0, y0) -> drawKolamCorner(canvas, x0, y0, 22f * u, c) }
            }
        }
    }

    private fun drawRangoliFlower(canvas: Canvas, cx: Float, cy: Float, r: Float, color: Int, accent: Int) {
        canvas.drawCircle(cx, cy, r, fill(Color.WHITE).apply { alpha = 200 })
        for (i in 0 until 8) {
            val a = Math.toRadians(i * 45.0)
            canvas.drawCircle(cx + (r * 0.62f * cos(a)).toFloat(), cy + (r * 0.62f * sin(a)).toFloat(), r * 0.28f, fill(color))
        }
        canvas.drawCircle(cx, cy, r * 0.32f, fill(accent))
    }

    private fun drawGopuram(canvas: Canvas, cx: Float, cy: Float, size: Float, color: Int) {
        val paint = fill(color)
        val base = cy + size * 0.35f
        for (tier in 0 until 4) {
            val half = size * (0.5f - tier * 0.1f)
            val bottomY = base - tier * size * 0.18f
            canvas.drawRect(cx - half, bottomY - size * 0.16f, cx + half, bottomY, paint)
        }
        canvas.drawCircle(cx, base - 4 * size * 0.18f - size * 0.05f, size * 0.07f, paint)
    }

    private fun drawFlower(canvas: Canvas, cx: Float, cy: Float, r: Float, petal: Int, centre: Int) {
        for (i in 0 until 5) {
            val a = Math.toRadians(i * 72.0 - 90)
            canvas.drawCircle(cx + (r * 0.55f * cos(a)).toFloat(), cy + (r * 0.55f * sin(a)).toFloat(), r * 0.45f, fill(petal).apply { alpha = 220 })
        }
        canvas.drawCircle(cx, cy, r * 0.32f, fill(centre))
    }

    private fun drawDiamond(canvas: Canvas, cx: Float, cy: Float, r: Float, color: Int, outline: Int) {
        val path = Path().apply { moveTo(cx, cy - r); lineTo(cx + r, cy); lineTo(cx, cy + r); lineTo(cx - r, cy); close() }
        canvas.drawPath(path, fill(color))
        canvas.drawPath(path, stroke(outline, r * 0.08f))
    }

    private fun drawKolamCorner(canvas: Canvas, cx: Float, cy: Float, gap: Float, color: Int) {
        canvas.drawCircle(cx, cy, gap * 2.4f, fill(Color.WHITE).apply { alpha = 230 })
        for (row in -1..1) for (col in -1..1) {
            val x = cx + col * gap
            val y = cy + row * gap
            canvas.drawCircle(x, y, gap * 0.12f, fill(color))
            canvas.drawCircle(x, y, gap * 0.48f, stroke(color, gap * 0.08f))
        }
    }

    // ---------- Motifs ----------

    private fun leaf(cx: Float, cy: Float, length: Float, angleDegrees: Float): Path {
        val path = Path().apply {
            moveTo(0f, 0f)
            quadTo(length * 0.35f, length * 0.5f, 0f, length)
            quadTo(-length * 0.35f, length * 0.5f, 0f, 0f)
        }
        val matrix = android.graphics.Matrix().apply { postRotate(angleDegrees); postTranslate(cx, cy) }
        path.transform(matrix)
        return path
    }

    private fun drawToranam(canvas: Canvas, w: Float, y: Float, u: Float) {
        canvas.drawLine(90f * u, y, w - 90f * u, y, stroke(0xFF8D6E63.toInt(), 4f * u))
        var x = 110f * u
        var i = 0
        while (x < w - 100f * u) {
            canvas.drawPath(leaf(x, y, 60f * u, if (i % 2 == 0) -8f else 8f), fill(LEAF_GREEN))
            if (i % 3 == 1) canvas.drawCircle(x, y + 4f * u, 11f * u, fill(MARIGOLD))
            x += 42f * u
            i++
        }
    }

    private fun drawMarigoldGarland(canvas: Canvas, w: Float, y: Float, u: Float) {
        val swags = 3
        val span = (w - 180f * u) / swags
        for (s in 0 until swags) {
            val start = 90f * u + s * span
            for (k in 0..14) {
                val t = k / 14f
                val x = start + t * span
                val yy = y + sin(Math.PI * t).toFloat() * 60f * u
                canvas.drawCircle(x, yy, 13f * u, fill(if (k % 2 == 0) MARIGOLD else MARIGOLD_LIGHT))
            }
        }
    }

    private fun drawKalasam(canvas: Canvas, cx: Float, cy: Float, u: Float, potColor: Int) {
        for (i in -2..2) canvas.drawPath(leaf(cx, cy - 40f * u, 75f * u, 180f + i * 28f), fill(LEAF_GREEN))
        canvas.drawOval(RectF(cx - 34f * u, cy - 110f * u, cx + 34f * u, cy - 30f * u), fill(0xFF8D6E63.toInt()))
        canvas.drawRect(cx - 40f * u, cy - 36f * u, cx + 40f * u, cy - 18f * u, fill(potColor))
        canvas.drawOval(RectF(cx - 80f * u, cy - 20f * u, cx + 80f * u, cy + 110f * u), fill(potColor))
        canvas.drawLine(cx - 70f * u, cy + 40f * u, cx + 70f * u, cy + 40f * u, stroke(Color.WHITE, 4f * u).apply { alpha = 170 })
        canvas.drawCircle(cx, cy + 62f * u, 9f * u, fill(0xFFC62828.toInt()))
    }

    private fun drawKolam(canvas: Canvas, cx: Float, cy: Float, u: Float, color: Int) {
        val gap = 28f * u
        for (row in -1..1) for (col in -3..3) {
            val x = cx + col * gap
            val y = cy + row * gap
            canvas.drawCircle(x, y, 3f * u, fill(color))
            if ((row + col) % 2 == 0) canvas.drawCircle(x, y, gap * 0.55f, stroke(color, 2.5f * u))
        }
    }

    private fun drawLotus(canvas: Canvas, cx: Float, cy: Float, u: Float, color: Int) {
        for (angle in listOf(-60f, -30f, 0f, 30f, 60f)) {
            canvas.drawPath(leaf(cx, cy, 90f * u, 180f + angle), fill(color).apply { alpha = 200 })
        }
        canvas.drawOval(RectF(cx - 70f * u, cy - 6f * u, cx + 70f * u, cy + 14f * u), fill(LEAF_GREEN).apply { alpha = 200 })
    }

    private fun drawDiya(canvas: Canvas, cx: Float, cy: Float, u: Float) {
        canvas.drawArc(RectF(cx - 48f * u, cy - 30f * u, cx + 48f * u, cy + 30f * u), 0f, 180f, true, fill(0xFFB5651D.toInt()))
        canvas.drawPath(Path().apply {
            moveTo(cx, cy - 60f * u)
            quadTo(cx + 16f * u, cy - 20f * u, cx, cy - 6f * u)
            quadTo(cx - 16f * u, cy - 20f * u, cx, cy - 60f * u)
        }, fill(0xFFFFA000.toInt()))
    }

    private fun drawBalloons(canvas: Canvas, w: Float, u: Float) {
        val colors = listOf(0xFFEF5350.toInt(), 0xFF29B6F6.toInt(), 0xFFFFCA28.toInt())
        listOf(150f * u, w - 150f * u).forEach { baseX ->
            colors.forEachIndexed { i, color ->
                val x = baseX + (i - 1) * 46f * u
                val y = 170f * u + (i % 2) * 36f * u
                canvas.drawLine(x, y + 52f * u, baseX, 330f * u, stroke(0xFF757575.toInt(), 2f * u))
                canvas.drawOval(RectF(x - 38f * u, y - 52f * u, x + 38f * u, y + 52f * u), fill(color))
            }
        }
    }

    private fun drawStars(canvas: Canvas, w: Float, h: Float, color: Int, u: Float) {
        listOf(0.18f to 0.12f, 0.82f to 0.1f, 0.12f to 0.55f, 0.88f to 0.6f, 0.25f to 0.86f, 0.78f to 0.88f).forEach { (fx, fy) ->
            val path = Path()
            for (i in 0 until 10) {
                val r = if (i % 2 == 0) 22f * u else 9f * u
                val a = Math.toRadians(i * 36.0 - 90)
                val x = w * fx + (r * cos(a)).toFloat()
                val y = h * fy + (r * sin(a)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            canvas.drawPath(path, fill(color).apply { alpha = 200 })
        }
    }

    private fun drawWatermark(canvas: Canvas, w: Float, h: Float, u: Float, color: Int) {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 26f * u
            this.color = color
            alpha = 170
            textAlign = Paint.Align.CENTER
        }
        // Above the temple border's teeth, which reach 76 units up from the bottom edge.
        canvas.drawText(WATERMARK, w / 2f, h - 92f * u, paint)
    }
}
