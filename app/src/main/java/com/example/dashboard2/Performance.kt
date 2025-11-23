package com.example.dashboard2

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.repository.CourseRepository
import com.example.dashboard2.data.model.CourseEnrollment

// Define standard colors
private val NormalBlue = Color(0xff3759b3)
private val LightGray = Color(0xfff6f6f6)
private val TextGray = Color(0xff484c52)
private val DarkBlue = Color(0xff192851)
private val ProgressBarGray = Color(0xff757575).copy(alpha = 0.3f)
private val TrophyYellow = Color(0xffeab308)
private val TrophyYellowBg = Color(0xffffef52).copy(alpha = 0.5f)
private val MedalBlueBg = Color(0xffccd5f2)
private val StarGreen = Color(0xff578453)
private val StarGreenBg = Color(0xffc6ebc3)

/**
 * Data class untuk statistics performance
 */
data class PerformanceStats(
    val totalEnrolledCourses: Int = 0,
    val completedCourses: Int = 0,
    val overallProgress: Int = 0,
    val totalLessonsWatched: Int = 0,
    val totalLessonsAvailable: Int = 0,
    val remainingLessons: Int = 0,
    val estimatedStudyTimeMinutes: Int = 0,
    val studyStreak: Int = 0,
    val lastStudyDate: Long = 0L
)

