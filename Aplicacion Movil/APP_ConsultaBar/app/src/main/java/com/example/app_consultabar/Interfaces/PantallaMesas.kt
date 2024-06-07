package com.example.app_consultabar.Interfaces

import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app_consultabar.Models.MesaViewModel
import com.example.app_consultabar.Models.TableEstado

@Composable
fun PantallaMesas(navController: NavController, viewModel: MesaViewModel = viewModel()) {
    val tables by viewModel.tables.observeAsState(emptyList())

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(tables) { table ->
            TableCard(table, navController)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// Actualización en TableCard.kt
@Composable
fun TableCard(table: TableEstado, navController: NavController) {
    val backgroundColor = when (table.estado) {
        "libre" -> Color.Green // Color para mesas libres
        "ocupado" -> Color.Red // Color para mesas ocupadas
        else -> MaterialTheme.colorScheme.surface // Color por defecto
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("table/${table.name}") }
            .padding(8.dp)
            .background(color = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = table.name)
        }
    }
}



















