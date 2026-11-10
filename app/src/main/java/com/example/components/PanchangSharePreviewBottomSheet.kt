package com.example.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.AppConfig
import com.example.data.model.Language
import com.example.data.model.PanchangData
import com.example.i18n.Translations
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleSaffron
import com.example.utils.PanchangShareCardGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanchangSharePreviewBottomSheet(
    panchang: PanchangData,
    targetDate: LocalDate,
    language: Language,
    cardsSharedCount: Int = 0,
    onDismiss: () -> Unit,
    onCardShared: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isGenerating by remember { mutableStateOf(true) }

    LaunchedEffect(panchang, targetDate, language) {
        isGenerating = true
        withContext(Dispatchers.Default) {
            val bmp = PanchangShareCardGenerator.createCardBitmap(
                context = context,
                panchang = panchang,
                language = language,
                targetDate = targetDate
            )
            withContext(Dispatchers.Main) {
                generatedBitmap = bmp
                isGenerating = false
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("panchang_share_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = Translations.tr("share_panchang_card", language),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = Translations.tr("share_card_subtitle", language),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card Preview Area (4:5 Aspect Ratio)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(4f / 5f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF5B0E1E))
                    .testTag("share_card_preview_container"),
                contentAlignment = Alignment.Center
            ) {
                if (isGenerating || generatedBitmap == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = TempleGold,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = Translations.tr("share_preparing", language),
                            fontSize = 13.sp,
                            color = Color(0xFFFFF7EC)
                        )
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, TempleGold.copy(alpha = 0.6f)),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Image(
                            bitmap = generatedBitmap!!.asImageBitmap(),
                            contentDescription = "Panchang Share Card Preview",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            if (cardsSharedCount > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format(Translations.tr("cards_shared_count", language), cardsSharedCount),
                    fontSize = 11.sp,
                    color = TempleGold,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Primary: Big Saffron "Share on WhatsApp" Button
                Button(
                    onClick = {
                        val bitmap = generatedBitmap ?: return@Button
                        shareCardViaWhatsApp(context, bitmap)
                        onCardShared()
                        onDismiss()
                    },
                    enabled = generatedBitmap != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("share_whatsapp_confirm_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TempleSaffron,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = Translations.tr("share_on_whatsapp", language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Secondary: "Save to gallery" Button
                OutlinedButton(
                    onClick = {
                        val bitmap = generatedBitmap ?: return@OutlinedButton
                        val saved = PanchangShareCardGenerator.saveBitmapToGallery(context, bitmap)
                        if (saved) {
                            Toast.makeText(
                                context,
                                Translations.tr("image_saved_toast", language),
                                Toast.LENGTH_LONG
                            ).show()
                            onCardShared()
                        } else {
                            Toast.makeText(context, "Could not save image to gallery", Toast.LENGTH_SHORT).show()
                        }
                        onDismiss()
                    },
                    enabled = generatedBitmap != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_to_gallery_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, TempleGold)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = TempleGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Translations.tr("save_to_gallery", language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

fun shareCardViaWhatsApp(
    context: Context,
    bitmap: Bitmap,
    shareText: String = "శుభోదయం! నేటి పంచాంగం • Today's Panchang – Shubhodaya app: ${AppConfig.PLAY_STORE_URL}",
    chooserTitle: String = "Share Panchang Card"
) {
    try {
        val file = PanchangShareCardGenerator.saveBitmapToCache(context, bitmap)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // Try direct WhatsApp first
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, shareText)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setPackage("com.whatsapp")
        }

        try {
            context.startActivity(whatsappIntent)
        } catch (e: Exception) {
            // Fallback to standard share chooser
            val chooser = Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                },
                chooserTitle
            )
            context.startActivity(chooser)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Failed to share image. Saved to gallery instead.", Toast.LENGTH_SHORT).show()
        PanchangShareCardGenerator.saveBitmapToGallery(context, bitmap)
    }
}