@Composable
fun PerformancePage(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {}
) {
    val courseRepository = remember { CourseRepository() }
    val enrollments by courseRepository.getUserEnrollments()
        .collectAsState(initial = emptyList())

    var isLoading by remember { mutableStateOf(true) }
    var stats by remember { mutableStateOf(PerformanceStats()) }

    // ✅ Calculate performance stats dari enrollments
    LaunchedEffect(enrollments) {
        isLoading = true
        try {
            Log.d("PERFORMANCE", "========== CALCULATING PERFORMANCE ==========")
            Log.d("PERFORMANCE", "Total enrollments: ${enrollments.size}")

            val totalEnrolled = enrollments.size
            val completed = enrollments.count { it.isCompleted }

            // Calculate overall progress (average dari semua course)
            val overallProgress = if (totalEnrolled > 0) {
                enrollments.sumOf { it.progress } / totalEnrolled
            } else 0

            // Calculate total lessons watched dan available
            var totalWatched = 0
            var totalAvailable = 0

            enrollments.forEach { enrollment ->
                val courseData = CourseDatabase.getCourseById(enrollment.courseId)
                if (courseData != null) {
                    totalWatched += enrollment.watchedLessonIds.size
                    totalAvailable += courseData.lessons.size

                    Log.d("PERFORMANCE", "Course ${enrollment.courseId}:")
                    Log.d("PERFORMANCE", "  - Watched: ${enrollment.watchedLessonIds.size}/${courseData.lessons.size}")
                    Log.d("PERFORMANCE", "  - Progress: ${enrollment.progress}%")
                }
            }

            val remaining = totalAvailable - totalWatched

            // Calculate estimated study time (berdasarkan lesson yang sudah ditonton)
            var totalMinutes = 0
            enrollments.forEach { enrollment ->
                val courseData = CourseDatabase.getCourseById(enrollment.courseId)
                if (courseData != null) {
                    enrollment.watchedLessonIds.forEach { lessonId ->
                        val lesson = courseData.lessons.find { it.id == lessonId }
                        if (lesson != null) {
                            // Parse duration string (e.g., "12 min" -> 12)
                            val minutes = lesson.duration.replace(" min", "").trim().toIntOrNull() ?: 0
                            totalMinutes += minutes
                        }
                    }
                }
            }

            // Calculate study streak (hari belajar berturut-turut)
            val streak = calculateStudyStreak(enrollments)
            val lastStudy = enrollments.maxOfOrNull { it.lastAccessedAt } ?: 0L

            stats = PerformanceStats(
                totalEnrolledCourses = totalEnrolled,
                completedCourses = completed,
                overallProgress = overallProgress,
                totalLessonsWatched = totalWatched,
                totalLessonsAvailable = totalAvailable,
                remainingLessons = remaining,
                estimatedStudyTimeMinutes = totalMinutes,
                studyStreak = streak,
                lastStudyDate = lastStudy
            )

            Log.d("PERFORMANCE", "Stats calculated:")
            Log.d("PERFORMANCE", "  - Overall Progress: $overallProgress%")
            Log.d("PERFORMANCE", "  - Lessons: $totalWatched/$totalAvailable")
            Log.d("PERFORMANCE", "  - Study Time: ${totalMinutes / 60}h ${totalMinutes % 60}m")
            Log.d("PERFORMANCE", "  - Streak: $streak days")
            Log.d("PERFORMANCE", "=========================================")

        } catch (e: Exception) {
            Log.e("PERFORMANCE", "Error calculating stats", e)
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize().background(color = NormalBlue)
    ) {
        // --- HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(152.dp)
                .background(NormalBlue)
                .padding(top = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            // Tombol Kembali
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 26.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(31.dp)
                        .rotate(degrees = 360f)
                )
            }
            // Judul
            Text(
                text = "Performance",
                color = Color.White,
                lineHeight = 0.67.em,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // --- Background Box ---
        Box(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 152.dp)
                .background(
                    color = LightGray,
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                )
                .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
        )

        // --- LOADING STATE ---
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NormalBlue)
            }
        } else {
            // --- Scrollable Content ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(
                    top = 20.dp,
                    bottom = 20.dp,
                    start = 20.dp,
                    end = 20.dp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 152.dp)
            ) {
                // --- Overall Progress Section ---
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Overall Progress",
                            color = Color.Black,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Course Completion",
                                color = Color.Black.copy(alpha = 0.8f),
                                style = TextStyle(fontSize = 14.sp)
                            )
                            Text(
                                text = "${stats.overallProgress}%",
                                color = NormalBlue,
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(11.dp)
                                .clip(shape = RoundedCornerShape(5.dp))
                                .background(color = ProgressBarGray)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = stats.overallProgress / 100f)
                                    .clip(shape = RoundedCornerShape(5.dp))
                                    .background(color = NormalBlue)
                            )
                        }
                    }
                }

                // --- Stats Row ---
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        StatItem(
                            value = stats.totalLessonsWatched.toString(),
                            label = "Lessons Completed"
                        )
                        StatItem(
                            value = stats.remainingLessons.toString(),
                            label = "Remaining"
                        )
                        StatItem(
                            value = formatStudyTime(stats.estimatedStudyTimeMinutes),
                            label = "Study Time"
                        )
                    }
                }

                // --- Study Progress Chart (REAL DATA) ---
                item {
                    WeeklyProgressChart(
                        enrollments = enrollments,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // --- Achievements ---
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Achievements",
                            color = Color.Black,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 5.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Fast Learner: Jika sudah menyelesaikan minimal 10 lesson
                            AchievementItem(
                                iconRes = R.drawable.ic_trophy,
                                label = "Fast Learner",
                                iconTint = TrophyYellow,
                                bgColor = TrophyYellowBg,
                                borderColor = TrophyYellow,
                                isUnlocked = stats.totalLessonsWatched >= 10,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(10.dp))
                            // Consistent: Jika study streak >= 3 hari
                            AchievementItem(
                                iconRes = R.drawable.ic_medal,
                                label = "Consistent",
                                iconTint = NormalBlue,
                                bgColor = MedalBlueBg,
                                borderColor = NormalBlue,
                                isUnlocked = stats.studyStreak >= 3,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(10.dp))
                            // Top Scorer: Jika ada course yang completed
                            AchievementItem(
                                iconRes = R.drawable.ic_star,
                                label = "Top Scorer",
                                iconTint = StarGreen,
                                bgColor = StarGreenBg,
                                borderColor = StarGreen,
                                isUnlocked = stats.completedCourses > 0,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // --- Study Streak ---
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Study Streak",
                            color = Color.Black,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .align(Alignment.Start)
                                .padding(bottom = 5.dp)
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clip(shape = RoundedCornerShape(20.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        0f to Color(0xff3783b3),
                                        1f to Color(0xff39184d),
                                        start = Offset(Float.POSITIVE_INFINITY, 0f),
                                        end = Offset(0f, Float.POSITIVE_INFINITY)
                                    )
                                )
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_fire),
                                contentDescription = "Streak Fire Icon",
                                colorFilter = ColorFilter.tint(Color.White),
                                modifier = Modifier.size(size = 46.dp)
                            )
                            Text(
                                text = stats.studyStreak.toString(),
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "Days in a row",
                                color = Color.White.copy(alpha = 0.9f),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = if (stats.studyStreak > 0) {
                                    "Keep it up! You're doing great!"
                                } else {
                                    "Start learning to build your streak!"
                                },
                                color = Color.White.copy(alpha = 0.9f),
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Helper function untuk format study time ---
fun formatStudyTime(minutes: Int): String {
    return if (minutes >= 60) {
        val hours = minutes / 60
        val mins = minutes % 60
        if (mins > 0) "${hours}h ${mins}m" else "${hours}h"
    } else {
        "${minutes}m"
    }
}

// --- Helper function untuk calculate study streak ---
fun calculateStudyStreak(enrollments: List<CourseEnrollment>): Int {
    if (enrollments.isEmpty()) return 0

    // Get semua tanggal yang ada activity (lastAccessedAt)
    val studyDates = enrollments
        .map { it.lastAccessedAt }
        .filter { it > 0 }
        .sorted()
        .reversed() // Sort descending (terbaru dulu)

    if (studyDates.isEmpty()) return 0

    // Check apakah ada activity hari ini atau kemarin
    val now = System.currentTimeMillis()
    val oneDayMs = 24 * 60 * 60 * 1000L
    val latestStudy = studyDates.first()

    // Jika study terakhir lebih dari 1 hari yang lalu, streak = 0
    if (now - latestStudy > oneDayMs) return 0

    // Calculate consecutive days
    var streak = 1
    var currentDay = latestStudy / oneDayMs

    for (i in 1 until studyDates.size) {
        val studyDay = studyDates[i] / oneDayMs
        if (currentDay - studyDay == 1L) {
            streak++
            currentDay = studyDay
        } else {
            break
        }
    }

    return streak
}

// --- Composable for Stats Items ---
@Composable
fun StatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 5.dp)
    ) {
        Text(
            text = value,
            color = Color.Black,
            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )
        Text(
            text = label,
            color = TextGray,
            style = TextStyle(fontSize = 12.sp)
        )
    }
}

// --- Composable for Achievement Items ---
@Composable
fun AchievementItem(
    @DrawableRes iconRes: Int,
    label: String,
    iconTint: Color,
    bgColor: Color,
    borderColor: Color,
    isUnlocked: Boolean = true,
    modifier: Modifier = Modifier
) {
    val displayBgColor = if (isUnlocked) bgColor else Color.LightGray.copy(alpha = 0.3f)
    val displayIconTint = if (isUnlocked) iconTint else Color.Gray
    val displayBorderColor = if (isUnlocked) borderColor else Color.Gray

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape = RoundedCornerShape(15.dp))
            .background(color = displayBgColor)
            .border(
                border = BorderStroke(1.dp, displayBorderColor),
                shape = RoundedCornerShape(15.dp)
            )
            .padding(vertical = 10.dp, horizontal = 5.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                colorFilter = if (iconRes != R.drawable.ic_star) {
                    ColorFilter.tint(displayIconTint)
                } else null,
                modifier = Modifier.size(size = 36.dp),
                alpha = if (isUnlocked) 1f else 0.5f
            )
            Text(
                text = label,
                color = displayIconTint,
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
}