package com.example.projectakhirpapb

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
fun AddPostScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    navController: NavController,
    viewModel: ForumViewModel = viewModel()
) {
    var inputText by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val authRepository = remember { AuthRepository() }
    var userName by remember { mutableStateOf("Loading...") }
    var userInstitution by remember { mutableStateOf("") }
    var userPhotoUrl by remember { mutableStateOf("") }
    var isUserLoaded by remember { mutableStateOf(false) }

    val headerBlue = Color(0xff3759b3)
    val backgroundGray = Color(0xfff6f6f6)
    val textGray = Color(0xff484c52)

    LaunchedEffect(Unit) {
        launch {
            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    userName = user.name
                    userInstitution = user.institution
                    userPhotoUrl = user.photoUrl
                    isUserLoaded = true
                    Log.d("AddPostScreen", "User loaded: $userName")
                    Log.d("AddPostScreen", "Photo URL: '$userPhotoUrl'")
                } else {
                    Log.e("AddPostScreen", "User is null")
                }
            } catch (e: Exception) {
                Log.e("AddPostScreen", "Error loading user", e)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = backgroundGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(152.dp)
                .background(color = headerBlue)
                .padding(top = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    Log.d("AddPostScreen", "Back button clicked")
                    navController.navigateUp()
                },
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
                text = "Forum",
                color = Color.White,
                lineHeight = 0.67.em,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.Top
            ) {
                // ✅ Profile Picture - Use InitialAvatar if no photo
                if (userPhotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = userPhotoUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .shadow(elevation = 4.dp, shape = CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else if (isUserLoaded) {
                    // ✅ Show InitialAvatar when no photo
                    InitialAvatar(
                        name = userName,
                        size = 40.dp,
                        backgroundColor = getColorFromName(userName),
                        borderWidth = 0.dp
                    )
                } else {
                    // Loading placeholder
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = if (isUserLoaded) {
                            "$userName | $userInstitution"
                        } else {
                            "Loading user info..."
                        },
                        color = Color.Black,
                        lineHeight = 1.25.em,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp)
                    )

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                            errorMessage = null
                            Log.d("AddPostScreen", "Text changed: $it")
                        },
                        placeholder = {
                            Text(
                                text = "Apa yang ingin kamu tanyakan atau bagikan?",
                                color = textGray
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeight(150.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = headerBlue,
                            unfocusedBorderColor = textGray,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        maxLines = 8
                    )

                    errorMessage?.let { error ->
                        Text(
                            text = error,
                            color = Color.Red,
                            style = TextStyle(fontSize = 12.sp)
                        )
                    }

                    Button(
                        onClick = {
                            Log.d("AddPostScreen", "=== POST BUTTON CLICKED ===")
                            Log.d("AddPostScreen", "User loaded: $isUserLoaded")
                            Log.d("AddPostScreen", "Input text: '$inputText'")

                            if (inputText.isNotBlank() && isUserLoaded) {
                                isPosting = true
                                viewModel.addPost(
                                    content = inputText,
                                    onSuccess = {
                                        Log.d("AddPostScreen", "Post added successfully")
                                        isPosting = false
                                        navController.navigateUp()
                                    },
                                    onError = { error ->
                                        Log.e("AddPostScreen", "Error: $error")
                                        errorMessage = error
                                        isPosting = false
                                    }
                                )
                            } else if (!isUserLoaded) {
                                errorMessage = "Please wait, loading user data..."
                            }
                        },
                        enabled = inputText.isNotBlank() && !isPosting,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = headerBlue,
                            disabledContainerColor = headerBlue.copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(horizontal = 30.dp, vertical = 10.dp)
                    ) {
                        if (isPosting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Post",
                                color = Color.White,
                                lineHeight = 1.5.em,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun AddPostScreenPreview() {
    MaterialTheme {
        AddPostScreen(
            navController = rememberNavController(),
            onBackClick = {}
        )
    }
}