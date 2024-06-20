package com.example.api_comandas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.api_comandas"})
@PropertySource("classpath:application.properties")
public class ApiComandasApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiComandasApplication.class, args);
    }
}


