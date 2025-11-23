package com.example.dashboard2

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.dashboard2.data.repository.EventRepository
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.util.Log

private val NormalBlue = Color(0xFF3759B3)

@Composable
fun QRScannerScreen(
    onNavigateBack: () -> Unit,
    onScanSuccess: (String) -> Unit = {},
    isStaffMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val eventRepository = remember { EventRepository() }

    var hasCameraPermission by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var scannerMode by remember { mutableStateOf(isStaffMode) }

    // ✅ Camera Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    // ✅ Request Permission on Launch
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(modifier = modifier.fillMaxSize().background(color = Color.Black)) {
        if (hasCameraPermission) {
            CameraPreview(
                onQRCodeScanned = { qrContent ->
                    if (!isProcessing) {
                        isProcessing = true

                        scope.launch {
                            try {
                                Log.d("QRScanner", "========== QR CODE SCANNED ==========")
                                Log.d("QRScanner", "QR Content: '$qrContent'")

                                // ✅ Parse QR Code: userId_eventId
                                val parts = qrContent.split("_", limit = 2)

                                if (parts.size < 2) {
                                    Log.e("QRScanner", "❌ Invalid QR format")
                                    Toast.makeText(
                                        context,
                                        "Format QR Code tidak valid",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isProcessing = false
                                    return@launch
                                }

                                val qrUserId = parts[0]
                                val eventId = parts[1]

                                Log.d("QRScanner", "Parsed:")
                                Log.d("QRScanner", "  User ID: '$qrUserId'")
                                Log.d("QRScanner", "  Event ID: '$eventId'")
                                Log.d("QRScanner", "======================================")

                                // ✅ Update event status (self check-in)
                                val result = eventRepository.markEventAsCompleted(qrContent)

                                result.onSuccess {
                                    Toast.makeText(
                                        context,
                                        "✓ Check-in berhasil! Event ditandai selesai",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    delay(1500)
                                    onScanSuccess(qrContent)
                                    onNavigateBack()
                                }.onFailure { error ->
                                    Toast.makeText(
                                        context,
                                        "Error: ${error.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    isProcessing = false
                                }

                            } catch (e: Exception) {
                                Log.e("QRScanner", "❌ Error parsing QR", e)
                                Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isProcessing = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // ✅ Overlay with mode toggle
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with back button and mode toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f))
                            .clickable { onNavigateBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // ✅ Mode Toggle (optional)
                    if (isStaffMode) {
                        Card(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { scannerMode = !scannerMode },
                            colors = CardDefaults.cardColors(
                                containerColor = if (scannerMode) {
                                    Color(0xFFFFA726)
                                } else {
                                    Color.White.copy(alpha = 0.3f)
                                }
                            )
                        ) {
                            Text(
                                text = if (scannerMode) "Staff Mode" else "Self Mode",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (scannerMode) Color.White else Color.Black
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // ✅ Scan Frame
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .padding(4.dp)
                ) {
                    // Corner indicators
                    ScannerCorners()
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Instructions
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ticketprof),
                            contentDescription = "QR Code",
                            tint = NormalBlue,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isProcessing) "Processing..." else "Scan Event Ticket",
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Align the QR code within the frame to scan",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.Gray
                            ),
                            textAlign = TextAlign.Center
                        )

                        if (isProcessing) {
                            Spacer(modifier = Modifier.height(16.dp))
                            CircularProgressIndicator(
                                color = NormalBlue,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        } else {
            // ✅ No Permission State
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ticketprof),
                    contentDescription = "Camera",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Camera Permission Required",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Please grant camera permission to scan QR codes",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = NormalBlue)
                ) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

// ✅ Scanner Corner Indicators
@Composable
fun ScannerCorners() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Top-left corner
        Canvas(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(40.dp)
        ) {
            drawLine(
                color = androidx.compose.ui.graphics.Color.White,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 8f
            )
            drawLine(
                color = androidx.compose.ui.graphics.Color.White,
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = 8f
            )
        }

        // Top-right corner
        Canvas(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(40.dp)
        ) {
            drawLine(
                color = androidx.compose.ui.graphics.Color.White,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 8f
            )
            drawLine(
                color = androidx.compose.ui.graphics.Color.White,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = 8f
            )
        }

        // Bottom-left corner
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(40.dp)
        ) {
            drawLine(
                color = androidx.compose.ui.graphics.Color.White,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 8f
            )
            drawLine(
                color = androidx.compose.ui.graphics.Color.White,
                start = Offset(0f, 0f),
                end = Offset(0f, size.height),
                strokeWidth = 8f
            )
        }

        // Bottom-right corner
        Canvas(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(40.dp)
        ) {
            drawLine(
                color = androidx.compose.ui.graphics.Color.White,
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 8f
            )
            drawLine(
                color = androidx.compose.ui.graphics.Color.White,
                start = Offset(size.width, 0f),
                end = Offset(size.width, size.height),
                strokeWidth = 8f
            )
        }
    }
}

// ✅ Camera Preview with QR Scanner
@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
fun CameraPreview(
    onQRCodeScanned: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor) { imageProxy ->
                            processImageProxy(barcodeScanner, imageProxy, onQRCodeScanned)
                        }
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier
    )

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

@androidx.camera.core.ExperimentalGetImage
private fun processImageProxy(
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    imageProxy: ImageProxy,
    onQRCodeScanned: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    if (barcode.valueType == Barcode.TYPE_TEXT || barcode.valueType == Barcode.TYPE_URL) {
                        barcode.rawValue?.let { qrContent ->
                            onQRCodeScanned(qrContent)
                        }
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}