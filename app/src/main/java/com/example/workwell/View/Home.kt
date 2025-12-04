package com.example.workwell.View

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workwell.Model.User
import com.example.workwell.Model.UserProviderFirebase
import com.example.workwell.R
import com.example.workwell.ViewModel.HomeViewModel
import com.example.workwell.ui.theme.AzulBotonLogin
import com.example.workwell.ui.theme.AzulNav
import com.example.workwell.ui.theme.WorkWellTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.workwell.ViewModel.AuthState
import com.example.workwell.ViewModel.AuthViewModel

// --- CLASE PRINCIPAL ---

val HomeViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            // Asegúrate de que tu provider tenga el scope de la aplicación
            return HomeViewModel(UserProviderFirebase()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// NUEVO COMPOSABLE: Este componente es el que obtiene los datos reactivos.
@SuppressLint("StaticFieldLeak")
lateinit var navControllerAll: NavController
@Composable
fun HomeWrapperScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory),
    authViewModel: AuthViewModel = viewModel(),
    navController: NavHostController
) {
    val name by viewModel.name.collectAsState()
    val authState by authViewModel.authState.observeAsState()
    navControllerAll = navController
    LaunchedEffect(authState) {
        if (authState is AuthState.Unauthenticated) {
            navController.navigate("signup") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // 3. UI
    HomeView(
        name = name,
        onLogout = { authViewModel.signout() }
    )
}

// --- ESTRUCTURA DE DATOS ---

data class CalendarEvent(
    val title: String,
    val time: String,
    val color: Color
)

val provider = UserProviderFirebase()
val HomeViewModel = HomeViewModel(provider)

// --- HOME VIEW (VISTA PRINCIPAL) ---

@Composable
fun HomeView(name: String, onLogout: () -> Unit) {
    // Estado para saber qué día (índice global 0-34) está seleccionado
    var selectedDayIndex by remember { mutableStateOf(14) } // Índice de ejemplo

    // Lista de todos los días en la cuadrícula (para el título dinámico)
    val daysInMonth = listOf(
        "29", "30", "31", "1", "2", "3", "4",
        "5", "6", "7", "8", "9", "10", "11",
        "12", "13", "14", "15", "16", "17", "18",
        "19", "20", "21", "22", "23", "24", "25",
        "26", "27", "28", "29", "30", "1", "2"
    )

    // Lógica para determinar qué tarjetas mostrar
    val eventsByDay = remember {
        mapOf(
            // Ejemplo de eventos para el día "10" (Índice global 9)
            9 to listOf(
                CalendarEvent("Reunión de Equipo", "10:00 - 11:00", Color(0xFF2196F3))
            ),
            // Ejemplo de eventos para el día "15" (Índice global 14)
            14 to listOf(
                CalendarEvent("Rutina de Pecho/Tríceps", "07:00 - 08:30", Color(0xFF4CAF50)),
                CalendarEvent("Llamada con Raúl S.", "14:00 - 15:00", Color(0xFFFF9800))
            ),
            // Ejemplo de eventos para el día "23" (Índice global 22)
            22 to listOf(
                CalendarEvent("Preparar Presentación", "16:00 - 18:00", Color(0xFFE91E63))
            )
        )
    }

    val eventsForSelectedDay = eventsByDay[selectedDayIndex] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Barra Superior ---
        Header(name)

        // --- Calendario Mensual ---
        MonthlyCalendar(
            selectedDayIndex = selectedDayIndex,
            onDaySelected = { index -> selectedDayIndex = index }
        )

        // --- Separador visual ---
        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

        // --- Contenido Principal (Botón y Tarjetas) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Título para la lista de tareas
            val dayNumberText = if (selectedDayIndex in daysInMonth.indices) daysInMonth[selectedDayIndex] else "?"

            Text(
                text = if (eventsForSelectedDay.isNotEmpty()) "Tareas para el $dayNumberText de Noviembre" else "Sin tareas",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Lista de eventos/rutinas para el día seleccionado
            if (eventsForSelectedDay.isNotEmpty()) {
                eventsForSelectedDay.forEach { event ->
                    EventCard(event = event)
                }
            } else {
                Text(
                    text = "¡Día libre! Crea una nueva rutina o tarea.",
                    modifier = Modifier.padding(top = 24.dp),
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotonCrearRutina(onClick = onLogout)
        }

        // --- Footer ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .wrapContentWidth(Alignment.CenterHorizontally) // Centra el contenido del Row
        ) {
            Footer()
        }
    }
}

// --- COMPONENTES DEL CALENDARIO ---

@Composable
fun DayCell(
    dayNumber: String,
    hasEvent: Boolean,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected -> Color(0xFF3D5AFE) // Azul fuerte para el seleccionado
        isToday -> Color(0xFFE0E7FF) // Azul claro para hoy
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> Color.White
        isCurrentMonth -> Color.Black
        else -> Color.Gray.copy(alpha = 0.6f)
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f) // Hace que la celda sea un cuadrado
            .padding(4.dp)
            .clip(MaterialTheme.shapes.small)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayNumber,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor
        )
        if (hasEvent) {
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White else Color(0xFFFF9800))
            )
        }
    }
}

