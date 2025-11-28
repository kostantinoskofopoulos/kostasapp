package com.kostas.kostasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.kostas.kostasapp.core.designsystem.SuperheroAppTheme
import com.kostas.kostasapp.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SuperheroAppTheme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}