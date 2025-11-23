package com.example.projectakhirpapb


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dashboard2.R

@Composable
fun OpeningScreen(
    modifier: Modifier = Modifier,
    onScreenClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .requiredWidth(412.dp)
            .requiredHeight(917.dp)
            .background(color = Color(0xff294386))
            .clickable { onScreenClick() }
    ) {
        Image(
            painter = painterResource(id = R.drawable.logofinai),
            contentDescription = "Opening Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 412.dp)
                .requiredWidth(283.dp)
                .requiredHeight(67.dp)
        )
    }
}


@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun OpeningScreenPreview() {
    OpeningScreen()
}