package com.example.dashboard2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@Composable
fun NewsDetailScreen(
    newsId: Int,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val news = getAllNews().find { it.id == newsId } ?: getAllNews()[0]

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.color_Foundation_Blue_Normal)
    ) {
        // Header dengan tombol back - FIXED POSITION
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(y = 56.dp)
                .fillMaxWidth()
                .padding(horizontal = 26.dp)
                .background(color = AppColors.color_Foundation_Blue_Normal)
                .zIndex(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(31.dp)
                    .rotate(degrees = 90f)
                    .clickable { onBackClick() }
            )

            Text(
                text = "News & Information",
                color = Color.White,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
        }

        // Scrollable Content area
        Column(
            modifier = Modifier
                .fillMaxSize() // PENTING: fillMaxSize bukan fillMaxHeight
                .offset(y = 152.dp)
                .background(color = Color(0xfff6f6f6))
                .verticalScroll(rememberScrollState())
                .padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 180.dp) // Bottom padding lebih besar
        ) {
            // News Image
            Image(
                painter = painterResource(id = news.imageRes),
                contentDescription = news.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // News Title
            Text(
                text = news.title,
                color = Color.Black,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 25.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Source and Date Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Source Logo
                Image(
                    painter = painterResource(id = news.logoRes),
                    contentDescription = news.source,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(shape = RoundedCornerShape(150.dp))
                )

                // Source Name
                Text(
                    text = news.source,
                    color = Color.Black.copy(alpha = 0.6f),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                // Date
                Text(
                    text = news.date,
                    color = Color.Black.copy(alpha = 0.6f),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // News Content - BISA PANJANG
            Text(
                text = news.content,
                color = Color.Black,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                )
            )
        }
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun NewsDetailScreenPreview() {
    NewsDetailScreen(newsId = 0)
}