package com.example.app_consultabar.Interfaces

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.app_consultabar.Models.Productos
import com.example.app_consultabar.Services.ApiService
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app_consultabar.Models.ProductoViewModel
import java.text.DecimalFormat
import com.example.app_consultabar.Models.MesaViewModel
import com.example.app_consultabar.Services.WebSocketManager
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


@Composable
fun DetallesMesas(
    navController: NavController,
    tableId: Long?,
    tableName: String?,
    productoViewModel: ProductoViewModel = viewModel(),
    mesaViewModel: MesaViewModel = viewModel()
) {
    var numComensales by remember { mutableStateOf(productoViewModel.obtenerNumComensales((tableId ?: "").toString()) ?: "") }
    var numCantidad by remember { mutableStateOf("") }
    var newProduct by remember { mutableStateOf("") }
    var selectedLineId by remember { mutableStateOf<Int?>(null) }
    val nombresProductos = remember { mutableStateListOf<String>() }
    val coroutineScope = rememberCoroutineScope()

    val mesas = mesaViewModel.tables.observeAsState()
    val mesaSeleccionada = mesas.value?.find { it.id == tableId?.toInt() }

    val productosPorMesa = remember {
        mutableStateListOf<Productos>().apply {
            addAll(productoViewModel.obtenerProductosPorMesa(tableId ?: -1))
        }
    }

    val webSocketManager = remember {
        WebSocketManager { message ->
            val json = Gson().fromJson(message, Map::class.java)
            val receivedTableId = (json["tableId"] as Double).toLong()
            if (receivedTableId == tableId) {
                val productos = Gson().fromJson(Gson().toJson(json["productos"]), Array<Productos>::class.java).toList()
                productosPorMesa.clear()
                productosPorMesa.addAll(productos)
            }
        }
    }

    DisposableEffect(Unit) {
        webSocketManager.connect()
        onDispose {
            webSocketManager.close()
        }
    }


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = ApiService.productService.obtenerDatosProductos()
                if (response.isSuccessful) {
                    val productos = response.body()
                    productos?.let {
                        nombresProductos.addAll(it)
                        Log.d("DetallesMesas", "Productos obtenidos: ${nombresProductos.size}")
                    } ?: run {
                        Log.d("DetallesMesas", "No se obtuvieron productos")
                    }
                } else {
                    Log.e("DetallesMesas", "Error en la solicitud: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("DetallesMesas", "Excepción al obtener productos", e)
            }
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() }) {
            Text("Atrás")
        }
    }

    Column(modifier = Modifier.padding(5.dp)) {
        Text(text = "Detalles de la $tableName", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = numComensales,
            onValueChange = {
                numComensales = it
                productoViewModel.establecerNumComensales((tableId ?: "").toString(), (it.toIntOrNull() ?: 0).toString())
            },
            label = { Text("Número de Comensales") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = numCantidad,
            onValueChange = { numCantidad = it },
            label = { Text("Cantidad de producto/plato:") },
            modifier = Modifier.fillMaxWidth()
        )

        AutoCompleteTextField(
            suggestions = nombresProductos,
            value = newProduct,
            onValueChange = { newProduct = it },
            onSuggestionClick = { selectedProduct ->
                newProduct = selectedProduct
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                if (newProduct.isNotEmpty() && numCantidad.isNotEmpty()) {
                    val cantidad = numCantidad.toIntOrNull()
                    if (cantidad != null) {
                        val precioUnitario = newProduct.split(" - ")[1].replace(",", ".").replace("€", "").toDouble()
                        val producto = Productos(newProduct, cantidad, precioUnitario)

                        coroutineScope.launch {
                            productoViewModel.agregarProducto(tableId ?: 0L, producto)

                            // Obtener y enviar la lista actualizada de productos al WebSocket
                            val productos = productoViewModel.obtenerProductosPorMesa(tableId ?: -1)
                            webSocketManager.send(tableId ?: 0L, productos)
                        }


                        val existingIndex = productosPorMesa.indexOfFirst {
                            it.producto.split(" - ")[0] == newProduct.split(" - ")[0]
                        }
                        if (existingIndex != -1) {
                            productosPorMesa[existingIndex] = productosPorMesa[existingIndex].copy(
                                precio = ((productosPorMesa[existingIndex].precio * productosPorMesa[existingIndex].cantidad) + (producto.precio * producto.cantidad)) / (productosPorMesa[existingIndex].cantidad + producto.cantidad)
                            )
                        } else {
                            productosPorMesa.add(producto)
                        }

                        Log.d("DetallesMesas", "ID de la mesa: $tableId")
                        val nuevoEstadoMesa = "ocupado"
                        val nuevoComensales = numComensales.toIntOrNull() ?: 0

                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val estadoResponse = mesaViewModel.cambiarEstadoMesa(tableId ?: 0L, nuevoEstadoMesa)
                                if (estadoResponse.isSuccessful) {
                                    Log.d("DetallesMesasScreen", "Estado de la mesa actualizado correctamente")
                                } else {
                                    Log.e("DetallesMesasScreen", "Error al actualizar estado de la mesa: ${estadoResponse.code()} - ${estadoResponse.message()}")
                                }
                                val comensalesResponse = mesaViewModel.actualizarComensalesMesa(tableId ?: 0L, nuevoComensales)
                                if (comensalesResponse.isSuccessful) {
                                    Log.d("DetallesMesasScreen", "Comensales de la mesa actualizados correctamente")
                                } else {
                                    Log.e("DetallesMesasScreen", "Error al actualizar comensales de la mesa: ${comensalesResponse.code()} - ${comensalesResponse.message()}")
                                }
                            } catch (e: Exception) {
                                Log.e("DetallesMesasScreen", "Error de red al actualizar estado o comensales de la mesa", e)
                            }
                        }

                        productosPorMesa.add(Productos("", 0, 0.0))
                        productosPorMesa.removeLast()
                        newProduct = ""
                        numCantidad = ""
                    }
                }
            }) {
                Text("Agregar Producto")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Comensales en la mesa: $numComensales")
        Spacer(modifier = Modifier.height(16.dp))

        Text("Comensales en la mesa confirmados: ${mesaSeleccionada?.comensales ?: 0}")
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            itemsIndexed(productosPorMesa) { index, product ->
                val nombreYPrecio = product.producto.split(" - ")
                val nombre = nombreYPrecio[0]
                val precio = nombreYPrecio[1]
                val cantidad = product.cantidad

                val totalPrecio = product.cantidad * product.precio
                val decimalFormat = DecimalFormat("#,###.00")
                val totalPrecioFormateado = decimalFormat.format(totalPrecio)

                val isSelected = selectedLineId == index
                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            selectedLineId = index
                        }
                        .background(backgroundColor)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = textColor)) {
                                append(nombre)
                            }
                            append(" - Cantidad: $cantidad - Precio unitario: $precio - Precio total: $totalPrecioFormateado€")
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        LaunchedEffect(productosPorMesa) {
            if (selectedLineId != null && selectedLineId !in productosPorMesa.indices) {
                selectedLineId = null
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Button(onClick = { /* Guardar cambios en la API */ }) {
                Text("Guardar")
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    selectedLineId?.let { selectedIndex ->
                        val selectedProduct = productosPorMesa[selectedIndex]
                        coroutineScope.launch {
                            productoViewModel.eliminarProducto(tableId ?: 0L, selectedProduct.producto)

                            // Enviar el mensaje al WebSocket
                            val productos = productoViewModel.obtenerProductosPorMesa(tableId ?: -1)
                            webSocketManager.send(tableId ?: 0L, productos)
                        }
                        productosPorMesa.removeAt(selectedIndex)
                        selectedLineId = null
                    }
                }) {
                    Text("Eliminar Producto Seleccionado")
                }
            }
        }
    }
}

@Composable
fun AutoCompleteTextField(
    suggestions: List<String>,
    value: String,
    onValueChange: (String) -> Unit,
    onSuggestionClick: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var filteredSuggestions by remember { mutableStateOf(emptyList<String>()) }

    Column {
        TextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(newValue)
                expanded = true
                filteredSuggestions = suggestions.filter {
                    it.startsWith(newValue, ignoreCase = true)
                }
            },
            label = { Text("Nuevo Producto") },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded && filteredSuggestions.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            filteredSuggestions.forEach { suggestion ->
                DropdownMenuItem(
                    onClick = {
                        onSuggestionClick(suggestion)
                        expanded = false
                    },
                    text = { Text(suggestion) }
                )
            }
        }
    }
}
