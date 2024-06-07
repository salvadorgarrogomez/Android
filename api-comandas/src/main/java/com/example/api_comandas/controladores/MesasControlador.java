package com.example.api_comandas.controladores;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
    
}
