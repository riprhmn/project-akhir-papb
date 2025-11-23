package com.example.dashboard2


import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dashboard2.data.repository.AuthRepository
import kotlinx.coroutines.launch
import coil.compose.AsyncImage

private val NormalBlue = Color(0xff3759b3)
private val LightGray = Color(0xfff6f6f6)
private val TextGray = Color(0xff484c52)

@Composable
fun ForumScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ForumViewModel = viewModel()
) {
    // ✅ TAMBAHKAN: Get current user from Firebase
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    var userName by remember { mutableStateOf("User") } // Default name

    // ✅ Load user name saat composable pertama kali dibuat
    LaunchedEffect(Unit) {
        scope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser != null) {
                userName = currentUser.name
            }
        }
    }

    val posts = viewModel.posts.collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = NormalBlue)
    ) {
        // Header
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .padding(top = 56.dp, start = 30.dp, end = 30.dp)
                .fillMaxWidth()
        ) {
            // ✅ UBAH: Tampilkan nama user dari Firebase
            Text(
                text = "Hi, $userName",  // ← Nama dinamis dari Firebase!
                color = Color.White,
                textAlign = TextAlign.Start,
                lineHeight = 0.69.em,
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Your space to ask, share, and learn",
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Start,
                lineHeight = 1.38.em,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        // Content Area
        Box(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 152.dp)
                .background(
                    color = LightGray,
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                )
                .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 30.dp,
                    bottom = 100.dp
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                // Welcome Banner
                item {
                    Image(
                        painter = painterResource(id = R.drawable.bannerforum),
                        contentDescription = "Welcome to FinAI Forum Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                }

                // Welcome Message
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.robot_forum),
                            contentDescription = "Forum Avatar",
                            modifier = Modifier
                                .size(size = 60.dp)
                                .clip(shape = CircleShape)
                                .padding(all = 2.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(
                                        topStart = 10.dp,
                                        topEnd = 10.dp,
                                        bottomEnd = 10.dp
                                    )
                                )
                                .clip(
                                    shape = RoundedCornerShape(
                                        topStart = 10.dp,
                                        topEnd = 10.dp,
                                        bottomEnd = 10.dp
                                    )
                                )
                                .background(color = Color.White)
                                .padding(all = 12.dp)
                        ) {
                            Text(
                                text = "✨ Welcome to the Forum!\n📣 You're now part of the FinAI community.\nTempat terbaik untuk bertukar pikiran, berbagi tips, dan bertanya seputar keuangan pribadi — dari budgeting, menabung, sampai investasi cerdas.\n\n💬 Post a question, share your insight, or just explore.  Di sini, setiap obrolan bisa membantumu jadi lebih #FinSmart bersama FinAI.\n\n🔹 Why FinAI Forum?\n✅ Dapatkan jawaban dari sesama pengguna & pakar\n✅ Pelajari strategi finansial baru tiap hari\n✅ Diskusi terbuka, bebas judgment\n✅ Terhubung langsung dengan AI Smart Assistant FinAI",
                                color = Color.Black.copy(alpha = 0.9f),
                                lineHeight = 1.5.em,
                                style = TextStyle(fontSize = 11.sp)
                            )
                        }
                    }
                }

                // Forum Posts List
                if (posts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No posts yet. Be the first to post! 📝",
                                color = TextGray.copy(alpha = 0.6f),
                                style = TextStyle(fontSize = 14.sp)
                            )
                        }
                    }
                } else {
                    items(posts) { post ->
                        ForumPostItem(
                            post = post,
                            onLikeClick = { viewModel.likePost(post.id) },
                            onCommentClick = {
                                navController.navigate(Screen.ForumDetail.createRoute(post.id))
                            }
                        )
                    }
                }
            }
        }

        // Bottom Navigation
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .requiredHeight(80.dp)
                .background(Color.White)
                .border(BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)))
        ) {
            BottomNavItemForum(
                iconRes = R.drawable.ic_homenonpick,
                label = "Home",
                isSelected = false,
                onClick = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )

            BottomNavItemForum(
                iconRes = R.drawable.ic_eventnonpick,
                label = "Event",
                isSelected = false,
                onClick = {
                    navController.navigate(Screen.Event.route) {
                        launchSingleTop = true
                    }
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            BottomNavItemForum(
                iconRes = R.drawable.ic_forumpick,
                label = "Forum",
                isSelected = true,
                onClick = { /* Already here */ }
            )

            BottomNavItemForum(
                iconRes = R.drawable.ic_profilenonpick,
                label = "Profile",
                isSelected = false,
                onClick = {
                    navController.navigate(Screen.Profile.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Bot Button (FAB)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp)
                .size(64.dp)
                .clip(CircleShape)
                .background(NormalBlue)
                .border(BorderStroke(4.dp, Color.White), CircleShape)
                .clickable {
                    navController.navigate(Screen.Bot.route) {
                        launchSingleTop = true
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_bot),
                contentDescription = "AI Bot",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // FAB (Add Post)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 100.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(NormalBlue)
                .clickable {
                    navController.navigate(Screen.ForumAddPost.route)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus),
                contentDescription = "Add Post",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ForumPostItem(
    post: ForumPost,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // ✅ User Avatar - Use InitialAvatar if no photo
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

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = post.authorName,
                    color = Color.Black,
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = post.authorInstitution,
                    color = TextGray.copy(alpha = 0.7f),
                    style = TextStyle(fontSize = 11.sp)
                )
            }

            Text(
                text = post.content,
                color = Color.Black.copy(alpha = 0.9f),
                style = TextStyle(fontSize = 13.sp),
                lineHeight = 1.4.em
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = post.timestamp,
                    color = TextGray.copy(alpha = 0.5f),
                    style = TextStyle(fontSize = 10.sp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCommentClick() }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_comment),
                            contentDescription = "Comments",
                            tint = TextGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${post.comments}",
                            color = TextGray,
                            style = TextStyle(fontSize = 11.sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.BottomNavItemForum(
    @DrawableRes iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .weight(1f)
            .clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = if (isSelected) NormalBlue else TextGray,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) NormalBlue else TextGray,
            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Preview(widthDp = 412, heightDp = 917)
@Composable
private fun ForumScreenPreview() {
    ForumScreen(navController = rememberNavController())
}