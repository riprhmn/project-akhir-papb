package com.example.dashboard2

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.dashboard2.data.repository.AuthRepository
import kotlinx.coroutines.launch

private val NormalBlue = Color(0xff3759b3)
private val LightGray = Color(0xfff6f6f6)
private val DarkBlue = Color(0xff192851)
private val FieldGray = Color(0xffe4e4e4)
private val TextGray = Color.Black.copy(alpha = 0.5f)

private const val TAG = "EDIT_PROFILE_PAGE"

@Composable
fun EditProfilePage(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit = {},
    onSaveComplete: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    // State untuk data user
    var name by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var currentPhotoUrl by remember { mutableStateOf<String?>(null) } // ✅ Current photo dari Firebase
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) } // ✅ Newly selected photo

    // State untuk UI
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Validation errors
    var nameError by remember { mutableStateOf<String?>(null) }
    var institutionError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // ✅ Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d(TAG, "========== IMAGE PICKER RESULT ==========")
        if (uri != null) {
            selectedImageUri = uri
            Log.d(TAG, "✓ Image selected: $uri")
            Toast.makeText(context, "Foto berhasil dipilih", Toast.LENGTH_SHORT).show()
        } else {
            Log.w(TAG, "⚠ No image selected")
        }
        Log.d(TAG, "==========================================")
    }

    // ✅ Load user data dari Firebase
    LaunchedEffect(Unit) {
        Log.d(TAG, "========== LOADING USER DATA ==========")
        scope.launch {
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    name = currentUser.name
                    email = currentUser.email
                    institution = currentUser.institution
                    currentPhotoUrl = currentUser.photoUrl // ✅ Load current photo
                    Log.d(TAG, "✓ User data loaded")
                    Log.d(TAG, "Photo URL: ${currentPhotoUrl ?: "NULL"}")
                } else {
                    Toast.makeText(
                        context,
                        "Silakan login terlebih dahulu",
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateBack()
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error loading profile", e)
                Toast.makeText(
                    context,
                    "Error loading profile: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                isLoading = false
                Log.d(TAG, "========================================")
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LightGray)
            .verticalScroll(rememberScrollState())
    ) {
        // ===== Header =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(152.dp)
                .background(NormalBlue)
                .padding(top = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = {
                    Log.d(TAG, "Back button clicked")
                    onNavigateBack()
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 26.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(31.dp).rotate(degrees = 360f)
                )
            }

            Text(
                text = "Edit Profile",
                color = Color.White,
                lineHeight = 0.67.em,
                style = MaterialTheme.typography.headlineSmall
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NormalBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                // ===== Profile Photo dengan Image Picker =====
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clickable {
                            Log.d(TAG, "Profile photo clicked - launching image picker")
                            imagePickerLauncher.launch("image/*")
                        }
                ) {
                    // ✅ Display image dengan priority
                    when {
                        // Priority 1: Newly selected image
                        selectedImageUri != null -> {
                            Log.d(TAG, "Rendering: NEWLY SELECTED image")
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(selectedImageUri)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Selected Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .shadow(elevation = 4.dp, shape = CircleShape)
                                    .clip(CircleShape)
                                    .border(BorderStroke(5.dp, DarkBlue), CircleShape)
                            )
                        }
                        // Priority 2: Current photo from Firebase
                        !currentPhotoUrl.isNullOrEmpty() -> {
                            Log.d(TAG, "Rendering: CURRENT Firebase photo")
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(currentPhotoUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Current Profile Picture",
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.fotoprofile),
                                error = painterResource(id = R.drawable.fotoprofile),
                                modifier = Modifier
                                    .size(100.dp)
                                    .shadow(elevation = 4.dp, shape = CircleShape)
                            )
                        }
                        // Priority 3: Initial Avatar
                        else -> {
                            Log.d(TAG, "Rendering: INITIAL AVATAR")
                            InitialAvatar(
                                name = name,
                                size = 100.dp,
                                backgroundColor = getColorFromName(name),
                                textColor = Color.White,
                                borderColor = DarkBlue,
                                borderWidth = 5.dp
                            )
                        }
                    }

                    // Camera icon overlay
                    Image(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Edit Profile Picture",
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(37.dp)
                    )
                }

                // ===== Form Fields =====
                Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    EditProfileField(
                        label = "Name",
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = null
                            Log.d(TAG, "Name changed: $it")
                        },
                        isError = nameError != null,
                        errorMessage = nameError
                    )

                    EditProfileField(
                        label = "Institution",
                        value = institution,
                        onValueChange = {
                            institution = it
                            institutionError = null
                            Log.d(TAG, "Institution changed: $it")
                        },
                        isError = institutionError != null,
                        errorMessage = institutionError
                    )

                    EditProfileField(
                        label = "Email",
                        value = email,
                        onValueChange = {},
                        enabled = false
                    )

                    EditPasswordField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                            Log.d(TAG, "Password changed (length: ${it.length})")
                        },
                        isError = passwordError != null,
                        errorMessage = passwordError
                    )
                }
            }

            // ===== Save Button =====
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(bottom = 28.dp, start = 40.dp, end = 40.dp)
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(10.dp))
                    .background(color = if (isSaving) Color.Gray else NormalBlue)
                    .clickable(enabled = !isSaving) {
                        Log.d(TAG, "========== SAVE BUTTON CLICKED ==========")
                        Log.d(TAG, "Name: $name")
                        Log.d(TAG, "Institution: $institution")
                        Log.d(TAG, "Password: ${if (password.isNotBlank()) "<SET>" else "<EMPTY>"}")
                        Log.d(TAG, "Selected Image URI: ${selectedImageUri ?: "NULL"}")

                        scope.launch {
                            // Reset errors
                            nameError = null
                            institutionError = null
                            passwordError = null

                            var isValid = true

                            // Validation
                            if (name.isBlank()) {
                                nameError = "Nama tidak boleh kosong"
                                isValid = false
                                Log.e(TAG, "Validation FAILED: Name is blank")
                            }

                            if (institution.isBlank()) {
                                institutionError = "Institution tidak boleh kosong"
                                isValid = false
                                Log.e(TAG, "Validation FAILED: Institution is blank")
                            }

                            if (password.isNotBlank() && password.length < 6) {
                                passwordError = "Password minimal 6 karakter"
                                isValid = false
                                Log.e(TAG, "Validation FAILED: Password too short")
                            }

                            if (!isValid) {
                                Log.w(TAG, "⚠ Validation failed")
                                return@launch
                            }

                            Log.d(TAG, "✓ Validation passed - starting save...")
                            isSaving = true

                            try {
                                // ✅ Update WITH photo URI
                                val success = authRepository.updateProfile(
                                    name = name,
                                    institution = institution,
                                    password = if (password.isNotBlank()) password else null,
                                    photoUri = selectedImageUri // ✅ Include photo
                                )

                                Log.d(TAG, "Update result: ${if (success) "SUCCESS" else "FAILED"}")

                                if (success) {
                                    Log.d(TAG, "✓✓✓ PROFILE UPDATE SUCCESSFUL ✓✓✓")
                                    Toast.makeText(
                                        context,
                                        "Profil berhasil diupdate!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onSaveComplete()
                                } else {
                                    Log.e(TAG, "❌ PROFILE UPDATE FAILED")
                                    Toast.makeText(
                                        context,
                                        "Gagal update profil",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "❌ EXCEPTION DURING SAVE", e)
                                Toast.makeText(
                                    context,
                                    "Error: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } finally {
                                isSaving = false
                                Log.d(TAG, "=========================================")
                            }
                        }
                    }
                    .padding(vertical = 15.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isSaving) "Saving..." else "Save Changes",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.1.em,
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = Color.Black,
            lineHeight = 1.em,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 10.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (enabled) FieldGray else Color.LightGray,
                unfocusedContainerColor = if (enabled) FieldGray else Color.LightGray,
                disabledContainerColor = Color.LightGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = if (enabled) TextGray else Color.Gray,
                disabledTextColor = Color.Gray,
                focusedBorderColor = if (isError) Color.Red else Color.Transparent,
                unfocusedBorderColor = if (isError) Color.Red else Color.Transparent,
                disabledBorderColor = Color.Transparent
            ),
            isError = isError,
            singleLine = true
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun EditPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Password",
            color = Color.Black,
            lineHeight = 1.em,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 10.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FieldGray,
                unfocusedContainerColor = FieldGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = TextGray,
                focusedBorderColor = if (isError) Color.Red else Color.Transparent,
                unfocusedBorderColor = if (isError) Color.Red else Color.Transparent
            ),
            textStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
            singleLine = true,
            visualTransformation = if (isPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            placeholder = {
                Text(
                    text = "Kosongkan jika tidak ingin ganti",
                    style = TextStyle(fontSize = 13.sp),
                    color = Color.Gray
                )
            },
            trailingIcon = {
                val imageRes = if (isPasswordVisible) {
                    R.drawable.close_eye
                } else {
                    R.drawable.iconeye
                }
                val description = if (isPasswordVisible) {
                    "Hide password"
                } else {
                    "Show password"
                }

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        painter = painterResource(id = imageRes),
                        contentDescription = description,
                        modifier = Modifier.size(20.dp),
                        tint = TextGray
                    )
                }
            },
            isError = isError
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}