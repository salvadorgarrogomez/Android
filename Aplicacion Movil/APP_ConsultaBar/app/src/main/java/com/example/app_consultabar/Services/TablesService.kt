package com.example.app_consultabar.Services

import com.example.app_consultabar.Models.Mesas
import retrofit2.http.GET

interface TablesService {
    @GET("/mesas/nombre")
    suspend fun getTables(): List<String>
}

interface TablesEstadoService {
    @GET("/mesas/estado")
    suspend fun getEstadoMesas(): List<String> // La API devuelve una lista de strings
}


interface TablesDatosService {
    @GET("/mesas")
    suspend fun getDatosMesas(): List<String>
}








