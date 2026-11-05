package br.com.fiap.esq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EsqApplication {

    public static void main(String[] args) {
        SpringApplication.run(EsqApplication.class, args);
    }
}
