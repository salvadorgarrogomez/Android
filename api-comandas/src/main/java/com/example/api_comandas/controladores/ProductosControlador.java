package com.example.api_comandas.controladores;

import com.example.api_comandas.servicios.ProductosServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ProductosControlador {

    @Autowired
    private ProductosServicio productoServicio;

    @GetMapping("/productos")
    public List<String> obtenerDatosProductos() {
        return productoServicio.obtenerDatosProductos();
    }

    @GetMapping("/productos/nombres")
    public List<String> obtenerNombresDeProductos() {
        return productoServicio.obtenerNombresDeProductos();
    }
}

