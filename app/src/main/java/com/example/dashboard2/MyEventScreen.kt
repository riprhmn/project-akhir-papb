package com.example.dashboard2

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.example.dashboard2.data.model.EventRegistration
import com.example.dashboard2.data.repository.AuthRepository
import com.example.dashboard2.data.repository.EventRepository
import kotlinx.coroutines.launch


// ✅ Warna Tema
private val NormalBlue = Color(0xFF3759B3)
private val LightGray = Color(0xFFF6F6F6)
private val TextGray = Color(0xFF484C52)
private val UpcomingYellow = Color(0xFFE19B2A)
private val UpcomingYellowBg = Color(0xFFFFC973).copy(alpha = 0.5f)
private val CompletedGreen = Color(0xFF319F43)
private val CompletedGreenBg = Color(0xFFA5FFB3).copy(alpha = 0.5f)

data class EventRegistration(
    val eventId: String = "",
    val eventTitle: String = "",  // ✅ Match dengan EventRepository
    val eventDate: String = "",
    val eventLocation: String = "",
    val eventImage: String = "",
    val status: String = "registered",
    val registeredAt: Long = 0L,
    val userId: String = "",
    val userEmail: String = "",
    val userName: String = ""
)

@Composable
fun MyEventScreen(
    onBackClick: () -> Unit,
    onNavigateToDetail: (EventRegistration) -> Unit = {}, // ✅ Add callback
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues()
) {
    var selectedTab by remember { mutableStateOf("Upcoming") }
    var userRegistrations by remember { mutableStateOf<List<EventRegistration>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // ✅ Fetch user registrations from Firestore (UPDATED PATH)
    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUserId)
                .collection("event_registrations")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        isLoading = false
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        userRegistrations = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(EventRegistration::class.java)?.copy(
                                eventId = doc.id
                            )
                        }.sortedByDescending { it.registeredAt }
                    }
                    isLoading = false
                }
        } else {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        val eventRepository = EventRepository()
        eventRepository.debugListUserRegistrations()
    }

    // ✅ Filter events berdasarkan tab
    val eventsToShow = remember(selectedTab, userRegistrations) {
        when (selectedTab) {
            "Upcoming" -> userRegistrations.filter { it.status == "registered" }
            "Completed" -> userRegistrations.filter { it.status == "completed" }
            else -> emptyList()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = NormalBlue)
            .padding(paddingValues)
    ) {
        // ✅ Header
        Text(
            text = "My Events",
            color = Color.White,
            lineHeight = 0.67.em,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .padding(top = 70.dp)
        )

        // ✅ Back Button
        Image(
            painter = painterResource(id = R.drawable.back),
            contentDescription = "Back Arrow",
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .padding(start = 26.dp, top = 71.dp)
                .size(31.dp)
                .rotate(degrees = 360f)
                .clickable { onBackClick() }
        )

        // ✅ Content Area
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
            Column(modifier = Modifier.fillMaxSize()) {
                // ✅ Tabs (Upcoming / Completed)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 20.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(NormalBlue.copy(alpha = 0.1f))
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EventTab(
                        text = "Upcoming",
                        isSelected = selectedTab == "Upcoming",
                        onClick = { selectedTab = "Upcoming" },
                        modifier = Modifier.weight(1f)
                    )
                    EventTab(
                        text = "Completed",
                        isSelected = selectedTab == "Completed",
                        onClick = { selectedTab = "Completed" },
                        modifier = Modifier.weight(1f)
                    )
                }

                // ✅ Loading atau Event List
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NormalBlue)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 10.dp,
                            bottom = 100.dp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        if (eventsToShow.isEmpty()) {
                            item {
                                EmptyEventState(
                                    isUpcoming = selectedTab == "Upcoming"
                                )
                            }
                        } else {
                            items(eventsToShow) { registration ->
                                EventListItem(
                                    registration = registration,
                                    isUpcoming = registration.status == "registered",
                                    onDetailClick = {
                                        onNavigateToDetail(registration) // ✅ Pass registration data
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ✅ Tab Component
@Composable
fun EventTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) NormalBlue else Color.Transparent
    val textColor = if (isSelected) Color.White else TextGray

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(30.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            maxLines = 1,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// ✅ Event List Item with New Design
@Composable
fun EventListItem(
    registration: EventRegistration,
    isUpcoming: Boolean, // ✅ Fixed: Changed to Boolean parameter
    onDetailClick: () -> Unit, // ✅ Fixed: Proper function signature
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .clip(shape = RoundedCornerShape(10.dp))
            .background(color = Color.White)
            .padding(horizontal = 15.dp, vertical = 10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            // ✅ Status Tag (Upcoming/Completed)
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val tagColor = if (isUpcoming) UpcomingYellow else CompletedGreen
                val tagBgColor = if (isUpcoming) UpcomingYellowBg else CompletedGreenBg

                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(10.dp))
                        .background(color = tagBgColor)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isUpcoming) "Upcoming" else "Completed",
                        color = tagColor,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // ✅ Event Title
            Text(
                text = registration.eventTitle,
                color = Color.Black,
                maxLines = 2,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 4.dp)
            )

            // ✅ Location
            Text(
                text = registration.eventLocation,
                color = Color.Black.copy(alpha = 0.7f),
                maxLines = 1,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(top = 2.dp)
            )

            // ✅ Date with Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Calendar Icon",
                    colorFilter = ColorFilter.tint(TextGray),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = registration.eventDate,
                    color = Color.Black.copy(alpha = 0.7f),
                    maxLines = 1,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // ✅ Cek Detail Button
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(14.dp))
                .background(color = NormalBlue)
                .clickable { onDetailClick() }
                .padding(horizontal = 15.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Cek Detail",
                color = Color.White,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// ✅ Empty State
@Composable
fun EmptyEventState(
    isUpcoming: Boolean,
    modifier: Modifier = Modifier
) {
    val message = if (isUpcoming) {
        "No upcoming events"
    } else {
        "No completed events yet"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ticketprof),
            contentDescription = "No Events",
            tint = Color.Gray.copy(alpha = 0.3f),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            color = Color.Black.copy(alpha = 0.5f),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isUpcoming) {
                "Register for events to see them here!"
            } else {
                "Completed events will appear here"
            },
            color = Color.Black.copy(alpha = 0.3f),
            style = TextStyle(fontSize = 14.sp),
            textAlign = TextAlign.Center
        )
    }
}