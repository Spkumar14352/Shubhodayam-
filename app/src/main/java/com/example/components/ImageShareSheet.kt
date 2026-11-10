package com.example.components

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Language
import com.example.i18n.Translations
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleSaffron
import com.example.utils.PanchangShareCardGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Preview of a generated card with "Share on WhatsApp" and "Save to gallery". */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageShareSheet(
    language: Language,
    title: String,
    shareText: String,
    onShared: () -> Unit,
    onDismiss: () -> Unit,
    createBitmap: () -> Bitmap
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(title) {
        bitmap = withContext(Dispatchers.Default) { createBitmap() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("image_share_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(4f / 5f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TempleMaroon),
                contentAlignment = Alignment.Center
            ) {
                val preview = bitmap
                if (preview == null) {
                    CircularProgressIndicator(color = TempleGold)
                } else {
                    Image(bitmap = preview.asImageBitmap(), contentDescription = title, modifier = Modifier.fillMaxSize())
                }
            }

            Button(
                onClick = {
                    val image = bitmap ?: return@Button
                    shareCardViaWhatsApp(context, image, shareText, title)
                    onShared()
                    onDismiss()
                },
                enabled = bitmap != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("image_share_confirm"),
                colors = ButtonDefaults.buttonColors(containerColor = TempleSaffron, contentColor = Color.White)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
                Text(Translations.tr("share_on_whatsapp", language), fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    val image = bitmap ?: return@OutlinedButton
                    if (PanchangShareCardGenerator.saveBitmapToGallery(context, image)) {
                        Toast.makeText(context, Translations.tr("image_saved_toast", language), Toast.LENGTH_LONG).show()
                        onShared()
                    } else {
                        Toast.makeText(context, "Could not save image to gallery", Toast.LENGTH_SHORT).show()
                    }
                    onDismiss()
                },
                enabled = bitmap != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                border = BorderStroke(1.dp, TempleGold)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = TempleGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(Translations.tr("save_to_gallery", language), color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
