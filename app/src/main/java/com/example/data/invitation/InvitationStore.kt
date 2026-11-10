package com.example.data.invitation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import org.json.JSONObject
import java.io.File
import kotlin.math.max

/** "My invitations": drafts saved as JSON files in the app's private storage. */
class InvitationStore(context: Context) {
    private val appContext = context.applicationContext
    private val dir = File(appContext.filesDir, "invitations").apply { mkdirs() }
    private val unlocks = appContext.getSharedPreferences("shubhalekha_unlocks", Context.MODE_PRIVATE)

    fun list(): List<Invitation> = dir.listFiles { file -> file.extension == "json" }
        .orEmpty()
        .mapNotNull { file -> runCatching { Invitation.fromJson(JSONObject(file.readText())) }.getOrNull() }
        .sortedByDescending { it.updatedAt }

    fun save(invitation: Invitation): Invitation {
        val saved = invitation.copy(updatedAt = System.currentTimeMillis())
        File(dir, "${saved.id}.json").writeText(saved.toJson().toString())
        return saved
    }

    fun delete(invitation: Invitation) {
        File(dir, "${invitation.id}.json").delete()
        if (invitation.photoPath.isNotEmpty()) File(invitation.photoPath).delete()
    }

    /** Copies a picked photo into private storage, scaled to at most [MAX_PHOTO_SIDE] pixels. */
    fun importPhoto(invitationId: String, uri: Uri): String {
        val bitmap = decode(uri)
        val file = File(dir, "${invitationId}_photo.jpg")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        return file.absolutePath
    }

    private fun decode(uri: Uri): Bitmap {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // ImageDecoder also applies the photo's EXIF rotation.
            return ImageDecoder.decodeBitmap(ImageDecoder.createSource(appContext.contentResolver, uri)) { decoder, info, _ ->
                val largest = max(info.size.width, info.size.height)
                if (largest > MAX_PHOTO_SIDE) {
                    val ratio = MAX_PHOTO_SIDE.toFloat() / largest
                    decoder.setTargetSize((info.size.width * ratio).toInt(), (info.size.height * ratio).toInt())
                }
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        }
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        appContext.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / sample > MAX_PHOTO_SIDE) sample *= 2
        return appContext.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        } ?: error("Could not read the photo")
    }

    /**
     * Marks an invitation as paid for, which removes the watermark and enables high-resolution and PDF export.
     *
     * TODO: Replace with Google Play Billing (e.g. RevenueCat or the Play Billing Library) before release —
     *  Play requires Play Billing for digital purchases. Right now this unlocks without any payment.
     */
    fun unlockInvitation(invitationId: String) {
        unlocks.edit().putBoolean(invitationId, true).apply()
    }

    fun isUnlocked(invitationId: String): Boolean = unlocks.getBoolean(invitationId, false)

    private companion object {
        const val MAX_PHOTO_SIDE = 1600
    }
}
