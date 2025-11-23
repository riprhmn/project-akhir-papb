package com.example.dashboard2

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.dashboard2.data.repository.AuthRepository
import kotlinx.coroutines.launch

private val DarkBlue = Color(0xff192851)
private val NormalBlue = Color(0xff3759b3)
private val LightGray = Color(0xfff6f6f6)
private val TextGray = Color(0xff484c52)
private val TextGrayDialog = Color(0xff9d9d9d)

@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    onEditProfileClick: () -> Unit = {},
    onNavigateToPerformance: () -> Unit = {},
    onNavigateToMyEvents: () -> Unit = {},
    onNavigateToSetLocation: () -> Unit = {},
    onNavigateToSignIn: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToEvent: () -> Unit = {},
    onNavigateToForum: () -> Unit = {},
    onNavigateToBot: () -> Unit = {},
    onNavigateToScanQR: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var userName by remember { mutableStateOf("User") }
    var userEmail by remember { mutableStateOf("") }
    var userInstitution by remember { mutableStateOf("Loading...") }
    var userPhotoUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(refreshKey) {
        isLoading = true
        scope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    userName = currentUser.name
                    userEmail = currentUser.email
                    userInstitution = currentUser.institution.ifEmpty { "Computer Science UB" }
                    userPhotoUrl = currentUser.photoUrl
                } else {
                    Toast.makeText(
                        context,
                        "Silakan login terlebih dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateToSignIn()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error loading profile: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = LightGray)
    ) {
        // ✅ Scrollable Content
        LazyColumn(
            contentPadding = PaddingValues(
                top = 200.dp,
                bottom = 100.dp,
                start = 20.dp,
                end = 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NormalBlue)
                    }
                }
            } else {
                // ✅ Profile Avatar & Info
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp)
                    ) {
                        if (userPhotoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(userPhotoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.fotoprofile),
                                error = painterResource(id = R.drawable.fotoprofile),
                                modifier = Modifier
                                    .size(81.dp)
                                    .clip(CircleShape)
                                    .shadow(elevation = 4.dp, shape = CircleShape)
                            )
                        } else {
                            InitialAvatar(
                                name = userName,
                                size = 81.dp,
                                backgroundColor = getColorFromName(userName),
                                textColor = Color.White,
                                borderColor = DarkBlue,
                                borderWidth = 3.dp
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(start = 15.dp)
                        ) {
                            Text(
                                text = userName,
                                color = Color.Black,
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = userInstitution,
                                color = Color.Black.copy(alpha = 0.7f),
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }
                    }
                }

                // ✅ Section Title
                item {
                    Text(
                        text = "Account Setting",
                        color = Color.Black,
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    )
                }

                // ✅ Edit Profile
                item {
                    SettingsRow(
                        iconRes = R.drawable.ic_edit_profile,
                        iconDesc = "Edit Profile Icon",
                        text = "Edit Profile",
                        iconTint = NormalBlue,
                        onRowClick = {
                            onEditProfileClick()
                            refreshKey++
                        }
                    )
                }

                // ✅ Performance
                item {
                    SettingsRow(
                        iconRes = R.drawable.ic_performance,
                        iconDesc = "Performance Icon",
                        text = "Performance",
                        iconTint = NormalBlue,
                        onRowClick = onNavigateToPerformance
                    )
                }

                // ✅ My Events
                item {
                    SettingsRow(
                        iconRes = R.drawable.ticketprof,
                        iconDesc = "My Event Icon",
                        text = "My event",
                        iconTint = NormalBlue,
                        onRowClick = onNavigateToMyEvents
                    )
                }

                // ✅ Scan Event Ticket
                item {
                    SettingsRow(
                        iconRes = R.drawable.ic_scan,
                        iconDesc = "Scan QR Icon",
                        text = "Scan Event Ticket",
                        iconTint = NormalBlue,
                        onRowClick = onNavigateToScanQR
                    )
                }

                // ✅ Set Location
                item {
                    SettingsRow(
                        iconRes = R.drawable.locprofil,
                        iconDesc = "Set Location Icon",
                        text = "Set Location",
                        iconTint = NormalBlue,
                        onRowClick = onNavigateToSetLocation
                    )
                }

                // ✅ Spacer
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }

                // ✅ Logout Button
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 20.dp)
                            .shadow(elevation = 4.dp, shape = RoundedCornerShape(10.dp))
                            .clip(shape = RoundedCornerShape(10.dp))
                            .background(color = NormalBlue)
                            .clickable { showLogoutDialog = true } // ✅ Show dialog
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "Logout",
                            color = LightGray,
                            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // ✅ Header Background - FULL
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .fillMaxWidth()
                .height(200.dp)
                .background(color = NormalBlue)
        )

        // ✅ Header Text
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .padding(top = 70.dp, start = 30.dp, end = 30.dp)
        ) {
            Text(
                text = "Hi, $userName",
                color = Color.White,
                textAlign = TextAlign.Start,
                lineHeight = 0.69.em,
                style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Manage your personal information here",
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Start,
                lineHeight = 1.38.em,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
            )
        }

        // ✅ Bottom Navigation
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
            BottomNavItem(
                iconRes = R.drawable.ic_homenonpick,
                label = "Home",
                isSelected = false,
                iconTint = TextGray,
                onClick = onNavigateToHome
            )
            BottomNavItem(
                iconRes = R.drawable.ic_eventnonpick,
                label = "Event",
                isSelected = false,
                iconTint = TextGray,
                onClick = onNavigateToEvent
            )
            Spacer(modifier = Modifier.weight(1f))
            BottomNavItem(
                iconRes = R.drawable.ic_forumnonpick,
                label = "Forum",
                isSelected = false,
                iconTint = TextGray,
                onClick = onNavigateToForum
            )
            BottomNavItem(
                iconRes = R.drawable.ic_profilepick,
                label = "Profile",
                isSelected = true,
                iconTint = NormalBlue,
                onClick = {}
            )
        }

        // ✅ AI Bot Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-30).dp)
                .size(64.dp)
                .clip(CircleShape)
                .clickable { onNavigateToBot() },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_ai),
                contentDescription = "AI Bot",
                modifier = Modifier.size(80.dp)
            )
        }
    }

    // ✅ Custom Logout Dialog
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onCancel = {
                showLogoutDialog = false
            },
            onConfirm = {
                showLogoutDialog = false
                scope.launch {
                    authRepository.signOut()
                    Toast.makeText(
                        context,
                        "Berhasil logout",
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateToSignIn()
                }
            }
        )
    }
}

