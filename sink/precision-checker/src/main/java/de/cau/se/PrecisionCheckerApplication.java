package de.cau.se;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrecisionCheckerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrecisionCheckerApplication.class, args);
    }
}
