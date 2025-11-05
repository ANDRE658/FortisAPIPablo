package br.unipar.projetointegrador.frotisapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FrotisApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrotisApiApplication.class, args);
    }

    @Bean // Informa ao Spring para gerenciar este objeto
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
