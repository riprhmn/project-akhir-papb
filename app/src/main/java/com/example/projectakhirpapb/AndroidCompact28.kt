package com.example.projectakhirpapb

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dashboard2.data.model.EventItem
import com.example.dashboard2.data.repository.AuthRepository
import com.example.dashboard2.data.repository.EventRepository
import kotlinx.coroutines.launch

private val NormalBlue = Color(0xFF3759B3)
private val LightGray = Color(0xFFF6F6F6)

@Composable
fun AndroidCompact28(
    modifier: Modifier = Modifier,
    event: EventItem? = null,
    onNavigateBack: () -> Unit = {},
    onNavigateToSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }
    val eventRepository = remember { EventRepository() }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isRegistering by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }

    // Load user data
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    email = currentUser.email
                    fullName = currentUser.name
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
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
        Text(
            text = "Event Registration",
            color = Color.White,
            lineHeight = 0.67.em,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .padding(top = 70.dp)
        )

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

        Box(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .padding(top = 152.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = LightGray)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = NormalBlue
                )
            } else if (event == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Event data not available", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxSize().padding(bottom = 20.dp)
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
                                modifier = Modifier.fillMaxWidth().height(155.dp)
                            )
                        } else if (event.imageRes != 0) {
                            Image(
                                painter = painterResource(id = event.imageRes),
                                contentDescription = event.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(155.dp)
                            )
                        }
                    }

                    // Event Info
                    item {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Text(
                                text = event.title,
                                color = Color.Black,
                                lineHeight = 1.25.em,
                                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(Color(0xFFE1E6F4))
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        event.category,
                                        color = Color(0xFF192851),
                                        style = TextStyle(fontSize = 12.sp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(15.dp))
                                        .background(
                                            if (event.price == 0.0) Color(0xFFC7E7C1) else Color(
                                                0xFFFFE5B4
                                            )
                                        )
                                        .padding(horizontal = 12.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        if (event.price == 0.0) "Free" else "Rp ${event.price.toInt()}",
                                        color = if (event.price == 0.0) Color(0xFF254A24) else Color(
                                            0xFF8B4513
                                        ),
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
                                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF6200EE)).padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_calendar),
                                    contentDescription = "Date & Time",
                                    colorFilter = ColorFilter.tint(Color.White),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.padding(start = 10.dp).weight(1f)
                            ) {
                                Text(
                                    "Date & Time",
                                    color = Color.Black.copy(alpha = 0.7f),
                                    style = TextStyle(fontSize = 14.sp)
                                )
                                Text(
                                    event.date,
                                    color = Color.Black,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    event.time,
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
                                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFEB383B)).padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_location),
                                    contentDescription = "Location",
                                    colorFilter = ColorFilter.tint(Color.White),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier.padding(start = 10.dp).weight(1f)
                            ) {
                                Text(
                                    "Location",
                                    color = Color.Black.copy(alpha = 0.7f),
                                    style = TextStyle(fontSize = 14.sp)
                                )
                                Text(
                                    event.location,
                                    color = Color.Black,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    // Full Name
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        ) {
                            Text(
                                "Full Name",
                                color = Color.Black,
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            )
                            OutlinedTextField(
                                value = fullName,
                                onValueChange = { fullName = it; nameError = null },
                                placeholder = {
                                    Text(
                                        "Input Name",
                                        color = Color.Black.copy(alpha = 0.5f)
                                    )
                                },
                                isError = nameError != null,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color.Black.copy(alpha = 0.3f),
                                    focusedBorderColor = NormalBlue,
                                    errorBorderColor = Color.Red
                                )
                            )
                        }
                    }

                    // Email
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        ) {
                            Text(
                                "Email Address",
                                color = Color.Black,
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            )
                            OutlinedTextField(
                                value = email,
                                onValueChange = {},
                                placeholder = {
                                    Text(
                                        "Input Email",
                                        color = Color.Black.copy(alpha = 0.5f)
                                    )
                                },
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = Color.Black.copy(alpha = 0.3f),
                                    disabledTextColor = Color.Black
                                )
                            )
                        }
                    }

                    // Register Button
                    item {
                        Button(
                            onClick = {
                                // Validation
                                if (fullName.isBlank()) {
                                    nameError = "Name is required"
                                    return@Button
                                }

                                // ✅ Register event
                                isRegistering = true
                                scope.launch {
                                    try {
                                        Log.d(
                                            "AndroidCompact28",
                                            "========== STARTING REGISTRATION =========="
                                        )
                                        Log.d("AndroidCompact28", "Event ID: ${event.id}")
                                        Log.d("AndroidCompact28", "Event Title: ${event.title}")
                                        Log.d("AndroidCompact28", "User Name: $fullName")
                                        Log.d("AndroidCompact28", "User Email: $email")

                                        val result = eventRepository.registerEvent(
                                            eventId = event.id,
                                            eventTitle = event.title,
                                            eventDate = event.date,
                                            eventLocation = event.location,
                                            eventLatitude = event.latitude,
                                            eventLongitude = event.longitude,
                                            userName = fullName
                                        )

                                        result.onSuccess { registration ->
                                            Log.d("AndroidCompact28", "✅ Registration SUCCESS!")
                                            Log.d(
                                                "AndroidCompact28",
                                                "Registration Object: $registration"
                                            )
                                            Log.d(
                                                "AndroidCompact28",
                                                "==========================================="
                                            )

                                            Toast.makeText(
                                                context,
                                                "Registration successful!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onNavigateToSuccess()
                                        }.onFailure { error ->
                                            Log.e("AndroidCompact28", "❌ Registration FAILED!")
                                            Log.e("AndroidCompact28", "Error: ${error.message}")
                                            Log.e("AndroidCompact28", "Error Stack:", error)
                                            Log.d(
                                                "AndroidCompact28",
                                                "==========================================="
                                            )

                                            Toast.makeText(
                                                context,
                                                error.message ?: "Registration failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Log.e(
                                            "AndroidCompact28",
                                            "❌ EXCEPTION during registration!"
                                        )
                                        Log.e("AndroidCompact28", "Exception: ${e.message}")
                                        Log.e("AndroidCompact28", "Stack trace:", e)

                                        Toast.makeText(
                                            context,
                                            "Error: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } finally {
                                        isRegistering = false
                                    }
                                }
                            },
                            enabled = !isRegistering,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NormalBlue,
                                disabledContainerColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            if (isRegistering) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = "Register Now",
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
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
        }
    }
}