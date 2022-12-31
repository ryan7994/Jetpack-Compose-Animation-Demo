package com.example.jetpackcomposeanimation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposeanimation.ui.theme.JetpackComposeAnimationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeAnimationTheme {

                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "sneaker") {

                    composable("Home") {
                        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                            Column {
                                Text(text = "Sneaker", modifier = Modifier.clickable {
                                    navController.navigate("sneaker")
                                })
                            }
                        }
                    }

                    composable("sneaker") {
                        SneakerDetail()
                    }


                }


            }
        }
    }
}
