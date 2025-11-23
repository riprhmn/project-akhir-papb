package com.example.projectakhirpapb

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dashboard2.data.model.EventItem
import com.example.dashboard2.data.repository.EventRepository
import kotlinx.coroutines.launch

private val NormalBlue = Color(0xFF3759B3)
private val LightGray = Color(0xFFF6F6F6)

@Composable
fun EventDetailScreen(
    modifier: Modifier = Modifier,
    event: EventItem? = null,
    onNavigateBack: () -> Unit = {},
    onNavigateToRegistration: (EventItem) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val eventRepository = remember { EventRepository() }

    var distance by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Calculate distance
    LaunchedEffect(event) {
        if (event != null && event.latitude != 0.0 && event.longitude != 0.0) {
            scope.launch {
                distance = eventRepository.calculateDistanceToEvent(event)
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = NormalBlue)
    ) {
        // Header
        Text(
            text = "Event Detail",
            color = Color.White,
            lineHeight = 0.67.em,
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
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
                .padding(top = 152.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = LightGray)
        ) {
            if (event == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Event not found", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 100.dp)
                ) {
                    // Event Image
                    item {
                        if (event.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = event.imageUrl,
                                contentDescription = event.title,
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.event1),
                                error = painterResource(id = R.drawable.event1),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        } else if (event.imageRes != 0) {
                            Image(
                                painter = painterResource(id = event.imageRes),
                                contentDescription = event.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                            )
                        }
                    }

                    // Event Details
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            // Title
                            Text(
                                text = event.title,
                                color = Color.Black,
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            // Category Tags
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                modifier = Modifier.padding(bottom = 15.dp)
                            ) {
                                // Category Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(Color(0xFFE1E6F4))
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = event.category,
                                        color = Color(0xFF192851),
                                        style = TextStyle(fontSize = 12.sp)
                                    )
                                }

                                // Price Badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(
                                            if (event.price == 0.0) Color(0xFFC7E7C1)
                                            else Color(0xFFFFE5B4)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = if (event.price == 0.0) "Free"
                                        else "Rp ${event.price.toInt()}",
                                        color = if (event.price == 0.0) Color(0xFF254A24)
                                        else Color(0xFF8B4513),
                                        style = TextStyle(fontSize = 12.sp)
                                    )
                                }
                            }
                        }
                    }

                    // Date & Time Card
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFEDEDED).copy(alpha = 0.59f))
                                .padding(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF6200EE))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_calendar),
                                    contentDescription = "Date & Time",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    text = "Date & Time",
                                    color = Color.Black.copy(alpha = 0.7f),
                                    style = TextStyle(fontSize = 14.sp)
                                )
                                Text(
                                    text = event.date,
                                    color = Color.Black,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = event.time,
                                    color = Color.Black.copy(alpha = 0.7f),
                                    style = TextStyle(fontSize = 14.sp)
                                )
                            }
                        }
                    }

                    // Location Card
                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFEDEDED).copy(alpha = 0.59f))
                                .padding(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFEB383B))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_location),
                                    contentDescription = "Location",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    text = "Location",
                                    color = Color.Black.copy(alpha = 0.7f),
                                    style = TextStyle(fontSize = 14.sp)
                                )
                                Text(
                                    text = event.location,
                                    color = Color.Black,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )

                                // Distance
                                if (distance != null && distance != "Lokasi tidak tersedia") {
                                    Text(
                                        text = "📍 $distance dari lokasi Anda",
                                        color = NormalBlue,
                                        style = TextStyle(
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Description
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "About Event",
                                color = Color.Black,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = event.description,
                                color = Color.Black.copy(alpha = 0.8f),
                                style = TextStyle(fontSize = 14.sp),
                                lineHeight = 1.5.em
                            )
                        }
                    }
                }

                // Register Button (Fixed at Bottom)
                Button(
                    onClick = { onNavigateToRegistration(event) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NormalBlue
                    ),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(
                        text = "Register Now",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}