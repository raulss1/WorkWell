package com.example.pruebasinterfaz

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workwell.ui.theme.WorkWellTheme

class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkWellTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ProfileScreen()
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), // Añade padding a los lados
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,// Texto en negrita
            color = Color(31, 65, 187, 255)
        )

        Spacer(modifier = Modifier.height(4.dp))
        Image(
            imageVector = Icons.Filled.Person,
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAEAEA), CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
        )



        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Manolo",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))
        ProfileOptionItem(
            icon = Icons.Filled.Favorite,
            text = "Favorite",
            onClick = {
                Toast.makeText(context, "Ir a Favoritos", Toast.LENGTH_SHORT).show()
            }
        )

        ProfileOptionItem(
            icon = Icons.Filled.Settings,
            text = "Settings",
            onClick = {
                Toast.makeText(context, "Ir a Configuración", Toast.LENGTH_SHORT).show()
            }
        )

        ProfileOptionItem(
            icon = Icons.Filled.ExitToApp,
            text = "Logout",
            onClick = {
                Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFCAD6FF), CircleShape)
                .padding(8.dp),
            tint = Color(31, 65, 187, 255)
        )

        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    WorkWellTheme {
        ProfileScreen()
    }
}