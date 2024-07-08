package com.example.api_comandas.servicios;

import com.example.api_comandas.entidades.Productos;
import com.example.api_comandas.repositorios.ProductosRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductosServicio {

    @Autowired
    private ProductosRepositorio productosRepositorio;

    public List<String> obtenerDatosProductos() {
        List<Productos> productos = productosRepositorio.findAll(Sort.by("id").ascending());
        return productos.stream()
                .map(producto -> String.format("%s - %.2f€ - %s", producto.getNombre(), producto.getPrecio(), producto.getTipo_porcion()))
                .collect(Collectors.toList());
    }

    public List<String> obtenerNombresDeProductos() {
        List<Productos> productos = productosRepositorio.findAll();
        return productos.stream()
                .map(Productos::getNombre) // Obtiene solo el nombre de cada categoría
                .collect(Collectors.toList());
    }
}
