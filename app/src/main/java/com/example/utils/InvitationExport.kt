package com.example.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.invitation.Invitation
import com.example.data.invitation.InvitationTemplate
import com.example.data.model.AppConfig
import java.io.File

/** Renders invitation pages to files and shares them. */
object InvitationExport {
    const val FREE_WIDTH = 1080
    const val FREE_HEIGHT = 1920
    const val HD_WIDTH = 2160
    const val HD_HEIGHT = 3840

    // A5 is 148 × 210 mm: 420 × 595 points, rendered at 300 dpi.
    private const val A5_WIDTH_POINTS = 420
    private const val A5_HEIGHT_POINTS = 595
    private const val A5_WIDTH_PIXELS = 1748
    private const val A5_HEIGHT_PIXELS = 2480

    val SHARE_TEXT = "Make your own Telugu shubhalekha free: ${AppConfig.PLAY_STORE_URL}"

    fun loadPhoto(invitation: Invitation): Bitmap? =
        invitation.photoPath.takeIf { it.isNotEmpty() && File(it).exists() }?.let(BitmapFactory::decodeFile)

    /** One PNG per page; free exports are 1080×1920 with a watermark, unlocked ones 2160×3840 without. */
    fun pngs(context: Context, invitation: Invitation, template: InvitationTemplate, unlocked: Boolean): List<File> {
        val photo = loadPhoto(invitation)
        val (width, height) = if (unlocked) HD_WIDTH to HD_HEIGHT else FREE_WIDTH to FREE_HEIGHT
        return invitation.pages.mapIndexed { index, page ->
            val bitmap = InvitationRenderer.render(page, invitation, template, width, height, watermark = !unlocked, photo = photo)
            val file = File(outputDir(context), "shubhalekha_${invitation.id.take(8)}_${index + 1}.png")
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            bitmap.recycle()
            file
        }
    }

    /** A print-ready A5 PDF, one page per invitation page, for unlocked invitations. */
    fun pdf(context: Context, invitation: Invitation, template: InvitationTemplate): File {
        val photo = loadPhoto(invitation)
        val document = PdfDocument()
        try {
            invitation.pages.forEachIndexed { index, page ->
                val pdfPage = document.startPage(PdfDocument.PageInfo.Builder(A5_WIDTH_POINTS, A5_HEIGHT_POINTS, index + 1).create())
                val bitmap = InvitationRenderer.render(page, invitation, template, A5_WIDTH_PIXELS, A5_HEIGHT_PIXELS, watermark = false, photo = photo)
                pdfPage.canvas.drawBitmap(
                    bitmap,
                    null,
                    RectF(0f, 0f, A5_WIDTH_POINTS.toFloat(), A5_HEIGHT_POINTS.toFloat()),
                    Paint(Paint.FILTER_BITMAP_FLAG)
                )
                bitmap.recycle()
                document.finishPage(pdfPage)
            }
            val file = File(outputDir(context), "shubhalekha_${invitation.id.take(8)}_A5.pdf")
            file.outputStream().use { document.writeTo(it) }
            return file
        } finally {
            document.close()
        }
    }

    fun share(context: Context, files: List<File>, mimeType: String) {
        val uris = ArrayList<Uri>(files.map { FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", it) })
        fun intent() = if (uris.size == 1) {
            Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_STREAM, uris.single())
        } else {
            Intent(Intent.ACTION_SEND_MULTIPLE).putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        }.setType(mimeType)
            .putExtra(Intent.EXTRA_TEXT, SHARE_TEXT)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            context.startActivity(intent().setPackage("com.whatsapp"))
        } catch (e: ActivityNotFoundException) {
            try {
                context.startActivity(Intent.createChooser(intent(), "Share Shubhalekha"))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No app available to share", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveToGallery(context: Context, files: List<File>): Boolean = files.all { file ->
        val bitmap = BitmapFactory.decodeFile(file.absolutePath) ?: return@all false
        PanchangShareCardGenerator.saveBitmapToGallery(context, bitmap).also { bitmap.recycle() }
    }

    private fun outputDir(context: Context) = File(context.cacheDir, "images").apply { mkdirs() }
}
