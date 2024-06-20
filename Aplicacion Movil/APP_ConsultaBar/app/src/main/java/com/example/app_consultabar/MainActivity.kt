package com.example.app_consultabar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app_consultabar.Interfaces.DetallesMesas
import com.example.app_consultabar.Interfaces.ImagenInicio
import com.example.app_consultabar.Interfaces.PantallaMesas
import com.example.app_consultabar.Models.ProductoViewModel

class MainActivity : ComponentActivity() {
    private val productoViewModel by viewModels<ProductoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            MyApp(navController = navController, productoViewModel = productoViewModel)
            }
        }
    }

@Composable
fun MyApp(navController: NavController, productoViewModel: ProductoViewModel) {
    NavHost(navController = navController as NavHostController, startDestination = "splash") {
        composable("splash") {
            ImagenInicio(navController)
        }
        composable("tables") {
            PantallaMesas(navController)
        }
        composable(
            "table/{tableId}/{tableName}",
            arguments = listOf(
                navArgument("tableId") { type = NavType.LongType },
                navArgument("tableName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tableId = backStackEntry.arguments?.getLong("tableId")
            val tableName = backStackEntry.arguments?.getString("tableName")
            if (tableId != null) {
                DetallesMesas(
                    navController,
                    tableId,
                    tableName,
                    productoViewModel)
            }
        }
    }
}