@Composable
fun MonthlyCalendar(selectedDayIndex: Int, onDaySelected: (Int) -> Unit) {
    val daysOfWeek = listOf("L", "M", "X", "J", "V", "S", "D")

    // Datos de ejemplo: 3 días del mes anterior, 30 días del mes actual, 2 días del mes siguiente.
    val daysInMonth = listOf(
        "29", "30", "31", "1", "2", "3", "4",
        "5", "6", "7", "8", "9", "10", "11",
        "12", "13", "14", "15", "16", "17", "18",
        "19", "20", "21", "22", "23", "24", "25",
        "26", "27", "28", "29", "30", "1", "2"
    )

    // Simulamos los días con eventos (usamos el índice global 0-34)
    val daysWithEventsIndices = setOf(9, 14, 22)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Noviembre 2025",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Fila de los títulos de los días de la semana
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentSize(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Grid del calendario (Matriz 7x5)
        val rows = daysInMonth.chunked(7)
        rows.forEachIndexed { rowIndex, week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                week.forEachIndexed { colIndex, dayNumber ->
                    // Calculamos el índice global (0 a 34)
                    val globalIndex = rowIndex * 7 + colIndex

                    val isCurrent = dayNumber.toIntOrNull() in 1..30
                    val isToday = globalIndex == 14 // Ejemplo: Hoy es el día 14 de la cuadrícula

                    Box(modifier = Modifier.weight(1f)) {
                        DayCell(
                            dayNumber = dayNumber,
                            hasEvent = daysWithEventsIndices.contains(globalIndex),
                            isCurrentMonth = isCurrent,
                            isToday = isToday,
                            isSelected = globalIndex == selectedDayIndex,
                            onClick = { onDaySelected(globalIndex) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: CalendarEvent) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = event.color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Línea de color a la izquierda
            Spacer(modifier = Modifier
                .width(4.dp)
                .height(40.dp)
                .background(event.color)
                .clip(MaterialTheme.shapes.extraSmall)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = event.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Detalles del evento",
                tint = event.color
            )
        }
    }
}

// --- OTROS COMPONENTES ---

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
                contentDescription = "Logo",
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
            .background(AzulNav, shape = CircleShape)
            .padding(vertical = 12.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* Acción Home */ },
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = Color.White
                )
            }

            IconButton(

                onClick = { navControllerAll.navigate("profile") },
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Perfil",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { /* Acción Calendario */ },
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Calendario",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun BotonCrearRutina(
    onClick: () -> Unit
){
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AzulBotonLogin,
            contentColor = Color.White
        ),
        modifier = Modifier
            .height(48.dp)
            .width(172.dp)
    ) {
        Text("Cerrar Sesión (Test)")
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    WorkWellTheme {
        //HomeView(name = "name")
    }
}

//TODO Base de datos

