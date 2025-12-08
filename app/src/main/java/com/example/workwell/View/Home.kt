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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
        onLogout = { authViewModel.signout() }
    )
}

// --- ESTRUCTURA DE DATOS ---

data class CalendarEvent(
    val title: String,
    val time: Date,
    val color: Color
)

val provider = UserProviderFirebase()
val HomeViewModel = HomeViewModel(provider)

// --- HOME VIEW (VISTA PRINCIPAL) ---

@Composable
fun HomeView(name: String, tasks: List<Task>, onLogout: () -> Unit) {
    // Estado para saber qué día (índice global 0-34) está seleccionado
    var selectedDayIndex by remember { mutableStateOf(14) } // Índice de ejemplo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // --- Barra Superior ---
        Header(name)

        // --- Calendario Mensual ---
        AndroidView(
            factory = { context ->
                android.widget.CalendarView(context)
            },
            modifier = Modifier.fillMaxWidth()
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

            for (task in tasks){
                Log.d("TareaName", "taskName: ${task.name}")
                val event = CalendarEvent(task.name, task.date, Color(0xFF4CAF50))
                EventCard(event = event)
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

fun dateToString(date: Date): String {
    return "${date.dia}/${date.mes}/${date.año}"
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
                    text = dateToString(event.time),
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
                onClick = { navControllerAll.navigate("calendar") },
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
        Text("Crear Rutina")
    }
}