package com.example.api_comandas.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.api_comandas.entidades.DetallesComanda;
import com.example.api_comandas.servicios.DetallesComandaServicio;

@RestController
public class DetallesComandaController {

    private final DetallesComandaServicio detallesComandaService;

    @Autowired
    public DetallesComandaController(DetallesComandaServicio detallesComandaService) {
        this.detallesComandaService = detallesComandaService;
    }

    @PostMapping("/detalles-comanda")
    public ResponseEntity<String> agregarDetallesComanda(@RequestBody DetallesComanda detallesComanda) {
        detallesComandaService.guardarDetallesComanda(detallesComanda);
        return ResponseEntity.ok("Detalles de comanda guardados correctamente");
    }

    @GetMapping("/detalles-comanda/{id}")
    public ResponseEntity<DetallesComanda> obtenerDetallesComanda(@PathVariable Long id) {
        DetallesComanda detallesComanda = detallesComandaService.obtenerDetallesComanda(id);
        if (detallesComanda != null) {
            return ResponseEntity.ok(detallesComanda);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

