package com.example.dashboard2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.repository.AuthRepository
import com.example.dashboard2.data.repository.CourseRepository
import kotlinx.coroutines.launch

object AppColors {
    val color_Foundation_Blue_Normal = Color(0xFF3759B3)
    val color_Foundation_Blue_Normal_hover = Color(0xFF2A4A9A)
}

@Composable
fun Dashboard2(
    modifier: Modifier = Modifier,
    onNewsViewAllClick: () -> Unit = {},
    onCourseClick: (Int) -> Unit = {},
    onPopularCourseClick: (Int) -> Unit = {},
    onNavigateToEvent: () -> Unit = {},
    onNavigateToForum: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToBot: () -> Unit = {},
    onNavigateToMyCourse: () -> Unit = {},
    onPopularCourseViewAllClick: () -> Unit = {},
) {
    // ✅ FIREBASE AUTH - Get current user
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("User") }

    // ✅ COURSE REPOSITORY - Get enrollments
    val courseRepository = remember { CourseRepository() }
    val enrollments by courseRepository.getUserEnrollments()
        .collectAsState(initial = emptyList())

    // ✅ Load user name
    LaunchedEffect(Unit) {
        scope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                userName = currentUser.name
            }
        }
    }

    var selectedCategory by remember { mutableStateOf("Semua") }
    val scrollState = rememberScrollState()

    // ✅ Ambil max 2 courses terbaru untuk ditampilkan di dashboard
    val displayedCourses = remember(enrollments) {
        enrollments
            .sortedByDescending { it.lastAccessedAt }
            .take(2)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = AppColors.color_Foundation_Blue_Normal)
    ) {
        // ================= SCROLLABLE CONTENT =================
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // ================= HEADER (Blue Background) =================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AppColors.color_Foundation_Blue_Normal)
                    .padding(horizontal = 32.dp)
                    .padding(top = 56.dp, bottom = 24.dp)
            ) {
                Text(
                    text = "Hi, $userName",
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Your learning adventure begins now!",
                    color = Color.White,
                    textAlign = TextAlign.Start,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // ================= CONTENT AREA (Gray Background) =================
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFF6F6F6))
                    .padding(bottom = 100.dp)
            ) {
                // ================= MY COURSE SECTION =================
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 24.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "My Course",
                            color = Color.Black,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "View all",
                            color = AppColors.color_Foundation_Blue_Normal,
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.clickable {
                                onNavigateToMyCourse() // ✅ Navigate ke MyCourse screen
                            }
                        )
                    }

                    // ✅ ENROLLED COURSES - Max 2, dari Firestore
                    if (displayedCourses.isEmpty()) {
                        // Show placeholder jika belum ada course
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No enrolled courses yet",
                                color = Color.Gray,
                                style = TextStyle(fontSize = 14.sp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            displayedCourses.forEach { enrollment ->
                                EnrolledCourseCard(
                                    courseId = enrollment.courseId,
                                    progress = enrollment.progress,
                                    onCourseClick = { onCourseClick(enrollment.courseId) }
                                )
                            }
                        }
                    }
                }

                // ================= NEWS & INFORMATION SECTION =================
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "News & Information",
                            color = Color.Black,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "View all",
                            color = AppColors.color_Foundation_Blue_Normal,
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.clickable { onNewsViewAllClick() }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(149.dp)
                            .shadow(4.dp, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.news),
                            contentDescription = "News Background",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = 0.4f))
                        )

                        Column(modifier = Modifier.padding(20.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AppColors.color_Foundation_Blue_Normal.copy(alpha = 0.9f))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Reksadana",
                                    color = Color.White,
                                    style = TextStyle(fontSize = 10.sp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 30.dp)
                            ) {
                                Text(
                                    text = "FinSmart.id",
                                    color = Color.White,
                                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                )
                                Text(
                                    text = "6 hours ago",
                                    color = Color.White,
                                    style = TextStyle(fontSize = 13.sp)
                                )
                            }

                            Text(
                                text = "Reksadana Pasar Uang Makin Diminati: Aman untuk Pemula?",
                                color = Color.White,
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // ================= POPULAR COURSE SECTION =================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Popular Course",
                            color = Color.Black,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "View all",
                            color = AppColors.color_Foundation_Blue_Normal,
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.clickable {
                                onPopularCourseViewAllClick()
                            }
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        val categories = listOf("Semua", "Reksadana", "Saham", "Crypto")
                        categories.forEach { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                label = {
                                    Text(
                                        text = category,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AppColors.color_Foundation_Blue_Normal,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = tween(500)),
                        exit = fadeOut(animationSpec = tween(500))
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            when (selectedCategory) {
                                "Semua" -> {
                                    CourseCard(
                                        courseId = 2,
                                        title = "Belajar saham dari nol",
                                        imageRes = R.drawable.belajarsaham,
                                        onClick = { onPopularCourseClick(2) }
                                    )
                                    CourseCard(
                                        courseId = 3,
                                        title = "Reksadana pemula",
                                        imageRes = R.drawable.reksadanapemula,
                                        onClick = { onPopularCourseClick(3) }
                                    )
                                }

                                "Reksadana" -> {
                                    CourseCard(
                                        courseId = 3,
                                        title = "Reksadana pemula",
                                        imageRes = R.drawable.reksadanapemula,
                                        onClick = { onPopularCourseClick(3) }
                                    )
                                    CourseCard(
                                        courseId = 4,
                                        title = "Investasi Reksadana",
                                        imageRes = R.drawable.investasireksadana,
                                        onClick = { onPopularCourseClick(4) }
                                    )
                                }

                                "Saham" -> {
                                    CourseCard(
                                        courseId = 2,
                                        title = "Belajar saham dari nol",
                                        imageRes = R.drawable.belajarsaham,
                                        onClick = { onPopularCourseClick(2) }
                                    )
                                    CourseCard(
                                        courseId = 5,
                                        title = "Mengatur Gaji Gen Z",
                                        imageRes = R.drawable.mengaturgaji,
                                        onClick = { onPopularCourseClick(5) }
                                    )
                                }

                                "Crypto" -> {
                                    CourseCard(
                                        courseId = 6,
                                        title = "Belajar Crypto dari 0",
                                        imageRes = R.drawable.belajarcrypto,
                                        onClick = { onPopularCourseClick(6) }
                                    )
                                    CourseCard(
                                        courseId = 7,
                                        title = "Crypto Trading untuk Pemula",
                                        imageRes = R.drawable.cryptotrading,
                                        onClick = { onPopularCourseClick(7) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }



        // ================= BOTTOM NAVIGATION (FIXED) =================
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(80.dp)
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)))
                    .padding(horizontal = 20.dp)
            ) {
                BottomNavItemDashboard(
                    iconRes = R.drawable.ic_home,
                    label = "Home",
                    isSelected = true,
                    onClick = { }
                )

                BottomNavItemDashboard(
                    iconRes = R.drawable.ic_event,
                    label = "Event",
                    isSelected = false,
                    onClick = onNavigateToEvent
                )

                Spacer(modifier = Modifier.width(64.dp))

                BottomNavItemDashboard(
                    iconRes = R.drawable.ic_forum,
                    label = "Forum",
                    isSelected = false,
                    onClick = onNavigateToForum
                )

                BottomNavItemDashboard(
                    iconRes = R.drawable.ic_profile,
                    label = "Profile",
                    isSelected = false,
                    onClick = onNavigateToProfile
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(AppColors.color_Foundation_Blue_Normal)
                    .border(BorderStroke(4.dp, Color.White), CircleShape)
                    .clickable { onNavigateToBot() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bot),
                    contentDescription = "AI Bot",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavItemDashboard(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = if (isSelected) AppColors.color_Foundation_Blue_Normal else Color(0xFF484C52),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) AppColors.color_Foundation_Blue_Normal else Color(0xFF484C52),
            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
fun EnrolledCourseCard(
    courseId: Int,
    progress: Int,  // ✅ Progress langsung dari Firestore
    onCourseClick: () -> Unit
) {
    val courseData = CourseDatabase.getCourseById(courseId)

    if (courseData == null) return

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .clickable { onCourseClick() }
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = courseData.imageRes),
            contentDescription = courseData.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .requiredWidth(100.dp)
                .requiredHeight(83.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = courseData.title,
                color = Color.Black,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = courseData.price,
                color = Color.Black.copy(alpha = 0.7f),
                style = TextStyle(fontSize = 12.sp)
            )

            Text(
                text = "$progress% complete",
                color = Color.Black,
                style = TextStyle(fontSize = 10.sp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(8.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress / 100f)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.linearGradient(
                                0f to Color(0xFFC1CCE7),
                                1f to AppColors.color_Foundation_Blue_Normal
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun CourseCard(
    courseId: Int,
    title: String,
    imageRes: Int,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .requiredWidth(156.dp)
            .shadow(4.dp, RoundedCornerShape(15.dp))
            .clip(RoundedCornerShape(15.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(75.dp)
                .clip(RoundedCornerShape(10.dp))
        )

        Text(
            text = title,
            color = Color.Black,
            style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            Text(
                text = "Free",
                color = Color.Black.copy(alpha = 0.7f),
                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(AppColors.color_Foundation_Blue_Normal)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.star),
                    contentDescription = "Star",
                    modifier = Modifier.requiredSize(10.dp)
                )
                Text(
                    text = "4.9",
                    color = Color.White,
                    style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}



@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun Dashboard2Preview() {
    Dashboard2(
        onNewsViewAllClick = {},
        onCourseClick = {},
        onPopularCourseClick = {}
    )
}