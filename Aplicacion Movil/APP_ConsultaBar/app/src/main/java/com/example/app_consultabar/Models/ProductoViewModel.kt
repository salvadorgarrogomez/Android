package com.example.app_consultabar.Models

import androidx.lifecycle.ViewModel


class ProductoViewModel : ViewModel() {
    val comensalesPorMesa: MutableMap<String, String> = mutableMapOf()
    private val productosPorMesa: MutableMap<Long, MutableList<Productos>> = mutableMapOf()

    fun agregarProducto(tableId: Long, producto: Productos) {
        val nombreSolo = producto.producto.split(" - ")[0]
        val productosMesa = productosPorMesa.getOrPut(tableId) { mutableListOf() }

        val existingProduct = productosMesa.find { it.producto.split(" - ")[0] == nombreSolo }
        if (existingProduct != null) {
            val nuevaCantidad = existingProduct.cantidad + producto.cantidad
            existingProduct.precio = ((existingProduct.precio * existingProduct.cantidad) + (producto.precio * producto.cantidad)) / nuevaCantidad
            existingProduct.cantidad = nuevaCantidad
        } else {
            productosMesa.add(producto)
        }
    }

    fun eliminarProducto(tableId: Long, productName: String) {
        val productosMesa = productosPorMesa[tableId] ?: return
        productosMesa.removeAll { it.producto.startsWith(productName) }
    }

    fun obtenerProductosPorMesa(tableId: Long): List<Productos> {
        return productosPorMesa[tableId] ?: emptyList()
    }

    fun establecerNumComensales(tableId: String, numComensales: String) {
        comensalesPorMesa[tableId] = numComensales
    }

    fun obtenerNumComensales(tableId: String): String? {
        return comensalesPorMesa[tableId]
    }
}







