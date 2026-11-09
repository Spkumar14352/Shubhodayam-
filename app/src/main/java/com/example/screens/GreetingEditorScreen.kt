package com.example.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GreetingTemplate
import com.example.data.model.Language
import com.example.data.model.SavedGreeting
import com.example.services.GreetingAiService
import com.example.ui.theme.TempleGold
import com.example.ui.theme.TempleMaroon
import com.example.ui.theme.TempleMaroonDark
import com.example.ui.theme.TempleSaffron
import com.example.utils.GreetingCardGenerator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GreetingEditorScreen(
    template: GreetingTemplate,
    initialLanguage: Language,
    savedUserName: String,
    onSaveUserName: (String) -> Unit,
    onSaveCreation: (SavedGreeting) -> Unit,
    onBack: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val aiService = remember { GreetingAiService() }

    var currentLang by remember { mutableStateOf(initialLanguage) }
    var senderName by remember { mutableStateOf(savedUserName) }
    var messageText by remember {
        mutableStateOf(
            if (currentLang == Language.TELUGU) template.defaultMessageTe else template.defaultMessageEn
        )
    }
    var selectedTextColor by remember { mutableLongStateOf(template.defaultTextColor) }
    var fontSizeMultiplier by remember { mutableFloatStateOf(1.0f) }

    // User Photo state
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var photoZoom by remember { mutableFloatStateOf(1.0f) }
    var photoPanX by remember { mutableFloatStateOf(0f) }
    var photoPanY by remember { mutableFloatStateOf(0f) }

    var isGeneratingAi by remember { mutableStateOf(false) }
    var isSavingOrSharing by remember { mutableStateOf(false) }

    // Photo picker launcher (Android Photo Picker - zero storage permissions required)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                photoBitmap = bitmap
                photoZoom = 1.0f
                photoPanX = 0f
                photoPanY = 0f
            } catch (e: Exception) {
                onShowMessage("ఫోటో లోడ్ చేయడం విఫలమైంది")
            }
        }
    }

    // Live preview bitmap generated dynamically
    var previewBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    fun updatePreview() {
        val params = GreetingCardGenerator.GreetingCardParams(
            template = template,
            messageText = messageText,
            senderName = senderName,
            textColor = selectedTextColor,
            fontSizeMultiplier = fontSizeMultiplier,
            language = currentLang,
            photoBitmap = photoBitmap,
            photoZoom = photoZoom,
            photoPanX = photoPanX,
            photoPanY = photoPanY,
            isPremium = false
        )
        previewBitmap = GreetingCardGenerator.createCardBitmap(params)
    }

    LaunchedEffect(
        template, messageText, senderName, selectedTextColor,
        fontSizeMultiplier, currentLang, photoBitmap, photoZoom, photoPanX, photoPanY
    ) {
        updatePreview()
    }

    val availableColors = listOf(
        Pair("Cream", 0xFFFFF7EC),
        Pair("Gold", 0xFFD4A537),
        Pair("White", 0xFFFFFFFF),
        Pair("Saffron", 0xFFF28C28)
    )

    val readyMessages = if (currentLang == Language.TELUGU) template.readyMessagesTe else template.readyMessagesEn

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (currentLang == Language.TELUGU) template.titleTe else template.titleEn,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFF7EC)
                        )
                        Text(
                            text = if (currentLang == Language.TELUGU) "కార్డు ఎడిటర్" else "Greeting Editor",
                            fontSize = 12.sp,
                            color = TempleGold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("greeting_editor_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFFFF7EC)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TempleMaroonDark
                )
            )
        },
        bottomBar = {
            Surface(
                color = TempleMaroonDark,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Save to Gallery
                    OutlinedButton(
                        onClick = {
                            if (isSavingOrSharing) return@OutlinedButton
                            isSavingOrSharing = true
                            coroutineScope.launch {
                                try {
                                    val bmp = previewBitmap ?: return@launch
                                    val uri = GreetingCardGenerator.saveToGallery(
                                        context = context,
                                        bitmap = bmp,
                                        title = template.titleEn
                                    )
                                    val cachedFile = GreetingCardGenerator.saveBitmapToCache(context, bmp)
                                    val saved = SavedGreeting(
                                        id = System.currentTimeMillis().toString(),
                                        filePath = cachedFile.absolutePath,
                                        templateTitle = if (currentLang == Language.TELUGU) template.titleTe else template.titleEn,
                                        userName = senderName,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    onSaveCreation(saved)
                                    onShowMessage(
                                        if (currentLang == Language.TELUGU)
                                            "కార్డు గ్యాలరీలో సేవ్ చేయబడింది! (Saved to Gallery)"
                                        else "Greeting saved to Photos Gallery!"
                                    )
                                } catch (e: Exception) {
                                    onShowMessage("సేవ్ చేయడం విఫలమైంది")
                                } finally {
                                    isSavingOrSharing = false
                                }
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TempleGold
                        ),
                        border = BorderStroke(1.5.dp, TempleGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("greeting_save_gallery_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentLang == Language.TELUGU) "గ్యాలరీలో సేవ్" else "Save Gallery",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Share Button (WhatsApp / Chooser)
                    Button(
                        onClick = {
                            if (isSavingOrSharing) return@Button
                            isSavingOrSharing = true
                            coroutineScope.launch {
                                try {
                                    val bmp = previewBitmap ?: return@launch
                                    val cachedFile = GreetingCardGenerator.saveBitmapToCache(context, bmp)
                                    val saved = SavedGreeting(
                                        id = System.currentTimeMillis().toString(),
                                        filePath = cachedFile.absolutePath,
                                        templateTitle = if (currentLang == Language.TELUGU) template.titleTe else template.titleEn,
                                        userName = senderName,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    onSaveCreation(saved)
                                    GreetingCardGenerator.shareGreetingCard(context, bmp)
                                } catch (e: Exception) {
                                    onShowMessage("షేర్ చేయడం విఫలమైంది")
                                } finally {
                                    isSavingOrSharing = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TempleSaffron,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("greeting_share_btn")
                    ) {
                        if (isSavingOrSharing) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (currentLang == Language.TELUGU) "వాట్సాప్ / షేర్" else "Share Greeting",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 1. Live Preview Card (1:1 Aspect Ratio)
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, TempleGold),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .testTag("greeting_live_preview_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                if (photoBitmap != null) {
                                    photoZoom = (photoZoom * zoom).coerceIn(0.6f, 3.0f)
                                    photoPanX += pan.x * 2.5f
                                    photoPanY += pan.y * 2.5f
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (previewBitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = previewBitmap!!.asImageBitmap(),
                            contentDescription = "Live Greeting Preview",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        CircularProgressIndicator(color = TempleGold)
                    }

                    if (photoBitmap != null) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Text(
                                text = if (currentLang == Language.TELUGU) "ఫోటో జూమ్/మూవ్ చేయవచ్చు" else "Drag/Pinch to adjust photo",
                                fontSize = 10.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Language & Details Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLang == Language.TELUGU) "భాష ఎంచుకోండి" else "Card Language",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = currentLang == Language.TELUGU,
                        onClick = {
                            currentLang = Language.TELUGU
                            messageText = template.defaultMessageTe
                        },
                        label = { Text("తెలుగు") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleMaroon,
                            selectedLabelColor = Color(0xFFFFF7EC)
                        )
                    )
                    FilterChip(
                        selected = currentLang == Language.ENGLISH,
                        onClick = {
                            currentLang = Language.ENGLISH
                            messageText = template.defaultMessageEn
                        },
                        label = { Text("English") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleMaroon,
                            selectedLabelColor = Color(0xFFFFF7EC)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Sender Name input
            OutlinedTextField(
                value = senderName,
                onValueChange = {
                    senderName = it
                    onSaveUserName(it)
                },
                label = {
                    Text(
                        if (currentLang == Language.TELUGU) "మీ పేరు (– ఇట్లు, మీ పేరు)" else "Your Name (– from Your Name)"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TempleGold,
                    unfocusedBorderColor = TempleGold.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("greeting_sender_name_input")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Photo Section
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                border = BorderStroke(1.dp, TempleGold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLang == Language.TELUGU) "మీ ఫోటో (ఐచ్ఛికం)" else "Your Photo (Optional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (photoBitmap == null) {
                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TempleMaroon),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("greeting_add_photo_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = TempleGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (currentLang == Language.TELUGU) "ఫోటో జోడించండి" else "Add Photo",
                                    fontSize = 12.sp,
                                    color = Color(0xFFFFF7EC)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    photoBitmap = null
                                    photoZoom = 1.0f
                                    photoPanX = 0f
                                    photoPanY = 0f
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove photo",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    if (photoBitmap != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (currentLang == Language.TELUGU) "జూమ్:" else "Zoom:",
                                fontSize = 12.sp,
                                modifier = Modifier.width(50.dp)
                            )
                            Slider(
                                value = photoZoom,
                                onValueChange = { photoZoom = it },
                                valueRange = 0.6f..2.5f,
                                colors = SliderDefaults.colors(
                                    thumbColor = TempleGold,
                                    activeTrackColor = TempleGold
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (currentLang == Language.TELUGU) "మార్చు" else "Change",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Message Text & AI Generation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (currentLang == Language.TELUGU) "సందేశం (Message)" else "Card Message",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // AI Write Button
                Button(
                    onClick = {
                        if (isGeneratingAi) return@Button
                        isGeneratingAi = true
                        coroutineScope.launch {
                            try {
                                val generated = aiService.generateGreetingMessage(
                                    template = template,
                                    language = currentLang,
                                    userSenderName = senderName
                                )
                                if (generated.isNotBlank()) {
                                    messageText = generated
                                }
                            } catch (e: Exception) {
                                onShowMessage("AI సందేశం సిద్ధం చేయడంలో ఆలస్యమైంది")
                            } finally {
                                isGeneratingAi = false
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TempleGold,
                        contentColor = TempleMaroonDark
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("greeting_ai_write_btn")
                ) {
                    if (isGeneratingAi) {
                        CircularProgressIndicator(
                            color = TempleMaroonDark,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (currentLang == Language.TELUGU) "✨ AI తో రాయండి" else "✨ Write with AI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ready-made messages pills
            Text(
                text = if (currentLang == Language.TELUGU) "సిద్ధంగా ఉన్న సందేశాలు (5 ఎంపికలు):" else "Ready Messages (5 choices):",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(readyMessages.take(5)) { msg ->
                    val isSelected = messageText == msg
                    FilterChip(
                        selected = isSelected,
                        onClick = { messageText = msg },
                        label = {
                            Text(
                                text = if (msg.length > 28) msg.take(28) + "..." else msg,
                                fontSize = 11.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TempleMaroon,
                            selectedLabelColor = Color(0xFFFFF7EC)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                label = { Text(if (currentLang == Language.TELUGU) "సందేశం టెక్స్ట్" else "Message Text") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TempleGold,
                    unfocusedBorderColor = TempleGold.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("greeting_message_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Text Color & Font Size Customization
            Text(
                text = if (currentLang == Language.TELUGU) "అక్షరాల రంగు & సైజు" else "Text Color & Size",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color chips
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    for ((label, colorVal) in availableColors) {
                        val isSelected = selectedTextColor == colorVal
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(colorVal))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) TempleMaroon else Color.Gray.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .clickable { selectedTextColor = colorVal },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = label,
                                    tint = if (colorVal == 0xFFFFF7EC || colorVal == 0xFFFFFFFF) Color.Black else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Font Size Slider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(160.dp)
                ) {
                    Text("A-", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Slider(
                        value = fontSizeMultiplier,
                        onValueChange = { fontSizeMultiplier = it },
                        valueRange = 0.75f..1.35f,
                        colors = SliderDefaults.colors(
                            thumbColor = TempleGold,
                            activeTrackColor = TempleGold
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Text("A+", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
