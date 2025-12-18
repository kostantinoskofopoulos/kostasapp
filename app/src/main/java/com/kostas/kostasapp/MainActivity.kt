package com.kostas.kostasapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kostas.common.logging.Logger
import com.kostas.kostasapp.core.designsystem.SuperheroAppTheme
import com.kostas.kostasapp.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var logger: Logger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SuperheroAppTheme {
                AppNavGraph(logger = logger)
            }
        }
    }
}