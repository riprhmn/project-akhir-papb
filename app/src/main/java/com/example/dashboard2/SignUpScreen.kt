package com.example.dashboard2

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.repository.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpSuccess: () -> Unit = {},
    onNavigateToSignIn: () -> Unit = {}
) {
    // ✅ FIREBASE REPOSITORY
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // STATE
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var termsAccepted by remember { mutableStateOf(false) }

    // ✅ LOADING & ERROR STATE
    var isLoading by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.cover),
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // White Card Container
        Box(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .offset(y = 193.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(shape = RoundedCornerShape(topStart = 45.dp, topEnd = 45.dp))
                .background(color = Color.White)
        )

        // Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 230.dp)
                .padding(horizontal = 32.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Title
            Text(
                text = "Get Started",
                color = Color(0xFF3250A1),
                textAlign = TextAlign.Center,
                lineHeight = 0.69.em,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )

            // Name Field
            SimpleTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null // Reset error saat user mengetik
                },
                label = "Name",
                isError = nameError != null,
                errorMessage = nameError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            SimpleTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null // Reset error saat user mengetik
                },
                label = "Email",
                isError = emailError != null,
                errorMessage = emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            PasswordField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null // Reset error saat user mengetik
                },
                isVisible = passwordVisible,
                onVisibilityChange = { passwordVisible = !passwordVisible },
                isError = passwordError != null,
                errorMessage = passwordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Terms & Conditions
            TermsRow(
                isChecked = termsAccepted,
                onCheckedChange = { termsAccepted = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ✅ SIGN UP BUTTON WITH FIREBASE
            Button(
                onClick = {
                    // Reset semua error
                    nameError = null
                    emailError = null
                    passwordError = null

                    // ✅ VALIDASI INPUT
                    var isValid = true

                    if (name.isBlank()) {
                        nameError = "Nama tidak boleh kosong"
                        isValid = false
                    }

                    if (email.isBlank()) {
                        emailError = "Email tidak boleh kosong"
                        isValid = false
                    } else if (!email.contains("@")) {
                        emailError = "Email harus menggunakan @"
                        isValid = false
                    }

                    if (password.isBlank()) {
                        passwordError = "Password tidak boleh kosong"
                        isValid = false
                    } else if (password.length < 6) {
                        passwordError = "Password minimal 6 karakter"
                        isValid = false
                    }

                    if (!termsAccepted) {
                        Toast.makeText(
                            context,
                            "Harap setujui Syarat & Ketentuan",
                            Toast.LENGTH_SHORT
                        ).show()
                        isValid = false
                    }

                    if (!isValid) return@Button

                    // ✅ PROSES SIGN UP KE FIREBASE
                    isLoading = true
                    scope.launch {
                        val result = authRepository.signUp(name, email, password)
                        isLoading = false

                        result.onSuccess { user ->
                            Toast.makeText(
                                context,
                                "Pendaftaran berhasil! Selamat datang ${user.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                            onSignUpSuccess()
                        }.onFailure { exception ->
                            Toast.makeText(
                                context,
                                exception.message ?: "Gagal mendaftar",
                                Toast.LENGTH_LONG
                            ).show()

                            // Set error ke field yang sesuai
                            when {
                                exception.message?.contains("Email sudah terdaftar") == true -> {
                                    emailError = "Email sudah terdaftar"
                                }
                                exception.message?.contains("email") == true -> {
                                    emailError = exception.message
                                }
                                exception.message?.contains("Password") == true -> {
                                    passwordError = exception.message
                                }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3759B3)
                ),
                enabled = !isLoading, // Disable button saat loading
                modifier = Modifier
                    .fillMaxWidth()
                    .height(47.dp)
            ) {
                if (isLoading) {
                    // ✅ SHOW LOADING INDICATOR
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = "Sign Up",
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            // Divider with "Sign Up With"
            Row(
                horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(114.dp)
                        .height(1.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                Text(
                    text = "Sign Up With",
                    color = Color.Black.copy(alpha = 0.5f),
                    lineHeight = 0.8.em,
                    style = TextStyle(fontSize = 15.sp)
                )
                Box(
                    modifier = Modifier
                        .width(114.dp)
                        .height(1.dp)
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Social Login Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(25.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SocialButtonFacebook(onClick = {
                    Toast.makeText(context, "Login dengan Facebook (Coming Soon)", Toast.LENGTH_SHORT).show()
                })
                SocialButtonGoogle(onClick = {
                    Toast.makeText(context, "Login dengan Google (Coming Soon)", Toast.LENGTH_SHORT).show()
                })
                SocialButtonTwitter(onClick = {
                    Toast.makeText(context, "Login dengan Twitter (Coming Soon)", Toast.LENGTH_SHORT).show()
                })
            }

            Spacer(modifier = Modifier.height(25.dp))

            // Navigate to Sign In
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account ?",
                    color = Color.Black.copy(alpha = 0.5f),
                    lineHeight = 1.em,
                    style = TextStyle(fontSize = 12.sp)
                )
                Text(
                    text = "Sign In",
                    color = Color(0xFF3759B3),
                    lineHeight = 1.em,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable {
                        onNavigateToSignIn()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    color = Color.Black.copy(alpha = 0.5f),
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3759B3),
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // ✅ ERROR MESSAGE
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onVisibilityChange: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = "Password",
                    color = Color.Black.copy(alpha = 0.5f),
                    style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                )
            },
            singleLine = true,
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val description = if (isVisible) "Hide password" else "Show password"
                IconButton(onClick = onVisibilityChange) {
                    if (isVisible) {
                        Icon(
                            painter = painterResource(id = R.drawable.close_eye),
                            contentDescription = description,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.iconeye),
                            contentDescription = description,
                            tint = Color(0xFF192851),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF3759B3),
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // ✅ ERROR MESSAGE
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun TermsRow(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
    ) {
        if (isChecked) {
            Icon(
                imageVector = Icons.Filled.CheckBox,
                contentDescription = "Checkbox Terms Checked",
                tint = Color(0xFF1877F2),
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.checkblank),
                contentDescription = "Checkbox Terms Unchecked",
                tint = Color(0xFF192851),
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black, fontSize = 10.sp)) {
                    append("Dengan Bergabung ke FinAi, saya setuju dengan ")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF1877F2),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append("Syarat & Ketentuan")
                }
                withStyle(style = SpanStyle(color = Color.Black, fontSize = 10.sp)) {
                    append(" serta ")
                }
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF1877F2),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append("Kebijakan Privasi")
                }
                withStyle(style = SpanStyle(color = Color.Black, fontSize = 10.sp)) {
                    append(" yang ada.")
                }
            },
            lineHeight = 12.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SocialButtonFacebook(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(75.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.facebook),
            contentDescription = "Facebook Login",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SocialButtonGoogle(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(75.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.google),
            contentDescription = "Google Login",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SocialButtonTwitter(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(75.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.twitter),
            contentDescription = "Twitter Login",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(widthDp = 412, heightDp = 917, showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    SignUpScreen()
}