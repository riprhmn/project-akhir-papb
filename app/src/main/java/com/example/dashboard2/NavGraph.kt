package com.example.dashboard2

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.dashboard2.data.model.EventDatabase
import com.example.dashboard2.data.model.EventItem
import com.example.dashboard2.data.model.EventRegistration
import com.example.dashboard2.data.repository.CourseRepository
import com.example.dashboard2.data.repository.EventRepository
import kotlinx.coroutines.launch

const val PERFORMANCE_ROUTE = "performance"
const val MY_EVENT_ROUTE = "my_event"
const val QR_SCANNER_ROUTE = "qr_scanner"
const val SET_LOCATION_ROUTE = "set_location"
const val LOCATION_SUCCESS_ROUTE = "location_success"

// ✅ Sealed class untuk type-safe navigation
sealed class Screen(val route: String) {
    object Opening : Screen("opening")
    object Landing : Screen("landing")
    object SignUp : Screen("sign_up")
    object SignIn : Screen("sign_in")
    object Dashboard : Screen("dashboard")
    object NewsInformation : Screen("news_information")

    object MyEvent : Screen(MY_EVENT_ROUTE)

    // ✅ Event Screens
    object Event : Screen("event")
    object AllEvents : Screen("all_events")  // ✅ Moved up here
    object EventDetail : Screen("event_detail/{eventId}") {
        const val ARG_EVENT_ID = "eventId"
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }
    object EventRegistration : Screen("event_registration")
    object EventSuccess : Screen("event_success")
    object EventTicket : Screen("event_ticket")

    object Forum : Screen("forum")
    object ForumAddPost : Screen("forum_add_post")

    data class ForumDetail(val postId: String) : Screen("forum_detail/{postId}") {
        companion object {
            const val route = "forum_detail/{postId}"
            fun createRoute(postId: String) = "forum_detail/$postId"
        }
    }
    object Profile : Screen("profile")

    object EditProfile : Screen("edit_profile")
    object Bot : Screen("bot")
    object MyCourse : Screen("my_course")
    object PopularCourse : Screen("popular_course")

    object CourseDetail : Screen("course_detail/{courseId}") {
        const val ARG_COURSE_ID = "courseId"
        fun createRoute(courseId: Int) = "course_detail/$courseId"
    }

    object NewsDetail : Screen("news_detail/{newsId}") {
        const val ARG_NEWS_ID = "newsId"
        fun createRoute(newsId: Int) = "news_detail/$newsId"
    }

    object CoursePreview : Screen("course_preview/{courseId}") {
        const val ARG_COURSE_ID = "courseId"
        fun createRoute(courseId: Int) = "course_preview/$courseId"
    }

    object Quiz : Screen("quiz/{courseId}") {
        const val ARG_COURSE_ID = "courseId"
        fun createRoute(courseId: Int) = "quiz/$courseId"
    }

    object QuizResult : Screen("quiz_result/{courseId}/{score}/{totalQuestions}/{correctAnswers}/{courseName}") {
        const val ARG_COURSE_ID = "courseId"
        const val ARG_SCORE = "score"
        const val ARG_TOTAL_QUESTIONS = "totalQuestions"
        const val ARG_CORRECT_ANSWERS = "correctAnswers"
        const val ARG_COURSE_NAME = "courseName"

        fun createRoute(
            courseId: Int,
            score: Int,
            totalQuestions: Int,
            correctAnswers: Int,
            courseName: String
        ) = "quiz_result/$courseId/$score/$totalQuestions/$correctAnswers/$courseName"
    }

    object QuizReview : Screen("quiz_review/{courseId}/{score}/{totalQuestions}/{correctAnswers}/{courseName}") {
        const val ARG_COURSE_ID = "courseId"
        const val ARG_SCORE = "score"
        const val ARG_TOTAL_QUESTIONS = "totalQuestions"
        const val ARG_CORRECT_ANSWERS = "correctAnswers"
        const val ARG_COURSE_NAME = "courseName"

        fun createRoute(
            courseId: Int,
            score: Int,
            totalQuestions: Int,
            correctAnswers: Int,
            courseName: String
        ) = "quiz_review/$courseId/$score/$totalQuestions/$correctAnswers/$courseName"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val progressManager = rememberCourseProgressManager()
    val courseRepository = remember { CourseRepository() }
    val scope = rememberCoroutineScope()

    val enrollments by courseRepository.getUserEnrollments()
        .collectAsState(initial = emptyList())

    LaunchedEffect(enrollments) {
        if (enrollments.isNotEmpty()) {
            progressManager.syncWithFirestore(enrollments)
        }
    }

