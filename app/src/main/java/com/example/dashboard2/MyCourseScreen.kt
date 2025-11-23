package com.example.dashboard2

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.repository.CourseRepository
import com.example.dashboard2.data.model.CourseEnrollment

enum class CourseFilter {
    ALL, ONGOING, COMPLETED
}

@Composable
fun MyCourseScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCourseClick: (Int) -> Unit = {}
) {
    val courseRepository = remember { CourseRepository() }
    val enrollments by courseRepository.getUserEnrollments()
        .collectAsState(initial = emptyList())

    var selectedFilter by remember { mutableStateOf(CourseFilter.ALL) }

    // Logging
    LaunchedEffect(enrollments) {
        Log.d("MY_COURSE", "========== ENROLLMENTS UPDATE ==========")
        Log.d("MY_COURSE", "Total enrollments: ${enrollments.size}")
        enrollments.forEach { enrollment ->
            Log.d("MY_COURSE", "Course ${enrollment.courseId}: ${enrollment.progress}%, isCompleted=${enrollment.isCompleted}")
        }
        Log.d("MY_COURSE", "=======================================")
    }

    // Calculate statistics
    val activeCourses = enrollments.size
    val completedCourses = enrollments.count { it.isCompleted }
    val averageProgress = if (activeCourses > 0) {
        enrollments.sumOf { it.progress } / activeCourses
    } else 0

    // Filter courses
    val filteredCourses = when (selectedFilter) {
        CourseFilter.ALL -> enrollments
        CourseFilter.ONGOING -> enrollments.filter { !it.isCompleted }
        CourseFilter.COMPLETED -> enrollments.filter { it.isCompleted }
    }.sortedByDescending { it.lastAccessedAt }

    // ✅ FIX: Use Column instead of nested Box
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF3759B3))
    ) {
        // ======= HEADER (FIXED HEIGHT) =======
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, start = 24.dp, end = 24.dp, bottom = 24.dp)
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
                text = "My Course",
                color = Color.White,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.weight(1f)
            )
        }

        // ======= CONTENT AREA (FLEXIBLE HEIGHT) =======
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)  // ✅ CRITICAL: Takes remaining space
                .background(
                    color = Color(0xFFF6F6F6),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        ) {
            // ✅ LazyColumn dengan fillMaxSize
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 24.dp,
                    bottom = 100.dp  // ✅ Extra padding untuk bottom nav
                )
            ) {
                // ======= STATISTICS =======
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        StatisticCard(
                            title = "Active Course",
                            value = activeCourses.toString(),
                            valueColor = AppColors.color_Foundation_Blue_Normal,
                            titleColor = AppColors.color_Foundation_Blue_Normal,
                            modifier = Modifier.weight(1f)
                        )

                        StatisticCard(
                            title = "Completed",
                            value = completedCourses.toString(),
                            valueColor = Color(0xFF24AF23),
                            titleColor = Color(0xFF1BAB25),
                            modifier = Modifier.weight(1f)
                        )

                        StatisticCard(
                            title = "Average",
                            value = "$averageProgress%",
                            valueColor = Color(0xFFEC921E),
                            titleColor = Color(0xFFED982B),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // ======= FILTER TABS =======
                item {
                    FilterTabs(
                        selectedFilter = selectedFilter,
                        onFilterSelected = {
                            Log.d("MY_COURSE", "User selected filter: $it")
                            selectedFilter = it
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ======= COURSE LIST =======
                if (filteredCourses.isEmpty()) {
                    item {
                        EmptyCourseState(filter = selectedFilter)
                    }
                } else {
                    items(
                        items = filteredCourses,
                        key = { it.getDocumentId() }
                    ) { enrollment ->
                        MyCourseCard(
                            enrollment = enrollment,
                            onClick = { onCourseClick(enrollment.courseId) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticCard(
    title: String,
    value: String,
    valueColor: Color,
    titleColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.height(64.dp)
    ) {
        Text(
            text = value,
            color = valueColor,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = titleColor,
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun FilterTabs(
    selectedFilter: CourseFilter,
    onFilterSelected: (CourseFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(Color(0xFFAFAFAF).copy(alpha = 0.25f))
            .padding(10.dp)
    ) {
        FilterTab(
            title = "All",
            isSelected = selectedFilter == CourseFilter.ALL,
            onClick = { onFilterSelected(CourseFilter.ALL) },
            modifier = Modifier.weight(1f)
        )

        FilterTab(
            title = "Ongoing",
            isSelected = selectedFilter == CourseFilter.ONGOING,
            onClick = { onFilterSelected(CourseFilter.ONGOING) },
            modifier = Modifier.weight(1f)
        )

        FilterTab(
            title = "Completed",
            isSelected = selectedFilter == CourseFilter.COMPLETED,
            onClick = { onFilterSelected(CourseFilter.COMPLETED) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FilterTab(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) AppColors.color_Foundation_Blue_Normal
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else Color(0xFF5B5B5B),
            textAlign = TextAlign.Center,
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
fun MyCourseCard(
    enrollment: CourseEnrollment,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val courseData = CourseDatabase.getCourseById(enrollment.courseId)

    if (courseData == null) return

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(id = courseData.imageRes),
            contentDescription = courseData.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(115.dp, 99.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = courseData.title,
                color = Color.Black,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = courseData.price,
                color = Color.Black.copy(alpha = 0.7f),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${enrollment.progress}% complete",
                color = Color.Black,
                style = TextStyle(fontSize = 10.sp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(11.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF757575).copy(alpha = 0.3f))
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (enrollment.progress > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(enrollment.progress / 100f)
                                .height(11.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFC1CCE7),
                                            Color(0xFF3759B3)
                                        )
                                    )
                                )
                        )
                    }

                    if (enrollment.progress > 0) {
                        Box(
                            modifier = Modifier
                                .size(11.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF3759B3))
                                .shadow(4.dp, CircleShape)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCourseState(
    filter: CourseFilter,
    modifier: Modifier = Modifier
) {
    val message = when (filter) {
        CourseFilter.ALL -> "No enrolled courses yet"
        CourseFilter.ONGOING -> "No ongoing courses"
        CourseFilter.COMPLETED -> "No completed courses yet"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp)
    ) {
        Text(
            text = message,
            color = Color.Black.copy(alpha = 0.5f),
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center
        )

        if (filter == CourseFilter.ALL) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start exploring courses to begin your learning journey!",
                color = Color.Black.copy(alpha = 0.3f),
                style = TextStyle(fontSize = 12.sp),
                textAlign = TextAlign.Center
            )
        }
    }
}