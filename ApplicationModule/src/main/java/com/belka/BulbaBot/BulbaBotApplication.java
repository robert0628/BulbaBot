package com.belka.BulbaBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.belka.BulbaBot",
        "com.belka.weather",
        "com.belka.core",
        "com.belka.newDiary",
        "com.belka.QR",
        "com.belka.users"})
public class BulbaBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(BulbaBotApplication.class, args);
    }
}
