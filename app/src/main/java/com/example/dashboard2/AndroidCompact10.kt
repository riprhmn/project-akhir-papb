package com.example.dashboard2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

// ✅ Enum untuk tab
enum class CourseTab {
    LESSON, DESCRIPTION, HISTORY
}

@Composable
fun AndroidCompact10(
    courseId: Int = 1,
    progressManager: CourseProgressManager? = null,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onLessonClick: (CourseLesson) -> Unit = {},
    onQuizClick: (Int) -> Unit = {}
) {
    // ✅ Get course data berdasarkan courseId
    val courseData = remember(courseId) {
        CourseDatabase.getCourseById(courseId)
    }

    // Jika course tidak ditemukan, tampilkan error
    if (courseData == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Course not found")
        }
        return
    }

    // State untuk tab yang dipilih
    var selectedTab by remember { mutableStateOf(CourseTab.LESSON) }

    // ✅ Get progress data from CourseProgressManager
    val courseProgress = progressManager?.getCourseProgress(courseId)?.value

    val watchedLessonIds = courseProgress?.watchedLessonIds ?: emptyList()
    val watchHistory = courseProgress?.watchHistory ?: emptyList()
    val progressPercentage = (courseProgress?.progressPercentage ?: 0).toFloat()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF3759B3))
    ) {
        // ======= HEADER =======
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 56.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBackClick() }
            )

            Text(
                text = "Course",
                color = Color.White,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
        }

        // ======= CONTENT AREA =======
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = 152.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .background(color = Color(0xFFF6F6F6))
        ) {
            // ✅ Conditional rendering based on tab
            if (selectedTab == CourseTab.HISTORY) {
                // History menggunakan LazyColumn untuk full screen scrollable
                Column(modifier = Modifier.fillMaxSize()) {
                    // Course Header dan Tab Bar tetap visible
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CourseHeader(courseData = courseData)

                        TabBar(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it },
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // History Content dengan LazyColumn
                    HistoryContentScrollable(
                        watchHistory = watchHistory,
                        totalLessons = courseData.lessons.size,
                        progressPercentage = progressPercentage,
                        courseId = courseId,
                        onHistoryItemClick = { historyEntry ->
                            val lesson = courseData.lessons.find { it.id == historyEntry.lessonId }
                            if (lesson != null) {
                                onLessonClick(lesson)
                            }
                        },
                        onQuizClick = onQuizClick,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    )
                }
            } else {
                // Lesson dan Description menggunakan regular scroll
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 24.dp)
                ) {
                    CourseHeader(courseData = courseData)

                    TabBar(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it },
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    when (selectedTab) {
                        CourseTab.LESSON -> {
                            LessonList(
                                lessons = courseData.lessons,
                                watchedLessonIds = watchedLessonIds,
                                onLessonClick = onLessonClick,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        CourseTab.DESCRIPTION -> {
                            DescriptionContent(
                                description = courseData.description,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun CourseHeader(
    courseData: CourseData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // Course Image
        Image(
            painter = painterResource(id = courseData.imageRes),
            contentDescription = courseData.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Course Title
        Text(
            text = courseData.title,
            color = Color.Black,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Price & Rating Row
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = courseData.price,
                color = Color.Black.copy(alpha = 0.7f),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF3759B3))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "Star Rating",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = courseData.rating,
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun TabBar(
    selectedTab: CourseTab,
    onTabSelected: (CourseTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        TabItem(
            title = "Lesson",
            isSelected = selectedTab == CourseTab.LESSON,
            onClick = { onTabSelected(CourseTab.LESSON) }
        )

        TabItem(
            title = "Description",
            isSelected = selectedTab == CourseTab.DESCRIPTION,
            onClick = { onTabSelected(CourseTab.DESCRIPTION) }
        )

        TabItem(
            title = "History",
            isSelected = selectedTab == CourseTab.HISTORY,
            onClick = { onTabSelected(CourseTab.HISTORY) }
        )
    }
}

@Composable
fun TabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) AppColors.color_Foundation_Blue_Normal_hover else Color.Black.copy(alpha = 0.5f),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        )

        if (isSelected) {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF3759B3))
            )
        }
    }
}

@Composable
fun LessonList(
    lessons: List<CourseLesson>,
    watchedLessonIds: List<Int>,
    onLessonClick: (CourseLesson) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        lessons.forEach { lesson ->
            val isWatched = watchedLessonIds.contains(lesson.id)
            LessonItem(
                lesson = lesson,
                isWatched = isWatched,
                onClick = { onLessonClick(lesson) }
            )
        }
    }
}

