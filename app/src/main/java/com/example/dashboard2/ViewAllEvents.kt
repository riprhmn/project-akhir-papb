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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.model.EventWithDistance
import com.example.dashboard2.data.repository.AuthRepository
import com.example.dashboard2.data.repository.EventRepository
import kotlinx.coroutines.launch

private val NormalBlue = Color(0xFF3759B3)
private val LightGray = Color(0xFFF6F6F6)
private const val TAG = "ALL_EVENTS"

@Composable
fun AllEventsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val authRepository = remember { AuthRepository() }
    val eventRepository = remember { EventRepository() }
    val scope = rememberCoroutineScope()

    var eventsWithDistance by remember { mutableStateOf<List<EventWithDistance>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ✅ Load ALL events
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                Log.d(TAG, "========== LOADING ALL EVENTS ==========")
                eventsWithDistance = eventRepository.getEventsSortedByDistance()
                Log.d(TAG, "✓ Loaded ${eventsWithDistance.size} events")
                Log.d(TAG, "========================================")
            } catch (e: Exception) {
                Log.e(TAG, "ERROR loading events", e)
                errorMessage = "Gagal memuat event: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = NormalBlue)
    ) {
        // Header
        Text(
            text = "All Events",
            color = Color.White,
            lineHeight = 0.67.em,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .padding(top = 70.dp)
        )

        // Back Button
        Image(
            painter = painterResource(id = R.drawable.back),
            contentDescription = "Back Arrow",
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .padding(start = 26.dp, top = 71.dp)
                .size(31.dp)
                .rotate(degrees = 360f)
                .clickable { onNavigateBack() }
        )

        // Content
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
                                    eventsWithDistance = eventRepository.getEventsSortedByDistance()
                                    isLoading = false
                                }
                            }
                        )
                    }
                }
            } else {
                // ✅ All Events List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header Info
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Showing ${eventsWithDistance.size} events",
                                color = Color.Black,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Sorted by distance from your location",
                                color = Color.Gray,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                    }

                    // ✅ All Events
                    if (eventsWithDistance.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada event tersedia",
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        items(eventsWithDistance) { eventWithDistance ->
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
    }
}