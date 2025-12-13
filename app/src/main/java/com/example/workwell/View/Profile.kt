package com.example.workwell.View

import ProfileViewModel
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue // Importación para el manejo de estado
import androidx.compose.runtime.mutableStateOf // Importación para el manejo de estado
import androidx.compose.runtime.remember // Importación para el manejo de estado
import androidx.compose.runtime.setValue // Importación para el manejo de estado
import androidx.compose.ui.graphics.asImageBitmap
import com.google.firebase.auth.FirebaseAuth // Importar Firebase Auth
import com.example.workwell.Model.UserProviderFirebase
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun Profile(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current

    // 1. LEEMOS la imagen del ViewModel
    val imageBitmap = viewModel.imageBitmap

    // 2. EFECTO DE LANZAMIENTO:
    // Esto se ejecuta una sola vez cuando se abre la pantalla.
    // Intenta cargar la foto guardada anteriormente.
    LaunchedEffect(Unit) {
        viewModel.loadImage(context)
    }

    // 3. Launcher de cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            // CAMBIO: Llamamos a saveImage del ViewModel
            viewModel.saveImage(context, bitmap)
            Toast.makeText(context, "Foto guardada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No se tomó foto", Toast.LENGTH_SHORT).show()
        }
    }

    // 1. Estados
    var userName by remember { mutableStateOf("Cargando...") }

    // 2. Inicialización de Firebase
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val userProvider = UserProviderFirebase()
    LaunchedEffect(userId) {
        val user = userProvider.getUser(userId.toString())
        userName = user.userName
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp).padding(top = 30.dp), // Añade padding a los lados
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Perfil(userName, imageBitmap, cameraLauncher)
        Spacer(modifier = Modifier.height(32.dp))

        Options(context)
        Spacer(modifier = Modifier.height(20.dp).weight(1f))
        Footer()
    }
}

@Composable
fun Perfil(
    userName: String,
    imageBitmap: Bitmap?,
    cameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>
)
{
    Text(
        text = "My Profile",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,// Texto en negrita
        color = Color(31, 65, 187, 255)
    )

    Spacer(modifier = Modifier.height(4.dp))
    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap.asImageBitmap(),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAEAEA), CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
                .clickable{cameraLauncher.launch()}
        )
    }
    else
    {
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
    }




    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = userName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
}
@Composable
fun Options(context: Context)
{
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