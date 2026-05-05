package edu.pzks.projtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class ProjTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjTestApplication.class, args);
    }

}
