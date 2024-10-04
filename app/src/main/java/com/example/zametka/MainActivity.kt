package com.example.zametka

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.zametka.navigation.MenuGraph
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val systemUiController = rememberSystemUiController()
            val navController = rememberNavController()
            val context = LocalContext.current
            val store = UserStore(context)
            // status bar
            SideEffect {
                systemUiController.setStatusBarColor(
                    color = Color.Black,
                    darkIcons = false
                )
            }




            MenuGraph(navController, context, store, systemUiController)

        }
    }
}




