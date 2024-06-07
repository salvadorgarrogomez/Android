package com.example.api_comandas.servicios;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.api_comandas.entidades.Mesas;
import com.example.api_comandas.repositorios.MesasRepositorio;

@Service
public class MesasServicio {

        private final MesasRepositorio mesasRepositorio;

    @Autowired
    public MesasServicio(MesasRepositorio mesasRepositorio) {
        this.mesasRepositorio = mesasRepositorio;
    }

    public List<String> obtenerDatosDeMesas() {
        List<Mesas> mesas = mesasRepositorio.findAll(Sort.by("id").ascending());
        return mesas.stream()
                .map(mesa -> String.format("%d - %s - %d - %s", mesa.getId(), mesa.getNombre(), mesa.getComensales(), mesa.getEstado()))
                .collect(Collectors.toList());
    }
    
    public List<String> obtenerNombreDeMesas() {
        List<Mesas> mesas = mesasRepositorio.findAll((Sort.by("id").ascending()));
        return mesas.stream()
                .map(Mesas::getNombre)
                .collect(Collectors.toList());
    }

    public List<String> obtenerEstadoDeMesas() {
        List<Mesas> mesas = mesasRepositorio.findAll((Sort.by("id").ascending()));
        return mesas.stream()
                .map(Mesas::getEstado)
                .collect(Collectors.toList());
    }
}
