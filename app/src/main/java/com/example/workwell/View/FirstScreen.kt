package com.example.workwell.View

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workwell.ui.theme.WorkWellTheme
import com.example.workwell.R
import com.example.workwell.ui.theme.AzulBotonLogin
import com.example.workwell.ui.theme.AzulBotonSingUp
import com.example.workwell.ui.theme.AzulFondo

class FirstScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkWellTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WelcomeView()
                }
            }
        }
    }
}

@Composable
fun WelcomeView() {
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
                    .padding(bottom = 24.dp),
                contentScale = ContentScale.Crop
            )

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
            BotonLogin()
            BotonSignUp()
        }
    }
}

@Composable
fun BotonLogin() {
    OutlinedButton(
        onClick = {
            // TODO: Navegar a pantalla de login
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AzulBotonLogin,
            contentColor = Color.White
        ),
        modifier = Modifier
            .height(48.dp)
            .width(172.dp)
    ) {
        Text(text = "Log In")
    }
}

@Composable
fun BotonSignUp() {
    OutlinedButton(
        onClick = {
            // TODO: Navegar a pantalla de registro
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AzulBotonSingUp,
            contentColor = AzulFondo
        ),
        modifier = Modifier
            .height(48.dp)
            .width(172.dp)
    ) {
        Text("Sign Up")
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePreview() {
    WorkWellTheme {
        WelcomeView()
    }
}