@Composable
fun LessonItem(
    lesson: CourseLesson,
    isWatched: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // ✅ Checkmark indicator untuk yang sudah ditonton
                if (isWatched) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AppColors.color_Foundation_Blue_Normal),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = lesson.title,
                        color = Color.Black,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = lesson.duration,
                        color = Color.Black.copy(alpha = 0.5f),
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Icon(
                painter = painterResource(id = R.drawable.rightarrow),
                contentDescription = "Go to lesson",
                tint = AppColors.color_Foundation_Blue_Normal_hover,
                modifier = Modifier.size(20.dp)
            )
        }

        HorizontalDivider(
            color = Color.Black.copy(alpha = 0.1f),
            thickness = 1.dp
        )
    }
}

@Composable
fun DescriptionContent(
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = description,
            color = Color.Black,
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        )
    }
}

// ✅ NEW: Scrollable History Content dengan LazyColumn
@Composable
fun HistoryContentScrollable(
    watchHistory: List<WatchHistoryEntry>,
    totalLessons: Int,
    progressPercentage: Float,
    courseId: Int,
    onHistoryItemClick: (WatchHistoryEntry) -> Unit,
    onQuizClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Group by unique lessons
    val uniqueHistory = remember(watchHistory) {
        watchHistory
            .groupBy { it.lessonId }
            .map { (_, entries) -> entries.maxByOrNull { it.watchedAt }!! }
            .sortedByDescending { it.watchedAt }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        modifier = modifier
    ) {
        // ======= PROGRESS SECTION =======
        item {
            Text(
                text = "Your progress",
                color = Color.Black,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Progress Card
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Completed",
                        color = Color.Black.copy(alpha = 0.7f),
                        style = TextStyle(fontSize = 14.sp)
                    )
                    Text(
                        text = "${progressPercentage.toInt()}%",
                        color = AppColors.color_Foundation_Blue_Normal,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFE0E0E0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressPercentage / 100f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .background(AppColors.color_Foundation_Blue_Normal)
                    )
                }

                // Lessons count
                Text(
                    text = "${watchHistory.map { it.lessonId }.distinct().size} of $totalLessons lessons completed",
                    color = Color.Black.copy(alpha = 0.5f),
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        }

        // ✅ ATTEMPT QUIZ SECTION (Only shows when progress = 100%)
        if (progressPercentage >= 100f) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Congratulations message
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F8FF))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "🎉 Congratulations!",
                        color = AppColors.color_Foundation_Blue_Normal,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "You have completed all lessons",
                        color = Color.Black.copy(alpha = 0.6f),
                        style = TextStyle(fontSize = 14.sp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Attempt Quiz Button
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(15.dp))
                        .background(color = AppColors.color_Foundation_Blue_Normal)
                        .clickable { onQuizClick(courseId) }
                        .padding(vertical = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Attempt Quiz",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // ======= WATCH HISTORY SECTION =======
        if (watchHistory.isEmpty()) {
            // Empty state
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    Text(
                        text = "No watch history yet",
                        color = Color.Black.copy(alpha = 0.5f),
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Start watching lessons to see your history",
                        color = Color.Black.copy(alpha = 0.3f),
                        style = TextStyle(fontSize = 12.sp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // History list header
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Text(
                    text = "Watch History",
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            // History items
            items(
                items = uniqueHistory,
                key = { it.lessonId }
            ) { historyEntry ->
                HistoryItem(
                    historyEntry = historyEntry,
                    onClick = { onHistoryItemClick(historyEntry) }
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    historyEntry: WatchHistoryEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()) }
    val watchedTime = remember(historyEntry.watchedAt) {
        dateFormat.format(Date(historyEntry.watchedAt))
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = historyEntry.lessonTitle,
                    color = Color.Black,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = historyEntry.lessonDuration,
                    color = Color.Black.copy(alpha = 0.5f),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                // ✅ Timestamp ketika ditonton
                Text(
                    text = "Watched on $watchedTime",
                    color = Color.Black.copy(alpha = 0.4f),
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.rightarrow),
                contentDescription = "Watch again",
                tint = AppColors.color_Foundation_Blue_Normal_hover,
                modifier = Modifier.size(20.dp)
            )
        }

        HorizontalDivider(
            color = Color.Black.copy(alpha = 0.1f),
            thickness = 1.dp
        )
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun AndroidCompact10Preview() {
    AndroidCompact10(courseId = 1)
}