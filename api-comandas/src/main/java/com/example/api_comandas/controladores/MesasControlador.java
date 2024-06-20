package com.example.api_comandas.controladores;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.api_comandas.servicios.MesasServicio;

@RestController
public class MesasControlador {

    private final MesasServicio mesasServicio;

    @Autowired
    public MesasControlador(MesasServicio mesasServicio) {
        this.mesasServicio = mesasServicio;
    }

    @GetMapping("/mesas")
    public List<String> obtenerDatosDeMesas() {
        return mesasServicio.obtenerDatosDeMesas();
    }

    @GetMapping("/mesas/nombre")
    public List<String> obtenerNombreDeMesas() {
        return mesasServicio.obtenerNombreDeMesas();
    }

    @GetMapping("/mesas/estado")
    public List<String> obtenerEstadoDeMesas() {
        return mesasServicio.obtenerEstadoDeMesas();
    }

    @GetMapping("/mesas/comensales")
    public List<Integer> obtenerComenaslesDeMesas() {
        return mesasServicio.obtenerComenaslesDeMesas();
    }

    @PutMapping("/mesas/{id}/cambiar-estado")
    public ResponseEntity<String> cambiarEstadoMesa(@PathVariable Long id, @RequestBody Map<String, String> estadoMap) {
        String nuevoEstado = estadoMap.get("estado");
        mesasServicio.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok("Estado de la mesa actualizado correctamente");
    }

    @PutMapping("/mesas/{id}/aniadir-comensales")
    public ResponseEntity<String> cambiarComensales(@PathVariable Long id, @RequestBody Map<String, Integer> comensalesMap) {
        Integer nuevoComensales = comensalesMap.get("comensales");
        mesasServicio.actualizarComensales(id, nuevoComensales);
        return ResponseEntity.ok("Comensales añadidos correctamente");
    }
}
