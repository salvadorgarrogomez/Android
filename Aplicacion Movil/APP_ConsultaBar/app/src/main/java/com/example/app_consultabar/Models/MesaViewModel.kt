package com.example.app_consultabar.Models

import androidx.lifecycle.ViewModel


class MesaViewModel : ViewModel() {
    val productosPorMesa: MutableMap<String, MutableList<ProductoConCantidad>> = mutableMapOf()
    val comensalesPorMesa: MutableMap<String, String> = mutableMapOf()

    fun agregarProducto(tableId: String, producto: ProductoConCantidad) {
        val nombreSolo = producto.producto.split(" - ")[0]
        // Obtener la lista de productos para esta mesa
        val productosMesa = productosPorMesa.getOrPut(tableId) { mutableListOf() }

        // Buscar el producto en la lista para esta mesa
        val existingProduct = productosMesa.find { it.producto.split(" - ")[0] == nombreSolo }
        if (existingProduct != null) {
            // Si el producto ya existe, actualizar la cantidad y el precio medio
            val nuevaCantidad = existingProduct.cantidad + producto.cantidad
            existingProduct.precio = ((existingProduct.precio * existingProduct.cantidad) + (producto.precio * producto.cantidad)) / nuevaCantidad
            existingProduct.cantidad = nuevaCantidad
        } else {
            // Si el producto no existe, agregarlo a la lista
            productosMesa.add(producto)
        }
    }

    fun establecerNumComensales(tableId: String, numComensales: String) {
        comensalesPorMesa[tableId] = numComensales
    }

    fun obtenerNumComensales(tableId: String): String? {
        return comensalesPorMesa[tableId]
    }

    fun eliminarProducto(tableId: String, productName: String) {
        val productosMesa = productosPorMesa[tableId] ?: return
        productosMesa.removeAll { it.producto.startsWith(productName) }
    }
}





