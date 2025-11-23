package com.example.dashboard2

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.dashboard2.data.repository.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun ForumDetailScreen(
    postId: String,
    navController: NavController,
    viewModel: ForumViewModel = viewModel()
) {
    val headerBlue = Color(0xff3759b3)
    val backgroundGray = Color(0xfff6f6f6)
    val textGray = Color(0xff484c52)

    val currentPost = viewModel.currentPost.collectAsState().value
    val comments = viewModel.comments.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value

    var commentText by remember { mutableStateOf("") }
    var isCommenting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load user data
    val authRepository = remember { AuthRepository() }
    var userName by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            val user = authRepository.getCurrentUser()
            if (user != null) {
                userName = user.name
                userPhotoUrl = user.photoUrl
            }
        }
    }

    // Load post and comments
    LaunchedEffect(postId) {
        viewModel.loadPostById(postId)
        viewModel.loadComments(postId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearPostDetail()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundGray)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(152.dp)
                .background(color = headerBlue)
                .padding(top = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 26.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .requiredSize(size = 31.dp)
                        .rotate(degrees = 360f)
                )
            }
            Text(
                text = "Discussion",
                color = Color.White,
                lineHeight = 0.67.em,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // Content
        if (currentPost == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = headerBlue)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Original Post
                item {
                    ForumPostDetailItem(post = currentPost)
                }

                // Comments Header
                item {
                    Text(
                        text = "Comments (${comments.size})",
                        color = Color.Black,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Comments List
                if (comments.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No comments yet. Be the first to comment!",
                                color = textGray.copy(alpha = 0.6f),
                                style = TextStyle(fontSize = 13.sp)
                            )
                        }
                    }
                } else {
                    items(comments) { comment ->
                        ForumCommentItem(comment = comment)
                    }
                }
            }

            // Comment Input Box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // User Avatar
                    if (userPhotoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = userPhotoUrl,
                            contentDescription = "Your Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else if (userName.isNotEmpty()) {
                        InitialAvatar(
                            name = userName,
                            size = 40.dp,
                            backgroundColor = getColorFromName(userName),
                            borderWidth = 0.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                    }

                    // Comment Input
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = {
                            commentText = it
                            errorMessage = null
                        },
                        placeholder = { Text("Add a comment...", color = textGray) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = headerBlue,
                            unfocusedBorderColor = textGray.copy(alpha = 0.5f)
                        ),
                        maxLines = 3
                    )

                    // Send Button
                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                isCommenting = true
                                viewModel.addComment(
                                    postId = postId,
                                    content = commentText,
                                    onSuccess = {
                                        commentText = ""
                                        isCommenting = false
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        isCommenting = false
                                    }
                                )
                            }
                        },
                        enabled = commentText.isNotBlank() && !isCommenting,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (commentText.isNotBlank()) headerBlue else headerBlue.copy(alpha = 0.3f))
                    ) {
                        if (isCommenting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_plus),
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ForumPostDetailItem(post: ForumPost) {
    val textGray = Color(0xff484c52)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // ✅ Use InitialAvatar if no photo
            if (post.authorPhotoUrl.isNotEmpty()) {
                AsyncImage(
                    model = post.authorPhotoUrl,
                    contentDescription = "Author Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                InitialAvatar(
                    name = post.authorName,
                    size = 48.dp,
                    backgroundColor = getColorFromName(post.authorName),
                    borderWidth = 0.dp
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = post.authorName,
                    color = Color.Black,
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = post.authorInstitution,
                    color = textGray.copy(alpha = 0.7f),
                    style = TextStyle(fontSize = 12.sp)
                )
                Text(
                    text = post.timestamp,
                    color = textGray.copy(alpha = 0.5f),
                    style = TextStyle(fontSize = 11.sp)
                )
            }
        }

        Text(
            text = post.content,
            color = Color.Black.copy(alpha = 0.9f),
            style = TextStyle(fontSize = 14.sp),
            lineHeight = 1.5.em
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${post.likes}",
                    color = textGray,
                    style = TextStyle(fontSize = 12.sp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_comment),
                    contentDescription = "Comments",
                    tint = textGray,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${post.comments}",
                    color = textGray,
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        }
    }
}

@Composable
fun ForumCommentItem(comment: ForumComment) {
    val textGray = Color(0xff484c52)

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        // ✅ Use InitialAvatar if no photo
        if (comment.authorPhotoUrl.isNotEmpty()) {
            AsyncImage(
                model = comment.authorPhotoUrl,
                contentDescription = "Commenter Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            InitialAvatar(
                name = comment.authorName,
                size = 36.dp,
                backgroundColor = getColorFromName(comment.authorName),
                borderWidth = 0.dp
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = comment.authorName,
                        color = Color.Black,
                        style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = comment.authorInstitution,
                        color = textGray.copy(alpha = 0.7f),
                        style = TextStyle(fontSize = 10.sp)
                    )
                }
                Text(
                    text = comment.timestamp,
                    color = textGray.copy(alpha = 0.5f),
                    style = TextStyle(fontSize = 10.sp)
                )
            }

            Text(
                text = comment.content,
                color = Color.Black.copy(alpha = 0.9f),
                style = TextStyle(fontSize = 13.sp),
                lineHeight = 1.4.em,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun ForumDetailScreenPreview() {
    ForumDetailScreen(
        postId = "sample",
        navController = rememberNavController()
    )
}