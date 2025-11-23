package com.example.dashboard2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@Composable
fun QuizResultScreen(
    score: Int,
    total: Int,
    userAnswers: List<UserAnswer>,
    modifier: Modifier = Modifier,
    onReviewAnswer: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val correctAnswers = score
    val wrongAnswers = total - score
    val percentage = ((score.toFloat() / total.toFloat()) * 100).toInt()

    val buttonBorderColor = Color(0xff3759b3)

    Box(
        modifier = modifier
            .requiredWidth(width = 412.dp)
            .requiredHeight(height = 917.dp)
            .background(color = Color(0xff3759b3))
    ) {
        Text(
            text = "Quiz Result",
            color = Color.White,
            lineHeight = 0.67.em,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(x = 0.5.dp, y = 70.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.back),
            contentDescription = "Back",
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(x = 26.dp, y = 71.dp)
                .requiredSize(size = 31.dp)
                .clickable { onBackClick() }
        )

        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(x = 0.dp, y = 152.dp)
                .requiredWidth(width = 412.dp)
                .requiredHeight(height = 765.dp)
                .background(color = Color(0xfff6f6f6))
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .offset(x = 0.dp, y = 165.dp)
                .requiredWidth(width = 344.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(vertical = 10.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.iconparksolidtrophy),
                            contentDescription = "Trophy",
                            modifier = Modifier
                                .padding(top = 20.dp)
                                .requiredSize(size = 80.dp)
                                .align(Alignment.CenterHorizontally)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 10.dp)
                        ) {
                            Text(
                                text = "Great Job!",
                                color = Color.Black,
                                lineHeight = 0.67.em,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 10.dp)
                        ) {
                            Text(
                                text = "You've completed the quiz",
                                color = Color(0xff606060),
                                lineHeight = 1.14.em,
                                style = TextStyle(fontSize = 14.sp)
                            )
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "$percentage%",
                                lineHeight = 0.44.em,
                                style = MaterialTheme.typography.displaySmall
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Score",
                                color = Color(0xff606060),
                                lineHeight = 1.14.em,
                                style = TextStyle(fontSize = 14.sp)
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(shape = RoundedCornerShape(20.dp))
                        .background(color = Color.White)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(all = 10.dp)
                    ) {
                        Text(
                            text = "Quiz summary",
                            color = Color.Black,
                            lineHeight = 0.8.em,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icroundcheck),
                                contentDescription = "Correct",
                                modifier = Modifier
                                    .requiredSize(size = 35.dp)
                                    .clip(shape = RoundedCornerShape(20.dp))
                            )

                            Column(modifier = Modifier.requiredWidth(width = 151.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(all = 10.dp)
                                ) {
                                    Text(
                                        text = "Correct Answers",
                                        color = Color.Black,
                                        lineHeight = 1.em,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(all = 10.dp)
                                ) {
                                    Text(
                                        text = "Good job",
                                        color = Color.Black.copy(alpha = 0.5f),
                                        lineHeight = 1.33.em,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "$correctAnswers",
                            color = Color(0xff1f7a25),
                            lineHeight = 0.67.em,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.icroundcancel),
                                contentDescription = "Wrong",
                                colorFilter = ColorFilter.tint(Color(0xffff6464)),
                                modifier = Modifier.requiredSize(size = 39.dp)
                            )

                            Column(modifier = Modifier.requiredWidth(width = 151.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(all = 10.dp)
                                ) {
                                    Text(
                                        text = "Wrong Answer",
                                        color = Color.Black,
                                        lineHeight = 1.em,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(all = 10.dp)
                                ) {
                                    Text(
                                        text = if (wrongAnswers == 0) "Perfect!" else "Keep trying",
                                        color = Color.Black.copy(alpha = 0.5f),
                                        lineHeight = 1.33.em,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "$wrongAnswers",
                            color = Color(0xffff6464),
                            lineHeight = 0.67.em,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.iconamoonclockfill),
                                contentDescription = "Time",
                                modifier = Modifier
                                    .requiredSize(size = 39.dp)
                                    .clip(shape = RoundedCornerShape(30.dp))
                            )

                            Column(modifier = Modifier.requiredWidth(width = 151.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(all = 10.dp)
                                ) {
                                    Text(
                                        text = "Time Taken",
                                        color = Color.Black,
                                        lineHeight = 1.em,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(all = 10.dp)
                                ) {
                                    Text(
                                        text = "Average pace",
                                        color = Color.Black.copy(alpha = 0.5f),
                                        lineHeight = 1.33.em,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "1:00",
                            color = Color(0xffff8000),
                            lineHeight = 0.67.em,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.linearGradient(
                            0f to Color(0xff3759b3),
                            1f to Color(0xff3a5bb1),
                            start = Offset(0f, 23f),
                            end = Offset(453f, 60.5f)
                        )
                    )
                    .clickable { onReviewAnswer() }
                    .padding(horizontal = 10.dp, vertical = 15.dp)
            ) {
                Text(
                    text = "Review Answer",
                    color = Color.White,
                    lineHeight = 1.em,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = Color.White)
                    .border(
                        border = BorderStroke(2.dp, buttonBorderColor),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onBackClick() }
                    .padding(horizontal = 10.dp, vertical = 15.dp)
            ) {
                Text(
                    text = "Back",
                    color = buttonBorderColor,
                    lineHeight = 1.em,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun QuizResultScreenPreview() {
    QuizResultScreen(
        score = 5,
        total = 5,
        userAnswers = emptyList()
    )
}