package com.example.app_consultabar.Interfaces

import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app_consultabar.Models.MesaViewModel
import com.example.app_consultabar.Models.Mesas
import com.example.app_consultabar.R


@Composable
fun PantallaMesas(navController: NavController, viewModel: MesaViewModel = viewModel()) {
    val tables by viewModel.tables.observeAsState(emptyList())

    if (tables.isEmpty()) {
        // Mostrar mensaje cuando no hay mesas disponibles y un botón para reintentar la conexión
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "La API no está activa",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Revisar el equipo TPV/Servidor.",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = painterResource(R.drawable.error_conexion),
                    contentDescription = "Error de conexión",
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.tables }) {
                    Text("Reintentar conexión")
                }
            }
        }
    } else {
        // Mostrar la lista de mesas
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(tables) { table ->
                TableCard(table, navController)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// Actualización en TableCard.kt
@Composable
fun TableCard(table: Mesas, navController: NavController) {
    val backgroundColor = when (table.estado) {
        "libre" -> Color.Green // Color para mesas libres
        "ocupado" -> Color.Red // Color para mesas ocupadas
        else -> MaterialTheme.colorScheme.surface // Color por defecto
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("table/${table.id}/${table.name}") }
            .padding(8.dp)
            .background(color = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = table.name)
        }
    }
}



















