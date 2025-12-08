package com.example.workwell.View

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.workwell.ui.theme.WorkWellTheme


@Composable
fun Login(modifier: Modifier = Modifier, navController: NavHostController, authViewModel: AuthViewModel) {

    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        // Asegúrate de que usas 'Authenticated' aquí si así lo llamaste en el ViewModel
        if (authState is AuthState.Authenticated) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
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
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            InitText(stringResource(R.string.logintitle), stringResource(R.string.loginsubtitle))
            Spacer(modifier = Modifier.height(50.dp))
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
        Text(
            text = loginText,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F41BB),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = welcomeText,
            fontSize = 20.sp,
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
    var customTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = customBlue,
        unfocusedContainerColor = customBackground,
        focusedContainerColor = customBackground,
        focusedTextColor = customBlue
    )

    Column (
        modifier = Modifier.width(350.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF626262)
                )},
            modifier = Modifier.fillMaxWidth(),
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = passwd,
            onValueChange = { passwd = it },
            label = { Text("Contraseña",
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF626262)
            ) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            colors = customTextFieldColors,
            shape = RoundedCornerShape(15)
        )
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
    val context = LocalContext.current

    Column(
        modifier = Modifier.width(350.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Has olvidado tu contraseña?",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F41BB),
            modifier = Modifier
                .width(250.dp)
                .align(Alignment.End)
                .clickable {
                    /*
                    *val intent = Intent(context, ForgotPassword::class.java)
                    context.startActivity(intent)
                    * */
                },
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier
                .width(350.dp)
                .height(60.dp)
                .shadow(
                    elevation = 15.dp,
                    shape = RoundedCornerShape(15.dp),
                    spotColor = Color(0xFF1F41BB),
                    ambientColor = Color(0xFF1F41BB)
                ),
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
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Crear nueva cuenta",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF494949),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(150.dp)
                .clickable {
                    navController.navigate("signup")
                }
        )
    }
}

/*
@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    WorkWellTheme {
        //Login(modifier, navController, authViewModel)
    }
}*/