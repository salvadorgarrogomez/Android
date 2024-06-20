package com.example.app_consultabar.Models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_consultabar.Services.ApiService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Response

class MesaViewModel : ViewModel() {
    private val _tables = MutableLiveData<List<Mesas>>()
    val tables: LiveData<List<Mesas>> = _tables

    init {
        viewModelScope.launch {
            obtenerDatosMesasPeriodicamente().collect { datos ->
                val tableEstados = datos.map { dato ->
                    val partes = dato.split(" - ")
                    Mesas(
                        id = partes[0].toInt(),
                        name = partes[1],
                        estado = partes[3],
                        comensales = partes[2].toInt()
                    )
                }
                _tables.value = tableEstados
            }
        }
    }

    private fun obtenerDatosMesasPeriodicamente(): Flow<List<String>> = flow {
        while (true) {
            try {
                val datos = ApiService.tablesDatosService.getDatosMesas()
                emit(datos)
            } catch (e: Exception) {
                e.printStackTrace()
                emit(emptyList())
            }
            delay(5000) // Intervalo de actualización de 5 segundos
        }
    }
    
    suspend fun cambiarEstadoMesa(id: Long, nuevoEstado: String): Response<Void> {
        return ApiService.tablesDatosService.cambiarEstadoMesa(id, mapOf("estado" to nuevoEstado))
    }
    suspend fun actualizarComensalesMesa(id: Long, nuevoComensales: Int): Response<Void> {
        return ApiService.tablesDatosService.actualizarComensalesMesa(id, mapOf("comensales" to nuevoComensales))
    }

}











