package com.example.workwell

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.workwell.View.FirstScreen
import com.example.workwell.View.Home
import com.example.workwell.View.HomeWrapperScreen
import com.example.workwell.View.Login
import com.example.workwell.View.Signup
import com.example.workwell.ViewModel.AuthViewModel

@Composable
fun NavigationController(modifier: Modifier, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "firstScreen", builder = {
        composable("firstScreen") {
            FirstScreen(modifier,navController)
        }

        composable("login") {
            Login(modifier, navController, authViewModel)
        }

        composable("signup") {
            Signup(modifier, navController, authViewModel)
        }

        composable("home") {
            HomeWrapperScreen()
        }
    })

        /*composable("home") {
            Home(modifier, navController, authViewModel)
        }

    })*/

}