@Composable
fun SettingsRow(
    @DrawableRes iconRes: Int,
    iconDesc: String,
    text: String,
    iconTint: Color = DarkBlue,
    onRowClick: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(15.dp))
            .clip(shape = RoundedCornerShape(15.dp))
            .background(color = Color.White)
            .border(
                border = BorderStroke(1.dp, iconTint.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(15.dp)
            )
            .clickable { onRowClick() }
            .padding(horizontal = 15.dp, vertical = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = iconDesc,
                tint = iconTint,
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = text,
                color = iconTint,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.rightarrow),
            contentDescription = "Arrow Right",
            tint = iconTint.copy(alpha = 0.6f),
            modifier = Modifier
                .size(30.dp)
                .rotate(degrees = 360f)
        )
    }
}

@Composable
fun RowScope.BottomNavItem(
    @DrawableRes iconRes: Int,
    label: String,
    isSelected: Boolean,
    iconTint: Color,
    onClick: () -> Unit = {}
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

// ✅ Custom Logout Confirmation Dialog (sesuai UI yang Anda berikan)
@Composable
fun LogoutConfirmationDialog(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onCancel) {
        Column(
            horizontalAlignment = Alignment.End,
            modifier = modifier
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp))
                .clip(shape = RoundedCornerShape(10.dp))
                .background(color = Color.White)
                .padding(all = 30.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Text(
                    text = "Logout Confirmation",
                    color = Color.Black,
                    lineHeight = 1.25.em,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    text = "Are you sure want to do logout?",
                    color = TextGrayDialog,
                    lineHeight = 1.88.em,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // ✅ Cancel Button
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = Color.White)
                        .border(
                            border = BorderStroke(1.dp, NormalBlue),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .clickable { onCancel() }
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Cancel",
                        color = NormalBlue,
                        lineHeight = 1.5.em,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // ✅ Confirm Button
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(shape = RoundedCornerShape(15.dp))
                        .background(color = NormalBlue)
                        .clickable { onConfirm() }
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Confirm",
                        color = Color.White,
                        lineHeight = 1.5.em,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}