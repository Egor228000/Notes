package com.example.zametka

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zametka.ui.theme.ZametkaTheme
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
class SplashActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZametkaTheme {
                SplashScreen()
            }
        }


    }
    @Preview
    @Composable
    private fun SplashScreen(){
        val alpha = remember {
            Animatable(0f)
        }
        LaunchedEffect(key1 = true) {
            alpha.animateTo(1f, animationSpec = tween(1500))
            delay(100)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF555454)), contentAlignment = Alignment.Center){
            Image(
                modifier = Modifier.size(200.dp).alpha(alpha.value),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "")
        }
    }

}