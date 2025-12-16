package com.example.workwell.View

import android.Manifest
import android.R
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.workwell.ViewModel.CreateRoutineViewModel
import com.example.workwell.ViewModel.NotificationScheduler
import com.example.workwell.ViewModel.RoutineData
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

// Definición de Colores (Basados en tu imagen)
val BluePrimary = Color(0xFF3D5AFE)
val BackgroundWhite = Color(0xFFF5F5F5) // Un blanco grisáceo muy suave para el fondo general si quieres contraste
val CardWhite = Color.White
val TextDark = Color(0xFF1E1E1E)
val TextGray = Color(0xFF757575)

val viewModel = CreateRoutineViewModel()

@Composable
fun CreateRoutineView(navController: NavController) {

    val context = LocalContext.current

    // 1️⃣ Crear canal (OBLIGATORIO)
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "routine_channel",
                "Recordatorios de Rutinas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos de rutinas y pausas"
                enableVibration(true)
                enableLights(true)
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    // 2️⃣ Notificación de prueba (DESBLOQUEA XIAOMI)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showTestNotification() {
        val notification = NotificationCompat.Builder(context, "routine_channel")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setContentTitle("WorkWell activo")
            .setContentText("Las notificaciones están habilitadas")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(context).notify(1001, notification)
    }

    // 3️⃣ Ejecutar al entrar a la pantalla
    LaunchedEffect(Unit) {
        createNotificationChannel()
        showTestNotification()
    }



    val scope = rememberCoroutineScope()
    val calendar = Calendar.getInstance()
    var pendingToggleAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    // --- INSTANCIA DEL SCHEDULER ---
    val scheduler = remember { NotificationScheduler(context) }

    // --- LAUNCHER DE PERMISOS ---
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                pendingToggleAction?.invoke()
                Toast.makeText(context, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Las notificaciones no funcionarán sin permiso", Toast.LENGTH_LONG).show()
            }
            pendingToggleAction = null
        }
    )

    // --- NUEVO HELPER: Lógica para pedir permiso al activar un Toggle ---
    val onToggleWithPermission: (Boolean, (Boolean) -> Unit) -> Unit =
        { shouldEnable, updateState ->

            if (!shouldEnable) {
                updateState(false)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    val hasPermission =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        updateState(true)
                    } else {
                        pendingToggleAction = { updateState(true) }
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }

                } else {
                    updateState(true)
                }
            }
        }


    // --- ESTADOS DEL FORMULARIO ---

    // 1. Datos básicos
    var routineName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Laboral") }
    var selectedPriority by remember { mutableStateOf("Media") }

    // 2. Fechas y Horas UI
    var dateText by remember { mutableStateOf("") }
    var startTimeText by remember { mutableStateOf("") }
    var endTimeText by remember { mutableStateOf("") }

    // 3. Variables internas Date
    var selYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var selDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    var startHour by remember { mutableIntStateOf(-1) }
    var startMinute by remember { mutableIntStateOf(-1) }
    var endHour by remember { mutableIntStateOf(-1) }
    var endMinute by remember { mutableIntStateOf(-1) }

    // 4. Notificaciones
    var eatEnabled by remember { mutableStateOf(false) }
    var eatFrequency by remember { mutableStateOf("") }

    var standUpEnabled by remember { mutableStateOf(false) }
    var standUpFrequency by remember { mutableStateOf("") }

    var stretchEnabled by remember { mutableStateOf(false) }
    var stretchFrequency by remember { mutableStateOf("") }


    // --- PICKERS ---
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selYear = year
            selMonth = month
            selDay = dayOfMonth
            dateText = "$dayOfMonth/${month + 1}/$year"
        },
        selYear, selMonth, selDay
    )

    val startTimePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            startHour = hour
            startMinute = minute
            startTimeText = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val endTimePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            endHour = hour
            endMinute = minute
            endTimeText = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY) + 1,
        calendar.get(Calendar.MINUTE),
        true
    )

    fun createDate(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopHeader(title = "Nueva Rutina", onBack = { navController.popBackStack() })
        },
        bottomBar = {
            Button(
                onClick = {
                    // --- VALIDACIÓN ---
                    if (routineName.isBlank()) {
                        Toast.makeText(context, "Ingresa un nombre para la rutina", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (dateText.isEmpty()) {
                        Toast.makeText(context, "Selecciona una fecha", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (startHour == -1 || endHour == -1) {
                        Toast.makeText(context, "Selecciona hora de inicio y fin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    // --- CHECK FINAL DE PERMISOS ---
                    // (Opcional si usas los toggles, pero bueno por seguridad)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permissionCheck = ContextCompat.checkSelfPermission(
                            context, android.Manifest.permission.POST_NOTIFICATIONS
                        )
                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            // Continuamos guardando, si el usuario acepta rápido funcionará, si no, la próxima vez.
                        }
                    }

                    // --- CONSTRUCCIÓN DE DATOS ---
                    val finalStartTime = createDate(selYear, selMonth, selDay, startHour, startMinute)
                    val finalEndTime = createDate(selYear, selMonth, selDay, endHour, endMinute)

                    val eatVal = if (eatEnabled && eatFrequency.isNotBlank()) eatFrequency else null
                    val standVal = if (standUpEnabled && standUpFrequency.isNotBlank()) standUpFrequency else null
                    val stretchVal = if (stretchEnabled && stretchFrequency.isNotBlank()) stretchFrequency else null

                    val newRoutine = RoutineData(
                        name = routineName,
                        startTime = finalStartTime,
                        endTime = finalEndTime,
                        type = selectedType,
                        priority = selectedPriority,
                        eatReminderMinutes = eatVal,
                        standUpReminderMinutes = standVal,
                        stretchReminderMinutes = stretchVal
                    )

                    // --- GUARDAR ---
                    scope.launch {
                        try {
                            viewModel.createRoutine(newRoutine)

                            // 1. Notificación COMER
                            if (eatEnabled && eatFrequency.isNotBlank()) {
                                scheduler.scheduleRoutineNotification(
                                    uniqueWorkName = "${routineName}_eat",
                                    taskName = "Comer/Beber ($routineName)",
                                    startHour = startHour,
                                    startMinute = startMinute,
                                    endHour = endHour,
                                    endMinute = endMinute,
                                    repeatIntervalMinutes = eatFrequency.toLongOrNull() ?: 15L
                                )
                            }

                            // 2. Notificación LEVANTARSE
                            if (standUpEnabled && standUpFrequency.isNotBlank()) {
                                scheduler.scheduleRoutineNotification(
                                    uniqueWorkName = "${routineName}_stand",
                                    taskName = "Levantarse ($routineName)",
                                    startHour = startHour,
                                    startMinute = startMinute,
                                    endHour = endHour,
                                    endMinute = endMinute,
                                    repeatIntervalMinutes = standUpFrequency.toLongOrNull() ?: 60L
                                )
                            }

                            // 3. Notificación ESTIRAR
                            if (stretchEnabled && stretchFrequency.isNotBlank()) {
                                scheduler.scheduleRoutineNotification(
                                    uniqueWorkName = "${routineName}_stretch",
                                    taskName = "Estirar ($routineName)",
                                    startHour = startHour,
                                    startMinute = startMinute,
                                    endHour = endHour,
                                    endMinute = endMinute,
                                    repeatIntervalMinutes = stretchFrequency.toLongOrNull() ?: 30L
                                )
                            }

                            Toast.makeText(context, "Rutina y alarmas creadas", Toast.LENGTH_SHORT).show()

                            // Avisar al Home para recargar
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("refresh_routines", true)

                            navController.popBackStack()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error al crear: ${e.message}", Toast.LENGTH_LONG).show()
                            // No hacemos popBackStack aquí para dejar que el usuario corrija
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "Crear Rutina", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Nombre
            SectionTitle("Nombre")
            CustomTextField(
                value = routineName,
                onValueChange = { routineName = it },
                placeholder = "Ej. Revisar correos",
                icon = Icons.Default.Notifications
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Fecha y Hora
            SectionTitle("Programación")

            ClickableField(
                value = if (dateText.isEmpty()) "Seleccionar fecha" else dateText,
                icon = Icons.Default.DateRange,
                onClick = { datePickerDialog.show() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    ClickableField(
                        value = if (startTimeText.isEmpty()) "Inicio" else startTimeText,
                        icon = Icons.Default.Schedule,
                        onClick = { startTimePickerDialog.show() }
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    ClickableField(
                        value = if (endTimeText.isEmpty()) "Fin" else endTimeText,
                        icon = Icons.Default.Schedule,
                        onClick = { endTimePickerDialog.show() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Tipo
            SectionTitle("Tipo de Rutina")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SelectableChip("Laboral", selectedType == "Laboral") { selectedType = "Laboral" }
                SelectableChip("Casual", selectedType == "Casual") { selectedType = "Casual" }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Prioridad
            SectionTitle("Prioridad")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Alta", "Media", "Baja").forEach { priority ->
                    SelectableChip(priority, selectedPriority == priority) { selectedPriority = priority }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Configuración de Avisos (AQUÍ ESTÁN LOS CAMBIOS PRINCIPALES)
            SectionTitle("Configuración de Avisos")

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FF)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // A) Comer - Usamos el helper onToggleWithPermission
                    NotificationRow(
                        title = "Recordatorio de Comer/Beber",
                        isEnabled = eatEnabled,
                        onToggle = { isChecked ->
                            onToggleWithPermission(isChecked) { eatEnabled = it }
                        }
                    ) {
                        FrequencyInput(eatFrequency, { eatFrequency = it }, "Recordar cada (minutos):", "Ej. 180")
                    }

                    // B) Levantarse - Usamos el helper
                    NotificationRow(
                        title = "Levantarse de la silla",
                        isEnabled = standUpEnabled,
                        onToggle = { isChecked ->
                            onToggleWithPermission(isChecked) { standUpEnabled = it }
                        }
                    ) {
                        FrequencyInput(standUpFrequency, { standUpFrequency = it }, "Recordar cada (minutos):", "Ej. 60")
                    }

                    // C) Estirar - Usamos el helper
                    NotificationRow(
                        title = "Estiramientos / Pausa Activa",
                        isEnabled = stretchEnabled,
                        onToggle = { isChecked ->
                            onToggleWithPermission(isChecked) { stretchEnabled = it }
                        }
                    ) {
                        FrequencyInput(stretchFrequency, { stretchFrequency = it }, "Recordar cada (minutos):", "Ej. 30")
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun TopHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF0F0F0), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextDark
        )
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = TextDark,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Color.Gray) },
        leadingIcon = if (icon != null) {
            { Icon(icon, contentDescription = null, tint = BluePrimary) }
        } else null,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BluePrimary,
            unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
            focusedContainerColor = Color(0xFFFAFAFA),
            unfocusedContainerColor = Color(0xFFFAFAFA)
        ),
        singleLine = true
    )
}

@Composable
fun ClickableField(
    value: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        enabled = false, // Deshabilitado para escritura directa
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Clickable maneja el evento
        leadingIcon = { Icon(icon, contentDescription = null, tint = BluePrimary) },
        trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = TextDark,
            disabledBorderColor = Color.LightGray.copy(alpha = 0.5f),
            disabledContainerColor = Color(0xFFFAFAFA),
            disabledLeadingIconColor = BluePrimary,
            disabledTrailingIconColor = Color.Gray
        )
    )
}

@Composable
fun SelectableChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) BluePrimary else Color.Transparent
    val contentColor = if (selected) Color.White else TextGray
    val borderColor = if (selected) BluePrimary else Color.LightGray

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
fun NotificationRow(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    content: @Composable () -> Unit // Aquí pasaremos el selector de hora o texto
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF1E1E1E)
            )
            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF3D5AFE),
                    checkedTrackColor = Color(0xFF3D5AFE).copy(alpha = 0.3f)
                )
            )
        }

        // Animación para mostrar el contenido extra (hora o minutos)
        AnimatedVisibility(visible = isEnabled) {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                content()
            }
        }

        // Línea separadora suave
        Divider(color = Color.LightGray.copy(alpha = 0.3f))
    }
}

@Composable
fun FrequencyInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        // Reutilizamos tu CustomTextField existente
        CustomTextField(
            value = value,
            onValueChange = {
                // Validación simple: solo permitir números
                if (it.all { char -> char.isDigit() }) onValueChange(it)
            },
            placeholder = placeholder,
            icon = Icons.Default.Notifications // O Icons.Default.Timer si prefieres
        )
    }
}