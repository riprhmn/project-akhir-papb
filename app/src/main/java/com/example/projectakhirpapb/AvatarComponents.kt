package com.example.dashboard2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

// ✅ Composable untuk Initial Avatar
@Composable
fun InitialAvatar(
    name: String,
    size: Dp,
    backgroundColor: Color = Color(0xFF3759B3),
    textColor: Color = Color.White,
    borderColor: Color = Color(0xFF192851),
    borderWidth: Dp = 3.dp
) {
    // Ambil initial dari nama (maksimal 2 huruf)
    val initials = name
        .trim()
        .split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")
        .ifEmpty { "?" }

    Box(
        modifier = Modifier
            .size(size)
            .shadow(elevation = 4.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = textColor,
            style = TextStyle(
                fontSize = (size.value * 0.4).sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// ✅ Function untuk generate warna dari nama
fun getColorFromName(name: String): Color {
    val colors = listOf(
        Color(0xFF3759B3), // Blue
        Color(0xFF00BCD4), // Cyan
        Color(0xFF4CAF50), // Green
        Color(0xFFFF9800), // Orange
        Color(0xFFE91E63), // Pink
        Color(0xFF9C27B0), // Purple
        Color(0xFFFF5722), // Red
        Color(0xFF795548), // Brown
    )

    val hash = name.hashCode()
    val index = abs(hash % colors.size)
    return colors[index]
}