package com.example.workwell.View

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.workwell.Model.Date
import com.example.workwell.Model.Task
import com.example.workwell.Model.UserProviderFirebase
import com.example.workwell.R
import com.example.workwell.ViewModel.AuthState
import com.example.workwell.ViewModel.AuthViewModel
import com.example.workwell.ViewModel.HomeViewModel
import com.example.workwell.ui.theme.AzulBotonLogin
import com.example.workwell.ui.theme.AzulNav
import com.google.firebase.auth.FirebaseAuth

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
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val name by viewModel.name.collectAsState()
    val tasks by viewModel.tasks.collectAsState() //Lista con todas las tareas
    Log.d("Tarea", "tasks: $tasks")
    val authState by authViewModel.authState.observeAsState()

    navControllerAll = navController

    val currentBackStackEntry = navController.currentBackStackEntry

    // 2. Obtenemos el LiveData. Puede ser nulo, así que no usamos 'by' todavía.
    val refreshLiveData = currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("refresh_routines")

    // 3. Observamos el estado. Si refreshLiveData es nulo (pasa a veces al iniciar),
    // usamos un estado falso por defecto.
    val refreshResult by refreshLiveData?.observeAsState(initial = false)
        ?: remember { mutableStateOf(false) }

    // 4. Reaccionamos al cambio
    LaunchedEffect(refreshResult) {
        if (refreshResult) {
            Log.d("HomeWrapper", "Señal de refresco recibida. Recargando tareas...")

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                viewModel.loadUserTask(userId)
            }

            // Limpiamos la señal para que no se ejecute en bucle
            currentBackStackEntry?.savedStateHandle?.set("refresh_routines", false)
        }
    }

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
        tasks = tasks,
        //onLogout = { authViewModel.signout() }
    )
}

// --- ESTRUCTURA DE DATOS ---

data class CalendarEvent(
    val title: String,
    val startTime: Date,
    val endTime: Date,
    val color: Color
)

val provider = UserProviderFirebase()
val HomeViewModel = HomeViewModel(provider)

val priorities = listOf("Alta", "Media", "Baja")
val priorityColors = listOf(
    Color(0xFFFF2020), // Rojo para Alta
    Color(0xFFFF9800), // Naranja para Media
    Color(0xFF4CAF50)  // Verde para Baja
)

// --- HOME VIEW (VISTA PRINCIPAL) ---

@Composable
fun HomeView(name: String, tasks: List<Task>) {
    // --- TUS VARIABLES DE ESTADO (Igual que antes) ---
    var selectedDateText by remember { mutableStateOf("Selecciona una fecha") }
    var selectedDay by remember { mutableStateOf(-1) }
    var selectedMonth by remember { mutableStateOf(-1) }
    var selectedYear by remember { mutableStateOf(-1) }

    val savedStateHandle = navControllerAll.currentBackStackEntry?.savedStateHandle
    val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

    // --- TU LAUNCHED EFFECT (Igual que antes) ---
    LaunchedEffect(Unit) {
        val result = savedStateHandle?.get<Boolean>("refresh_routines")
        if (result == true) {
            provider.getUserTask(currentUserId)
            savedStateHandle.remove<Boolean>("refresh_routines")
        }
    }

    // --- ESTRUCTURA UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Top // Importante: Top para que empiece arriba
    ) {
        // ---------------------------------------------------------
        // 1. ZONA SUPERIOR FIJA (Header y Calendario)
        // ---------------------------------------------------------
        Header(name)

        AndroidView(
            factory = { context ->
                android.widget.CalendarView(context).apply {
                    setOnDateChangeListener { view, year, month, dayOfMonth ->
                        val realMonth = month + 1
                        selectedDateText = "$dayOfMonth/$realMonth/$year"
                        selectedDay = dayOfMonth
                        selectedMonth = realMonth
                        selectedYear = year
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Fecha seleccionada: $selectedDateText",
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
            color = Color.DarkGray
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

        // ESTE BOX OCUPA EL RESTO DE LA PANTALLA (Contiene Lista + Footer flotante)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // ---------------------------------------------------------
            // 1. LA LISTA (Ahora recortada con padding)
            // ---------------------------------------------------------
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // --- CAMBIO CLAVE AQUÍ ---
                    // 1. Mantenemos el margen horizontal de 20.dp
                    // 2. Añadimos margen inferior (bottom) de 100.dp (aprox altura del footer + margen)
                    // Esto hace que el scroll se "corte" antes de llegar al footer.
                    .padding(start = 20.dp, end = 30.dp, bottom = 100.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- A) LÓGICA DE FILTRADO (Igual) ---
                val filteredTasks = if (selectedDay != -1) {
                    tasks.filter {
                        it.startDate.dia == selectedDay &&
                                it.startDate.mes == selectedMonth &&
                                it.startDate.año == selectedYear
                    }
                } else {
                    emptyList()
                }

                // --- B) BUCLE TARJETAS (Igual) ---
                for (task in filteredTasks) {
                    val colorIndex = priorities.indexOf(task.priority)
                    val safeColorIndex = if (colorIndex >= 0) colorIndex else 1
                    val color = priorityColors[safeColorIndex]

                    val event = CalendarEvent(task.name, task.startDate, task.endDate, color)
                    EventCard(event = event)
                }

                // --- C) MENSAJE VACÍO (Igual) ---
                if (filteredTasks.isEmpty() && selectedDay != -1) {
                    Text(
                        text = "No hay tareas para este día",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- D) BOTÓN (Igual) ---
                BotonCrearRutina()

                Spacer(modifier = Modifier.height(50.dp))
            }

            // ---------------------------------------------------------
            // 2. EL FOOTER CON SOMBRA MANUAL (Capa superior flotante)
            // ---------------------------------------------------------
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter) // Pegado abajo
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .fillMaxWidth()
            ) {
                // TRUCO DE SOMBRA HACIA ARRIBA
                // Dibujamos un degradado transparente->negro justo ENCIMA del footer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp) // Altura de la "sombra"
                        .align(Alignment.TopCenter) // Se alinea arriba del contenedor del footer
                        .offset(y = (-30).dp) // Lo subimos 30dp para que asome por encima
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,             // Arriba transparente
                                    Color.Black.copy(alpha = 0.1f) // Abajo gris suave
                                )
                            )
                        )
                )

                // TU FOOTER ORIGINAL
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        Footer()
                    }
                }
            }
        }
    }
}

fun dateToString(date: Date): String {
    return "${date.dia}/${date.mes}/${date.año}"
}

fun hourToString(date: Date): String {
    val horaFormateada = date.hora.toString().padStart(2, '0')
    val minutoFormateado = date.minuto.toString().padStart(2, '0')

    return "$horaFormateada:$minutoFormateado"
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
                    text = hourToString(event.startTime) + " - " + hourToString(event.endTime),
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
                onClick = { navControllerAll.navigate("home") },
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
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
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Perfil",
                    tint = Color.White
                )
            }

            IconButton(
                onClick = { navControllerAll.navigate("habit") },
                modifier = Modifier
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.GridView,
                    contentDescription = "Contenido de salud",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun BotonCrearRutina(
){
    OutlinedButton(
        onClick = { navControllerAll.navigate("createRoutine") },
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