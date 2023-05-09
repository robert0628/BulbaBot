package com.belka.BulbaBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.belka.BulbaBot", "com.belka.weather"})
public class BulbaBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BulbaBotApplication.class, args);
    }
//todo refactor dependencies in POM
    // todo QR module pulls @value from new Weather application.properties
}
