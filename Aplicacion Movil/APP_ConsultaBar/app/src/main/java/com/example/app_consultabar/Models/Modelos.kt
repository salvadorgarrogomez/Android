package com.example.app_consultabar.Models

data class Productos(
    val producto: String,
    var cantidad: Int,
    var precio: Double
)

data class Mesas(
    val id: Int,
    val name: String,
    val estado: String,
    val comensales: Int
)


