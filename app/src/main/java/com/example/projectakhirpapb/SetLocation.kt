package com.example.dashboard2

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.repository.AuthRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.resume

private val NormalBlue = Color(0xFF3759B3)
private val TextGray = Color.Black.copy(alpha = 0.5f)
private val BorderGray = Color(0xFF1E1E1E)

@Composable
fun SetLocationScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    // ✅ States
    var searchText by remember { mutableStateOf("") }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedAddress by remember { mutableStateOf("") }
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Default location (Malang, East Java)
    val defaultLocation = LatLng(-7.9666, 112.6326)

    // ✅ Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 15f)
    }

    // ✅ Load existing location from Firebase
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val result = authRepository.getUserLocation()
                result.onSuccess { (address, lat, lng) ->
                    if (address.isNotEmpty()) {
                        selectedAddress = address
                        searchText = address
                        val location = LatLng(lat, lng)
                        markerPosition = location
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)
                    }
                }
            } catch (e: Exception) {
                // Ignore error, will use default location
            } finally {
                isLoading = false
            }
        }
    }

    // ✅ Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (hasLocationPermission) {
            getCurrentLocation(context) { location ->
                currentLocation = location
                if (markerPosition == null) {
                    markerPosition = location
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)

                    scope.launch {
                        val address = getAddressFromLatLng(context, location)
                        selectedAddress = address
                        searchText = address
                    }
                }
            }
        } else {
            Toast.makeText(
                context,
                "Location permission is required",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ✅ Request permission on launch
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        // ✅ Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(152.dp)
                .background(NormalBlue)
        ) {
            Image(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back Arrow",
                modifier = Modifier
                    .align(alignment = Alignment.TopStart)
                    .padding(start = 26.dp, top = 71.dp)
                    .size(size = 31.dp)
                    .rotate(degrees = 360f)
                    .clickable { onNavigateBack() }
            )
            Text(
                text = "Set Location",
                color = Color.White,
                lineHeight = 0.67.em,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter)
                    .padding(top = 70.dp)
            )
        }

        // ✅ Google Maps
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = NormalBlue
                )
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = hasLocationPermission
                    ),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
                        zoomControlsEnabled = true
                    ),
                    onMapClick = { latLng ->
                        markerPosition = latLng
                        scope.launch {
                            val address = getAddressFromLatLng(context, latLng)
                            selectedAddress = address
                            searchText = address
                        }
                    }
                ) {
                    markerPosition?.let { position ->
                        Marker(
                            state = MarkerState(position = position),
                            title = "Selected Location",
                            snippet = selectedAddress
                        )
                    }
                }
            }

            // ✅ Search & Save UI at bottom
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 26.dp, vertical = 30.dp)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(color = Color.White)
                    .border(
                        border = BorderStroke(1.dp, NormalBlue),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(all = 20.dp)
            ) {
                // ✅ Search Field
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = {
                        Text(
                            text = "Cari atau pilih di Peta",
                            color = TextGray,
                            style = TextStyle(fontSize = 12.sp)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_search),
                            contentDescription = "Search Icon",
                            modifier = Modifier.size(24.dp),
                            tint = NormalBlue
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderGray,
                        focusedBorderColor = NormalBlue,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    singleLine = true
                )

                // ✅ Search Button
                Button(
                    onClick = {
                        if (searchText.isNotBlank()) {
                            scope.launch {
                                val location = getLatLngFromAddress(context, searchText)
                                if (location != null) {
                                    markerPosition = location
                                    selectedAddress = searchText
                                    cameraPositionState.position =
                                        CameraPosition.fromLatLngZoom(location, 15f)
                                    Toast.makeText(
                                        context,
                                        "Location found!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Location not found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NormalBlue
                    ),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text(
                        text = "SEARCH LOCATION",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // ✅ Save Location Button
                Button(
                    onClick = {
                        markerPosition?.let { location ->
                            if (selectedAddress.isNotBlank()) {
                                isSaving = true
                                scope.launch {
                                    try {
                                        val result = authRepository.updateUserLocation(
                                            address = selectedAddress,
                                            latitude = location.latitude,
                                            longitude = location.longitude
                                        )

                                        result.onSuccess {
                                            onSaveSuccess()
                                        }.onFailure { error ->
                                            Toast.makeText(
                                                context,
                                                "Error: ${error.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            isSaving = false
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        isSaving = false
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please select a location first",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } ?: Toast.makeText(
                            context,
                            "Please select a location first",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        disabledContainerColor = Color.Gray
                    ),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "SAVE LOCATION",
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

// ✅ Helper: Get current location
private fun getCurrentLocation(
    context: Context,
    onLocationReceived: (LatLng) -> Unit
) {
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                onLocationReceived(LatLng(it.latitude, it.longitude))
            }
        }
    } catch (e: SecurityException) {
        e.printStackTrace()
    }
}

// ✅ Helper: Convert Address to LatLng (Geocoding) - NEW API
private suspend fun getLatLngFromAddress(
    context: Context,
    address: String
): LatLng? = suspendCancellableCoroutine { continuation ->
    try {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // ✅ API 33+ (Android 13+) - New API
            geocoder.getFromLocationName(address, 1) { addresses ->
                if (addresses.isNotEmpty()) {
                    val location = addresses[0]
                    continuation.resume(LatLng(location.latitude, location.longitude))
                } else {
                    continuation.resume(null)
                }
            }
        } else {
            // ✅ API < 33 - Old API (Deprecated but still works)
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocationName(address, 1)
            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                continuation.resume(LatLng(location.latitude, location.longitude))
            } else {
                continuation.resume(null)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        continuation.resume(null)
    }
}

// ✅ Helper: Convert LatLng to Address (Reverse Geocoding) - NEW API
private suspend fun getAddressFromLatLng(
    context: Context,
    latLng: LatLng
): String = suspendCancellableCoroutine { continuation ->
    try {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // ✅ API 33+ (Android 13+) - New API
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                if (addresses.isNotEmpty()) {
                    continuation.resume(addresses[0].getAddressLine(0) ?: "Unknown location")
                } else {
                    continuation.resume("Unknown location")
                }
            }
        } else {
            // ✅ API < 33 - Old API (Deprecated but still works)
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                continuation.resume(addresses[0].getAddressLine(0) ?: "Unknown location")
            } else {
                continuation.resume("Unknown location")
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        continuation.resume("Unknown location")
    }
}