package com.example.dashboard2

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.dashboard2.data.repository.AuthRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onSignInSuccess: () -> Unit = {},
    onNavigateToSignUp: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    // ✅ FIREBASE REPOSITORY
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // STATE
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // ✅ LOADING & ERROR STATE
    var isLoading by remember { mutableStateOf(false) }
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Title
                Text(
                    text = "Welcome back",
                    color = Color(0xFF3250A1),
                    textAlign = TextAlign.Center,
                    lineHeight = 0.69.em,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(top = 40.dp)
                )

                Spacer(modifier = Modifier.height(40.dp))

                // ✅ EMAIL FIELD WITH ERROR
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null // Reset error saat user mengetik
                        },
                        label = {
                            Text(
                                "Email",
                                color = Color.Black.copy(alpha = 0.5f),
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        isError = emailError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3759B3),
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.49f),
                            errorBorderColor = Color.Red,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        singleLine = true
                    )

                    // ✅ EMAIL ERROR MESSAGE
                    if (emailError != null) {
                        Text(
                            text = emailError!!,
                            color = Color.Red,
                            style = TextStyle(fontSize = 12.sp),
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ✅ PASSWORD FIELD WITH ERROR
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null // Reset error saat user mengetik
                        },
                        label = {
                            Text(
                                "Password",
                                color = Color.Black.copy(alpha = 0.5f),
                                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        isError = passwordError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3759B3),
                            unfocusedBorderColor = Color.Black.copy(alpha = 0.49f),
                            errorBorderColor = Color.Red,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        ),
                        textStyle = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val description = if (passwordVisible) "Hide password" else "Show password"
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                if (passwordVisible) {
                                    Image(
                                        painter = painterResource(id = R.drawable.close_eye),
                                        contentDescription = description,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.iconeye),
                                        contentDescription = description,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    )

                    // ✅ PASSWORD ERROR MESSAGE
                    if (passwordError != null) {
                        Text(
                            text = passwordError!!,
                            color = Color.Red,
                            style = TextStyle(fontSize = 12.sp),
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Forgot Password Link
                Text(
                    text = "Forget Password ?",
                    color = Color(0xFF3759B3),
                    lineHeight = 1.em,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onForgotPassword() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ✅ SIGN IN BUTTON WITH FIREBASE
                Button(
                    onClick = {
                        // Reset semua error
                        emailError = null
                        passwordError = null

                        // ✅ VALIDASI INPUT
                        var isValid = true

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
                        }

                        if (!isValid) return@Button

                        // ✅ PROSES SIGN IN KE FIREBASE
                        isLoading = true
                        scope.launch {
                            val result = authRepository.signIn(email, password)
                            isLoading = false

                            result.onSuccess { user ->
                                Toast.makeText(
                                    context,
                                    "Selamat datang kembali, ${user.name}!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onSignInSuccess()
                            }.onFailure { exception ->
                                // Reset password field untuk keamanan
                                password = ""

                                val errorMsg = exception.message ?: "Gagal login"
                                Toast.makeText(
                                    context,
                                    errorMsg,
                                    Toast.LENGTH_LONG
                                ).show()

                                // ✅ SET ERROR KE FIELD YANG SESUAI
                                when {
                                    errorMsg.contains("email", ignoreCase = true) -> {
                                        emailError = errorMsg
                                    }
                                    errorMsg.contains("password", ignoreCase = true) -> {
                                        passwordError = errorMsg
                                    }
                                    errorMsg.contains("salah", ignoreCase = true) -> {
                                        // Jika email/password salah, set error ke kedua field
                                        emailError = "Email atau password salah"
                                        passwordError = "Email atau password salah"
                                    }
                                    else -> {
                                        passwordError = errorMsg
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3759B3)
                    ),
                    enabled = !isLoading // Disable button saat loading
                ) {
                    if (isLoading) {
                        // ✅ SHOW LOADING INDICATOR
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Sign In",
                            color = Color.White,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                // Divider "Sign In With"
                Row(
                    horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                    Text(
                        text = "Sign In With",
                        color = Color.Black.copy(alpha = 0.5f),
                        lineHeight = 0.8.em,
                        style = TextStyle(fontSize = 15.sp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Social Login Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth()
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

                Spacer(modifier = Modifier.height(40.dp))

                // Navigate to Sign Up
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Don't have an account ?",
                        color = Color.Black.copy(alpha = 0.5f),
                        lineHeight = 1.em,
                        style = TextStyle(fontSize = 12.sp)
                    )
                    Text(
                        text = "Sign Up",
                        color = Color(0xFF3759B3),
                        lineHeight = 1.em,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.clickable {
                            onNavigateToSignUp()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Preview(widthDp = 412, heightDp = 917, showBackground = true)
@Composable
private fun SignInScreenPreview() {
    SignInScreen()
}