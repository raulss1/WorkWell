package com.example.workwell

import androidx.compose.runtime.Composable
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
import com.example.workwell.View.CreateRoutine
import com.example.workwell.ViewModel.AuthViewModel


@Composable
fun NavigationController(modifier: Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val sharedAuthViewModel: AuthViewModel = viewModel()

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
            CreateRoutine(navController)
        }
    })

        /*composable("home") {
            Home(modifier, navController, authViewModel)
        }

    })*/

}
