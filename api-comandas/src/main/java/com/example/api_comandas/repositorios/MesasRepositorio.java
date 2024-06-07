package com.example.api_comandas.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.api_comandas.entidades.Mesas;

public interface MesasRepositorio extends JpaRepository<Mesas, Long>{
    
}
