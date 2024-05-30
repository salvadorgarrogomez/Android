package com.example.app_consultabar.Interfaces

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.app_consultabar.Models.MesaViewModel
import com.example.app_consultabar.Models.ProductoConCantidad
import com.example.app_consultabar.Services.ApiService
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.itemsIndexed


@Composable
fun DetallesMesas(navController: NavController, tableId: String?, mesaViewModel: MesaViewModel) {
    var numComensales by remember { mutableStateOf(mesaViewModel.obtenerNumComensales(tableId ?: "") ?: "") }
    var numCantidad by remember { mutableStateOf("") }
    var newProduct by remember { mutableStateOf("") }
    var selectedLineId by remember { mutableStateOf<Int?>(null) }
    val nombresProductos = remember { mutableStateListOf<String>() }
    val coroutineScope = rememberCoroutineScope()

    // Obtener la lista de productos para esta mesa del ViewModel
    val productosPorMesa = remember { mutableStateListOf<ProductoConCantidad>().apply { addAll(mesaViewModel.productosPorMesa[tableId ?: ""] ?: emptyList()) } }

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
        Text(text = "Detalles de la $tableId", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = numComensales,
            onValueChange = {
                numComensales = it
                // Guardar el número de comensales en el ViewModel
                mesaViewModel.establecerNumComensales(tableId ?: "", it)
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
            // Cuando agregas un producto, guárdalo en el ViewModel
            Button(onClick = {
                if (newProduct.isNotEmpty() && numCantidad.isNotEmpty()) {
                    val cantidad = numCantidad.toIntOrNull()
                    if (cantidad != null) {
                        val precioUnitario = newProduct.split(" - ")[1].replace(",", ".").replace("€", "").toDouble()
                        val producto = ProductoConCantidad(newProduct, cantidad, precioUnitario)
                        // Agregar o actualizar el producto en el ViewModel
                        mesaViewModel.agregarProducto(tableId ?: "", producto)
                        // Actualizar el estado de la lista de productos
                        val existingIndex = productosPorMesa.indexOfFirst { it.producto.split(" - ")[0] == newProduct.split(" - ")[0] }
                        if (existingIndex != -1) {
                            productosPorMesa[existingIndex] = productosPorMesa[existingIndex].copy(
                                precio = ((productosPorMesa[existingIndex].precio * productosPorMesa[existingIndex].cantidad) + (producto.precio * producto.cantidad)) / (productosPorMesa[existingIndex].cantidad + producto.cantidad)
                            )
                        } else {
                            productosPorMesa.add(producto)
                        }
                        // Forzar una recomposición del Composable
                        productosPorMesa.add(ProductoConCantidad("", 0, 0.0))
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

        LazyColumn {
            itemsIndexed(productosPorMesa) { index, product ->
                val nombreYPrecio = product.producto.split(" - ")
                val nombre = nombreYPrecio[0]
                val precio = nombreYPrecio[1]
                val cantidad = product.cantidad
                val totalPrecio = product.cantidad * product.precio

                val isSelected = selectedLineId == index
                val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            // Guardar el ID del producto seleccionado
                            selectedLineId = index
                        }
                        .background(backgroundColor)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = textColor)) {
                                append(nombre)
                            }
                            append(" - Cantidad: $cantidad - Precio unitario: $precio - Precio total: $totalPrecio€")
                        },
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        LaunchedEffect(productosPorMesa) {
            if (selectedLineId != null && selectedLineId !in productosPorMesa.indices) {
                // Si el elemento seleccionado ya no está en la lista, restablecer selectedLineId a null
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
                Button(
                    onClick = {
                        // Implementación para borrar la línea seleccionada
                        selectedLineId?.let { index ->
                            val productName = productosPorMesa.getOrNull(index)?.producto?.split(" - ")?.get(0)
                            productName?.let { productId ->
                                mesaViewModel.eliminarProducto(tableId ?: "", productId)
                                // Actualizar el estado de la lista de productos
                                productosPorMesa.removeAt(index)
                                // Restablecer selectedLineId a null
                                selectedLineId = null
                            }
                        }
                    }
                ) {
                    Text("Borrar línea")
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

