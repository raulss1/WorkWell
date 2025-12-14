package com.example.workwell

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.workwell.View.FirstScreen
import com.example.workwell.View.HomeWrapperScreen
import com.example.workwell.View.Login
import com.example.workwell.View.Signup
import com.example.workwell.View.Profile
import com.example.workwell.View.Calendar
import com.example.workwell.View.HabitContent
import com.example.workwell.View.HabitDetailScreen
import com.example.workwell.View.HabitRoutes
import com.example.workwell.View.SectionDetailScreen
import com.example.workwell.View.CreateRoutineView
import com.example.workwell.ViewModel.AuthViewModel
import com.example.workwell.ViewModel.HabitsViewModel


@Composable
fun NavigationController(modifier: Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val sharedAuthViewModel: AuthViewModel = viewModel()
    val habitsViewModel: HabitsViewModel = viewModel()

    NavHost(navController, startDestination = "firstScreen", builder = {
        composable("firstScreen") {
            FirstScreen(modifier,navController)
        }

        composable("login") {
            Login(modifier, navController, sharedAuthViewModel)
        }

        composable("signup") {
            Signup(modifier, navController, sharedAuthViewModel)
        }

        composable("home") {
            HomeWrapperScreen(authViewModel = sharedAuthViewModel, navController = navController)
        }

        composable("profile") {
            Profile(modifier)
        }
        composable("calendar") {
            Calendar(modifier)
        }

        composable("createRoutine") {
            CreateRoutineView(navController)
        }

        composable("habit") {
            HabitContent(modifier, navController = navController)
        }
        composable(HabitRoutes.DETAIL_LEVEL_1) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId")
            val uiState by habitsViewModel.state.observeAsState()

            val habit = (uiState as? HabitsViewModel.HabitsUiState.Success)
                ?.habits
                ?.find { it.id == habitId }

            if (habit != null) {
                HabitDetailScreen(habit = habit, navController = navController)
            } else if (uiState is HabitsViewModel.HabitsUiState.Loading) {
                Text("Cargando detalles...")
            } else {
                Text("Error: Hábito no encontrado o fallo de carga.")
            }
        }

        composable(HabitRoutes.DETAIL_LEVEL_2) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getString("habitId")
            val sectionTitle = backStackEntry.arguments?.getString("sectionTitle")
            val uiState by habitsViewModel.state.observeAsState()

            val section = (uiState as? HabitsViewModel.HabitsUiState.Success)
                ?.habits
                ?.find { it.id == habitId }
                ?.sections
                ?.find { it.title == sectionTitle }

            if (section != null) {
                SectionDetailScreen(section = section, navController = navController)
            } else {
                Text("Error: Sección no encontrada.")
            }
        }
    })

}
