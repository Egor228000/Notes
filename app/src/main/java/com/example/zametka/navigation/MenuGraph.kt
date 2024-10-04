package com.example.zametka.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.zametka.GlavScreen
import com.example.zametka.Prew
import com.example.zametka.UserStore
import com.google.accompanist.systemuicontroller.SystemUiController


@Composable
fun MenuGraph(navController: NavHostController, context: Context, store: UserStore, systemUiController: SystemUiController) {

    var isLoggedIn by remember { mutableStateOf(store.isLoggedIn) }

    Scaffold(Modifier.padding()) { padding ->
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "glav_screen" else "prew",
            Modifier.padding(padding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            composable("prew") {
                Prew(navController, context, store)
            }
            composable("glav_screen",
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(
                            200, easing = LinearEasing
                        )
                    ) + slideIntoContainer(
                        animationSpec = tween(200, easing = EaseIn),
                        towards = AnimatedContentTransitionScope.SlideDirection.Start
                    )
                }) {
                GlavScreen(context, store, systemUiController)
            }
        }
    }
}






