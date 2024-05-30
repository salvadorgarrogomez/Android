package com.example.app_consultabar.Models

data class Table(
    val id: Int,
    val name: String,
    val products: List<Productos>
)

data class ProductoConCantidad(
    val producto: String,
    var cantidad: Int,
    var precio: Double
)

data class Productos(
    val id: Int,
    val name: String,
    val precio: Double,
    val tipoPorcion: String
)

