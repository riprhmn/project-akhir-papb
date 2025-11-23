package com.example.dashboard2

import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dashboard2.data.model.EventWithDistance
import com.example.dashboard2.data.repository.AuthRepository
import com.example.dashboard2.data.repository.EventRepository
import kotlinx.coroutines.launch

private val DarkBlue = Color(0xFF192851)
private val NormalBlue = Color(0xFF3759B3)
private val LightGray = Color(0xFFF6F6F6)
private val TextGray = Color(0xFF484C52)

private const val TAG = "EVENT_PAGE"

@Composable
fun EventPage(
    modifier: Modifier = Modifier,
    onNavigateToDetail: (String) -> Unit = {},
    onNavigateToAllEvents: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToForum: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToBot: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val eventRepository = remember { EventRepository() }
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("User") }
    var allEvents by remember { mutableStateOf<List<EventWithDistance>>(emptyList()) }
    var nearbyEvents by remember { mutableStateOf<List<EventWithDistance>>(emptyList()) } // ✅ New
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ✅ Load user name dan events
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                Log.d(TAG, "========== LOADING EVENT PAGE ==========")

                // Load user name
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    userName = currentUser.name
                    Log.d(TAG, "User name: $userName")
                }

                // ✅ Load ALL events sorted by distance
                Log.d(TAG, "Fetching events sorted by distance...")
                allEvents = eventRepository.getEventsSortedByDistance()
                Log.d(TAG, "✓ Loaded ${allEvents.size} total events")

                // ✅ Filter nearby events (distance < 30 KM)
                nearbyEvents = allEvents.filter { eventWithDistance ->
                    eventWithDistance.distanceKm < 30.0 &&
                            eventWithDistance.distanceText != "Lokasi tidak tersedia"
                }

                Log.d(TAG, "✓ Found ${nearbyEvents.size} nearby events (< 30 KM)")
                Log.d(TAG, "Nearby events:")
                nearbyEvents.forEach { event ->
                    Log.d(TAG, "  - ${event.event.title}: ${event.distanceText}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "ERROR loading events", e)
                errorMessage = "Gagal memuat event: ${e.message}"
            } finally {
                isLoading = false
                Log.d(TAG, "========================================")
            }
        }
    }

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
                .padding(top = 56.dp, start = 30.dp)
        ) {
            Text(
                text = "Hi, $userName",
                color = Color.White,
                textAlign = TextAlign.Start,
                lineHeight = 0.69.em,
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Your event experience starts here",
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
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
        ) {
            if (isLoading) {
                // Loading State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = NormalBlue)
                }
            } else if (errorMessage != null) {
                // Error State
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "Terjadi kesalahan",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Refresh",
                            color = NormalBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                isLoading = true
                                errorMessage = null
                                scope.launch {
                                    allEvents = eventRepository.getEventsSortedByDistance()
                                    nearbyEvents = allEvents.filter { it.distanceKm < 30.0 }
                                    isLoading = false
                                }
                            }
                        )
                    }
                }
            } else {
                // Events List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(bottom = 90.dp)
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    item {
                        Image(
                            painter = painterResource(id = R.drawable.bannerevent),
                            contentDescription = "Event Banner",
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(height = 136.dp)
                                .clip(shape = RoundedCornerShape(20.dp))
                        )
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "Nearby Event",
                                    color = Color.Black,
                                    textAlign = TextAlign.Start,
                                    lineHeight = 1.1.em,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                // ✅ Show count of nearby events
                                Text(
                                    text = "${nearbyEvents.size} events within 30 KM",
                                    color = Color.Gray,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                )
                            }

                            Text(
                                text = "View all",
                                color = NormalBlue,
                                textAlign = TextAlign.End,
                                lineHeight = 1.57.em,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.clickable { onNavigateToAllEvents() }
                            )
                        }
                    }

                    // ✅ Display NEARBY events only (< 30 KM)
                    if (nearbyEvents.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_location),
                                        contentDescription = "No events",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "Tidak ada event terdekat dalam radius 30 KM",
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center,
                                        style = TextStyle(fontSize = 14.sp)
                                    )
                                    Text(
                                        text = "Lihat semua event",
                                        color = NormalBlue,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { onNavigateToAllEvents() },
                                        style = TextStyle(fontSize = 14.sp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(nearbyEvents) { eventWithDistance ->
                            EventItem(
                                eventWithDistance = eventWithDistance,
                                onRegisterClick = {
                                    onNavigateToDetail(eventWithDistance.event.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Navigation (unchanged)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .requiredHeight(80.dp)
                    .background(Color.White)
                    .border(BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)))
                    .padding(horizontal = 20.dp)
            ) {
                BottomNavItemEvent(
                    iconRes = R.drawable.ic_home,
                    label = "Home",
                    isSelected = false,
                    onClick = onNavigateToHome
                )
                BottomNavItemEvent(
                    iconRes = R.drawable.ic_event,
                    label = "Event",
                    isSelected = true,
                    onClick = { }
                )
                Spacer(modifier = Modifier.width(64.dp))
                BottomNavItemEvent(
                    iconRes = R.drawable.ic_forum,
                    label = "Forum",
                    isSelected = false,
                    onClick = onNavigateToForum
                )
                BottomNavItemEvent(
                    iconRes = R.drawable.ic_profile,
                    label = "Profile",
                    isSelected = false,
                    onClick = onNavigateToProfile
                )
            }

            // AI Bot Button
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(NormalBlue)
                    .border(BorderStroke(4.dp, Color.White), CircleShape)
                    .clickable { onNavigateToBot() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_bot),
                    contentDescription = "AI Bot",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun BottomNavItemEvent(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 8.dp)
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

@Composable
fun EventItem(
    eventWithDistance: EventWithDistance,
    onRegisterClick: () -> Unit
) {
    val event = eventWithDistance.event

    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp))
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color = Color.White)
            .padding(all = 10.dp)
    ) {
        // ✅ Event Image dari Firebase atau drawable
        if (event.imageUrl.isNotEmpty()) {
            AsyncImage(
                model = event.imageUrl,
                contentDescription = "${event.title} image",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.event1),
                error = painterResource(id = R.drawable.event1),
                modifier = Modifier
                    .weight(0.45f)
                    .requiredHeight(height = 89.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
            )
        } else if (event.imageRes != 0) {
            Image(
                painter = painterResource(id = event.imageRes),
                contentDescription = "${event.title} image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(0.45f)
                    .requiredHeight(height = 89.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
            )
        } else {
            // Placeholder if no image
            Box(
                modifier = Modifier
                    .weight(0.45f)
                    .requiredHeight(height = 89.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_event),
                    contentDescription = "Event placeholder",
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(0.55f)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Title
                Text(
                    text = event.title,
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // Location with Distance
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "Location Icon",
                        colorFilter = ColorFilter.tint(Color(0xFFFF2424)),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = event.location,
                        color = Color.Black.copy(alpha = 0.8f),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // ✅ Distance Badge
                    if (eventWithDistance.distanceText != "Lokasi tidak tersedia") {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = NormalBlue.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = eventWithDistance.distanceText,
                                style = TextStyle(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = NormalBlue
                            )
                        }
                    }
                }

                // Date
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "Calendar Icon",
                        colorFilter = ColorFilter.tint(Color.Black.copy(alpha = 0.8f)),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "${event.date} • ${event.time}",
                        color = Color.Black.copy(alpha = 0.8f),
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Register Button
            Surface(
                shape = RoundedCornerShape(5.dp),
                color = NormalBlue,
                border = BorderStroke(1.dp, Color(0xFFC1CCE7).copy(alpha = 0.2f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(5.dp))
                    .clickable { onRegisterClick() },
                shadowElevation = 2.dp
            ) {
                Text(
                    text = "Register",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }
    }
}