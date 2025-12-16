package com.example.workwell.View

import ProfileViewModel
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth // Importar Firebase Auth
import com.example.workwell.Model.UserProviderFirebase
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.workwell.MainActivity
import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.workwell.ViewModel.FirestoreUserFacade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


val auth = FirebaseAuth.getInstance()


@Composable
fun Profile(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    ScreenWithFooter {
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

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Si dijo que SÍ, lanzamos la cámara
                cameraLauncher.launch()
            } else {
                // Si dijo que NO, mostramos aviso
                Toast.makeText(context, "Permiso denegado. No se puede abrir la cámara.", Toast.LENGTH_SHORT).show()
            }
        }

        // 5. NUEVO: Función lógica para decidir qué hacer al hacer click
        val onImageClick = {
            // Verificamos si YA tenemos el permiso
            val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

            if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                // Caso A: Ya tenemos permiso -> Abrir cámara directo
                cameraLauncher.launch()
            } else {
                // Caso B: No tenemos permiso -> Pedirlo
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        // 1. Estados
        var userName by remember { mutableStateOf("Cargando...") }

        // 2. Inicialización de Firebase
        val userId = auth.currentUser?.uid
        val userProvider = UserProviderFirebase()
        LaunchedEffect(userId) {
            val user = userProvider.getUser(userId.toString())
            userName = user.userName
        }


        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 30.dp), // Añade padding a los lados
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Perfil(userName, imageBitmap, onImageClick)
            Spacer(modifier = Modifier.height(32.dp))

            Options(context, scope)
            //Spacer(modifier = Modifier.height(20.dp).weight(1f))


        }
    }

}

@Composable
fun Perfil(
    userName: String,
    imageBitmap: Bitmap?,
    cameraLauncher: () -> Unit,
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
                .clickable { cameraLauncher() }
                .background(Color(0xFFEAEAEA), CircleShape)
                .border(1.dp, Color.Gray, CircleShape)

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
                .clickable { cameraLauncher() }
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
fun Options(context: Context, scope: CoroutineScope)
{
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) } // NUEVO
    ProfileOptionItem(
        icon = Icons.Filled.Favorite,
        text = "Favorite",
        onClick = {
            Toast.makeText(context, "Ir a Favoritos", Toast.LENGTH_SHORT).show()
        }
    )

    ProfileOptionItem(
        icon = Icons.Filled.Settings,
        text = "Edit",
        onClick = {
            showEditDialog = true
        }
    )

    if (showEditDialog) {
        EditUserDialog(
            //TODO Poner los datos actuales
            currentName = "", // Pasa aquí tus variables reales
            currentUserName = "",
            currentPassword = "",
            currentBirthday = "",
            onDismiss = { showEditDialog = false },
            onSave = { newName, newUserName, newPassword, newBirthDate ->
                // 3. AQUÍ RECIBES LOS DATOS NUEVOS CUANDO LE DAN A GUARDAR
                var valido = true
                if (newName == ""  || newUserName == "" || newPassword == "" || newBirthDate == "")
                {
                    valido = false
                }
                val userId = auth.currentUser?.uid
                if (valido){
                    val db = FirebaseFirestore.getInstance()
                    val cosa = FirestoreUserFacade(db)
                    val formato = SimpleDateFormat("dd/MM/yyyy/HH/mm", Locale.getDefault())
                    val conver: java.util.Date = formato.parse(newBirthDate) ?: java.util.Date()
                    scope.launch {
                        cosa.saveUser(
                            userId = userId.toString(),
                            name = newName,
                            username = newUserName,
                            email = "mohamed1@gmail.com",
                            birthDate = conver
                        )

                        // Opcional: Aquí puedes poner el Toast de éxito porque esto se ejecuta al terminar
                        Toast.makeText(context, "Guardado", Toast.LENGTH_SHORT).show()
                        showEditDialog = false
                    }

                } else {
                    Toast.makeText(context, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    ProfileOptionItem(
        icon = Icons.Filled.ExitToApp,
        text = "Logout",
        onClick = {
            showLogoutDialog = true
        }

    )
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false }, // Si tocan fuera, se cierra
            title = { Text(text = "Cerrar sesión") },
            text = { Text(text = "¿Estás seguro de que quieres salir de la aplicación?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // --- AQUÍ SÍ CERRAMOS SESIÓN ---
                        showLogoutDialog = false // Ocultar diálogo

                        // 1. Desloguear de Firebase (usa tu variable global 'auth')
                        auth.signOut()

                        Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()

                        // 2. Reiniciar hacia el Login (MainActivity)
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                ) {
                    Text("Sí, salir", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false } // Solo cerrar ventana
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

}
@Composable
fun EditUserDialog(
    currentName: String,
    currentUserName: String,
    currentPassword: String,
    currentBirthday: String, // String con formato "dd/MM/yyyy/HH/mm"
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit // Retorna: Name, UserName, Pass, BirthDateString
) {
    // --- ESTADOS LOCALES DEL FORMULARIO ---
    var name by remember { mutableStateOf(currentName) }
    var userName by remember { mutableStateOf(currentUserName) }
    var password by remember { mutableStateOf(currentPassword) }
    var birthDateString by remember { mutableStateOf(currentBirthday) } // String formateado

    // Estados para la lógica visual
    var showDatePicker by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar Datos") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // 1. NAME
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true
                )

                // 2. USERNAME
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("UserName") },
                    singleLine = true
                )

                // 3. PASSWORD (con ojito para ver/ocultar)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    }
                )

                // 4. FECHA DE NACIMIENTO (Selector)
                OutlinedTextField(
                    value = birthDateString.ifEmpty { "Selecciona Fecha" },
                    onValueChange = { },
                    label = { Text("BirthDate") },
                    readOnly = true, // Importante: No dejar escribir a mano
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Calendario")
                        }
                    },
                    // Hacemos que todo el campo sea clickeable
                    modifier = Modifier.clickable { showDatePicker = true }
                )

                // LÓGICA DEL CALENDARIO
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState()

                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    // AQUÍ CONVERTIMOS MILLIS -> STRING FORMATO TU MODELO
                                    birthDateString = convertMillisToMyFormat(millis)
                                }
                                showDatePicker = false
                            }) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Al guardar, enviamos los 4 datos
                    onSave(name, userName, password, birthDateString)
                }
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Función auxiliar para generar TU formato específico "dd/MM/yyyy/HH/mm"
fun convertMillisToMyFormat(millis: Long): String {
    // Usamos java.util.Date explícitamente para evitar conflictos
    val formatter = SimpleDateFormat("dd/MM/yyyy/HH/mm", Locale.getDefault())
    return formatter.format(java.util.Date(millis))
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