/*
class HomeViewModel : ViewModel() {
    // 1. Datos del usuario
    private val _userName = mutableStateOf("Cargando...")
    val userName: State<String> = _userName

    // 2. Control del calendario
    private val _currentMonth = mutableStateOf(YearMonth.now())
    val currentMonth: State<YearMonth> = _currentMonth

    // 3. Tareas (simulando datos de BD)
    // Map<LocalDate, List<CalendarEvent>>
    private val _events = mutableStateOf(emptyMap<LocalDate, List<CalendarEvent>>())
    val events: State<Map<LocalDate, List<CalendarEvent>>> = _events

    // Al inicializar, carga los datos
    init {
        // TODO: Inicia la carga del nombre del usuario de la BD
        // _userName.value = db.getUserName()
        _userName.value = "Raúl (Desde ViewModel)" // Ejemplo

        // TODO: Carga las tareas del mes actual de la BD
        loadEventsForMonth(_currentMonth.value)
    }

    // Lógica para cargar las tareas (simulación)
    private fun loadEventsForMonth(month: YearMonth) {
        // En un futuro, esto llamaría a tu Repositorio/DAO
        // Simulación: Tarea para el día 20 del mes actual
        val sampleDate = month.atDay(20)

        _events.value = mapOf(
            sampleDate to listOf(
                CalendarEvent("Rutina de Hombro", "18:00", Color.Red)
            )
        )
    }

    // Función para obtener la cuadrícula de días real (35 o 42 celdas)
    fun getCalendarGrid(): List<LocalDate?> {
        // Lógica compleja para calcular los días del mes anterior, actual y siguiente.
        // Se asegura que la cuadrícula siempre tenga 6 semanas (42 celdas) o 5 semanas (35 celdas).
        // (La implementación exacta es extensa, pero esta función la devolvería)

        // --- Implementación simple (solo para referencia) ---
        val firstDayOfMonth = _currentMonth.value.atDay(1)
        val startDay = firstDayOfMonth.minusDays(firstDayOfMonth.dayOfWeek.value.toLong() - 1) // Inicia en Lunes

        return (0L until 42L).map { i ->
            val date = startDay.plusDays(i)
            if (YearMonth.from(date) == _currentMonth.value ||
                YearMonth.from(date) == _currentMonth.value.minusMonths(1) ||
                YearMonth.from(date) == _currentMonth.value.plusMonths(1)) {
                date
            } else {
                null // Opcional: para manejar celdas fuera del rango visible
            }
        }
    }
}

// Home.kt
// Importa las clases de fecha necesarias
import java.time.LocalDate
import java.time.YearMonth
import androidx.lifecycle.viewmodel.compose.viewModel // Para obtener el ViewModel
import com.example.workwell.ViewModel.HomeViewModel // Asumiendo esta ruta

// ...

@Composable
fun HomeView(
    homeViewModel: HomeViewModel = viewModel() // Usa la inyección estándar de Compose
) {
    // Obtenemos los estados del ViewModel
    val userName by homeViewModel.userName
    val eventsByDate by homeViewModel.events
    val currentMonth by homeViewModel.currentMonth

    // Estado para el día seleccionado (usamos LocalDate real en lugar de un índice)
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    // Obtener las tareas para la fecha seleccionada
    val eventsForSelectedDay = eventsByDate[selectedDate] ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Barra Superior: Pasa el nombre real ---
        Header(name = userName) // <--- ¡Nombre real!

        // --- Calendario Mensual: Pasa los datos dinámicos ---
        MonthlyCalendar(
            currentMonth = currentMonth,
            calendarGrid = homeViewModel.getCalendarGrid(), // <--- Días calculados
            eventsByDate = eventsByDate, // <--- Eventos reales
            selectedDate = selectedDate,
            onDaySelected = { date -> selectedDate = date }
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

        // --- Contenido Principal (Tarjetas) ---
        Column(
            // ... (modificadores de Column)
        ) {

            Text(
                text = if (eventsForSelectedDay.isNotEmpty()) "Tareas para el ${selectedDate.dayOfMonth} de ${currentMonth.month.name}" else "Sin tareas",
                // ...
            )

            if (eventsForSelectedDay.isNotEmpty()) {
                eventsForSelectedDay.forEach { event ->
                    EventCard(event = event)
                }
            } else {
                // ... (Texto de día libre)
            }

            // ... (BotonCrearRutina)
        }

        // --- Footer ---
        Footer()
    }
}

// Home.kt
// ...

@Composable
fun MonthlyCalendar(
    currentMonth: YearMonth,
    calendarGrid: List<LocalDate?>,
    eventsByDate: Map<LocalDate, List<CalendarEvent>>,
    selectedDate: LocalDate,
    onDaySelected: (LocalDate) -> Unit
) {
    val daysOfWeek = listOf("L", "M", "X", "J", "V", "S", "D")
    val today = LocalDate.now()

    Column( /* ... */ ) {
        Text(
            // Aquí usamos el nombre del mes real
            text = "${currentMonth.month.name.lowercase().capitalize()} ${currentMonth.year}",
            // ...
        )

        // Títulos de los días (misma lógica)
        // ...

        // Grid del calendario (Matriz)
        calendarGrid.chunked(7).forEach { week ->
            Row( /* ... */ ) {
                week.forEach { date ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (date != null) { // Solo si la celda es un día real (no null en la cuadrícula)
                            val hasEvent = eventsByDate.containsKey(date)

                            DayCell(
                                dayNumber = date.dayOfMonth.toString(),
                                hasEvent = hasEvent,
                                isCurrentMonth = YearMonth.from(date) == currentMonth,
                                isToday = date == today,
                                isSelected = date == selectedDate,
                                onClick = { onDaySelected(date) } // <--- ¡Pasa la fecha real!
                            )
                        } else {
                            // Celda vacía para días de la cuadrícula que no representan un día
                            Spacer(modifier = Modifier.aspectRatio(1f).padding(4.dp))
                        }
                    }
                }
            }
        }
    }
}

// Y DayCell recibe el día y la lógica ya calculada
@Composable
fun DayCell(
    dayNumber: String,
    hasEvent: Boolean,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // ... (El código de DayCell permanece casi igual, solo usa los parámetros dinámicos)
}


*/