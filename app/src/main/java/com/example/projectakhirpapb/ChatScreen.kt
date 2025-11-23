package com.example.projectakhirpapb

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.dashboard2.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.core.graphics.drawable.toBitmap

private const val TAG = "CHAT_SCREEN"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onNavigateBack: () -> Unit = {}
) {
    val vm = viewModel<ChatViewModel>()
    val state = vm.chatState.collectAsState().value

    // ✅ Load user data
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("User") }
    var isLoadingUser by remember { mutableStateOf(true) }

    // ✅ Fetch current user name
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                Log.d(TAG, "Loading current user data...")
                val currentUser = authRepository.getCurrentUser()

                if (currentUser != null) {
                    userName = currentUser.name
                    Log.d(TAG, "✓ User name loaded: $userName")
                } else {
                    Log.w(TAG, "⚠️ No user found, using default name")
                    userName = "User"
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading user data", e)
                userName = "User"
            } finally {
                isLoadingUser = false
            }
        }
    }

    // Image picker (opsional)
    val uriState = remember { MutableStateFlow("") }
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> uri?.let { uriState.update { it } } }
    val bitmap = rememberPickedBitmap(uri = uriState.collectAsState().value)

    Scaffold(
        topBar = {
            FinAIHeader(
                userName = userName,
                isLoading = isLoadingUser,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F6F6))
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = true,
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (state.isLoading) item { ModelTypingItem() }

                itemsIndexed(state.chatList) { _, chat ->
                    if (chat.isFromUser) {
                        UserChatItem(prompt = chat.prompt, bitmap = chat.bitmap)
                    } else {
                        ModelChatItem(response = chat.prompt)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "picked image",
                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.width(8.dp))
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(28.dp),
                    tonalElevation = 0.dp,
                    color = Color.White
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddPhotoAlternate,
                            contentDescription = "Add Photo",
                            tint = Color(0xFF3759B3),
                            modifier = Modifier.size(24.dp).clickable {
                                imagePicker.launch(
                                    PickVisualMediaRequest.Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        .build()
                                )
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        TextField(
                            value = state.prompt,
                            onValueChange = { vm.onEvent(ChatUiEvent.UpdatePrompt(it)) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Ask anything", fontSize = 14.sp) },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                                cursorColor = Color(0xFF3759B3)
                            )
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Send,
                            contentDescription = "Send",
                            tint = if (state.isLoading) Color.Gray.copy(alpha = 0.4f) else Color(0xFF3759B3),
                            modifier = Modifier.size(32.dp).clickable(enabled = !state.isLoading) {
                                vm.onEvent(ChatUiEvent.SendPrompt(state.prompt, bitmap))
                                uriState.update { "" }
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun FinAIHeader(
    userName: String,
    isLoading: Boolean = false,
    onNavigateBack: () -> Unit = {}
) {
    Surface(color = Color(0xFF3759B3)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ Back button
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onNavigateBack() }
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // ✅ Display dynamic username
                if (isLoading) {
                    Text(
                        "Hi, ...",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        "Hi, $userName",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Let me help manage your money",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun UserChatItem(prompt: String, bitmap: Bitmap?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(14.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(6.dp))
            }
            Surface(
                color = Color(0xFFE3EEFF),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp
            ) {
                Text(text = prompt, modifier = Modifier.padding(12.dp), fontSize = 15.sp, color = Color.Black)
            }
        }

        Spacer(Modifier.width(8.dp))

        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = "User Profile",
                tint = Color(0xFF3759B3),
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
private fun ModelChatItem(response: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = Color(0xFFE8F5FF)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bot),
                contentDescription = "AI",
                tint = Color(0xFF3759B3),
                modifier = Modifier.padding(4.dp)
            )
        }
        Spacer(Modifier.width(8.dp))

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 2.dp
        ) {
            MarkdownText(
                text = response,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun ModelTypingItem() {
    Row(modifier = Modifier.padding(start = 12.dp, end = 16.dp, bottom = 16.dp)) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bot),
                contentDescription = "AI Typing",
                tint = Color(0xFF3759B3),
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(Modifier.width(8.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) { TypingDots() }
        }
    }
}

@Composable
private fun TypingDots() {
    val transition = rememberInfiniteTransition(label = "dots")
    val a1 = transition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 900; 1f at 300 }),
        label = "a1"
    ).value
    val a2 = transition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 900; 1f at 600 }),
        label = "a2"
    ).value
    val a3 = transition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 900; 1f at 900 }),
        label = "a3"
    ).value

    Row(verticalAlignment = Alignment.CenterVertically) {
        Dot(alpha = a1); Spacer(Modifier.width(6.dp))
        Dot(alpha = a2); Spacer(Modifier.width(6.dp))
        Dot(alpha = a3)
    }
}

