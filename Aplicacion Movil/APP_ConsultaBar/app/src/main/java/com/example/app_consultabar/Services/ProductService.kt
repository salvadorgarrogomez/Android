package com.example.app_consultabar.Services

import retrofit2.Response
import retrofit2.http.GET

interface ProductService {
    @GET("/productos")
    suspend fun obtenerDatosProductos(): Response<List<String>>
}





