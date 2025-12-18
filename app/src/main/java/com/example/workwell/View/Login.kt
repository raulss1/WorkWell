package com.example.workwell.View

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.example.workwell.R
import com.example.workwell.ViewModel.AuthState
import com.example.workwell.ViewModel.AuthViewModel
import com.example.workwell.ui.theme.AzulLogo
import com.example.workwell.ui.theme.WorkWellTheme


@Composable
fun Login(modifier: Modifier = Modifier, navController: NavHostController, authViewModel: AuthViewModel) {
    val scrollState = rememberScrollState()
    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        authViewModel.resetErrorFields()
    }
    LaunchedEffect(authState) {
        // Asegúrate de que usas 'Authenticated' aquí si así lo llamaste en el ViewModel
        if (authState is AuthState.Authenticated) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
            Toast.makeText(context, "Login correcto", Toast.LENGTH_SHORT).show()
        }

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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            InitText(stringResource(R.string.logintitle), stringResource(R.string.loginsubtitle))
            Spacer(modifier = Modifier.height(20.dp))
            AddLoginForm(navController = navController, authViewModel = authViewModel)
        }
    }
}

@Composable
fun InitText(loginText: String, welcomeText: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de la app",
            modifier = Modifier
                .size(180.dp)
                .background(AzulLogo, CircleShape)
                .clip(CircleShape)
                .padding(5.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = loginText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F41BB),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = welcomeText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun AddLoginForm(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    var email: String by remember { mutableStateOf("") }
    var passwd: String by remember { mutableStateOf("") }
    var customBlue = Color(0xFF1F41BB)
    var customBackground = Color(0xFFF1F4FF)
    val unfocusedIconColor = Color(0xFF6A6A6A)
    val unfocusedLabelColor = Color(0xFF626262)
    var customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = customBlue,
        unfocusedContainerColor = customBackground,
        focusedContainerColor = customBackground,

        focusedLeadingIconColor = customBlue,
        unfocusedLeadingIconColor = unfocusedIconColor,

        focusedLabelColor = customBlue,
        unfocusedLabelColor = unfocusedLabelColor
    )

    Column (
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            label = { Text("Email",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Introduce el email",
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        AddErrorText(authViewModel.emailError.value)

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = passwd,
            onValueChange = { passwd = it },
            label = { Text("Contraseña",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )},
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Password,
                    contentDescription = "Introduce la contraseña",
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        AddErrorText(authViewModel.passwordError.value)

        Spacer(modifier = Modifier.height(20.dp))
        AddButton(
            navController = navController,
            authViewModel = authViewModel,
            email = email,
            password = passwd
        )
    }
}


@Composable
fun AddButton(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    email: String,
    password: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val authState by authViewModel.authState.observeAsState()


    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Has olvidado tu contraseña?",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F41BB),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .clickable {
                    if (email.isEmpty()) {
                        authViewModel.emailError.value = "Introduce tu email"
                    } else {
                        authViewModel.resetPassword(email)
                    }
                },
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(15),
                    spotColor = Color.Black.copy(alpha = 0.5f),
                    ambientColor = Color.Black.copy(alpha = 0.5f)
                )
                .fillMaxWidth()
                .height(46.dp),
            onClick = {
                authViewModel.login(email, password)
            },
            interactionSource = interactionSource,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPressed) Color(0xFF163099) else Color(0xFF1F41BB)
            ),
            shape = RoundedCornerShape(15)
        ) {
            Text(text = "Iniciar sesión",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
        authState?.let {
            if (it is AuthState.Error) {
                Text(
                    text = it.message,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Crear nueva cuenta",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF494949),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("signup")
                }
        )
    }
}

@Composable
fun AddErrorText(text: String) {
    if (text.isEmpty()) return
    Text(
        text = text,
        color = Color.Red,
        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
    )
}

/*
@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    WorkWellTheme {
        //Login(modifier, navController, authViewModel)
    }
}*/