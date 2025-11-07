package com.example.workwell.View

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workwell.R
import com.example.workwell.ui.theme.AzulBotonLogin
import com.example.workwell.ui.theme.AzulFondo
import com.example.workwell.ui.theme.WorkWellTheme

class Home : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkWellTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeView(name = "Raúl")
                }
            }
        }
    }
}

@Composable
fun HomeView(name: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Barra Superior ---
        Header(name)

        // --- Contenido Principal ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BotonCrearRutina()
        }

        // --- Footer ---
        Column(
            modifier = Modifier
                .fillMaxWidth().padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Footer()
        }
    }
}


@Composable
fun Header(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Parte izquierda (perfil y texto) ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Imagen circular del usuario
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Hi, WelcomeBack",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF3D5AFE) // azul ejemplo
                )
                Text(
                    text = "$name",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
            }
        }

        // --- Parte derecha (íconos) ---
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Acción notificaciones */ },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE0E7FF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notificaciones",
                    tint = Color.Black
                )
            }

            IconButton(
                onClick = { /* Acción configuración */ },
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE0E7FF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configuración",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
fun Footer(){
    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .background(AzulFondo, shape = CircleShape)
            .padding(vertical = 12.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Acción notificaciones */ },
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Notificaciones",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { /* Acción notificaciones */ },
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Notificaciones",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { /* Acción notificaciones */ },
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Notificaciones",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun BotonCrearRutina(){
    OutlinedButton(
        onClick = {
            // TODO: Navegar a pantalla de crear rutina
        },
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AzulBotonLogin,
            contentColor = Color.White
        ),
        modifier = Modifier
            .height(48.dp)
            .width(172.dp)
    ) {
        Text("Crear Rutina")
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    WorkWellTheme {
        HomeView(name = "Raúl")
    }
}