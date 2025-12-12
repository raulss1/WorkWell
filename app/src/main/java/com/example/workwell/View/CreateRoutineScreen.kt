package com.example.workwell.View

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import java.util.Calendar

// Definición de Colores (Basados en tu imagen)
val BluePrimary = Color(0xFF3D5AFE)
val BackgroundWhite = Color(0xFFF5F5F5) // Un blanco grisáceo muy suave para el fondo general si quieres contraste
val CardWhite = Color.White
val TextDark = Color(0xFF1E1E1E)
val TextGray = Color(0xFF757575)

@Composable
fun CreateRoutine(navController: NavController) {
    // --- ESTADOS DEL FORMULARIO ---
    var routineName by remember { mutableStateOf("") }

    // Fechas y Horas (Strings para visualización)
    var dateText by remember { mutableStateOf("") }
    var startTimeText by remember { mutableStateOf("") }
    var endTimeText by remember { mutableStateOf("") }

    // Selectores
    var selectedType by remember { mutableStateOf("Laboral") } // Opciones: Laboral, Casual
    var selectedPriority by remember { mutableStateOf("Media") } // Opciones: Alta, Media, Baja

    // Notificaciones
    var notificationsEnabled by remember { mutableStateOf(false) }
    var notificationFrequency by remember { mutableStateOf("") }

    // Contexto para los Pickers
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // --- PICKERS (Lógica básica) ---
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            dateText = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val startTimePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            startTimeText = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val endTimePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            endTimeText = String.format("%02d:%02d", hour, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY) + 1,
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopHeader(
                title = "Nueva Rutina",
                onBack = { navController.popBackStack() }
            )
        },
        bottomBar = {
            // Botón fijo abajo
            Button(
                onClick = {
                    // AQUÍ LLAMAS A TU VIEWMODEL PARA GUARDAR
                    // viewModel.saveRoutine(...)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
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

            // 1. Nombre de la Rutina
            SectionTitle("Nombre")
            CustomTextField(
                value = routineName,
                onValueChange = { routineName = it },
                placeholder = "Ej. Revisar correos",
                icon = Icons.Default.Notifications // Icono genérico o null
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Fecha y Hora
            SectionTitle("Programación")

            // Fecha
            ClickableField(
                value = if (dateText.isEmpty()) "Seleccionar fecha" else dateText,
                icon = Icons.Default.DateRange,
                onClick = { datePickerDialog.show() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Hora Inicio y Fin en una fila
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

            // 3. Tipo de Rutina (Laboral / Casual)
            SectionTitle("Tipo de Rutina")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SelectableChip(
                    text = "Laboral",
                    selected = selectedType == "Laboral",
                    onClick = { selectedType = "Laboral" }
                )
                SelectableChip(
                    text = "Casual",
                    selected = selectedType == "Casual",
                    onClick = { selectedType = "Casual" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Prioridad
            SectionTitle("Prioridad")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Alta", "Media", "Baja").forEach { priority ->
                    SelectableChip(
                        text = priority,
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- ESTADOS PARA LAS 3 NOTIFICACIONES ---

            // 1. Comer (Ej. Snack cada 120 min)
            var eatEnabled by remember { mutableStateOf(false) }
            var eatFrequency by remember { mutableStateOf("") } // Antes era eatTime

            // 2. Levantarse (Ej. Pausa activa cada 60 min)
            var standUpEnabled by remember { mutableStateOf(false) } // Cambié nombre a standUp para ser más preciso
            var standUpFrequency by remember { mutableStateOf("") }  // Antes era wakeUpTime

            // 3. Estirar (Ej. Estiramiento cada 45 min)
            var stretchEnabled by remember { mutableStateOf(false) }
            var stretchFrequency by remember { mutableStateOf("") }

// ...

            // Helper para abrir el reloj (reutilizable)
            fun showTimePicker(onTimeSelected: (String) -> Unit) {
                val cal = Calendar.getInstance()
                TimePickerDialog(
                    context,
                    { _, hour, minute -> onTimeSelected(String.format("%02d:%02d", hour, minute)) },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                ).show()
            }

            // 5. SECCIÓN NOTIFICACIONES
            SectionTitle("Configuración de Avisos")

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FF)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // A) NOTIFICACIÓN PARA COMER (Repetible)
                    NotificationRow(
                        title = "Recordatorio de Comer/Beber",
                        isEnabled = eatEnabled,
                        onToggle = { eatEnabled = it }
                    ) {
                        FrequencyInput(
                            value = eatFrequency,
                            onValueChange = { eatFrequency = it },
                            label = "Recordar cada (minutos):",
                            placeholder = "Ej. 180 (3 horas)"
                        )
                    }

                    // B) NOTIFICACIÓN PARA LEVANTARSE (Repetible)
                    NotificationRow(
                        title = "Levantarse de la silla",
                        isEnabled = standUpEnabled,
                        onToggle = { standUpEnabled = it }
                    ) {
                        FrequencyInput(
                            value = standUpFrequency,
                            onValueChange = { standUpFrequency = it },
                            label = "Recordar cada (minutos):",
                            placeholder = "Ej. 60 (1 hora)"
                        )
                    }

                    // C) NOTIFICACIÓN PARA ESTIRAR (Repetible)
                    NotificationRow(
                        title = "Estiramientos / Pausa Activa",
                        isEnabled = stretchEnabled,
                        onToggle = { stretchEnabled = it }
                    ) {
                        FrequencyInput(
                            value = stretchFrequency,
                            onValueChange = { stretchFrequency = it },
                            label = "Recordar cada (minutos):",
                            placeholder = "Ej. 30"
                        )
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