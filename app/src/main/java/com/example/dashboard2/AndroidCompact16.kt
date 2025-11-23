package com.example.dashboard2

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.repository.CourseRepository
import kotlinx.coroutines.launch

// ✅ Data class untuk course preview
data class CoursePreview(
    val id: Int,
    val title: String,
    val imageRes: Int,
    val price: String,
    val rating: String,
    val lessons: List<PreviewLesson>
)

data class PreviewLesson(
    val title: String,
    val duration: String
)

@Composable
fun AndroidCompact16(
    coursePreview: CoursePreview,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onEnrollClick: (CoursePreview) -> Unit = {}
) {
    // ✅ FIREBASE INTEGRATION
    val courseRepository = remember { CourseRepository() }
    val scope = rememberCoroutineScope()

    // ✅ State management
    var isEnrolling by remember { mutableStateOf(false) }
    var enrollmentError by remember { mutableStateOf<String?>(null) }
    var isEnrolled by remember { mutableStateOf(false) }
    var isCheckingEnrollment by remember { mutableStateOf(true) }

    // ✅ CHECK ENROLLMENT STATUS on screen load
    LaunchedEffect(coursePreview.id) {
        Log.d("COURSE_DETAIL", "========== CHECKING ENROLLMENT STATUS ==========")
        Log.d("COURSE_DETAIL", "Course ID: ${coursePreview.id}")

        val enrollment = courseRepository.getEnrollment(coursePreview.id)
        isEnrolled = enrollment != null
        isCheckingEnrollment = false

        Log.d("COURSE_DETAIL", "Enrollment Status: ${if (isEnrolled) "ENROLLED" else "NOT ENROLLED"}")
        Log.d("COURSE_DETAIL", "===============================================")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFF3759B3))
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
                .background(
                    color = Color(0xFFF6F6F6),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp)
            ) {
                // ======= COURSE HEADER =======
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    // Course Image
                    Image(
                        painter = painterResource(id = coursePreview.imageRes),
                        contentDescription = coursePreview.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Course Title
                    Text(
                        text = coursePreview.title,
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
                            text = coursePreview.price,
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
                                text = coursePreview.rating,
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                // ======= TAB BAR (Static - Only Lesson visible) =======
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Lesson",
                            color = AppColors.color_Foundation_Blue_Normal_hover,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .width(50.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFF3759B3))
                        )
                    }
                }

                // ======= LESSON LIST =======
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    coursePreview.lessons.forEach { lesson ->
                        PreviewLessonItem(lesson = lesson)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // ======= ENROLL/CONTINUE BUTTON (Fixed at bottom) =======
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // ✅ Show enrollment status indicator
                if (isEnrolled) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.star),
                            contentDescription = "Enrolled",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "You are enrolled in this course",
                            color = Color(0xFF4CAF50),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                // ✅ Main Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when {
                                isCheckingEnrollment -> Color.Gray
                                isEnrolling -> Color.Gray
                                isEnrolled -> Color(0xFF4CAF50)  // Green for continue
                                else -> AppColors.color_Foundation_Blue_Normal_hover  // Blue for enroll
                            }
                        )
                        .clickable(enabled = !isEnrolling && !isCheckingEnrollment) {
                            if (isEnrolled) {
                                // ✅ CONTINUE LEARNING - Just navigate back
                                Log.d("COURSE_DETAIL", "========== CONTINUE LEARNING ==========")
                                Log.d("COURSE_DETAIL", "Course ID: ${coursePreview.id}")
                                Log.d("COURSE_DETAIL", "Already enrolled, navigating to course...")
                                Log.d("COURSE_DETAIL", "======================================")
                                onEnrollClick(coursePreview)

                            } else {
                                // ✅ ENROLL LOGIC - Save to Firestore
                                isEnrolling = true
                                enrollmentError = null

                                scope.launch {
                                    Log.d("COURSE_DETAIL", "========== STARTING ENROLLMENT ==========")
                                    Log.d("COURSE_DETAIL", "Course ID: ${coursePreview.id}")
                                    Log.d("COURSE_DETAIL", "Course Title: ${coursePreview.title}")

                                    val result = courseRepository.enrollCourse(coursePreview.id)

                                    result
                                        .onSuccess {
                                            Log.d(
                                                "COURSE_DETAIL",
                                                "✓ Successfully enrolled in course ${coursePreview.id}"
                                            )
                                            Log.d(
                                                "COURSE_DETAIL",
                                                "========== ENROLLMENT COMPLETED =========="
                                            )

                                            // Update state
                                            isEnrolled = true
                                            isEnrolling = false

                                            // Navigate back to dashboard
                                            onEnrollClick(coursePreview)

                                        }.onFailure { error ->
                                            Log.e(
                                                "COURSE_DETAIL",
                                                "✗ Enrollment failed: ${error.message}"
                                            )
                                            Log.e("COURSE_DETAIL", "========== ENROLLMENT FAILED ==========")
                                            enrollmentError = error.message
                                            isEnrolling = false
                                        }
                                }
                            }
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            isCheckingEnrollment -> "Loading..."
                            isEnrolling -> "Enrolling..."
                            isEnrolled -> "Continue Learning"
                            else -> "Enroll Now"
                        },
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // ✅ Show error message if enrollment fails
                enrollmentError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PreviewLessonItem(
    lesson: PreviewLesson,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.weight(1f)
            ) {
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

            Icon(
                painter = painterResource(id = R.drawable.rightarrow),
                contentDescription = "Lesson preview",
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
private fun AndroidCompact16Preview() {
    val sampleCourse = CoursePreview(
        id = 1,
        title = "Belajar saham dari nol",
        imageRes = R.drawable.belajarsaham,
        price = "Free",
        rating = "4.9",
        lessons = listOf(
            PreviewLesson("Memahami Apa Itu Saham", "05.00 min"),
            PreviewLesson("Cara Membeli dan Menjual Saham", "05.00 min"),
            PreviewLesson("Analisis Fundamental Saham", "05.00 min")
        )
    )
    AndroidCompact16(coursePreview = sampleCourse)
}