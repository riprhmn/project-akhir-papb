package com.example.dashboard2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Composable
fun LandingPage(
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .requiredWidth(412.dp)
            .requiredHeight(917.dp)
            .background(color = Color(0xff294386))
    ) {
        Image(
            painter = painterResource(id = R.drawable.vec203),
            contentDescription = "Vector 203",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = (-26).dp)
                .requiredWidth(520.dp)
                .requiredHeight(999.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.vec201),
            contentDescription = "Vector 201",
            modifier = Modifier
                .requiredWidth(332.dp)
                .requiredHeight(917.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.vec204),
            contentDescription = "Vector 204",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 171.dp)
                .requiredWidth(241.dp)
                .requiredHeight(314.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.vec205),
            contentDescription = "Vector 205",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 226.dp, y = 36.dp)
                .requiredWidth(191.dp)
                .requiredHeight(184.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.ling107),
            contentDescription = "Lingkaran atas",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 206.dp, y = 114.dp)
                .requiredWidth(106.dp)
                .requiredHeight(107.dp)
                .clip(CircleShape)
        )

        Image(
            painter = painterResource(id = R.drawable.vec202),
            contentDescription = "Vector 202",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = 256.dp)
                .requiredWidth(412.dp)
                .requiredHeight(661.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-20.5).dp)
                .requiredWidth(397.dp)
                .requiredHeight(120.dp)
                .padding(10.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
                modifier = Modifier.requiredWidth(253.dp)
            ) {
                Text(
                    text = "Welcome Back !",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 0.69.em,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Manage your money smartly with your personal AI assistant.",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.47.em,
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.ling105),
            contentDescription = "Lingkaran tengah",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 53.dp, y = 482.dp)
                .requiredSize(83.dp)
                .clip(CircleShape)
        )

        Image(
            painter = painterResource(id = R.drawable.ling104),
            contentDescription = "Lingkaran bawah",
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 70.dp, y = 589.dp)
                .requiredWidth(203.dp)
                .requiredHeight(208.dp)
        )

        SignInButton(
            onClick = onSignInClick,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(y = (-1).dp)
        )

        SignUpButton(
            onClick = onSignUpClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(y = (-1).dp)
        )
    }
}

@Composable
fun SignInButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 65.5.dp, vertical = 40.dp)
    ) {
        Text(
            text = "Sign In",
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 1.1.em,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.requiredWidth(82.dp)
        )
    }
}

@Composable
fun SignUpButton(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 61.5.dp, vertical = 40.dp)
    ) {
        Text(
            text = "Sign Up",
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 1.1.em,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.requiredWidth(75.dp)
        )
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun LandingPagePreview() {
    LandingPage()
}