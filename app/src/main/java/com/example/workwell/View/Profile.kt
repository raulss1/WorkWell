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
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.graphics.asImageBitmap
    import androidx.core.content.ContextCompat
    import com.google.firebase.auth.FirebaseAuth
    import com.example.workwell.Model.UserProviderFirebase
    import androidx.lifecycle.viewmodel.compose.viewModel
    import com.example.workwell.MainActivity
    import android.Manifest
    import android.util.Log
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
    import com.example.workwell.ViewModel.FirebaseAuthFacade
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.tasks.await
    import java.util.TimeZone
    import com.example.workwell.ViewModel.AuthViewModel

    val auth = FirebaseAuth.getInstance()

    @Composable
    fun Profile(
        modifier: Modifier = Modifier,
        viewModel: ProfileViewModel = viewModel()
    ) {
        val scope = rememberCoroutineScope()

        // --- 1. ESTADO GLOBAL DE LA PANTALLA ---
        // Definimos las variables aquí para que estén listas antes de abrir cualquier diálogo
        var currentName by remember { mutableStateOf("Cargando...") }
        var currentUserName by remember { mutableStateOf("") }
        var currentBirthday by remember { mutableStateOf("") }
        var currentEmail by remember { mutableStateOf("") }

        ScreenWithFooter {
            val context = LocalContext.current

            // LEEMOS la imagen del ViewModel
            val imageBitmap = viewModel.imageBitmap

            // CARGAMOS LA IMAGEN GUARDADA
            LaunchedEffect(Unit) {
                viewModel.loadImage(context)
            }

            // --- 2. CARGAMOS LOS DATOS DEL USUARIO ---
            val userId = auth.currentUser?.uid
            val userProvider = UserProviderFirebase()

            LaunchedEffect(userId) {
                if (userId != null) {
                    val db = FirebaseFirestore.getInstance()
                    try {
                        // Hacemos la petición directa a la colección "users"
                        val documentSnapshot =
                            db.collection("user") // Ojo: Revisa si es "users" o "user"
                                .document(userId)
                                .get()
                                .await() // Esperamos la respuesta
                        val user = userProvider.getUser(userId)
                        if (user != null) {
                            currentName = user.name
                            currentUserName = user.userName
                            val fecha = documentSnapshot.getTimestamp("BirthDate")
                            currentBirthday = if (fecha != null)
                            {
                                val date = fecha.toDate()
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                formatter.timeZone = TimeZone.getTimeZone("UTC")
                                formatter.format(date)
                            }
                            else
                            {
                                ""
                            }
                            currentEmail = documentSnapshot.getString("Email") ?: ""
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Launcher de cámara
            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview()
            ) { bitmap ->
                if (bitmap != null) {
                    viewModel.saveImage(context, bitmap)
                    Toast.makeText(context, "Foto guardada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "No se tomó foto", Toast.LENGTH_SHORT).show()
                }
            }

            // Launcher de Permisos
            val permissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    cameraLauncher.launch()
                } else {
                    Toast.makeText(context, "Permiso denegado.", Toast.LENGTH_SHORT).show()
                }
            }

            // Lógica del Click en Imagen
            val onImageClick = {
                val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch()
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }

            Column(
                modifier = modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Usamos la variable 'currentUserName' que ya cargamos arriba
                Perfil(currentUserName, imageBitmap, onImageClick)

                Spacer(modifier = Modifier.height(32.dp))

                // Pasamos los datos y una función para actualizar la UI cuando se guarde
                Options(
                    context = context,
                    scope = scope,
                    currentName = currentName,
                    currentUserName = currentUserName,
                    currentBirthday = currentBirthday,
                    currentEmail = currentEmail,
                    onProfileUpdate = { newName, newUser, newBirth ->
                        // Esta función se ejecuta cuando el diálogo guarda cambios exitosamente
                        currentName = newName
                        currentUserName = newUser
                        currentBirthday = newBirth
                    }
                )
            }
        }
    }

    @Composable
    fun Perfil(
        userName: String,
        imageBitmap: Bitmap?,
        onImageClick: () -> Unit, // Cambiado nombre para claridad
    ) {
        Text(
            text = "My Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(31, 65, 187, 255)
        )

        Spacer(modifier = Modifier.height(4.dp))

        val modifierImg = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable { onImageClick() }
            .background(Color(0xFFEAEAEA), CircleShape)
            .border(1.dp, Color.Gray, CircleShape)

        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap.asImageBitmap(),
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = modifierImg
            )
        } else {
            Image(
                imageVector = Icons.Filled.Person,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = modifierImg
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
    fun Options(
        context: Context,
        scope: CoroutineScope,
        // Recibimos los datos desde Profile
        currentName: String,
        currentUserName: String,
        currentBirthday: String,
        currentEmail: String,
        // Callback para devolver los datos actualizados
        onProfileUpdate: (String, String, String) -> Unit
    ) {
        var showLogoutDialog by remember { mutableStateOf(false) }
        var showEditDialog by remember { mutableStateOf(false) }

        ProfileOptionItem(
            icon = Icons.Filled.Favorite,
            text = "Favorite",
            onClick = { Toast.makeText(context, "Ir a Favoritos", Toast.LENGTH_SHORT).show() }
        )

        ProfileOptionItem(
            icon = Icons.Filled.Settings,
            text = "Edit",
            onClick = { showEditDialog = true }
        )

        if (showEditDialog) {
            // AQUÍ YA NO CARGAMOS DATOS CON LAUNCHEDEFFECT
            // Usamos directamente los parámetros que recibimos (currentName, etc.)

            EditUserDialog(
                currentName = currentName,
                currentUserName = currentUserName,
                currentPassword = "",
                currentBirthday = currentBirthday,
                currentEmail = currentEmail,
                onDismiss = { showEditDialog = false },
                onSave = { newName, newUserName, currentPassword, newPassword, newBirthDate ->

                    var valido = true
                    if (newName.isBlank() || newUserName.isBlank() || newPassword.isBlank() || newBirthDate.isBlank()) {
                        valido = false
                    }

                    val userId = auth.currentUser?.uid

                    if (valido && userId != null && currentPassword == newPassword) {
                        val db = FirebaseFirestore.getInstance()
                        val cosa = FirestoreUserFacade(db)

                        // Conversión segura de fecha
                        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val conver: java.util.Date = try {
                            formato.parse(newBirthDate) ?: java.util.Date()
                        } catch (e: Exception) { java.util.Date() }

                        scope.launch {
                            cosa.saveUser(
                                userId = userId,
                                name = newName,
                                username = newUserName,
                                email = currentEmail,
                                birthDate = conver
                            )

                            Toast.makeText(context, "Guardado", Toast.LENGTH_SHORT).show()

                            // IMPORTANTE: Actualizamos la UI principal
                            onProfileUpdate(newName, newUserName, newBirthDate)
                            AuthViewModel().changePassword(currentEmail, currentPassword, newPassword)
                            showEditDialog = false
                        }
                    } else {
                        Toast.makeText(context, "Error: Rellena todos los campos", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        ProfileOptionItem(
            icon = Icons.Filled.ExitToApp,
            text = "Logout",
            onClick = { showLogoutDialog = true }
        )

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(text = "Cerrar sesión") },
                text = { Text(text = "¿Estás seguro de que quieres salir de la aplicación?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            val cosa = FirebaseAuthFacade()
                            cosa.logout()
                            Toast.makeText(context, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Sí, salir", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }

    // --- RESTO DE COMPONENTES (EditUserDialog, etc.) SIGUEN IGUAL ---
    // ... (EditUserDialog, convertMillisToMyFormat, ProfileOptionItem) ...
    // Asegúrate de incluir el código de EditUserDialog que te pasé antes aquí abajo
    @Composable
    fun EditUserDialog(
        currentName: String,
        currentUserName: String,
        currentPassword: String,
        currentBirthday: String,
        currentEmail: String,
        onDismiss: () -> Unit,
        onSave: (String, String, String, String, String) -> Unit
    ) {
        var name by remember { mutableStateOf(currentName) }
        var userName by remember { mutableStateOf(currentUserName) }
        var password by remember { mutableStateOf(currentPassword) }
        var newPassword by remember { mutableStateOf(currentPassword) }
        var birthDateString by remember { mutableStateOf(currentBirthday) }

        var showDatePicker by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Editar Datos") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        label = { Text("UserName") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("CurrentPassword") },
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
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("NewPassword") },
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
                    OutlinedTextField(
                        value = birthDateString.ifEmpty { "Selecciona Fecha" },
                        onValueChange = { },
                        label = { Text("BirthDate") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Calendario")
                            }
                        },
                        modifier = Modifier.clickable { showDatePicker = true }
                    )

                    if (showDatePicker) {
                        val initialMillis = remember(birthDateString) {
                            if (birthDateString.isNotEmpty()) {
                                try {
                                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                                    sdf.parse(birthDateString)?.time
                                } catch (e: Exception) { null }
                            } else {
                                null
                            }
                        }
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = initialMillis // B. Se lo pasamos aquí
                        )
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
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
                    onClick = { onSave(name, userName, password, newPassword, birthDateString) }
                ) { Text("Guardar Cambios") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text("Cancelar") }
            }
        )
    }

    fun convertMillisToMyFormat(millis: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // Es importante ajustar la zona horaria a UTC porque el DatePicker te da la fecha en UTC
        formatter.timeZone = TimeZone.getTimeZone("UTC")
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
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
    }