package com.example.dashboard2

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.repository.CourseRepository
import kotlinx.coroutines.launch

enum class CourseCategory {
    ALL, REKSADANA, SAHAM, CRYPTO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularCourseScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCourseClick: (Int) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf(CourseCategory.ALL) }

    // ✅ FIREBASE INTEGRATION
    val courseRepository = remember { CourseRepository() }
    val enrolledCourses by courseRepository.getUserEnrollments().collectAsState(initial = emptyList())
    val enrolledCourseIds = remember(enrolledCourses) {
        enrolledCourses.map { it.courseId }.toSet()
    }

    // ✅ Filter courses berdasarkan category
    val filteredCourses = when (selectedCategory) {
        CourseCategory.ALL -> CourseDatabase.getAllCourses()
        CourseCategory.REKSADANA -> CourseDatabase.getCoursesByCategory("Reksadana")
        CourseCategory.SAHAM -> CourseDatabase.getCoursesByCategory("Saham")
        CourseCategory.CRYPTO -> CourseDatabase.getCoursesByCategory("Crypto")
    }

    // ✅ COLUMN LAYOUT untuk flexible scrolling
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF3759B3))
    ) {
        // ======= HEADER (FIXED) =======
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, start = 24.dp, end = 24.dp, bottom = 20.dp)
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
                text = "Popular Course",
                color = Color.White,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // ======= CONTENT BOX (FLEXIBLE) =======
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)  // ✅ Takes remaining space
                .background(
                    color = Color(0xFFF6F6F6),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ======= FILTER CHIPS =======
                Row(
                    horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 14.dp)
                        .padding(top = 13.dp)
                ) {
                    FilterChip(
                        label = {
                            Text(
                                text = "Semua",
                                color = if (selectedCategory == CourseCategory.ALL) Color.White else Color(0xFF3759B3),
                                textAlign = TextAlign.Center,
                                lineHeight = 1.83.em,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        shape = RoundedCornerShape(15.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF3759B3),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF3759B3)
                        ),
                        selected = selectedCategory == CourseCategory.ALL,
                        onClick = { selectedCategory = CourseCategory.ALL }
                    )

                    FilterChip(
                        label = {
                            Text(
                                text = "Reksadana",
                                color = if (selectedCategory == CourseCategory.REKSADANA) Color.White else Color(0xFF3759B3),
                                textAlign = TextAlign.Center,
                                lineHeight = 1.83.em,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        shape = RoundedCornerShape(15.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF3759B3),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF3759B3)
                        ),
                        selected = selectedCategory == CourseCategory.REKSADANA,
                        onClick = { selectedCategory = CourseCategory.REKSADANA }
                    )

                    FilterChip(
                        label = {
                            Text(
                                text = "Saham",
                                color = if (selectedCategory == CourseCategory.SAHAM) Color.White else Color(0xFF3759B3),
                                textAlign = TextAlign.Center,
                                lineHeight = 1.83.em,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        shape = RoundedCornerShape(15.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF3759B3),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF3759B3)
                        ),
                        selected = selectedCategory == CourseCategory.SAHAM,
                        onClick = { selectedCategory = CourseCategory.SAHAM }
                    )

                    FilterChip(
                        label = {
                            Text(
                                text = "Crypto",
                                color = if (selectedCategory == CourseCategory.CRYPTO) Color.White else Color(0xFF3759B3),
                                textAlign = TextAlign.Center,
                                lineHeight = 1.83.em,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        },
                        shape = RoundedCornerShape(15.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF3759B3),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF3759B3)
                        ),
                        selected = selectedCategory == CourseCategory.CRYPTO,
                        onClick = { selectedCategory = CourseCategory.CRYPTO }
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // ======= COURSE GRID (SCROLLABLE) =======
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()  // ✅ Fill all available space
                        .padding(horizontal = 29.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),  // ✅ Space for bottom nav
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = filteredCourses,
                        key = { it.id }
                    ) { course ->
                        // ✅ Check if course is enrolled
                        val isEnrolled = enrolledCourseIds.contains(course.id)

                        CourseCard(
                            course = course,
                            isEnrolled = isEnrolled,
                            onCourseClick = { onCourseClick(course.id) },
                            courseRepository = courseRepository,
                            modifier = Modifier.height(220.dp)  // ✅ Increased height for button
                        )
                    }
                }
            }
        }
    }
}

// ✅ COURSE CARD WITH ENROLL BUTTON
@Composable
fun CourseCard(
    course: CourseData,
    isEnrolled: Boolean,
    onCourseClick: () -> Unit,
    courseRepository: CourseRepository,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isEnrolling by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = Color.White)
            .padding(all = 10.dp)
    ) {
        // ======= TOP SECTION (Clickable) =======
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onCourseClick)
        ) {
            // ======= IMAGE =======
            Image(
                painter = painterResource(id = course.imageLargeRes),
                contentDescription = course.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(height = 106.dp)
                    .clip(shape = RoundedCornerShape(8.dp))
            )

            // ======= TITLE =======
            Text(
                text = course.title,
                color = Color.Black,
                lineHeight = 1.83.em,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            // ======= PRICE & RATING ROW =======
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = course.price,
                    color = Color.Black.copy(alpha = 0.5f),
                    lineHeight = 2.2.em,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(3.dp))
                        .background(color = Color(0xFF3759B3))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.star),
                        contentDescription = "Star Rating",
                        alpha = 0.8f,
                        modifier = Modifier.requiredSize(10.dp)
                    )

                    Text(
                        text = course.rating,
                        color = Color.White,
                        lineHeight = 2.75.em,
                        style = TextStyle(fontSize = 8.sp)
                    )
                }
            }
        }

        // ======= ENROLL BUTTON (Bottom) =======
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(
                    when {
                        isEnrolled -> Color(0xFF4CAF50)  // Green for enrolled
                        isEnrolling -> Color.Gray
                        else -> Color(0xFF3759B3)  // Blue for enroll
                    }
                )
                .clickable(enabled = !isEnrolling && !isEnrolled) {
                    if (!isEnrolled) {
                        // ✅ ENROLL LOGIC
                        isEnrolling = true

                        scope.launch {
                            Log.d("POPULAR_COURSE", "========== ENROLLING COURSE ==========")
                            Log.d("POPULAR_COURSE", "Course ID: ${course.id}")
                            Log.d("POPULAR_COURSE", "Course Title: ${course.title}")

                            val result = courseRepository.enrollCourse(course.id)

                            result.onSuccess {
                                Log.d("POPULAR_COURSE", "✓ Successfully enrolled in course ${course.id}")
                                Log.d("POPULAR_COURSE", "====================================")
                                isEnrolling = false

                            }.onFailure { error ->
                                Log.e("POPULAR_COURSE", "✗ Enrollment failed: ${error.message}")
                                Log.e("POPULAR_COURSE", "====================================")
                                isEnrolling = false
                            }
                        }
                    }
                }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when {
                    isEnrolled -> "Enrolled ✓"
                    isEnrolling -> "Enrolling..."
                    else -> "Enroll"
                },
                color = Color.White,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}