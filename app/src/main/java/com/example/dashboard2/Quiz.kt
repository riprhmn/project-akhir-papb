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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// ✅ Data class untuk Quiz Question
data class QuizQuestion(
    val questionNumber: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val imageRes: Int
)

// ✅ Data class untuk User Answer (tracking jawaban user)
data class UserAnswer(
    val questionNumber: Int,
    val selectedAnswer: Int, // index jawaban yang dipilih user
    val correctAnswer: Int,  // index jawaban yang benar
    val isCorrect: Boolean   // apakah jawaban user benar
)

// ✅ Sample Quiz Data
object QuizData {
    fun getQuizByCourseId(courseId: Int): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                questionNumber = 1,
                question = "Apa arti dari singkatan TBK dalam konteks perusahaan?",
                options = listOf(
                    "Tertutup Berjangka Kredit",
                    "Terbuka",
                    "Terbatas",
                    "Terdaftar bursa khusus"
                ),
                correctAnswer = 1,
                imageRes = R.drawable.rectangle4249
            ),
            QuizQuestion(
                questionNumber = 2,
                question = "Berapa lembar yang didapat jika membeli 1 lot?",
                options = listOf(
                    "1",
                    "10",
                    "100",
                    "1000"
                ),
                correctAnswer = 2,
                imageRes = R.drawable.soal2
            ),
            QuizQuestion(
                questionNumber = 3,
                question = "Saham Gudang Garam dipecah menjadi berapa lembar saham?",
                options = listOf(
                    "100 juta",
                    "1,9 miliar",
                    "500 juta",
                    "116 miliar"
                ),
                correctAnswer = 1,
                imageRes = R.drawable.soal3
            ),
            QuizQuestion(
                questionNumber = 4,
                question = "Satuan yang digunakan di jual beli saham di Indonesia?",
                options = listOf(
                    "Unit",
                    "Pack",
                    "Lot",
                    "Share"
                ),
                correctAnswer = 2,
                imageRes = R.drawable.soal4
            ),
            QuizQuestion(
                questionNumber = 5,
                question = "Istilah 'dividen' dalam saham merujuk pada?",
                options = listOf(
                    "Utang Perusahaan",
                    "Penurunan nilai saham",
                    "Pembagian keuntungan perusahaan",
                    "Harga saham per lot"
                ),
                correctAnswer = 2,
                imageRes = R.drawable.soal5
            )
        )
    }
}

@Composable
fun QuizScreen(
    courseId: Int,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onQuizComplete: (score: Int, totalQuestions: Int, correctAnswers: Int) -> Unit = { _, _, _ -> }
) {
    val questions = remember { QuizData.getQuizByCourseId(courseId) }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var selectedAnswerIndex by remember { mutableIntStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }

    // ✅ Track all user answers
    val userAnswers = remember { mutableStateListOf<UserAnswer>() }

    val currentQuestion = questions.getOrNull(currentQuestionIndex)

    if (showResult) {
        // ✅ Calculate correct answers from userAnswers list
        val correctAnswersCount = userAnswers.count { it.isCorrect }
        val finalScore = correctAnswersCount * 20 // 20 points per correct answer (5 questions)

        // Navigate to result (handled by parent)
        LaunchedEffect(Unit) {
            onQuizComplete(finalScore, questions.size, correctAnswersCount)
        }
    } else {
        currentQuestion?.let { question ->
            Box(
                modifier = modifier
                    .requiredWidth(width = 412.dp)
                    .requiredHeight(height = 917.dp)
                    .background(color = Color(0xff3759b3))
            ) {
                Text(
                    text = "Quiz",
                    color = Color.White,
                    lineHeight = 0.67.em,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .align(alignment = Alignment.TopCenter)
                        .offset(x = 0.5.dp, y = 79.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 26.dp, y = 71.dp)
                        .requiredSize(size = 31.dp)
                        .rotate(degrees = 360f)
                        .clickable { onBackClick() }
                )

                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopStart)
                        .offset(x = 0.dp, y = 152.dp)
                        .requiredWidth(width = 412.dp)
                        .requiredHeight(height = 765.dp)
                        .clip(shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .background(color = Color(0xfff6f6f6))
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(alignment = Alignment.TopCenter)
                        .offset(x = 0.dp, y = 180.dp)
                        .requiredWidth(width = 356.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 10.dp)
                        ) {
                            Image(
                                painter = painterResource(id = question.imageRes),
                                contentDescription = "Question ${question.questionNumber} Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .requiredHeight(height = 159.dp)
                                    .clip(shape = RoundedCornerShape(24.dp))
                            )
                        }

                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(all = 10.dp)
                            ) {
                                Text(
                                    text = "Question ${question.questionNumber}",
                                    color = Color.Black,
                                    lineHeight = 1.47.em,
                                    style = TextStyle(fontSize = 15.sp)
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
                                    text = question.question,
                                    color = Color.Black,
                                    lineHeight = 1.25.em,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.requiredWidth(width = 336.dp)
                                )
                            }
                        }
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.requiredWidth(width = 338.dp)
                    ) {
                        question.options.forEachIndexed { index, text ->
                            val isSelected = (selectedAnswerIndex == index)
                            val backgroundColor = if (isSelected) Color(0xff3759b3) else Color.White
                            val textColor = if (isSelected) Color.White else Color.Black.copy(alpha = 0.7f)

                            Tab(
                                selected = isSelected,
                                onClick = { selectedAnswerIndex = index },
                                text = {
                                    Text(
                                        text = text,
                                        color = textColor,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 1.57.em,
                                        style = TextStyle(fontSize = 14.sp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(color = backgroundColor)
                                    .padding(horizontal = 10.dp, vertical = 15.dp)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .requiredWidth(width = 336.dp)
                            .clip(shape = RoundedCornerShape(15.dp))
                            .background(
                                color = if (selectedAnswerIndex != -1) Color(0xff3759b3)
                                else Color(0xff3759b3).copy(alpha = 0.5f)
                            )
                            .clickable(enabled = selectedAnswerIndex != -1) {
                                // ✅ Save user answer
                                val isCorrect = selectedAnswerIndex == question.correctAnswer
                                userAnswers.add(
                                    UserAnswer(
                                        questionNumber = question.questionNumber,
                                        selectedAnswer = selectedAnswerIndex,
                                        correctAnswer = question.correctAnswer,
                                        isCorrect = isCorrect
                                    )
                                )

                                if (currentQuestionIndex < questions.size - 1) {
                                    currentQuestionIndex++
                                    selectedAnswerIndex = -1
                                } else {
                                    showResult = true
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 15.dp)
                    ) {
                        Text(
                            text = if (currentQuestionIndex == questions.size - 1) "Submit" else "Answer",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            lineHeight = 1.1.em,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun QuizScreenPreview() {
    MaterialTheme {
        QuizScreen(courseId = 1)
    }
}