    val forumViewModel = remember { ForumViewModel() }

    NavHost(
        navController = navController,
        startDestination = Screen.Opening.route
    ) {
        // ================= AUTH SCREENS =================

        composable(Screen.Opening.route) {
            OpeningScreen(
                onScreenClick = {
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(Screen.Opening.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Landing.route) {
            LandingPage(
                onSignInClick = {
                    navController.navigate(Screen.SignIn.route)
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Landing.route) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onForgotPassword = {
                    // TODO: Navigate to Forgot Password screen
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Landing.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route)
                }
            )
        }

        // ================= MAIN SCREENS =================

        composable(Screen.Dashboard.route) {
            Dashboard2(
                onNewsViewAllClick = {
                    navController.navigate(Screen.NewsInformation.route)
                },
                onCourseClick = { courseId ->
                    navController.navigate(Screen.CourseDetail.createRoute(courseId))
                },
                onPopularCourseClick = { courseId ->
                    navController.navigate(Screen.CoursePreview.createRoute(courseId))
                },
                onNavigateToEvent = {
                    navController.navigate(Screen.Event.route)
                },
                onNavigateToForum = {
                    navController.navigate(Screen.Forum.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToBot = {
                    navController.navigate(Screen.Bot.route)
                },
                onNavigateToMyCourse = {
                    navController.navigate(Screen.MyCourse.route)
                },
                onPopularCourseViewAllClick = {
                    navController.navigate(Screen.PopularCourse.route)
                }
            )
        }

        composable(Screen.MyCourse.route) {
            MyCourseScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onCourseClick = { courseId ->
                    navController.navigate(Screen.CourseDetail.createRoute(courseId))
                }
            )
        }

        // ================= EVENT SCREENS =================

        composable(Screen.Event.route) {
            EventPage(
                onNavigateToDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onNavigateToAllEvents = {
                    navController.navigate(Screen.AllEvents.route)
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToForum = {
                    navController.navigate(Screen.Forum.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToBot = {
                    navController.navigate(Screen.Bot.route)
                }
            )
        }

        // ✅ All Events Screen (CORRECTED - removed duplicate)
        composable(Screen.AllEvents.route) {
            AllEventsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { eventId ->
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                }
            )
        }

        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(
                navArgument(Screen.EventDetail.ARG_EVENT_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString(Screen.EventDetail.ARG_EVENT_ID) ?: ""
            val eventRepository = remember { EventRepository() }
            var event by remember { mutableStateOf<EventItem?>(null) }

            LaunchedEffect(eventId) {
                event = eventRepository.getEventById(eventId)
            }

            EventDetailScreen(
                event = event,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToRegistration = { selectedEvent ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("selected_event", selectedEvent)
                    navController.navigate(Screen.EventRegistration.route)
                }
            )
        }

        composable(Screen.EventRegistration.route) {
            val event = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<EventItem>("selected_event")

            AndroidCompact28(
                event = event,
                onNavigateBack = {
                    navController.navigateUp()
                },
                onNavigateToSuccess = {
                    navController.navigate(Screen.EventSuccess.route) {
                        popUpTo(Screen.EventRegistration.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EventSuccess.route) {
            AndroidCompact29(
                onNavigateBackToList = {
                    navController.navigate(Screen.Event.route) {
                        popUpTo(Screen.Event.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = MY_EVENT_ROUTE) {
            MyEventScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onNavigateToDetail = { registration ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("ticket_registration", registration)
                    navController.navigate(Screen.EventTicket.route)
                }
            )
        }

        composable(Screen.EventTicket.route) {
            val registration = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<EventRegistration>("ticket_registration")

            if (registration != null) {
                DetailTicketScreen(
                    eventRegistration = registration,
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Event data not found", color = Color.Gray)
                        Button(onClick = { navController.navigateUp() }) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }

        // ✅ REMOVED DUPLICATE - this was causing the issue!
        // composable(Screen.AllEvents.route) {
        //     EventPage(
        //         onNavigateToHome = { navController.navigateUp() }
        //     )
        // }

        // ================= FORUM SCREENS =================

        composable(Screen.Forum.route) {
            ForumScreen(
                navController = navController,
                viewModel = forumViewModel
            )
        }

        composable(Screen.ForumAddPost.route) {
            AddPostScreen(
                navController = navController,
                onBackClick = {
                    navController.navigateUp()
                },
                viewModel = forumViewModel
            )
        }

        composable(
            route = Screen.ForumDetail.route,
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            ForumDetailScreen(
                postId = postId,
                navController = navController
            )
        }

        // ================= PROFILE SCREENS =================

        composable(Screen.Profile.route) {
            ProfilePage(
                onEditProfileClick = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToPerformance = {
                    navController.navigate(PERFORMANCE_ROUTE)
                },
                onNavigateToMyEvents = {
                    navController.navigate(MY_EVENT_ROUTE)
                },
                onNavigateToSetLocation = {
                    navController.navigate(SET_LOCATION_ROUTE)
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                },
                onNavigateToEvent = {
                    navController.navigate(Screen.Event.route)
                },
                onNavigateToForum = {
                    navController.navigate(Screen.Forum.route)
                },
                onNavigateToBot = {
                    navController.navigate(Screen.Bot.route)
                },
                onNavigateToScanQR = {
                    navController.navigate(QR_SCANNER_ROUTE)
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfilePage(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onSaveComplete = {
                    navController.navigateUp()
                    android.widget.Toast.makeText(
                        navController.context,
                        "Profil berhasil diupdate!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        composable(route = PERFORMANCE_ROUTE) {
            PerformancePage(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(route = QR_SCANNER_ROUTE) {
            QRScannerScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onScanSuccess = { qrContent ->
                    android.widget.Toast.makeText(
                        navController.context,
                        "Scan successful!",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        composable(route = SET_LOCATION_ROUTE) {
            SetLocationScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onSaveSuccess = {
                    navController.navigate(LOCATION_SUCCESS_ROUTE) {
                        popUpTo(SET_LOCATION_ROUTE) { inclusive = true }
                    }
                }
            )
        }

        composable(route = LOCATION_SUCCESS_ROUTE) {
            PopUpLocationScreen(
                onBackToProfile = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Profile.route) { inclusive = true }
                    }
                }
            )
        }

        // ================= BOT SCREEN =================

        composable(Screen.Bot.route) {
            ChatScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // ================= COURSE SCREENS =================

        composable(
            route = Screen.CourseDetail.route,
            arguments = listOf(
                navArgument(Screen.CourseDetail.ARG_COURSE_ID) {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt(Screen.CourseDetail.ARG_COURSE_ID) ?: 1
            val context = LocalContext.current

            LaunchedEffect(courseId) {
                progressManager.loadProgressFromFirestore(courseId)
            }

            AndroidCompact10(
                courseId = courseId,
                progressManager = progressManager,
                onBackClick = {
                    navController.navigateUp()
                },
                onLessonClick = { lesson ->
                    progressManager.markLessonWatched(courseId, lesson)
                    openYoutubeVideo(context, lesson.videoUrl)
                },
                onQuizClick = { courseId ->
                    navController.navigate(Screen.Quiz.createRoute(courseId))
                }
            )
        }

        composable(
            route = Screen.CoursePreview.route,
            arguments = listOf(
                navArgument(Screen.CoursePreview.ARG_COURSE_ID) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt(Screen.CoursePreview.ARG_COURSE_ID) ?: 0
            val courseData = CourseDatabase.getCourseById(courseId)

            if (courseData != null) {
                val coursePreview = CoursePreview(
                    id = courseData.id,
                    title = courseData.title,
                    imageRes = courseData.imageRes,
                    price = courseData.price,
                    rating = courseData.rating,
                    lessons = courseData.lessons.map { lesson ->
                        PreviewLesson(lesson.title, lesson.duration)
                    }
                )

                AndroidCompact16(
                    coursePreview = coursePreview,
                    onBackClick = {
                        navController.navigateUp()
                    },
                    onEnrollClick = { course ->
                        navController.navigateUp()
                    }
                )
            }
        }

        // ================= QUIZ SCREENS =================

        composable(
            route = Screen.Quiz.route,
            arguments = listOf(
                navArgument(Screen.Quiz.ARG_COURSE_ID) {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt(Screen.Quiz.ARG_COURSE_ID) ?: 1
            val courseData = CourseDatabase.getCourseById(courseId)

            QuizScreen(
                courseId = courseId,
                onBackClick = {
                    navController.navigateUp()
                },
                onQuizComplete = { score, totalQuestions, correctAnswers ->
                    val courseName = courseData?.title ?: "Course"
                    val route = Screen.QuizResult.createRoute(
                        courseId = courseId,
                        score = score,
                        totalQuestions = totalQuestions,
                        correctAnswers = correctAnswers,
                        courseName = courseName.replace("/", "-").replace(" ", "_")
                    )

                    navController.navigate(route) {
                        popUpTo(Screen.Quiz.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.QuizResult.route,
            arguments = listOf(
                navArgument(Screen.QuizResult.ARG_COURSE_ID) {
                    type = NavType.IntType
                    defaultValue = 1
                },
                navArgument(Screen.QuizResult.ARG_SCORE) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(Screen.QuizResult.ARG_TOTAL_QUESTIONS) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(Screen.QuizResult.ARG_CORRECT_ANSWERS) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(Screen.QuizResult.ARG_COURSE_NAME) {
                    type = NavType.StringType
                    defaultValue = "Course"
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt(Screen.QuizResult.ARG_COURSE_ID) ?: 1
            val score = backStackEntry.arguments?.getInt(Screen.QuizResult.ARG_SCORE) ?: 0
            val totalQuestions = backStackEntry.arguments?.getInt(Screen.QuizResult.ARG_TOTAL_QUESTIONS) ?: 0
            val correctAnswers = backStackEntry.arguments?.getInt(Screen.QuizResult.ARG_CORRECT_ANSWERS) ?: 0
            val courseName = backStackEntry.arguments?.getString(Screen.QuizResult.ARG_COURSE_NAME) ?: "Course"

            val userAnswers = emptyList<UserAnswer>()

            QuizResultScreen(
                score = correctAnswers,
                total = totalQuestions,
                userAnswers = userAnswers,
                onReviewAnswer = {
                    navController.navigate(
                        Screen.QuizReview.createRoute(
                            courseId = courseId,
                            score = score,
                            totalQuestions = totalQuestions,
                            correctAnswers = correctAnswers,
                            courseName = courseName
                        )
                    )
                },
                onBackClick = {
                    navController.navigate(Screen.CourseDetail.createRoute(courseId)) {
                        popUpTo(Screen.CourseDetail.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.QuizReview.route,
            arguments = listOf(
                navArgument(Screen.QuizReview.ARG_COURSE_ID) {
                    type = NavType.IntType
                    defaultValue = 1
                },
                navArgument(Screen.QuizReview.ARG_SCORE) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(Screen.QuizReview.ARG_TOTAL_QUESTIONS) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(Screen.QuizReview.ARG_CORRECT_ANSWERS) {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument(Screen.QuizReview.ARG_COURSE_NAME) {
                    type = NavType.StringType
                    defaultValue = "Course"
                }
            )
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getInt(Screen.QuizReview.ARG_COURSE_ID) ?: 1
            val score = backStackEntry.arguments?.getInt(Screen.QuizReview.ARG_SCORE) ?: 0
            val totalQuestions = backStackEntry.arguments?.getInt(Screen.QuizReview.ARG_TOTAL_QUESTIONS) ?: 0
            val correctAnswers = backStackEntry.arguments?.getInt(Screen.QuizReview.ARG_CORRECT_ANSWERS) ?: 0
            val courseName = backStackEntry.arguments?.getString(Screen.QuizReview.ARG_COURSE_NAME) ?: "Course"

            ResultQuizScreen(
                score = score,
                totalQuestions = totalQuestions,
                correctAnswers = correctAnswers,
                courseName = courseName.replace("-", "/").replace("_", " "),
                courseId = courseId,
                onRetakeQuiz = {
                    navController.navigate(Screen.Quiz.createRoute(courseId)) {
                        popUpTo(Screen.CourseDetail.route) { inclusive = false }
                    }
                },
                onBackToCourse = {
                    navController.navigate(Screen.CourseDetail.createRoute(courseId)) {
                        popUpTo(Screen.CourseDetail.route) { inclusive = true }
                    }
                }
            )
        }

        // ================= NEWS SCREENS =================

        composable(Screen.NewsInformation.route) {
            NewsInformationScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onNewsClick = { newsId ->
                    navController.navigate(Screen.NewsDetail.createRoute(newsId))
                }
            )
        }

        composable(
            route = Screen.NewsDetail.route,
            arguments = listOf(
                navArgument(Screen.NewsDetail.ARG_NEWS_ID) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getInt(Screen.NewsDetail.ARG_NEWS_ID) ?: 0
            NewsDetailScreen(
                newsId = newsId,
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        // ================= POPULAR COURSE SCREEN =================

        composable(Screen.PopularCourse.route) {
            PopularCourseScreen(
                onBackClick = {
                    navController.navigateUp()
                },
                onCourseClick = { courseId ->
                    navController.navigate(
                        Screen.CourseDetail.createRoute(courseId)
                    )
                }
            )
        }
    }
}

// ✅ Extension functions
fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

fun NavHostController.navigateAndClearBackStack(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}