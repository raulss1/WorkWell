package com.example.workwell.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.workwell.R
import com.example.workwell.ViewModel.AuthState
import com.example.workwell.ViewModel.AuthViewModel
import com.example.workwell.ui.theme.AzulBotonLogin
import com.example.workwell.ui.theme.AzulBotonSingUp
import com.example.workwell.ui.theme.AzulFondo
import com.example.workwell.ui.theme.AzulLogo

@Composable
fun FirstScreen(modifier: Modifier, navController: NavHostController, authViewModel: AuthViewModel) {
    val authState by authViewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        // Asegúrate de que usas 'Authenticated' aquí si así lo llamaste en el ViewModel
        if (authState is AuthState.Authenticated) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }

    }
    // Estructura de la pantalla: todo centrado verticalmente
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulFondo)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen
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
            Spacer(modifier = Modifier.height(40.dp))

            // Texto
            Text(
                text = "Inicia sesión para comenzar a crear tu rutina",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        // ---- Contenedor de botones (parte inferior) ----
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp), // Espacio entre botones
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp) // Separación del borde inferior
        ) {
            ButtonLogin(navController)
            ButtonSignUp(navController)
        }
    }
}

@Composable
fun ButtonLogin(navController: NavHostController) {
    OutlinedButton(
        onClick = {
            navController.navigate("login")
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AzulBotonLogin,
            contentColor = Color.White
        ),
        modifier = Modifier
            .height(48.dp)
            .width(172.dp)
    ) {
        Text(text = "Iniciar sesión")
    }
}

@Composable
fun ButtonSignUp(navController: NavHostController) {
    OutlinedButton(
        onClick = {
            navController.navigate("signup")
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AzulBotonSingUp,
            contentColor = AzulFondo
        ),
        modifier = Modifier
            .height(48.dp)
            .width(172.dp)
    ) {
        Text("Crear cuenta")
    }
}

/*
@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    WorkWellTheme {
        WelcomeView()
    }
}*/
