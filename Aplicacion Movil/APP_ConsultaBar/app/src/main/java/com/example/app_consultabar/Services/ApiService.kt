package com.example.app_consultabar.Services

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiService {
    private const val BASE_URL = "http://192.168.1.140:8081/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val productService: ProductService = retrofit.create(ProductService::class.java)
    val tableService: TablesService = retrofit.create(TablesService::class.java)
    val tablesEstadoService: TablesEstadoService = retrofit.create(TablesEstadoService::class.java)
    val tablesDatosService: TablesDatosService = retrofit.create(TablesDatosService::class.java)


}
