package com.example.dashboard2

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.repository.AuthRepository
import com.example.dashboard2.data.model.EventRegistration
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch

private const val TAG = "DetailTicketScreen"
private val NormalBlue = Color(0xFF3759B3)
private val LightGray = Color(0xFFF6F6F6)
private val TextGray = Color(0xFF484C52)

@Composable
fun DetailTicketScreen(
    eventRegistration: EventRegistration,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    // State
    var userName by remember { mutableStateOf("Loading...") }
    var userEmail by remember { mutableStateOf("Loading...") }
    var userInstitution by remember { mutableStateOf("Loading...") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // ✅ Load user data and generate QR
    LaunchedEffect(eventRegistration) {
        scope.launch {
            try {
                Log.d(TAG, "========== LOADING TICKET DETAILS ==========")
                Log.d(TAG, "Event Registration:")
                Log.d(TAG, "  - Event ID: ${eventRegistration.eventId}")
                Log.d(TAG, "  - Event Title: ${eventRegistration.eventTitle}")
                Log.d(TAG, "  - User ID: ${eventRegistration.userId}")
                Log.d(TAG, "  - User Email: ${eventRegistration.userEmail}")
                Log.d(TAG, "  - User Name: ${eventRegistration.userName}")
                Log.d(TAG, "  - Status: ${eventRegistration.status}")
                Log.d(TAG, "  - Registered At: ${eventRegistration.registeredAt}")

                // ✅ Load current user data from Firebase
                Log.d(TAG, "Loading user data from AuthRepository...")
                val currentUser = authRepository.getCurrentUser()

                if (currentUser != null) {
                    userName = currentUser.name
                    userEmail = currentUser.email
                    userInstitution = currentUser.institution.ifEmpty { "Not Set" }

                    Log.d(TAG, "✅ User data loaded from Firebase:")
                    Log.d(TAG, "  - Name: $userName")
                    Log.d(TAG, "  - Email: $userEmail")
                    Log.d(TAG, "  - Institution: $userInstitution")
                } else {
                    // ✅ Fallback to event registration data
                    Log.w(TAG, "⚠️ No current user found, using registration data")
                    userName = eventRegistration.userName
                    userEmail = eventRegistration.userEmail
                    userInstitution = "Not Set"

                    Log.d(TAG, "Using fallback data:")
                    Log.d(TAG, "  - Name: $userName")
                    Log.d(TAG, "  - Email: $userEmail")
                }

                // ✅ Generate QR Code with correct format: userId_eventId
                val qrContent = "${eventRegistration.userId}_${eventRegistration.eventId}"

                Log.d(TAG, "Generating QR Code:")
                Log.d(TAG, "  - QR Content: $qrContent")
                Log.d(TAG, "  - Format: EVENT:eventId|USER:email|TICKET:timestamp")
                Log.d(TAG, "  - Event ID: ${eventRegistration.eventId}")
                Log.d(TAG, "  - User Email: ${eventRegistration.userEmail}")
                Log.d(TAG, "  - Timestamp: ${eventRegistration.registeredAt}")

                qrBitmap = generateQRCode(
                    content = qrContent,
                    size = 512
                )

                if (qrBitmap != null) {
                    Log.d(TAG, "✅ QR Code generated successfully")
                } else {
                    Log.e(TAG, "❌ Failed to generate QR Code")
                }

                Log.d(TAG, "============================================")

            } catch (e: Exception) {
                Log.e(TAG, "❌ ERROR loading ticket details", e)
                e.printStackTrace()

                // ✅ Set fallback values on error
                userName = eventRegistration.userName.ifEmpty { "Unknown" }
                userEmail = eventRegistration.userEmail.ifEmpty { "No email" }
                userInstitution = "Not Set"

                android.widget.Toast.makeText(
                    context,
                    "Error loading user data: ${e.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
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
            text = "Ticket Detail",
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = NormalBlue)
                        Text(
                            text = "Loading ticket details...",
                            color = TextGray,
                            style = TextStyle(fontSize = 14.sp)
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 20.dp)
                ) {
                    // ✅ Event Header Card
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                            .clip(shape = RoundedCornerShape(15.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    0f to Color(0xFF131F3F),
                                    1f to Color(0xFF3251A5),
                                    start = Offset(0f, 0f),
                                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 20.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = eventRegistration.eventTitle,
                                color = Color.White,
                                maxLines = 2,
                                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "GENERAL ADMISSION",
                                color = Color.White.copy(alpha = 0.8f),
                                style = TextStyle(fontSize = 14.sp)
                            )

                            // ✅ Status Badge
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when (eventRegistration.status) {
                                            "completed" -> Color(0xFF4CAF50)
                                            "registered" -> Color(0xFFFFC107)
                                            else -> Color.Gray
                                        }
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when (eventRegistration.status) {
                                        "completed" -> "✓ Completed"
                                        "registered" -> "Registered"
                                        else -> eventRegistration.status
                                    },
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ticketprof),
                            contentDescription = "Ticket Icon",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // ✅ Scrollable Content
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // Date & Time Card
                        item {
                            DetailCard(
                                iconRes = R.drawable.ic_calendar,
                                iconBgColor = Color(0xFF6200EE),
                                label = "Date & Time",
                                line1 = eventRegistration.eventDate,
                                line2 = "09:00 - 12:00 WIB"
                            )
                        }

                        // Location Card
                        item {
                            DetailCard(
                                iconRes = R.drawable.locprofil,
                                iconBgColor = Color(0xFFEB383B),
                                label = "Location",
                                line1 = eventRegistration.eventLocation,
                                line2 = "Malang, East Java"
                            )
                        }

                        // Divider
                        item {
                            HorizontalDivider(
                                color = TextGray.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            )
                        }

                        // Attendee Information Header
                        item {
                            Text(
                                text = "Attendee Information",
                                color = Color.Black,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 5.dp)
                            )
                        }

                        // ✅ User Profile (with InitialAvatar)
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                InitialAvatar(
                                    name = userName,
                                    size = 50.dp,
                                    backgroundColor = getColorFromName(userName),
                                    textColor = Color.White,
                                    borderColor = Color.Gray,
                                    borderWidth = 1.dp
                                )

                                Spacer(Modifier.width(15.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = userName,
                                        color = Color.Black,
                                        style = TextStyle(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = userInstitution,
                                        color = TextGray,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                    )
                                }
                            }
                        }

                        // Email
                        item {
                            DetailRow(
                                iconRes = R.drawable.ic_email,
                                text = userEmail
                            )
                        }

                        // Ticket ID
                        item {
                            DetailRow(
                                iconRes = R.drawable.ic_hastag,
                                text = "TICKET-${eventRegistration.registeredAt}"
                            )
                        }

                        // Divider
                        item {
                            HorizontalDivider(
                                color = TextGray.copy(alpha = 0.3f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                            )
                        }

                        // ✅ QR Code Section
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .clip(shape = RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .border(
                                        border = BorderStroke(2.dp, Color.LightGray),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Event Check-in QR Code",
                                    color = Color.Black,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                if (qrBitmap != null) {
                                    Image(
                                        bitmap = qrBitmap!!.asImageBitmap(),
                                        contentDescription = "QR Code",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier
                                            .size(180.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White)
                                    )

                                    Spacer(Modifier.height(10.dp))

                                    Text(
                                        text = "Show this QR code at the entrance",
                                        color = TextGray,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )

                                    // ✅ Debug Info (can be removed in production)
                                    Text(
                                        text = "QR: ${eventRegistration.userId.take(8)}_${eventRegistration.eventId}",
                                        color = TextGray.copy(alpha = 0.5f),
                                        style = TextStyle(fontSize = 10.sp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                } else {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(180.dp),
                                        color = NormalBlue
                                    )
                                    Text(
                                        text = "Generating QR Code...",
                                        color = TextGray,
                                        style = TextStyle(fontSize = 12.sp),
                                        modifier = Modifier.padding(top = 10.dp)
                                    )
                                }
                            }
                        }

                        // ✅ Additional Info
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFF9E6)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "ℹ️",
                                        style = TextStyle(fontSize = 24.sp),
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                    Text(
                                        text = "Please arrive 15 minutes before the event starts. Bring a valid ID for verification.",
                                        color = Color(0xFF856404),
                                        style = TextStyle(fontSize = 13.sp),
                                        lineHeight = 1.4.em
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ✅ Generate QR Code Function with Logging
fun generateQRCode(content: String, size: Int): Bitmap? {
    return try {
        Log.d(TAG, "Generating QR Code Bitmap:")
        Log.d(TAG, "  - Content: $content")
        Log.d(TAG, "  - Size: ${size}x${size}")

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) AndroidColor.BLACK else AndroidColor.WHITE
                )
            }
        }

        Log.d(TAG, "✅ QR Code bitmap created successfully")
        bitmap

    } catch (e: Exception) {
        Log.e(TAG, "❌ Error generating QR code", e)
        null
    }
}

// ✅ Detail Card Component
@Composable
fun DetailCard(
    iconRes: Int,
    iconBgColor: Color,
    label: String,
    line1: String,
    line2: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(18.dp))
            .background(color = Color.White)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .background(iconBgColor)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$label Icon",
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier.size(24.dp)
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                color = Color.Black.copy(alpha = 0.7f),
                style = TextStyle(fontSize = 14.sp)
            )
            Text(
                text = line1,
                color = Color.Black,
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = line2,
                color = Color.Black.copy(alpha = 0.7f),
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}

// ✅ Detail Row Component
@Composable
fun DetailRow(
    iconRes: Int,
    text: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "$text icon",
            colorFilter = ColorFilter.tint(TextGray),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = text,
            color = Color.Black,
            style = TextStyle(fontSize = 14.sp)
        )
    }
}