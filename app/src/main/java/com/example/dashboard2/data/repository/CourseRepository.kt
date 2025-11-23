package com.example.dashboard2.data.repository

import android.util.Log
import com.example.dashboard2.data.model.CourseEnrollment
import com.example.dashboard2.data.model.CourseStatistics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CourseRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val enrollmentsCollection = firestore.collection("enrollments")

    companion object {
        private const val TAG = "COURSE_REPO"
        private const val DEFAULT_COURSE_ID = 1
    }

    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // ✅ FIXED: enrollDefaultCourse dengan explicit field mapping
    suspend fun enrollDefaultCourse(): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))

            Log.d(TAG, "========== ENROLL DEFAULT COURSE ==========")
            Log.d(TAG, "User ID: $userId")

            val docId = "${userId}_$DEFAULT_COURSE_ID"
            val existingDoc = enrollmentsCollection.document(docId).get().await()

            if (existingDoc.exists()) {
                Log.d(TAG, "Default course already enrolled")
                return Result.success(Unit)
            }

            // ✅ CRITICAL: Explicit HashMap to ensure correct field names
            val enrollmentData = hashMapOf<String, Any>(
                "userId" to userId,
                "courseId" to DEFAULT_COURSE_ID,
                "progress" to 0,
                "isCompleted" to false,  // ✅ ONLY isCompleted, NO "completed"
                "watchedLessonIds" to emptyList<Int>(),
                "enrolledAt" to System.currentTimeMillis(),
                "lastAccessedAt" to System.currentTimeMillis()
            )

            Log.d(TAG, "Creating enrollment with fields: ${enrollmentData.keys}")

            enrollmentsCollection
                .document(docId)
                .set(enrollmentData)
                .await()

            Log.d(TAG, "✓ Default course enrolled with isCompleted field")
            Log.d(TAG, "==========================================")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "✗ Error enrolling default course", e)
            Result.failure(e)
        }
    }

    // ✅ FIXED: enrollCourse dengan explicit field mapping
    suspend fun enrollCourse(courseId: Int): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))

            Log.d(TAG, "========== ENROLL COURSE ==========")
            Log.d(TAG, "User ID: $userId")
            Log.d(TAG, "Course ID: $courseId")

            val docId = "${userId}_$courseId"

            val existingDoc = enrollmentsCollection.document(docId).get().await()
            if (existingDoc.exists()) {
                Log.d(TAG, "Course $courseId already enrolled")
                return Result.success(Unit)
            }

            // ✅ CRITICAL: Explicit HashMap to ensure correct field names
            val enrollmentData = hashMapOf<String, Any>(
                "userId" to userId,
                "courseId" to courseId,
                "progress" to 0,
                "isCompleted" to false,  // ✅ ONLY isCompleted, NO "completed"
                "watchedLessonIds" to emptyList<Int>(),
                "enrolledAt" to System.currentTimeMillis(),
                "lastAccessedAt" to System.currentTimeMillis()
            )

            Log.d(TAG, "Creating enrollment with fields: ${enrollmentData.keys}")

            enrollmentsCollection
                .document(docId)
                .set(enrollmentData)
                .await()

            Log.d(TAG, "✓ Course $courseId enrolled with isCompleted field")
            Log.d(TAG, "===================================")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "✗ Error enrolling course", e)
            Result.failure(e)
        }
    }

    // ✅ UPDATE PROGRESS
    suspend fun updateProgress(
        courseId: Int,
        progress: Int,
        watchedLessonIds: List<Int> = emptyList()
    ): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not logged in"))

            Log.d(TAG, "========== UPDATING PROGRESS ==========")
            Log.d(TAG, "Course ID: $courseId")
            Log.d(TAG, "New Progress: $progress%")
            Log.d(TAG, "Watched Lessons: ${watchedLessonIds.size}")

            val docId = "${userId}_$courseId"
            val normalizedProgress = progress.coerceIn(0, 100)
            val isCompleted = normalizedProgress >= 100

            val updates = hashMapOf<String, Any>(
                "progress" to normalizedProgress,
                "isCompleted" to isCompleted,  // ✅ ONLY isCompleted
                "watchedLessonIds" to watchedLessonIds,
                "lastAccessedAt" to System.currentTimeMillis()
            )

            if (isCompleted && normalizedProgress == 100) {
                updates["completedAt"] = System.currentTimeMillis()
                Log.d(TAG, "✓ Course marked as COMPLETED")
            }

            val docSnapshot = enrollmentsCollection.document(docId).get().await()
            if (!docSnapshot.exists()) {
                Log.e(TAG, "✗ Document not found: $docId")
                return Result.failure(Exception("Enrollment not found"))
            }

            enrollmentsCollection
                .document(docId)
                .update(updates)
                .await()

            val verifyDoc = enrollmentsCollection.document(docId).get().await()
            val verifyEnrollment = verifyDoc.toObject(CourseEnrollment::class.java)

            Log.d(TAG, "✓ Progress updated successfully")
            Log.d(TAG, "  - Progress: ${verifyEnrollment?.progress}%")
            Log.d(TAG, "  - isCompleted: ${verifyEnrollment?.isCompleted}")
            Log.d(TAG, "  - Watched Lessons: ${verifyEnrollment?.watchedLessonIds?.size}")

            if (isCompleted) {
                Log.d(TAG, "🎉 COURSE COMPLETED! 🎉")
            }

            Log.d(TAG, "======================================")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "✗ Error updating progress", e)
            Result.failure(e)
        }
    }

    // ✅ GET ALL ENROLLMENTS
    fun getUserEnrollments(): Flow<List<CourseEnrollment>> = callbackFlow {
        val userId = getCurrentUserId()

        if (userId == null) {
            Log.e(TAG, "Cannot get enrollments: User not logged in")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        Log.d(TAG, "Setting up real-time enrollment listener")

        val listener = enrollmentsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening to enrollments", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val enrollments = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val enrollment = doc.toObject(CourseEnrollment::class.java)
                        if (enrollment != null) {
                            Log.d(TAG, "Enrollment: courseId=${enrollment.courseId}, " +
                                    "progress=${enrollment.progress}%, " +
                                    "isCompleted=${enrollment.isCompleted}, " +
                                    "watchedLessons=${enrollment.watchedLessonIds.size}")
                        }
                        enrollment
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing enrollment: ${doc.id}", e)
                        null
                    }
                } ?: emptyList()

                Log.d(TAG, "========== ENROLLMENTS LOADED ==========")
                Log.d(TAG, "Total: ${enrollments.size}")
                Log.d(TAG, "Completed: ${enrollments.count { it.isCompleted }}")
                Log.d(TAG, "Ongoing: ${enrollments.count { !it.isCompleted }}")
                Log.d(TAG, "========================================")

                trySend(enrollments)
            }

        awaitClose {
            Log.d(TAG, "Removing enrollment listener")
            listener.remove()
        }
    }

    // ✅ GET SPECIFIC ENROLLMENT
    suspend fun getEnrollment(courseId: Int): CourseEnrollment? {
        return try {
            val userId = getCurrentUserId() ?: return null
            val docId = "${userId}_$courseId"

            val doc = enrollmentsCollection.document(docId).get().await()
            val enrollment = doc.toObject(CourseEnrollment::class.java)

            if (enrollment != null) {
                Log.d(TAG, "Got enrollment for course $courseId:")
                Log.d(TAG, "  - progress: ${enrollment.progress}%")
                Log.d(TAG, "  - isCompleted: ${enrollment.isCompleted}")
                Log.d(TAG, "  - watchedLessons: ${enrollment.watchedLessonIds.size}")
            }

            enrollment

        } catch (e: Exception) {
            Log.e(TAG, "Error getting enrollment", e)
            null
        }
    }

    // ✅ CALCULATE STATISTICS
    suspend fun getStatistics(): CourseStatistics {
        return try {
            val userId = getCurrentUserId() ?: return CourseStatistics()

            val snapshot = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val enrollments = snapshot.documents.mapNotNull {
                it.toObject(CourseEnrollment::class.java)
            }

            if (enrollments.isEmpty()) {
                return CourseStatistics()
            }

            val activeCourses = enrollments.size
            val completedCourses = enrollments.count { it.isCompleted }
            val averageProgress = enrollments.sumOf { it.progress } / activeCourses

            Log.d(TAG, "========== STATISTICS ==========")
            Log.d(TAG, "Active: $activeCourses")
            Log.d(TAG, "Completed: $completedCourses")
            Log.d(TAG, "Average: $averageProgress%")
            Log.d(TAG, "===============================")

            CourseStatistics(
                activeCourses = activeCourses,
                completedCourses = completedCourses,
                averageProgress = averageProgress
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error calculating statistics", e)
            CourseStatistics()
        }
    }

    // ✅ GET ONGOING COURSES
    suspend fun getOngoingCourses(): List<CourseEnrollment> {
        return try {
            val userId = getCurrentUserId() ?: return emptyList()

            val snapshot = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isCompleted", false)
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(CourseEnrollment::class.java)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting ongoing courses", e)
            emptyList()
        }
    }

    // ✅ GET COMPLETED COURSES
    suspend fun getCompletedCourses(): List<CourseEnrollment> {
        return try {
            val userId = getCurrentUserId() ?: return emptyList()

            val snapshot = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("isCompleted", true)
                .get()
                .await()

            snapshot.documents.mapNotNull {
                it.toObject(CourseEnrollment::class.java)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting completed courses", e)
            emptyList()
        }
    }

    // ✅ CHECK IF USER HAS ENROLLMENT
    suspend fun hasAnyEnrollment(): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false

            val snapshot = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .limit(1)
                .get()
                .await()

            !snapshot.isEmpty

        } catch (e: Exception) {
            Log.e(TAG, "Error checking enrollment", e)
            false
        }
    }

    // ✅ CLEANUP: Remove duplicate "completed" field
    suspend fun cleanupCompletedField(): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Not logged in"))

            Log.d(TAG, "========== CLEANUP DUPLICATE FIELD ==========")

            val snapshot = enrollmentsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            var cleanedCount = 0

            snapshot.documents.forEach { doc ->
                val data = doc.data

                if (data?.containsKey("completed") == true) {
                    Log.d(TAG, "Removing 'completed' field from ${doc.id}")

                    doc.reference.update(
                        mapOf("completed" to com.google.firebase.firestore.FieldValue.delete())
                    ).await()

                    cleanedCount++
                }
            }

            Log.d(TAG, "✓ Cleaned $cleanedCount documents")
            Log.d(TAG, "============================================")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up", e)
            Result.failure(e)
        }
    }
}