package com.example.api_comandas.servicios;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.api_comandas.entidades.Mesas;
import com.example.api_comandas.repositorios.MesasRepositorio;

import jakarta.persistence.EntityNotFoundException;

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
                .map(mesa -> String.format("%d - %s - %d - %s", mesa.getId(), mesa.getNombre(), mesa.getComensales(),
                        mesa.getEstado()))
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

    public List<Integer> obtenerComenaslesDeMesas() {
        List<Mesas> mesas = mesasRepositorio.findAll((Sort.by("id").ascending()));
        return mesas.stream()
                .map(Mesas::getComensales)
                .collect(Collectors.toList());
    }

    public void actualizarEstado(Long id, String nuevoEstado) {
        Mesas mesa = mesasRepositorio.findById(id).orElseThrow(() -> new EntityNotFoundException("Mesa no encontrada con ID: " + id));
        mesa.setEstado(nuevoEstado);
        mesasRepositorio.save(mesa);
    }

    public Mesas actualizarComensales(Long id, Integer nuevoComensales) {
        Mesas mesa = mesasRepositorio.findById(id).orElseThrow(() -> new RuntimeException("Mesa no encontrada"));
        mesa.setComensales(nuevoComensales);
        return mesasRepositorio.save(mesa);
    }
}
