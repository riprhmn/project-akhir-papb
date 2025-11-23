package com.example.dashboard2

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.model.CourseEnrollment
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

/**
 * Data class untuk weekly study data
 */
data class WeeklyStudyData(
    val dayOfWeek: String,
    val lessonsCompleted: Int,
    val date: Date
)

/**
 * Helper function untuk calculate weekly study data
 */
fun calculateWeeklyStudyData(enrollments: List<CourseEnrollment>): List<WeeklyStudyData> {
    val calendar = Calendar.getInstance()
    val today = calendar.time

    // Get start of this week (Monday)
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val weekStart = calendar.timeInMillis

    // Initialize weekly data (Mon-Sun)
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val weeklyData = daysOfWeek.mapIndexed { index, day ->
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY + index)
        WeeklyStudyData(
            dayOfWeek = day,
            lessonsCompleted = 0,
            date = calendar.time
        )
    }.toMutableList()

    // Count lessons completed per day from enrollments
    enrollments.forEach { enrollment ->
        enrollment.watchedLessonIds.forEach { lessonId ->
            val lessonDate = Date(enrollment.lastAccessedAt)
            val lessonCalendar = Calendar.getInstance().apply {
                time = lessonDate
            }

            // Check if lesson is in current week
            if (enrollment.lastAccessedAt >= weekStart) {
                val dayOfWeek = when (lessonCalendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> 0
                    Calendar.TUESDAY -> 1
                    Calendar.WEDNESDAY -> 2
                    Calendar.THURSDAY -> 3
                    Calendar.FRIDAY -> 4
                    Calendar.SATURDAY -> 5
                    Calendar.SUNDAY -> 6
                    else -> -1
                }

                if (dayOfWeek >= 0) {
                    weeklyData[dayOfWeek] = weeklyData[dayOfWeek].copy(
                        lessonsCompleted = weeklyData[dayOfWeek].lessonsCompleted + 1
                    )
                }
            }
        }
    }

    return weeklyData
}

/**
 * Composable untuk Weekly Progress Chart
 */
@Composable
fun WeeklyProgressChart(
    enrollments: List<CourseEnrollment>,
    modifier: Modifier = Modifier
) {
    val weeklyData = remember(enrollments) {
        calculateWeeklyStudyData(enrollments)
    }

    val maxLessons = max(weeklyData.maxOfOrNull { it.lessonsCompleted } ?: 1, 1)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Weekly Progress",
            color = Color.Black,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = size.width
                val height = size.height
                val bottomPadding = 40f
                val topPadding = 20f
                val chartHeight = height - bottomPadding - topPadding

                val spacing = width / (weeklyData.size + 1)

                // Draw Y-axis grid lines
                val gridLineColor = Color.LightGray.copy(alpha = 0.3f)
                for (i in 0..4) {
                    val y = topPadding + (chartHeight * i / 4)
                    drawLine(
                        color = gridLineColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Draw line chart
                val points = weeklyData.mapIndexed { index, data ->
                    val x = spacing * (index + 1)
                    val normalizedValue = if (maxLessons > 0) {
                        data.lessonsCompleted.toFloat() / maxLessons.toFloat()
                    } else 0f
                    val y = topPadding + chartHeight - (normalizedValue * chartHeight)
                    Offset(x, y)
                }

                // Draw gradient fill
                if (points.isNotEmpty()) {
                    val path = Path().apply {
                        moveTo(points.first().x, height - bottomPadding)
                        lineTo(points.first().x, points.first().y)

                        points.forEach { point ->
                            lineTo(point.x, point.y)
                        }

                        lineTo(points.last().x, height - bottomPadding)
                        close()
                    }

                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF3759B3).copy(alpha = 0.3f),
                                Color(0xFF3759B3).copy(alpha = 0.05f)
                            )
                        )
                    )
                }

                // Draw line
                if (points.size > 1) {
                    val linePath = Path().apply {
                        moveTo(points.first().x, points.first().y)
                        points.drop(1).forEach { point ->
                            lineTo(point.x, point.y)
                        }
                    }

                    drawPath(
                        path = linePath,
                        color = Color(0xFF3759B3),
                        style = Stroke(
                            width = 3f,
                            cap = StrokeCap.Round
                        )
                    )
                }

                // Draw points (dots)
                points.forEach { point ->
                    drawCircle(
                        color = Color(0xFF3759B3),
                        radius = 6f,
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3f,
                        center = point
                    )
                }

                // Draw X-axis labels (days)
                weeklyData.forEachIndexed { index, data ->
                    val x = spacing * (index + 1)
                    val y = height - 10f

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = Color.Gray.toArgb()
                            textSize = 28f
                            textAlign = Paint.Align.CENTER
                        }
                        drawText(
                            data.dayOfWeek,
                            x,
                            y,
                            paint
                        )
                    }
                }

                // Draw Y-axis labels (lesson count)
                for (i in 0..4) {
                    val y = topPadding + (chartHeight * i / 4)
                    val value = maxLessons - (maxLessons * i / 4)

                    drawContext.canvas.nativeCanvas.apply {
                        val paint = Paint().apply {
                            color = Color.Gray.toArgb()
                            textSize = 24f
                            textAlign = Paint.Align.RIGHT
                        }
                        drawText(
                            value.toString(),
                            -10f,
                            y + 8f,
                            paint
                        )
                    }
                }
            }
        }

        // Summary info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val totalThisWeek = weeklyData.sumOf { it.lessonsCompleted }
            val todayData = weeklyData.find {
                SimpleDateFormat("EEE", Locale.ENGLISH).format(Date()) == it.dayOfWeek
            }

            Column {
                Text(
                    text = "This Week",
                    color = Color.Gray,
                    style = TextStyle(fontSize = 12.sp)
                )
                Text(
                    text = "$totalThisWeek lessons",
                    color = Color(0xFF3759B3),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Today",
                    color = Color.Gray,
                    style = TextStyle(fontSize = 12.sp)
                )
                Text(
                    text = "${todayData?.lessonsCompleted ?: 0} lessons",
                    color = Color(0xFF3759B3),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Alternative: Simpler bar chart version
 */
@Composable
fun WeeklyProgressBarChart(
    enrollments: List<CourseEnrollment>,
    modifier: Modifier = Modifier
) {
    val weeklyData = remember(enrollments) {
        calculateWeeklyStudyData(enrollments)
    }

    val maxLessons = max(weeklyData.maxOfOrNull { it.lessonsCompleted } ?: 1, 1)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Weekly Progress",
            color = Color.Black,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            weeklyData.forEach { data ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Bar
                    val heightFraction = if (maxLessons > 0) {
                        data.lessonsCompleted.toFloat() / maxLessons.toFloat()
                    } else 0f

                    Box(
                        modifier = Modifier
                            .width(24.dp)
                            .height((120 * heightFraction).dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF3759B3),
                                        Color(0xFF5B7CC9)
                                    )
                                )
                            )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Label
                    Text(
                        text = data.dayOfWeek,
                        color = Color.Gray,
                        style = TextStyle(fontSize = 10.sp)
                    )
                }
            }
        }

        // Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val totalThisWeek = weeklyData.sumOf { it.lessonsCompleted }

            Text(
                text = "Total: $totalThisWeek lessons this week",
                color = Color(0xFF3759B3),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}