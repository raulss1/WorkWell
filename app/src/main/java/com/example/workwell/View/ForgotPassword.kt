package com.example.workwell.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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

    LaunchedEffect(key1 = Unit) {
        authViewModel.resetErrorFields()
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
                label = { Text("Email Corporativo",
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
                    .height(46.dp)
                    .shadow(4.dp, RoundedCornerShape(15)),
                onClick = {
                    authViewModel.resetPassword(email)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F41BB)),
                shape = RoundedCornerShape(15)
            ) {
                Text(text = "Enviar Enlace", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            authState?.let {
                if (it is AuthState.Error) {
                    Text(
                        text = it.message,
                        color = if (it.message.contains("enviado")) Color(0xFF2E7D32) else Color.Red,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Volver al inicio de sesión",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF494949),
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )
        }
    }
}