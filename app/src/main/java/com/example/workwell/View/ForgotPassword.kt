package com.example.workwell.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.workwell.R
import com.example.workwell.ViewModel.AuthState
import com.example.workwell.ViewModel.AuthViewModel


@Composable
fun ForgotPassword(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val scrollState = rememberScrollState()
    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current
    var email by rememberSaveable { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        authViewModel.resetErrorFields()
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Error && (authState as AuthState.Error).message.contains("enviado")) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "¡Correo Enviado!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Hemos enviado un enlace de recuperación a $email. Por favor, revisa tu bandeja de entrada y la carpeta de spam.",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack() // Volver al login tras el éxito
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F41BB))
                ) {
                    Text("Entendido")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val image = painterResource(R.drawable.login_screen)
        Image(
            painter = image,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alpha = 0.5F,
            modifier = Modifier.matchParentSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            InitText(
                loginText = "Recuperar Contraseña",
                welcomeText = "Introduce tu email para recibir un enlace de restablecimiento"
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                label = { Text("Email de usuario",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1F41BB),
                    unfocusedContainerColor = Color(0xFFF1F4FF),
                    focusedContainerColor = Color(0xFFF1F4FF)
                ),
                shape = RoundedCornerShape(15)
            )
            AddErrorText(authViewModel.emailError.value)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(
                        elevation = if (authState is AuthState.Loading) 0.dp else 6.dp,
                        shape = RoundedCornerShape(15)
                    ),
                onClick = { authViewModel.resetPassword(email) },
                enabled = authState !is AuthState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1F41BB),
                    disabledContainerColor = Color(0xFF1F41BB).copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(15)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (authState is AuthState.Loading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Enviando...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    } else {
                        Text(
                            text = "Enviar Enlace",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            authState?.let { state ->
                if (state is AuthState.Error) {
                    val isSuccessMessage = state.message.contains("enviado", ignoreCase = true)
                    val isNotEmpty = state.message.isNotBlank()

                    if (!isSuccessMessage && isNotEmpty) {
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = state.message,
                                color = Color.Red,
                                modifier = Modifier.padding(12.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Volver al inicio de sesión",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF494949),
                modifier = Modifier.clickable { navController.popBackStack() }
            )
        }
    }
}