@Composable
private fun Dot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color(0xFF3759B3).copy(alpha = alpha))
    )
}

@Composable
private fun rememberPickedBitmap(uri: String): Bitmap? {
    if (uri.isEmpty()) return null
    val painterState: AsyncImagePainter.State = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(uri).size(Size.ORIGINAL).build()
    ).state
    return if (painterState is AsyncImagePainter.State.Success) {
        painterState.result.drawable.toBitmap()
    } else null
}

private sealed class MdBlock {
    data class Para(val text: AnnotatedString) : MdBlock()
    data class Bullet(val text: AnnotatedString) : MdBlock()
    data class Heading(val level: Int, val text: AnnotatedString) : MdBlock()
}

@Composable
private fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier
) {
    val blocks = remember(text) { parseMarkdownBlocks(text) }
    Column(modifier = modifier) {
        blocks.forEach { b ->
            when (b) {
                is MdBlock.Heading -> {
                    val style = when (b.level) {
                        1 -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        2 -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        else -> MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = b.text,
                        style = style,
                        color = Color.Black
                    )
                }
                is MdBlock.Para -> {
                    Text(
                        text = b.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Black
                    )
                }
                is MdBlock.Bullet -> {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            "•  ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                        Text(
                            text = b.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

private fun parseMarkdownBlocks(input: String): List<MdBlock> =
    input.lines().mapNotNull { raw ->
        val line = raw.trim()
        if (line.isEmpty()) return@mapNotNull null

        val h = Regex("^(#{1,6})\\s+(.*)$").find(line)
        if (h != null) {
            val level = h.groupValues[1].length
            val content = h.groupValues[2]
            return@mapNotNull MdBlock.Heading(level, annotateInlineStyles(content))
        }

        if (line.startsWith("* ") || line.startsWith("- ")) {
            return@mapNotNull MdBlock.Bullet(annotateInlineStyles(line.drop(2).trim()))
        }

        MdBlock.Para(annotateInlineStyles(line))
    }

private fun annotateInlineStyles(line: String): AnnotatedString {
    val token = Regex("(\\*\\*.+?\\*\\*|(?<!\\*)\\*\\S.*?\\S\\*)(?!\\*)")
    return buildAnnotatedString {
        var last = 0
        for (m in token.findAll(line)) {
            append(line.substring(last, m.range.first))

            val seg = m.value
            if (seg.startsWith("**") && seg.endsWith("**") && seg.length >= 4) {
                val inner = seg.substring(2, seg.length - 2)
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                append(inner)
                pop()
            } else if (seg.startsWith("*") && seg.endsWith("*") && seg.length >= 2) {
                val inner = seg.substring(1, seg.length - 1)
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append(inner)
                pop()
            } else {
                append(seg)
            }
            last = m.range.last + 1
        }
        if (last < line.length) append(line.substring(last))
    }
}

@Preview(showBackground = true, name = "Chat Screen")
@Composable
private fun ChatScreenPreview() {
    ChatScreen()
}