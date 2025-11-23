package com.example.dashboard2

import android.util.Log
import androidx.compose.runtime.*
import com.example.dashboard2.data.repository.CourseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Data class untuk menyimpan progress per course
 */
data class CourseProgress(
    val courseId: Int,
    val watchedLessonIds: List<Int> = emptyList(),
    val watchHistory: List<WatchHistoryEntry> = emptyList(),
    val progressPercentage: Int = 0
)

data class WatchHistoryEntry(
    val lessonId: Int,
    val lessonTitle: String,
    val lessonDuration: String,
    val watchedAt: Long
)

/**
 * ✅ SOLUTION 2: CourseProgressManager with Firestore watchedLessonIds sync
 */
class CourseProgressManager(
    private val courseRepository: CourseRepository,
    private val scope: CoroutineScope
) {
    companion object {
        private const val TAG = "PROGRESS_MANAGER"
    }

    // ✅ State map untuk cache
    private val progressCache: MutableMap<Int, MutableState<CourseProgress>> = mutableMapOf()

    /**
     * Get progress untuk course tertentu
     */
    fun getCourseProgress(courseId: Int): State<CourseProgress> {
        return progressCache.getOrPut(courseId) {
            mutableStateOf(CourseProgress(courseId = courseId))
        }
    }

    /**
     * Get watched lesson IDs
     */
    fun getWatchedLessonIds(courseId: Int): List<Int> {
        return getCourseProgress(courseId).value.watchedLessonIds
    }

    /**
     * Get watch history
     */
    fun getWatchHistory(courseId: Int): List<WatchHistoryEntry> {
        return getCourseProgress(courseId).value.watchHistory
    }

    /**
     * ✅ SOLUTION 2: Mark lesson watched & save watchedLessonIds to Firestore
     */
    fun markLessonWatched(courseId: Int, lesson: CourseLesson) {
        scope.launch {
            try {
                Log.d(TAG, "========== MARK LESSON WATCHED ==========")
                Log.d(TAG, "Course ID: $courseId")
                Log.d(TAG, "Lesson ID: ${lesson.id}")
                Log.d(TAG, "Lesson Title: ${lesson.title}")

                val currentProgress = getCourseProgress(courseId).value

                // Tambah ke watched lessons (cegah duplikasi)
                val updatedWatchedIds = if (lesson.id !in currentProgress.watchedLessonIds) {
                    currentProgress.watchedLessonIds + lesson.id
                } else {
                    currentProgress.watchedLessonIds
                }

                // Tambah ke history
                val historyEntry = WatchHistoryEntry(
                    lessonId = lesson.id,
                    lessonTitle = lesson.title,
                    lessonDuration = lesson.duration,
                    watchedAt = System.currentTimeMillis()
                )
                val updatedHistory = currentProgress.watchHistory + historyEntry

                // Calculate progress
                val courseData = CourseDatabase.getCourseById(courseId)
                val totalLessons = courseData?.lessons?.size ?: 0

                if (totalLessons == 0) {
                    Log.e(TAG, "✗ Course data not found or has no lessons")
                    return@launch
                }

                val newProgressPercentage =
                    ((updatedWatchedIds.size.toFloat() / totalLessons.toFloat()) * 100).toInt()

                Log.d(TAG, "Progress Calculation:")
                Log.d(TAG, "  - Watched: ${updatedWatchedIds.size} / $totalLessons lessons")
                Log.d(TAG, "  - Percentage: $newProgressPercentage%")
                Log.d(TAG, "  - Is 100%: ${newProgressPercentage >= 100}")

                // ✅ Update local cache (immediate UI update)
                val updatedProgress = CourseProgress(
                    courseId = courseId,
                    watchedLessonIds = updatedWatchedIds,
                    watchHistory = updatedHistory,
                    progressPercentage = newProgressPercentage
                )

                progressCache[courseId]?.value = updatedProgress
                Log.d(TAG, "✓ Local cache updated")

                // ✅ CRITICAL: Save to Firestore WITH watchedLessonIds
                Log.d(TAG, "Saving to Firestore...")
                val result = courseRepository.updateProgress(
                    courseId = courseId,
                    progress = newProgressPercentage,
                    watchedLessonIds = updatedWatchedIds  // ✅ PASS watched lessons
                )

                result.onSuccess {
                    Log.d(TAG, "✓✓✓ SUCCESS: Progress saved to Firestore")
                    Log.d(TAG, "  - Progress: $newProgressPercentage%")
                    Log.d(TAG, "  - isCompleted: ${newProgressPercentage >= 100}")
                    Log.d(TAG, "  - Watched Lessons: ${updatedWatchedIds.size}")

                    if (newProgressPercentage >= 100) {
                        Log.d(TAG, "🎉 COURSE COMPLETED! 🎉")
                    }

                }.onFailure { error ->
                    Log.e(TAG, "✗✗✗ FAILED: Could not save to Firestore")
                    Log.e(TAG, "Error: ${error.message}", error)
                }

                Log.d(TAG, "========== MARK LESSON COMPLETED ==========")

            } catch (e: Exception) {
                Log.e(TAG, "✗ Exception in markLessonWatched", e)
            }
        }
    }

    /**
     * Calculate progress percentage
     */
    fun getProgressPercentage(courseId: Int): Int {
        return getCourseProgress(courseId).value.progressPercentage
    }

    /**
     * ✅ SOLUTION 2: Load progress AND watchedLessonIds from Firestore
     */
    suspend fun loadProgressFromFirestore(courseId: Int) {
        try {
            Log.d(TAG, "========== LOADING FROM FIRESTORE ==========")
            Log.d(TAG, "Course ID: $courseId")

            val enrollment = courseRepository.getEnrollment(courseId)

            if (enrollment != null) {
                Log.d(TAG, "✓ Found enrollment:")
                Log.d(TAG, "  - Progress: ${enrollment.progress}%")
                Log.d(TAG, "  - isCompleted: ${enrollment.isCompleted}")
                Log.d(TAG, "  - Watched Lessons: ${enrollment.watchedLessonIds.size}")
                Log.d(TAG, "  - Watched Lesson IDs: ${enrollment.watchedLessonIds}")

                // ✅ Reconstruct watch history from watchedLessonIds
                val courseData = CourseDatabase.getCourseById(courseId)
                val watchHistory = if (courseData != null) {
                    enrollment.watchedLessonIds.mapNotNull { lessonId ->
                        val lesson = courseData.lessons.find { it.id == lessonId }
                        if (lesson != null) {
                            WatchHistoryEntry(
                                lessonId = lesson.id,
                                lessonTitle = lesson.title,
                                lessonDuration = lesson.duration,
                                watchedAt = enrollment.lastAccessedAt
                            )
                        } else null
                    }
                } else emptyList()

                // ✅ Update cache dengan data dari Firestore
                progressCache.getOrPut(courseId) {
                    mutableStateOf(CourseProgress(courseId = courseId))
                }.value = CourseProgress(
                    courseId = courseId,
                    watchedLessonIds = enrollment.watchedLessonIds,  // ✅ Load from Firestore
                    watchHistory = watchHistory,
                    progressPercentage = enrollment.progress
                )

                Log.d(TAG, "✓ Cache updated with Firestore data")
                Log.d(TAG, "========================================")

            } else {
                Log.d(TAG, "No enrollment found for course $courseId")
                Log.d(TAG, "========================================")
            }

        } catch (e: Exception) {
            Log.e(TAG, "✗ Error loading from Firestore", e)
            Log.e(TAG, "========================================")
        }
    }

    /**
     * ✅ Sync enrollments dengan cache
     */
    /**
     * ✅ Sync enrollments dengan cache (NULL-SAFE)
     */
    fun syncWithFirestore(enrollments: List<com.example.dashboard2.data.model.CourseEnrollment>) {
        Log.d(TAG, "========== SYNCING WITH FIRESTORE ==========")
        Log.d(TAG, "Total enrollments: ${enrollments.size}")

        enrollments.forEach { enrollment ->
            try {
                // ✅ NULL-SAFE: Handle missing courseData
                val courseData = CourseDatabase.getCourseById(enrollment.courseId)

                val watchHistory = if (courseData != null) {
                    enrollment.watchedLessonIds.mapNotNull { lessonId ->
                        val lesson = courseData.lessons.find { it.id == lessonId }
                        if (lesson != null) {
                            WatchHistoryEntry(
                                lessonId = lesson.id,
                                lessonTitle = lesson.title,
                                lessonDuration = lesson.duration,
                                watchedAt = enrollment.lastAccessedAt
                            )
                        } else {
                            Log.w(
                                TAG,
                                "Lesson $lessonId not found in course ${enrollment.courseId}"
                            )
                            null
                        }
                    }
                } else {
                    Log.w(TAG, "Course ${enrollment.courseId} not found in database")
                    emptyList()
                }

                progressCache.getOrPut(enrollment.courseId) {
                    mutableStateOf(CourseProgress(courseId = enrollment.courseId))
                }.value = CourseProgress(
                    courseId = enrollment.courseId,
                    watchedLessonIds = enrollment.watchedLessonIds,
                    watchHistory = watchHistory,
                    progressPercentage = enrollment.progress
                )

                Log.d(TAG, "Synced course ${enrollment.courseId}:")
                Log.d(TAG, "  - Progress: ${enrollment.progress}%")
                Log.d(TAG, "  - Watched: ${enrollment.watchedLessonIds.size} lessons")
                Log.d(TAG, "  - isCompleted: ${enrollment.isCompleted}")

            } catch (e: Exception) {
                Log.e(TAG, "Error syncing course ${enrollment.courseId}", e)
            }
        }

        Log.d(TAG, "✓ Synced ${enrollments.size} courses")
        Log.d(TAG, "========================================")
    }
}

/**
 * Composable helper untuk remember progress manager
 */
@Composable
fun rememberCourseProgressManager(): CourseProgressManager {
    val courseRepository = remember { CourseRepository() }
    val scope = rememberCoroutineScope()

    return remember {
        CourseProgressManager(courseRepository, scope)
    }
}