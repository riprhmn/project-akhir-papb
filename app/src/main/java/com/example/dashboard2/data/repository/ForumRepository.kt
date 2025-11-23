package com.example.dashboard2.data.repository

import android.util.Log
import com.example.dashboard2.ForumComment
import com.example.dashboard2.ForumPost
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ForumRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val postsCollection = firestore.collection("forum_posts")

    // ✅ Add new post to Firestore
    suspend fun addPost(
        content: String,
        authorName: String,
        authorInstitution: String,
        authorPhotoUrl: String
    ): Result<String> {
        return try {
            val postData = hashMapOf(
                "authorName" to authorName,
                "authorInstitution" to authorInstitution,
                "authorPhotoUrl" to authorPhotoUrl,
                "content" to content,
                "timestamp" to System.currentTimeMillis(),
                "likes" to 0,
                "comments" to 0
            )

            val docRef = postsCollection.add(postData).await()
            Log.d("ForumRepository", "Post added with ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("ForumRepository", "Error adding post", e)
            Result.failure(e)
        }
    }

    // ✅ Get all posts in real-time
    fun getAllPosts(): Flow<List<ForumPost>> = callbackFlow {
        val listener = postsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ForumRepository", "Error listening to posts", error)
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull { doc ->
                        try {
                            ForumPost(
                                id = doc.id,
                                authorName = doc.getString("authorName") ?: "Anonymous",
                                authorInstitution = doc.getString("authorInstitution") ?: "",
                                authorPhotoUrl = doc.getString("authorPhotoUrl") ?: "",
                                content = doc.getString("content") ?: "",
                                timestamp = formatTimestamp(doc.getLong("timestamp") ?: 0L),
                                likes = doc.getLong("likes")?.toInt() ?: 0,
                                comments = doc.getLong("comments")?.toInt() ?: 0
                            )
                        } catch (e: Exception) {
                            Log.e("ForumRepository", "Error parsing post", e)
                            null
                        }
                    }
                    Log.d("ForumRepository", "Loaded ${posts.size} posts")
                    trySend(posts)
                }
            }

        awaitClose { listener.remove() }
    }

    // ✅ Get single post by ID
    suspend fun getPostById(postId: String): Result<ForumPost?> {
        return try {
            val doc = postsCollection.document(postId).get().await()
            if (doc.exists()) {
                val post = ForumPost(
                    id = doc.id,
                    authorName = doc.getString("authorName") ?: "Anonymous",
                    authorInstitution = doc.getString("authorInstitution") ?: "",
                    authorPhotoUrl = doc.getString("authorPhotoUrl") ?: "",
                    content = doc.getString("content") ?: "",
                    timestamp = formatTimestamp(doc.getLong("timestamp") ?: 0L),
                    likes = doc.getLong("likes")?.toInt() ?: 0,
                    comments = doc.getLong("comments")?.toInt() ?: 0
                )
                Result.success(post)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e("ForumRepository", "Error getting post", e)
            Result.failure(e)
        }
    }

    // ✅ Add comment to a post
    suspend fun addComment(
        postId: String,
        content: String,
        authorName: String,
        authorInstitution: String,
        authorPhotoUrl: String
    ): Result<String> {
        return try {
            val commentData = hashMapOf(
                "authorName" to authorName,
                "authorInstitution" to authorInstitution,
                "authorPhotoUrl" to authorPhotoUrl,
                "content" to content,
                "timestamp" to System.currentTimeMillis()
            )

            // Add comment to subcollection
            val commentRef = postsCollection
                .document(postId)
                .collection("comments")
                .add(commentData)
                .await()

            // Increment comment count
            val postRef = postsCollection.document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)
                val currentComments = snapshot.getLong("comments") ?: 0
                transaction.update(postRef, "comments", currentComments + 1)
            }.await()

            Log.d("ForumRepository", "Comment added with ID: ${commentRef.id}")
            Result.success(commentRef.id)
        } catch (e: Exception) {
            Log.e("ForumRepository", "Error adding comment", e)
            Result.failure(e)
        }
    }

    // ✅ Get all comments for a post in real-time
    fun getComments(postId: String): Flow<List<ForumComment>> = callbackFlow {
        val listener = postsCollection
            .document(postId)
            .collection("comments")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ForumRepository", "Error listening to comments", error)
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull { doc ->
                        try {
                            ForumComment(
                                id = doc.id,
                                authorName = doc.getString("authorName") ?: "Anonymous",
                                authorInstitution = doc.getString("authorInstitution") ?: "",
                                authorPhotoUrl = doc.getString("authorPhotoUrl") ?: "",
                                content = doc.getString("content") ?: "",
                                timestamp = formatTimestamp(doc.getLong("timestamp") ?: 0L)
                            )
                        } catch (e: Exception) {
                            Log.e("ForumRepository", "Error parsing comment", e)
                            null
                        }
                    }
                    Log.d("ForumRepository", "Loaded ${comments.size} comments for post $postId")
                    trySend(comments)
                }
            }

        awaitClose { listener.remove() }
    }

    // ✅ Like a post
    suspend fun likePost(postId: String): Result<Unit> {
        return try {
            val docRef = postsCollection.document(postId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)
                val currentLikes = snapshot.getLong("likes") ?: 0
                transaction.update(docRef, "likes", currentLikes + 1)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ForumRepository", "Error liking post", e)
            Result.failure(e)
        }
    }

    // ✅ Format timestamp
    private fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hour${if (hours > 1) "s" else ""} ago"
            days < 7 -> "$days day${if (days > 1) "s" else ""} ago"
            else -> {
                val weeks = days / 7
                "$weeks week${if (weeks > 1) "s" else ""} ago"
            }
        }
    }
}