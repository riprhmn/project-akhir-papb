package com.example.projectakhirpapb

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashboard2.data.repository.AuthRepository
import com.example.dashboard2.data.repository.ForumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ✅ Data class untuk Forum Post
data class ForumPost(
    val id: String,
    val authorName: String,
    val authorInstitution: String,
    val authorPhotoUrl: String,
    val content: String,
    val timestamp: String,
    val likes: Int = 0,
    val comments: Int = 0
)

// ✅ Data class untuk Forum Comment
data class ForumComment(
    val id: String,
    val authorName: String,
    val authorInstitution: String,
    val authorPhotoUrl: String,
    val content: String,
    val timestamp: String
)

// ✅ ViewModel untuk manage forum posts
class ForumViewModel : ViewModel() {
    private val forumRepository = ForumRepository()
    private val authRepository = AuthRepository()

    private val _posts = MutableStateFlow<List<ForumPost>>(emptyList())
    val posts = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _currentUser = MutableStateFlow<com.example.dashboard2.data.model.User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _currentPost = MutableStateFlow<ForumPost?>(null)
    val currentPost = _currentPost.asStateFlow()

    private val _comments = MutableStateFlow<List<ForumComment>>(emptyList())
    val comments = _comments.asStateFlow()

    init {
        loadPosts()
        loadCurrentUser()
    }

    // ✅ Load current user data
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val user = authRepository.getCurrentUser()
                _currentUser.value = user
                Log.d("ForumViewModel", "Current user loaded: ${user?.name}")
            } catch (e: Exception) {
                Log.e("ForumViewModel", "Error loading current user", e)
            }
        }
    }

    // ✅ Load posts from Firestore
    private fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                forumRepository.getAllPosts().collect { postsList ->
                    _posts.value = postsList
                    Log.d("ForumViewModel", "Posts updated: ${postsList.size} posts")
                }
            } catch (e: Exception) {
                Log.e("ForumViewModel", "Error loading posts", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ Load single post by ID
    fun loadPostById(postId: String) {
        viewModelScope.launch {
            try {
                val result = forumRepository.getPostById(postId)
                if (result.isSuccess) {
                    _currentPost.value = result.getOrNull()
                    Log.d("ForumViewModel", "Post loaded: ${_currentPost.value?.content}")
                }
            } catch (e: Exception) {
                Log.e("ForumViewModel", "Error loading post", e)
            }
        }
    }

    // ✅ Load comments for a post
    fun loadComments(postId: String) {
        viewModelScope.launch {
            try {
                forumRepository.getComments(postId).collect { commentsList ->
                    _comments.value = commentsList
                    Log.d("ForumViewModel", "Comments updated: ${commentsList.size} comments")
                }
            } catch (e: Exception) {
                Log.e("ForumViewModel", "Error loading comments", e)
            }
        }
    }

    // ✅ Function untuk add new post
    fun addPost(content: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = authRepository.getCurrentUser()

                if (currentUser != null) {
                    val result = forumRepository.addPost(
                        content = content,
                        authorName = currentUser.name,
                        authorInstitution = currentUser.institution,
                        authorPhotoUrl = currentUser.photoUrl
                    )

                    if (result.isSuccess) {
                        Log.d("ForumViewModel", "Post added successfully")
                        onSuccess()
                    } else {
                        Log.e("ForumViewModel", "Failed to add post")
                        onError("Failed to add post")
                    }
                } else {
                    Log.e("ForumViewModel", "User not logged in")
                    onError("Please login first")
                }
            } catch (e: Exception) {
                Log.e("ForumViewModel", "Error adding post", e)
                onError(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ Function untuk add comment
    fun addComment(postId: String, content: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = authRepository.getCurrentUser()

                if (currentUser != null) {
                    val result = forumRepository.addComment(
                        postId = postId,
                        content = content,
                        authorName = currentUser.name,
                        authorInstitution = currentUser.institution,
                        authorPhotoUrl = currentUser.photoUrl
                    )

                    if (result.isSuccess) {
                        Log.d("ForumViewModel", "Comment added successfully")
                        onSuccess()
                    } else {
                        Log.e("ForumViewModel", "Failed to add comment")
                        onError("Failed to add comment")
                    }
                } else {
                    Log.e("ForumViewModel", "User not logged in")
                    onError("Please login first")
                }
            } catch (e: Exception) {
                Log.e("ForumViewModel", "Error adding comment", e)
                onError(e.message ?: "Unknown error")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ Function untuk like post
    fun likePost(postId: String) {
        viewModelScope.launch {
            try {
                val result = forumRepository.likePost(postId)
                if (result.isSuccess) {
                    Log.d("ForumViewModel", "Post liked successfully")
                } else {
                    Log.e("ForumViewModel", "Failed to like post")
                }
            } catch (e: Exception) {
                Log.e("ForumViewModel", "Error liking post", e)
            }
        }
    }

    // ✅ Clear current post and comments when leaving detail screen
    fun clearPostDetail() {
        _currentPost.value = null
        _comments.value = emptyList()
    }

    // ✅ Helper function untuk get timestamp
    private fun getCurrentTimestamp(): String {
        val now = System.currentTimeMillis()
        val seconds = (now / 1000) % 60
        val minutes = (now / (1000 * 60)) % 60

        return when {
            minutes == 0L -> "Just now"
            minutes < 60 -> "$minutes min ago"
            else -> {
                val hours = minutes / 60
                "$hours hour${if (hours > 1) "s" else ""} ago"
            }
        }
    }
}