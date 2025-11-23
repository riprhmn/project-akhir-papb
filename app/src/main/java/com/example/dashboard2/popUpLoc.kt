package com.example.dashboard2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val NormalBlue = Color(0xFF3759B3)

@Composable
fun PopUpLocationScreen(
    modifier: Modifier = Modifier,
    onBackToProfile: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.52f))
            .padding(horizontal = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(shape = RoundedCornerShape(20.dp))
                .background(color = Color.White)
                .padding(horizontal = 20.dp, vertical = 30.dp)
        ) {
            // ✅ Image
            Image(
                painter = painterResource(id = R.drawable.pop_loc),
                contentDescription = "Location Saved Illustration",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .heightIn(min = 164.dp, max = 250.dp)
                    .padding(bottom = 10.dp)
            )

            // ✅ Text Section
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Yeay! Lokasimu sudah tersimpan.",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Data lokasi berhasil ditambahkan ke akunmu.",
                    color = Color.Black.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }

            // ✅ Back Button
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = NormalBlue)
                    .clickable { onBackToProfile() }
                    .padding(horizontal = 40.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Back",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}