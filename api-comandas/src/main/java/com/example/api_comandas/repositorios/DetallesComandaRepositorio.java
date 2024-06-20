package com.example.api_comandas.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.api_comandas.entidades.DetallesComanda;

@Repository
public interface DetallesComandaRepositorio extends JpaRepository<DetallesComanda, Long> {
    
}
