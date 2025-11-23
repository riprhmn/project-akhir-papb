package com.example.dashboard2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultQuizScreen(
    score: Int,
    totalQuestions: Int,
    correctAnswers: Int,
    courseName: String,
    courseId: Int = 1,
    onRetakeQuiz: () -> Unit,
    onBackToCourse: () -> Unit
) {
    val percentage = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0
    val wrongAnswers = totalQuestions - correctAnswers

    // ✅ Get quiz questions and user answers
    val questions = remember { QuizData.getQuizByCourseId(courseId) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF3759B3))
    ) {
        // ======= HEADER =======
        TopAppBar(
            title = {
                Text(
                    text = "Review Answer",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackToCourse) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFF3759B3)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = 56.dp)
        )

        // ======= SCROLLABLE CONTENT =======
        Column(
            modifier = Modifier
                .padding(top = 152.dp)
                .fillMaxSize()
                .clip(shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(color = Color(0xFFF6F6F6))
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // ======= SCORE CARD =======
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(344.dp)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = Color.White)
            ) {
                // Trophy Section
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.iconparksolidtrophy),
                        contentDescription = "Trophy",
                        modifier = Modifier
                            .size(68.dp)
                            .clip(shape = RoundedCornerShape(60.dp))
                            .background(Color(0xFF6EE7B7))
                            .padding(all = 10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Great Job!",
                        color = Color.Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "You've completed the quiz",
                        color = Color(0xFF808080),
                        fontSize = 14.sp
                    )
                }

                // Dashed Divider
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .drawBehind {
                            val strokeWidthPx = 1.dp.toPx()
                            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = strokeWidthPx,
                                pathEffect = dashPathEffect
                            )
                        }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stats Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(17.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    // Correct
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$correctAnswers",
                            color = Color(0xFF1F7A25),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Correct",
                            color = Color(0xFF484C52),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Vertical Divider
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(52.dp)
                            .drawBehind {
                                val strokeWidthPx = 1.dp.toPx()
                                val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, size.height),
                                    strokeWidth = strokeWidthPx,
                                    pathEffect = dashPathEffect
                                )
                            }
                    )

                    // Wrong
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$wrongAnswers",
                            color = Color(0xFFFF6464),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Wrong",
                            color = Color(0xFF484C52),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Vertical Divider
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(52.dp)
                            .drawBehind {
                                val strokeWidthPx = 1.dp.toPx()
                                val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, size.height),
                                    strokeWidth = strokeWidthPx,
                                    pathEffect = dashPathEffect
                                )
                            }
                    )

                    // Score Percentage
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "$percentage%",
                            color = Color(0xFF3759B3),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Score",
                            color = Color(0xFF484C52),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ======= REVIEW ANSWER SECTION =======
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 31.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Review Answer",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ======= QUESTIONS LIST =======
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(350.dp)
            ) {
                questions.forEachIndexed { index, question ->
                    QuestionReviewItem(
                        questionNumber = question.questionNumber,
                        questionText = question.question,
                        options = question.options,
                        correctAnswerIndex = question.correctAnswer,
                        userAnswerIndex = question.correctAnswer // TODO: Get from user answers
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ======= ACTION BUTTONS =======
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .width(344.dp)
                    .padding(bottom = 24.dp)
            ) {
                Button(
                    onClick = onRetakeQuiz,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3759B3)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Retake Quiz",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = onBackToCourse,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF3759B3)
                    ),
                    border = BorderStroke(2.dp, Color(0xFF3759B3)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Back to Course",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionReviewItem(
    questionNumber: Int,
    questionText: String,
    options: List<String>,
    correctAnswerIndex: Int,
    userAnswerIndex: Int
) {
    val isCorrect = userAnswerIndex == correctAnswerIndex

    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(color = Color.White)
            .border(
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 20.dp, vertical = 25.dp)
    ) {
        // Question Header
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Question $questionNumber",
                color = Color.Black.copy(alpha = 0.75f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            // Correct/Wrong Indicator
            Icon(
                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                contentDescription = if (isCorrect) "Correct" else "Wrong",
                tint = if (isCorrect) Color(0xFF1F7A25) else Color(0xFFFF6464),
                modifier = Modifier.size(24.dp)
            )
        }

        // Question Text
        Text(
            text = questionText,
            color = Color.Black,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 1.25.em,
            modifier = Modifier.fillMaxWidth()
        )

        // Options
        options.forEachIndexed { index, option ->
            val isUserAnswer = index == userAnswerIndex
            val isCorrectAnswer = index == correctAnswerIndex

            val backgroundColor = when {
                isCorrectAnswer -> Color(0xFFA6FFB7).copy(alpha = 0.35f)
                isUserAnswer && !isCorrect -> Color(0xFFFFCDD2).copy(alpha = 0.5f)
                else -> Color.White
            }

            val borderColor = when {
                isCorrectAnswer -> Color(0xFF92DCAB)
                isUserAnswer && !isCorrect -> Color(0xFFFF6464)
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = backgroundColor)
                    .then(
                        if (borderColor != Color.Transparent) {
                            Modifier.border(
                                border = BorderStroke(3.dp, borderColor),
                                shape = RoundedCornerShape(10.dp)
                            )
                        } else Modifier
                    )
                    .padding(horizontal = 10.dp, vertical = 20.dp)
            ) {
                Text(
                    text = option,
                    color = Color.Black.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}