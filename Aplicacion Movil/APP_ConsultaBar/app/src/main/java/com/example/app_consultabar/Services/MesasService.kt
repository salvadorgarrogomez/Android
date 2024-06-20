package com.example.app_consultabar.Services

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface MesasDatosService {
    @GET("/mesas")
    suspend fun getDatosMesas(): List<String>
    @PUT("/mesas/{id}/cambiar-estado")
    suspend fun cambiarEstadoMesa(@Path("id") id: Long, @Body estadoMap: Map<String, String>): Response<Void>
    @PUT("/mesas/{id}/aniadir-comensales")
    suspend fun actualizarComensalesMesa(@Path("id") id: Long, @Body comensalesMap: Map<String, Int>): Response<Void>
}












