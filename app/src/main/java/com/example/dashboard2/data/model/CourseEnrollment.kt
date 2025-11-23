package com.example.dashboard2.data.model

import com.google.firebase.firestore.PropertyName

/**
 * ✅ ULTIMATE FIX: CourseEnrollment with @get:PropertyName
 *
 * CRITICAL: Untuk Kotlin data class + Firestore, gunakan @get:PropertyName
 * bukan hanya @PropertyName untuk memastikan getter/setter benar
 */
data class CourseEnrollment(
    @get:PropertyName("userId")
    @set:PropertyName("userId")
    var userId: String = "",

    @get:PropertyName("courseId")
    @set:PropertyName("courseId")
    var courseId: Int = 0,

    @get:PropertyName("progress")
    @set:PropertyName("progress")
    var progress: Int = 0,

    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,  // ✅ CRITICAL: @get and @set annotations

    @get:PropertyName("watchedLessonIds")
    @set:PropertyName("watchedLessonIds")
    var watchedLessonIds: List<Int> = emptyList(),

    @get:PropertyName("enrolledAt")
    @set:PropertyName("enrolledAt")
    var enrolledAt: Long = System.currentTimeMillis(),

    @get:PropertyName("completedAt")
    @set:PropertyName("completedAt")
    var completedAt: Long? = null,

    @get:PropertyName("lastAccessedAt")
    @set:PropertyName("lastAccessedAt")
    var lastAccessedAt: Long = System.currentTimeMillis()
) {
    // ✅ CRITICAL: No-arg constructor MUST exist for Firestore
    constructor() : this(
        userId = "",
        courseId = 0,
        progress = 0,
        isCompleted = false,
        watchedLessonIds = emptyList(),
        enrolledAt = System.currentTimeMillis(),
        completedAt = null,
        lastAccessedAt = System.currentTimeMillis()
    )

    fun getDocumentId(): String = "${userId}_$courseId"
}

data class CourseStatistics(
    val activeCourses: Int = 0,
    val completedCourses: Int = 0,
    val averageProgress: Int = 0
)