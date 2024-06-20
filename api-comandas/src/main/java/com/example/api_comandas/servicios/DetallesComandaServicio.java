package com.example.api_comandas.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.api_comandas.entidades.DetallesComanda;
import com.example.api_comandas.repositorios.DetallesComandaRepositorio;

@Service
public class DetallesComandaServicio {

    private final DetallesComandaRepositorio detallesComandaRepositorio;

    @Autowired
    public DetallesComandaServicio(DetallesComandaRepositorio detallesComandaRepositorio) {
        this.detallesComandaRepositorio = detallesComandaRepositorio;
    }

    public void guardarDetallesComanda(DetallesComanda detallesComanda) {
        detallesComandaRepositorio.save(detallesComanda);
    }

    public DetallesComanda obtenerDetallesComanda(Long id) {
        return detallesComandaRepositorio.findById(id).orElse(null);
    }

}
