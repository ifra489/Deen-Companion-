package com.deencompanion.app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.deencompanion.app.presentation.ui.auth.AuthViewModel
import com.deencompanion.app.util.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Image(
                painter = painterResource(id = R.drawable.splash_logo),
                contentDescription = "Splash Screen",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            LaunchedEffect(Unit) {
                val startTime = System.currentTimeMillis()
                
                authViewModel.authResult.collect { result ->
                    if (result !is UiState.Loading) {
                        val elapsed = System.currentTimeMillis() - startTime
                        val remainingDelay = 1200 - elapsed
                        if (remainingDelay > 0) {
                            delay(remainingDelay)
                        }
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }
}
