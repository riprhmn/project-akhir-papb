package com.example.projectakhirpapb  // ✅ Package diganti ke dashboard2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AndroidCompact29(
    modifier: Modifier = Modifier,
    onNavigateBackToList: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .requiredWidth(width = 412.dp)
            .requiredHeight(height = 917.dp)
            .clickable(onClick = {  })
            .background(color = Color.Black.copy(alpha = 0.52f))
            .padding(horizontal = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp))
                .clip(shape = RoundedCornerShape(20.dp))
                .background(color = Color.White)
                .clickable(enabled = false, onClick = {})
                .padding(vertical = 20.dp, horizontal = 20.dp)

        ) {
            Image(
                painter = painterResource(id = R.drawable.receivemail),
                contentDescription = "Receiving mail messages via email",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .requiredWidth(width = 198.dp)
                    .requiredHeight(height = 174.dp)
                    .padding(bottom = 16.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    text = buildAnnotatedString {
                        append("🎉 ")
                        withStyle(style = SpanStyle(
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold)) {
                            append("Pendaftaran Berhasil!")
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Terima kasih telah mendaftar!\nKami sudah mengirimkan detail workshop ke email kamu. Jangan lupa catat tanggalnya dan siapkan dirimu untuk pengalaman seru dan penuh wawasan!",
                    color = Color.Black.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 1.4.em,
                    style = TextStyle(
                        fontSize = 13.sp),
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    InputChip(
                        label = {
                            Text(
                                text = "Back",
                                color = Color.White,
                                lineHeight = 1.33.em,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xff3759b3)
                        ),
                        selected = true,
                        onClick = { onNavigateBackToList() }
                    )
                    InputChip(
                        label = {
                            Text(
                                text = "Check",
                                color = Color.White,
                                lineHeight = 1.33.em,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xff3759b3)
                        ),
                        selected = true,
                        onClick = { onNavigateBackToList() }
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun AndroidCompact29Preview() {
    AndroidCompact29(
        modifier = Modifier,
        onNavigateBackToList = {}
    )
}